/**
 * 
 */
package de.tum.in.i22.uc.policy.translation.actionrefinement;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import de.tum.in.i22.uc.policy.classes.Resource;
import de.tum.in.i22.uc.policy.classes.ResourceCreator;
import de.tum.in.i22.uc.policy.translation.Config;
import de.tum.in.i22.uc.policy.translation.Filter;

/**
 * @author ladmin
 *
 */
public class ActionRefinement implements Filter{
	
	/**
	 * Status of pipe and filter operation
	 */
	private FilterStatus fStatus;
	/**
	 * Messages during data movement through our
	 * pipe and filter system
	 */
	private String sMessage;

	/**
	 * Creates an instance of action refinement class 
	 */
	public ActionRefinement() {
		fStatus=FilterStatus.FAILURE;
	}
	
	//represents access configuration file of properties
	private static Config config;
	//input string from future-to-past translation
	protected static String sInput;
	//represents a data resource
	private static Resource resource;
	private static String sPolicyParameters;
	public static String sOutput;
	
	/**
	 * Create ActionRefinement instance
	 * 
	 * @param sInput
	 * @param sOutput
	 */
	public ActionRefinement(String string){
		
		//some initialization		
		sXmlOutputTauEvent="";
		//sOutputFile=sOutput;
		sInput=string;
		sOutput="";
		fStatus=FilterStatus.FAILURE;
		
	}
	
	/**
	 * Main entrance into action refinement.
	 */
	@Override
	public void filter(){
		try {				
			config = new Config();				
			resource=prepareResourceInstance();				
			tauAction();
			fStatus=FilterStatus.SUCCESS;
			sMessage="- Action refinement was successful. ";
		}
		catch(ActionMatchingException ame){
			fStatus=FilterStatus.FAILURE;			
			sMessage="- "+ame.getMessage();
		}
		catch (Exception e) {
			fStatus=FilterStatus.FAILURE;			
			sMessage="- Action refinement was unsuccessful.";
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @return Returns the output of action refinement for use
	 * in a later stage.
	 */
	public String getOutput(){
		return sOutput;
	}
	
	/**
	 * Main entrance into refinement routine.
	 * Tau(Action) = TauEvent(Event) + TauState(State)
	 * 
	 * @throws ActionMatchingException 
	 * @throws DOMException 
	 * @throws XPathExpressionException 
	 * @throws IOException 
	 * 
	 */
	private void tauAction() throws XPathExpressionException, DOMException, IOException, ActionMatchingException {
		
		//1st call: TauEvent(Event)
		//this will throw an exception		
//		String sFinalOutput = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";		
//		String sSubject=getPolicySubject();
//		sFinalOutput += "<Policy Subject=\""+sSubject+"\"><Obligations>";	
		
//		new TauEvRules().runTauEvRules(sInput);
		
		//----------
		PIMAction action;
	
			action = PIMAction.createAction(resource);
		
		if(action==null)
			throw new ActionMatchingException("No matching action {"+resource.getExtraInformation().toString()+"} in domain file.");
				
		//init
		preparePolicyParameters();
		
		//1st step: action refinement for events, i.e. PIM -> PSM -> ISM
		//our result is in sXmlOutputTauEvent
		tauEvent(action);		
		//----------
	
		//2nd step: action refinement for state
		String sXmlOutputTauState;
		
		sXmlOutputTauState=tauState();
		//set data in state formula
		sXmlOutputTauState=setStateFormulaData(sXmlOutputTauState,resource.getDataSource());

		//3rd step:
		// action refinement is a disjunction of event and state -based refinements
		// so push an <or> node first before adding the refinement into the formula
		sOutput += "<or>"+sXmlOutputTauEvent + sXmlOutputTauState.trim() +"</or>";
		
		
//		sFinalOutput += "<Obligation><or>";
//		sFinalOutput += sXmlOutputTauEvent + sXmlOutputTauState.trim() + "</or></Obligation></Obligations></Policy>";
//			
//		
//		sOutput=sFinalOutput;
//		System.out.println("================================");
//		System.out.println("Action Refinement: ");
//		System.out.println(sOutput);
//		System.out.println("================================");
		
		//write result to file
		/*if(!sFinalOutput.equals("")){
			BufferedWriter bWriter=new BufferedWriter(new FileWriter(sOutputFile));
			bWriter.write(sFinalOutput);
			bWriter.close();
		}*/
	}
		 
	
	/**
	 * 
	 * @return Returns state formula for data.
	 */
	private String tauState(){
		String sResult="<false />";
		
		try{
			//open event declaration file
			String sEventDeclFile=config.getProperty("eventDecl");
			Document xmlDocXpath=ActionRefinement.openXmlInput(sEventDeclFile, "file");
			//Try to find our action
			XPathFactory factory=XPathFactory.newInstance();
			XPath xpath=factory.newXPath();
			String sExpression="//event[eventName='"+resource.getExtraInformation().toString()+"']/stateFormula";
			NodeList nlStateFormula=(NodeList)xpath.evaluate(sExpression, xmlDocXpath, XPathConstants.NODESET);
			if(nlStateFormula!=null){
				
				if(nlStateFormula.getLength()>0){					
					Node nodeStateFormula=nlStateFormula.item(0);						
					//Get the content of the state formula 
					//node as a string
					StringWriter sw = new StringWriter();
					StreamResult sr = new StreamResult(sw);
					DOMSource src = new DOMSource(nodeStateFormula);
					TransformerFactory transfac = TransformerFactory.newInstance();
					Transformer trans = transfac.newTransformer();
					trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
					trans.transform(src, sr);
					String sStateFormula=sw.toString();
					//Remove open and closing <stateformula> tags
					sStateFormula=sStateFormula.replace("<stateFormula>", " ");
					sStateFormula=sStateFormula.replace("</stateFormula>", " ");
					//trim result
					sResult=sStateFormula.trim();					
				}
			}
				
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
		return sResult;
	}
	
	/**
	 * Given a state formula source string and provided we have a concrete data
	 * instance, this replacement works
	 * 
	 * @param source
	 * @param data
	 * @return Returns a string of modified state formula
	 */
	private String setStateFormulaData(String source, String data){
		String sReplacement="param1=\""+data+"\"";
		return source.replaceFirst("param1=\"\\w+\"", sReplacement);
	}
	
	private static String sAssociation="";
	
	/**
	 * Entrance to refinement of action
	 * 
	 * @param action
	 * @throws XPathExpressionException
	 * @throws DOMException
	 */
	private void tauEvent(PIMAction action) throws XPathExpressionException, DOMException{
		action.tau();
	}
	
	 
	/**
	 * If during xml refinement output preparation, we have 3
	 * events e1, e2, e3, our output will be
	 * <and>e1<eventually><and>e2<eventually>e3
	 * </eventually></and></eventually></and>
	 * meaning we will have 1 </eventually> and 2 </and></eventually>
	 * For n events e1, e2, ... , e(n), we should have
	 * 1 </eventually> and n - 1 </and></eventually> so we loop from
	 * i= 1 to n-1
	 * In our case, the number of events = number of sequence calls
	 * 
	 * @return Returns a proper closing xml sequence
	 */
	public static String prepareClosingSequenceTags(int numCalls){
		String sClosingTag="";
		for(int i=1; i<numCalls; ++i){
			if(sClosingTag.equals("")) sClosingTag="</eventually>";
			else sClosingTag += "</and></eventually>";
		}		
		return sClosingTag;
	}
	 
	/**
	 * If during xml refinement output preparation, we have 3
	 * events e1, e2, e3, our output will be
	 * <or>e1<or>e2<or>e3</or></or></or>
	 * The first <or> and last </or> is handled
	 * so we are left with adding 2 closing </or> tags
	 * For n events, we append n-1 closing </or> tags.
	 * 
	 * @return Returns a proper closing xml set
	 */
	public static String prepareClosingSetTags(int numCalls){
		String sClosingTag="";
		for(int i=1; i<=numCalls; ++i){
			if(sClosingTag.equals("")) sClosingTag="</or>";
			else sClosingTag += "</or>";
		}		
		return sClosingTag;
	}
	
	/**
	 * Handles preparation of action refinement result during recursive
	 * operation.
	 * 
	 * @param reason
	 * @param event
	 * @param extraInfo
	 */
	public static void createXmlResult(Reason reason, Event event, Object extraInfo){				
						
		switch(reason){			
		
			case SET_PROCESSING:
				if(sXmlOutputTauEvent.equals("")) sXmlOutputTauEvent="<or>";
				else sXmlOutputTauEvent += "<or>";		
			break;
			
			case SET_END_PROCESSING:							
				sXmlOutputTauEvent += prepareClosingSetTags(Integer.valueOf(extraInfo.toString()));							
				break;
				
			case SEQUENCE_START:
				if(sXmlOutputTauEvent.equals("")) sXmlOutputTauEvent="<and>";
				else sXmlOutputTauEvent += "<and>";
				break;
				
			case SEQUENCE_END:								
				sXmlOutputTauEvent += prepareClosingSequenceTags(Integer.valueOf(extraInfo.toString()))+ "</and>";
				break;			
				
			case A_FINAL_TRANSFORMER_IN_SEQ:
				
				int iNum1=event.iOutputSequence;	
				if(iNum1==1){
					if(ActionRefinement.sXmlOutputTauEvent.equals("")) 
						ActionRefinement.sXmlOutputTauEvent = "<eventually>";
					else 
						ActionRefinement.sXmlOutputTauEvent += "<eventually>";
				}
				else if(iNum1==0){}
				else{
					if(ActionRefinement.sXmlOutputTauEvent.equals("")) 
						ActionRefinement.sXmlOutputTauEvent = "<eventually><and>";
					else 
						ActionRefinement.sXmlOutputTauEvent += "<eventually><and>";
				}
					
				break;
				
			case A_FINAL_TRANSFORMER:				
				if(sXmlOutputTauEvent.equals("")) sXmlOutputTauEvent = styleEvent(event);
				else sXmlOutputTauEvent += styleEvent(event);				
				break;
				
		}

	}
	

	/**
	 * Formatting output of an event
	 * 
	 * @param event
	 * @return Returns an event xml string for output
	 */
	public static String styleEvent(Event event){		
		String sResult="<event name=\""+event.getName()+"\">";
		sResult += sPolicyParameters + "</event>";
		return sResult;
	}
	
	/**
	 * Final output of action refinement
	 */
	private static String sXmlOutputTauEvent;
	
	
	/**
	 * Prepare a resource object e.g. for a resource
	 * 
	 * @return A resource instance
	 */
	private Resource prepareResourceInstance(){
		Document doc=openXmlInput(sInput,"string");
		Resource resource=null;
		if(doc!=null){
			NodeList nlParam=doc.getElementsByTagName("Param");
			//e4ts to prevent null pointer exceptions
			if(nlParam!=null){
				if(nlParam.getLength()>0){
				for(int i=0; i<nlParam.getLength(); ++i){
					//we want the param element with name attribute as object
					Node nodeAttr=nlParam.item(i).getAttributes().getNamedItem("name");
					String sName=nodeAttr.getNodeValue();
					if(sName.equalsIgnoreCase("object")){
						Node nodeParam=nlParam.item(i);	
						//set the class
						String sClass="";
						Node nodeClass=nodeParam.getAttributes().getNamedItem("class");
						if(nodeClass!=null)	sClass=nodeClass.getNodeValue();
						//create our resource
						resource=ResourceCreator.create(sClass);
						//set the value
						String sValue="";
						Node nodeValue=nodeParam.getAttributes().getNamedItem("value");
						if(nodeValue!=null) sValue=nodeValue.getNodeValue();
						resource.setDataSource(sValue);							
						Node nodeAction=doc.getElementsByTagName("Action").item(0);
						if(nodeAction!=null){
							sName="";
							Node nodeName=nodeAction.getAttributes().getNamedItem("name");
							if(nodeName!=null) sName=nodeName.getNodeValue();
							resource.setExtraInformation(sName);	
						}
						
					}
				}
			  }
			}
			
			
			}		
		return resource;
	}
	
	/**
	 * Prepares the parameter information enclosed in <params> tag
	 * 
	 * @throws XPathExpressionException
	 */
	private void preparePolicyParameters() throws XPathExpressionException{	
		
		//reset
		sPolicyParameters="";
		String sValue="";		
		
		String sExpression="//Param";
		Document doc=openXmlInput(sInput,"string");
		XPathFactory factory=XPathFactory.newInstance();
		XPath xpath=factory.newXPath();
		NodeList nlParams=(NodeList)xpath.evaluate(sExpression, doc, XPathConstants.NODESET);
		for(int i=0; i<nlParams.getLength(); ++i){
			Node nodeParam=nlParams.item(i);
			//Get parameter name
			String sName="";
			Node nodeName=nodeParam.getAttributes().getNamedItem("name");
			if(nodeName!=null) sName=nodeName.getNodeValue();			
			String sParam="<Param name=\""+sName;
			
			//Get policy type. Only applicable to instance rows
			if(sName.equalsIgnoreCase("object")){
				String sPolicyType="";
				Node nodePolicyType=nodeParam.getAttributes().getNamedItem("policyType");
				if(nodePolicyType!=null)sPolicyType=nodePolicyType.getNodeValue();
				sParam += "\" policyType=\""+sPolicyType;				
			}
			

			//Get parameter value
			Node nodeValue=nodeParam.getAttributes().getNamedItem("value");
			if(nodeValue!=null) sValue=nodeValue.getNodeValue();
			sParam += "\" value=\"" +sValue + "\" />";
						
			
			if(sPolicyParameters==null)
				sPolicyParameters=sParam;
			else sPolicyParameters += sParam;
			sParam="";
		}
		
	}
	

	/**
	 * Provide help with reading from xml strings and file paths 
	 * 
	 * @param input
	 * @param type
	 * @return Returns an xml document
	 */
	public static Document openXmlInput(String input, String type){
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			Document doc=null;
			
			try {
				DocumentBuilder builder = factory.newDocumentBuilder();
				InputSource inputSource;
				if(type.equalsIgnoreCase("string"))
				 {
					inputSource = new InputSource(new StringReader(input));
					doc = builder.parse(inputSource);
				 }
				else if(type.equalsIgnoreCase("file"))
					doc=builder.parse(new File(input));		
							
			} catch (ParserConfigurationException e) {
				e.printStackTrace();
			} catch (SAXException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			return doc;
		}
	
	/**
	 * Runs processing of xpath addresses provided
	 * 
	 * @param expression
	 * @return Returns a node list of results of processing
	 */
	public static NodeList processXpathExpression(String expression){
		NodeList nodeList=null;		
		Document xmlDocXpath=ActionRefinement.openXmlInput(config.getProperty("domainmodel"), "file");
		XPathFactory factory=XPathFactory.newInstance();
		XPath xpath=factory.newXPath();
		try {
			nodeList=(NodeList)xpath.evaluate(expression,xmlDocXpath,XPathConstants.NODESET);
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}
		return nodeList;
	}
	
	/**
	 * For some reason, the xpath expressions in sns.xml are zero-based
	 * and they don't work and have unnecessary @ symbols. This routine
	 * redefines it.
	 * 
	 * @param address
	 * @return Returns the correct xpath expression for a given one
	 */
	public static String getNewXPathAddress(String address){
		String result=null;
		
		//1st step: remove @ symbols
		String [] sArrTempResult=address.split("@");
		String sTempResult="";
		for(int i=0; i<sArrTempResult.length; ++i){			
			if(sTempResult.equals("")) sTempResult=sArrTempResult[i];
			else sTempResult += sArrTempResult[i];			
		}
		
		//2nd step: make one-based address		
		String []sArrTempResult1=sTempResult.split("\\.");		
		int iIndex=Integer.valueOf(sArrTempResult1[1]) + 1;
		result=sArrTempResult1[0] + "[" + String.valueOf(iIndex) + "]";
		return result;		
	}
	
	/**
	 * Get the subject of a policy.
	 * 
	 * @return
	 */
//	public static String getPolicySubject(){
//		Document oInputDocument=openXmlInput(sInput,"string");		
//		return oInputDocument.getDocumentElement().getAttributes().getNamedItem("Subject").getNodeValue();			
//	}		
	
	
	/**
	 * This function returns the refined action
	 * part of the policy
	 * 
	 * @param string
	 * @return
	 * @throws IOException
	 */
	
	/*
	 * (non-Javadoc)
	 * commented because this method actually does nothing 
	 * than embedding the string in an <actionref> node
	 * 
	 * @see de.tum.in.i22.uc.policy.translation.Filter#getFilterStatus()
	 */
//	public String getActionTranslation(String string) throws IOException{
//		String str= "<actionref>"+string+"</actionref>";
//		
//		
//		Config cfg = new Config();
//		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
//		try {
//			DocumentBuilder builder = factory.newDocumentBuilder();
//			
//			// take out data value, class and action from the policy
//			InputSource inputSource = new InputSource( new StringReader(string) );
//			org.w3c.dom.Document doc = builder.parse(inputSource);
//			
//			Node actionNode = doc.getFirstChild();
//			HashMap<String, String> actionHmap = new HashMap<String, String>();
//			actionHmap.put("actionName", ((Element) actionNode).getAttribute("name"));
//			
//			for(int i=0; i<doc.getElementsByTagName("Param").getLength();i++){
//				if(((Element)doc.getElementsByTagName("Param").item(i)).getAttribute("name").equalsIgnoreCase("object")){
//					actionHmap.put("dataClass", ((Element)doc.getElementsByTagName("Param").item(i)).getAttribute("class"));
//					actionHmap.put("dataValue", ((Element) doc.getElementsByTagName("Param").item(i)).getAttribute("value"));
//					break;
//				}
//			}
//			
//			
//			// build DOMs from the xml model
//			org.w3c.dom.Document domModelFile = builder.parse(new File(cfg.getProperty("domainmodel")));
//            
//			NodeList actionList = domModelFile.getElementsByTagName("pimactions");
//             for(int i=0; i<actionList.getLength(); i++){
//        	  if(actionList.item(i) instanceof Element){
//        		  if(((Element) actionList.item(i)).getAttribute("name").equalsIgnoreCase(string)){
//        			  
//        		  }
//        	  }
//           }
//				
//        } catch (ParserConfigurationException e) {
//			e.printStackTrace();
//		} catch (SAXException e) {
//			e.printStackTrace();
//		}
//		return str;		
//		
//	}

	@Override
	public FilterStatus getFilterStatus() {
		return fStatus;
	}

	@Override
	public String getMessage() {
		return sMessage;
	}

}
