package de.tum.in.i22.uc.policy.translation.ecacreation;

import java.awt.Container;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Map;

import javax.swing.JDialog;
import javax.swing.JRadioButton;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
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

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import de.tum.in.i22.uc.ecajaxb.AuthorizationActionType;
import de.tum.in.i22.uc.ecajaxb.AuthorizationAllowType;
import de.tum.in.i22.uc.ecajaxb.AuthorizationInhibitType;
import de.tum.in.i22.uc.ecajaxb.BinaryOperatorType;
import de.tum.in.i22.uc.ecajaxb.ConditionType;
import de.tum.in.i22.uc.ecajaxb.DelayActionType;
import de.tum.in.i22.uc.ecajaxb.EmptyOperatorType;
import de.tum.in.i22.uc.ecajaxb.EventMatchingOperatorType;
import de.tum.in.i22.uc.ecajaxb.ExecuteActionType;
import de.tum.in.i22.uc.ecajaxb.IOperatorType.OperatorTypeValue;
import de.tum.in.i22.uc.ecajaxb.ModifyActionType;
import de.tum.in.i22.uc.ecajaxb.ParamMatchType;
import de.tum.in.i22.uc.ecajaxb.ParameterInstance;
import de.tum.in.i22.uc.ecajaxb.PolicySetType;
import de.tum.in.i22.uc.ecajaxb.PreventiveMechanismType;
import de.tum.in.i22.uc.ecajaxb.StateBasedOperatorType;
import de.tum.in.i22.uc.ecajaxb.TimeBoundedUnaryOperatorType;
import de.tum.in.i22.uc.ecajaxb.TimeUnitType;
import de.tum.in.i22.uc.ecajaxb.UnaryOperatorType;
import de.tum.in.i22.uc.policy.translation.Filter;

/**
 * Responsible for the final part of translation- generation
 * of Event-Condition-Action (ECA) rules.
 * 
 * @author ELIJAH
 *
 */
public class ECARulesCreator implements Filter{
	
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
	 * Result of the ECA creation
	 */
	private String output;
	
	/**
	 * Receives input as an xml string from
	 * action refinement
	 * 
	 * @param xmlInput
	 */
	public ECARulesCreator(Map<String, String> params, String xmlInput, Container container){
		sXmlInput=xmlInput;
		alSubformula=new ArrayList<Subformula>();
		fStatus=FilterStatus.FAILURE;
		oContainer=container;
		ecaTemplateId = params.get("template_id");
		if(ecaTemplateId == null)
			ecaTemplateId = "";
		policyObjectId = params.get("object_instance");
		if(policyObjectId == null) 
			policyObjectId = "";
		this.policyParams = params;
	}
	
	/**
	 * Receives input as an xml string from
	 * action refinement
	 * 
	 * @param xmlInput
	 */
	public ECARulesCreator(Map<String, String> params, String xmlInput){
		this(params, xmlInput, new JDialog());
	}
	
	private String sXmlInput;
	private static ArrayList<Subformula> alSubformula;
	private String sOutputFile;
	private Container oContainer; // it's only old code which was not refactored. it's not used anywhere.
	private String ecaTemplateId ;
	private String policyObjectId;
	private Map<String,String> policyParams;
	/**
	 * Entrance into main routine for rule creation
	 * 
	 * @param container
	 */
	@Override
	public void filter(){
		if(sXmlInput==null) return;
		if(sXmlInput.equals("")) return;
		
		//1.
		//open xml input
		Document document=openXmlInput(sXmlInput);
		if(document==null)
			return;
		//Get obligations		
		NodeList nlObligation=processXpathExpression("//Obligation", document);
		if(nlObligation!=null){
			if(nlObligation.getLength()>0){
				//processing obligation nodes				
				for(int i=0; i<nlObligation.getLength(); ++i){					
					NodeList nlOb=nlObligation.item(i).getChildNodes();
					Node n= nlObligation.item(i);					
					if(nlOb!=null){
						if(nlOb.getLength()==1)
							createSubFormulas(nlOb.item(0));
						else System.out.println("OSL syntax error: one obligation has only one propositional statement");
					}
					
				}
				
			}
		}
		else {
			fStatus=FilterStatus.FAILURE;
			sMessage="- Policy contains no obligations.";
			return;
		}
		
		
		//2. Get rid of repetitive parts in the formula		
		minimizeExpression(alSubformula);
		
		if(ecaTemplateId.length() != 0){
			ECARulesAutomation ecaGenerator = new ECARulesAutomation(policyParams, alSubformula);
			ecaGenerator.transform();
		}
		else {
			//3. Launch the ECA UI to configure ECA rules
			ECARulesCreatorView.launch(alSubformula, new JDialog());
			if(ECARulesCreatorView.isUserCancelled()){
				fStatus=FilterStatus.FAILURE;
				sMessage="- ECA rules creation was cancelled.";
				return;
			}
		}
		
		//System.out.println("OUTPUT FILE:\n");
		//3. Write ECA FTPRules to file
		createOutputFile();
		
		fStatus=FilterStatus.SUCCESS;
		sMessage="- ECA rules created successfully.";
	}
	
	private void minimizeExpression(ArrayList<Subformula> alSubformula2) {
		ArrayList<Subformula> formulas = new ArrayList<Subformula>(alSubformula2);
		ArrayList<Integer> toRemove = new ArrayList<Integer>();
		for(int index = 0; index<formulas.size()-1; index++){
			Subformula f = formulas.get(index);				
			
			for(int i=index+1; i<formulas.size(); i++){
				Subformula ff = formulas.get(i);
				String sf = f.getNodeString();
				String sff = ff.getNodeString();
				boolean remove = f.equals(ff);
				if(remove && sf.equals(sff))
					toRemove.add(index);
			}
		}
		
		ArrayList<Subformula> processedFormulas = new ArrayList<Subformula>();
		for(int index=0; index<formulas.size(); index++){
			if(!toRemove.contains(index)){
				processedFormulas.add(formulas.get(index));
			}
		}
		
		formulas.clear();
		toRemove.clear();
		for(int i=0; i<processedFormulas.size(); i++){
			Subformula form = processedFormulas.get(i);
			String subformulaNodeName = form.getNodeSource().getNodeName();
			if(subformulaNodeName.trim().equals("false")){
				toRemove.add(i);
			}
		}
		for(int index=0; index<processedFormulas.size(); index++){
			if(!toRemove.contains(index)){
				formulas.add(processedFormulas.get(index));
			}
		}
		
		alSubformula = formulas;
	}

	/**
	 * Creates the ECA xml output file from configured information
	 */
	private void createOutputFile(){
		PolicySetType policyType;
		try{
			//root element
			policyType=new PolicySetType();
			
			if(alSubformula.size()<1) throw new Exception("ECA FTPRules not set");
			
			//process and prepare ECA rule for each
			//subformula
			for(int i=0; i<alSubformula.size(); ++i){
				ECARule rule=alSubformula.get(i).getECARule();
				PreventiveMechanismType mechanism=getMechanismType(rule, i);				
				policyType.getDetectiveMechanismOrPreventiveMechanism().add(mechanism);

				//FileOutputStream stream=new FileOutputStream(new File(sOutputFile));
			}
			
			//write to file
			JAXBContext context=JAXBContext.newInstance("de.tum.in.i22.uc.ecajaxb");
			Marshaller marshaller=context.createMarshaller();			
			//FileOutputStream stream=new FileOutputStream(new File(sOutputFile));
			
			DocumentBuilderFactory dbf=DocumentBuilderFactory.newInstance();
			dbf.setNamespaceAware(false);
			DocumentBuilder db=dbf.newDocumentBuilder();
			Document doc=db.newDocument();
			
		    //I am to be marshalling to the stream as
			//seen in the commented code below but
			//it does not give what it expected.
			//The schema file from which I created jaxb classes
			//for generating ECA rules needs to be updated.
			marshaller.marshal(policyType, doc);
			//marshaller.marshal(policyType, System.out);
			//marshaller.marshal(policyType, stream);
			//stream.close();
			
			
			
			String sResult=getNodeString(doc.getDocumentElement());
			
			sResult=organizeNamespaces(sResult);
			
			System.out.println("\n\n\n### ECA Result:\n" + sResult + "\n");
			
			output = sResult;
			//
			//FileWriter fw=new FileWriter(sOutputFile);
			//fw.write(sResult);
			//fw.close();
			
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	private Document policyCreator(String xmlPolicy){
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();  
        DocumentBuilder builder;  
        try 
        {  
            builder = factory.newDocumentBuilder();  
            Document doc = builder.parse( new InputSource( new StringReader( xmlPolicy ) ) ); 
            return doc;
        } catch (Exception e) {  
            e.printStackTrace();  
        } 
        return null;
	}
	
	/**
	 * Takes xml input of ECA rules as a string and properly
	 * prepares the namespaces. 
	 * 
	 * @param text
	 * @return Returns ECA rules in xml as a string with namespaces
	 * properly set to what the PDP can allow.
	 */
	private String organizeNamespaces(String text){
		String sResult;
		
		sResult=text.replaceAll("ns2:", "");			
		sResult=sResult.replaceAll("ns3:", "");			
		sResult=sResult.replaceAll("ns4:", "");	
		
		//modify <policySet>
		String sReplacement="<policy name=\""+ this.policyParams.get("policy_id") +"\" xmlns=\"http://www22.in.tum.de/enforcementLanguage\" xmlns:tns=\"http://www22.in.tum.de/enforcementLanguage\" xmlns:a=\"http://www22.in.tum.de/action\" xmlns:e=\"http://www22.in.tum.de/event\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">";
		sResult=sResult.replaceAll("<policy (.+)>", sReplacement);
		
		return sResult;
	}
	
	/**
	 * 
	 * @return Returns a node and its content as an xml string
	 */
	public String getNodeString(Node nodeSource){
		StringWriter sw=null;
		StreamResult sr=null;		
		try{
			sw = new StringWriter();
			sr = new StreamResult(sw);								
			DOMSource src = new DOMSource(nodeSource);			
			TransformerFactory transfac = TransformerFactory.newInstance();
			//			
			Transformer trans = transfac.newTransformer();			
			//
			trans.setOutputProperty(OutputKeys.INDENT, "yes");
			trans.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
			trans.transform(src, sr);				
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return sw.toString();		
	}
	
	
	/**
	 * Intelligently returns a mechanism based on an eca rule.
	 * If any one of Event or condition or Action is null,
	 * the returned object is null
	 * 
	 * @param rule
	 * @return
	 */
	private PreventiveMechanismType getMechanismType(ECARule rule, int iSubformula){
		PreventiveMechanismType mechanism=new PreventiveMechanismType();
		String sMechanism="Mechanism_"+String.valueOf(iSubformula+1)+"_"+rule.getType()+"_"+policyObjectId;
		/* used to uniquely identify a policy */
		String policyId = policyParams.get("policy_id");
		sMechanism = policyId +"_"+ sMechanism;
		mechanism.setName(sMechanism);
		
		if(rule!=null){			
			
			//1. Get EventMatchingOperatorType from configured Event			
			EventMatchingOperatorType event=getMatchingEvent(rule.getEvent());
			mechanism.setTrigger(event);
			
			//2. Get ConditionType from condition configured Condition
			ConditionType conditionType=getConditionForMatchingEvent(rule.getCondition(), iSubformula);
			mechanism.setCondition(conditionType);
//			System.out.println("rule.getcondition: "+rule.getCondition().toString());
			//3.			
			AuthorizationActionType actionType=getAuthorizationAction(rule.getAction(), iSubformula);
			mechanism.getAuthorizationAction().add(actionType);
			
			
			//4.
						
		}
		else {System.out.println("No ECA rule for this subformula");}
//		System.out.println("Mechanism.condition: "+mechanism.getCondition().toString());
		return mechanism;
		
	}
	
	/**
	 * Derive trigger event from a subformula event suitable
	 * for persisting in an xml file
	 * 
	 * @param event
	 * @return Returns an EventMatchingOperatorType for a trigger event
	 */
	private EventMatchingOperatorType getMatchingEvent(SubformulaEvent event){
		EventMatchingOperatorType matchingEvent=new EventMatchingOperatorType();
		String eventName="";
		if(event!=null){
			eventName=event.getEventName();			
			if(eventName!=null) matchingEvent.setAction(eventName);
			matchingEvent.setIsTry(true);
			if(event.getParameters().size()>0){
				String paramName="", paramValue="", paramContainment="";
				for(int i=0; i<event.getParameters().size(); ++i){
					SubformulaEventParameter param=event.getParameters().get(i);
					ParamMatchType paramMatchType=new ParamMatchType();
					paramName=param.getName();
					paramValue=param.getValue();
					paramContainment=param.getPolicyType();
					if(paramName!=null)	paramMatchType.setName(paramName);
					if(paramValue!=null) paramMatchType.setValue(paramValue);
					if(paramName.equalsIgnoreCase("object")){
						if(paramContainment!=null) paramMatchType.setType(paramContainment);
					}
					matchingEvent.getParamMatch().add(paramMatchType);
				}
			}
		}		
		
		return matchingEvent;
	}
	
	/**
	 * Derive trigger event from a subformula event suitable
	 * for persisting in an xml file
	 * 
	 * @param event
	 * @return Returns an EventMatchingOperatorType for a trigger event
	 */
	private EventMatchingOperatorType getMatchingEvent(Node node){
		EventMatchingOperatorType matchingEvent=new EventMatchingOperatorType();
		String eventName="";
		if(node!=null){
			if(node.getAttributes().getLength()>0){
				if(node.getAttributes().getNamedItem("name")!=null)
				eventName=node.getAttributes().getNamedItem("name").getNodeValue();
			}
			matchingEvent.setAction(eventName);
			matchingEvent.setIsTry(true);
			if(node.getChildNodes().getLength()>0){				
				NodeList nlParams=node.getChildNodes();
				for(int i=0; i<nlParams.getLength(); ++i){
					String paramName="", paramValue="", paramContainment="";
					Node nParam=nlParams.item(i);
					if(nParam.getNodeType()==Node.ELEMENT_NODE){						
						ParamMatchType paramMatchType=new ParamMatchType();
						if(nParam.getAttributes().getLength()>0){
							//name
							Node nName=nParam.getAttributes().getNamedItem("name");
							if(nName!=null) paramName=nName.getNodeValue();
							//containment
							Node nCont=nParam.getAttributes().getNamedItem("policyType");
							if(nCont!=null) paramContainment=nCont.getNodeValue();
							//value
							Node nValue=nParam.getAttributes().getNamedItem("value");
							if(nValue!=null) paramValue=nValue.getNodeValue();
						}						
						if(paramName!=null)	paramMatchType.setName(paramName);
						if(paramValue!=null) paramMatchType.setValue(paramValue);
						if(paramContainment!=null){
							if(!paramContainment.equals("")) paramMatchType.setType(paramContainment);
						}
						
						matchingEvent.getParamMatch().add(paramMatchType);
					}
				}
			}
		}		
		
		return matchingEvent;
	}
	

	
	/**
	 * Prepares the condition part of an ECA rule for a given
	 * subformula.
	 * 
	 * @param condition
	 * @param iSubformula
	 * @return Returns the conditionType for a given subformula
	 */
	private ConditionType getConditionForMatchingEvent(Object condition, int iSubformula){
		ConditionType conditionType=new ConditionType();
		if(condition!=null){
			String sCondition=(String)condition;
			Subformula subformula=alSubformula.get(iSubformula);
			//
			JRadioButton button=(JRadioButton)subformula.getRbCondition();
			if(button == null){
				//AUTOMATION branch
				sCondition = sCondition.trim();
				if(sCondition.equalsIgnoreCase("<true/>")){
					conditionType.setTrue(new EmptyOperatorType());
				}
				else if(sCondition.equalsIgnoreCase("<false/>")){
					conditionType.setFalse(new EmptyOperatorType());
				}
				else {
					conditionType=prepareConditionForSubformula(sCondition);
				}
//				System.out.println("condition type here: "+conditionType);
			}
			//UI EDITOR branch
			else if(button.getText().equalsIgnoreCase("true"))
				conditionType.setTrue(new EmptyOperatorType());
			else if(button.getText().equalsIgnoreCase("subformula")){
				conditionType=prepareConditionForSubformula(sCondition.trim());
//				System.out.println("condition type here: "+conditionType);
			}
			else conditionType.setXPathEval(sCondition);
			
		}
		return conditionType;
	}
	
	/**
	 * Prepares the condition for subformula properly after a recursive
	 * call.
	 * 
	 * @param subformula
	 * @return Returns a condition for provided subformula.
	 */
	private ConditionType prepareConditionForSubformula(String subformula){
		ConditionType conditionType=new ConditionType();
			
				DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
				DocumentBuilder builder;
				try {
					builder = factory.newDocumentBuilder();
				
				InputSource inputSource;				
				inputSource = new InputSource(new StringReader(subformula));
				Document doc;
				
					doc = builder.parse(inputSource);
				
				processNode(doc.getDocumentElement(),null);
				//
				if(oConditionType instanceof UnaryOperatorType){
					UnaryOperatorType uot=(UnaryOperatorType) oConditionType;
					if(uot.isAlways()) conditionType.setAlways(uot);
					else if(uot.isEventually()) conditionType.setEventually(uot);
					else if(uot.isNot()) conditionType.setNot(uot);		
				}
				else if(oConditionType instanceof BinaryOperatorType){
					BinaryOperatorType bot=(BinaryOperatorType) oConditionType;
					if(bot.isAnd()) conditionType.setAnd(bot);
					else if(bot.isImplies()) conditionType.setImplies(bot);
					else if(bot.isOr()) conditionType.setOr(bot);
					else if(bot.isSince()) conditionType.setSince(bot);
				}
				else if(oConditionType instanceof TimeBoundedUnaryOperatorType){
					TimeBoundedUnaryOperatorType tbuot=(TimeBoundedUnaryOperatorType) oConditionType;
					if(tbuot.isBefore()) conditionType.setBefore(tbuot);
					else if(tbuot.isDuring()) conditionType.setDuring(tbuot);					
				}
				else if(oConditionType instanceof EventMatchingOperatorType){
					conditionType.setEventMatch((EventMatchingOperatorType) oConditionType);
				}
				else if(oConditionType instanceof StateBasedOperatorType){
					conditionType.setStateBasedFormula((StateBasedOperatorType) oConditionType);
				}
				else if(oConditionType instanceof TimeBoundedUnaryOperatorType.RepLim)
					conditionType.setRepLim((TimeBoundedUnaryOperatorType.RepLim) oConditionType);
				else if(oConditionType instanceof TimeBoundedUnaryOperatorType.RepMax)
					conditionType.setRepMax((TimeBoundedUnaryOperatorType.RepMax) oConditionType);
				else if(oConditionType instanceof TimeBoundedUnaryOperatorType.RepSince)
					conditionType.setRepSince((TimeBoundedUnaryOperatorType.RepSince) oConditionType);
				} catch (ParserConfigurationException e) {
					e.printStackTrace();
				} catch (SAXException | IOException e) {
					e.printStackTrace();
				}
			
		return conditionType;
	}
	
	private Object oConditionType;
	
	/**
	 * Recursively prepare an operator type from a subformula xml node
	 * 
	 * @param node
	 * @return Returns a nested operator type
	 */
	private void processNode(Node node, Object parent){		
//		System.out.print("node's second child: "+ node.getFirstChild().getNextSibling().getNodeName());
		Object oParent2Pass=null;
		
		String sNodeName=node.getNodeName();
		
		Node nodeOperator = node.getAttributes().getNamedItem("operator");
		String sNodeOperator ="";
		if(nodeOperator!=null){
			sNodeOperator = nodeOperator.getNodeValue();
		}
		
		OperatorTypeValue type=determineType(sNodeName);
		//
		
		switch(type){
		
			case UNARY:				
				UnaryOperatorType uot=new UnaryOperatorType();							
				if(parent==null){
					//set return result because this is a fresh call
					oConditionType=uot;					
				}				
				uot.configureMyself(sNodeName,parent);
				oParent2Pass=uot.getParentToPass();							
				break;
				
			case BINARY:				
				BinaryOperatorType bot=new BinaryOperatorType();				
				if(parent==null){
					//set return result because this is a fresh call
					oConditionType=bot;					
				}				
				bot.configureMyself(sNodeName, parent);
				oParent2Pass=bot.getParentToPass();
				
				break;
			
			case EVENT:				
				EventMatchingOperatorType emot=getMatchingEvent(node);
				if(parent==null){
					//set return result because this is a fresh call
					oConditionType=emot;
				}
				emot.configureMyself(sNodeName, parent);
			
				break;
				
			case STATEBASED:
				StateBasedOperatorType sbot=new StateBasedOperatorType();
				if(parent==null){
					//set return result because this is a fresh call
					oConditionType=sbot;
				}				
				sbot.configureMyself(sNodeName, parent);
				sbot.populateWithData(node);
				//Just like in the previous case, we need not pass any parent to further steps
				
				break;
				
			case TIMEBOUNDEDUNARY:
				TimeBoundedUnaryOperatorType tbuot= new TimeBoundedUnaryOperatorType().getMatchingTemporalOperator(node);			
				if(parent==null){
					//set return result because this is a fresh call
					oConditionType=tbuot;
				}
				
				tbuot.configureMyself(sNodeName, parent);
				oParent2Pass = tbuot.getParentToPass();
				
				break;
			
			default:
				
				break;
		
		}
			
		NodeList nodeList=node.getChildNodes();
//		int childrenCounter = nodeList.getLength();
		//if no children, stop.
		if(nodeList.getLength()>0){
			//else call this routine for the children
			for(int i=0; i<nodeList.getLength(); ++i){
				Node child = nodeList.item(i);
				if(child.getNodeType()==Node.ELEMENT_NODE)
					processNode(nodeList.item(i),oParent2Pass);
			}
		}		
		
		
	}
		
	/**
	 *  
	 * @param text
	 * @param sNodeOperator 
	 * @return Returns the operator type value for a given operator.
	 */
	private OperatorTypeValue determineType(String text){
		if(text.equalsIgnoreCase("not") || text.equalsIgnoreCase("eventually") || text.equalsIgnoreCase("always") || text.equalsIgnoreCase("true") || text.equalsIgnoreCase("false") )
			return OperatorTypeValue.UNARY;
		else if(text.equalsIgnoreCase("or") || text.equalsIgnoreCase("and") || text.equalsIgnoreCase("implies") || text.equalsIgnoreCase("since")  )
			return OperatorTypeValue.BINARY;
		else if( text.equalsIgnoreCase("before") || text.equalsIgnoreCase("during") || text.equalsIgnoreCase("within") 
				|| text.equalsIgnoreCase("replim") || text.equalsIgnoreCase("repsince") || text.equalsIgnoreCase("repmax") )
			return OperatorTypeValue.TIMEBOUNDEDUNARY;
		else if(text.equalsIgnoreCase("event")) return OperatorTypeValue.EVENT;
		else if(text.equalsIgnoreCase("stateBasedFormula") )
			return OperatorTypeValue.STATEBASED;		
		return OperatorTypeValue.OTHER;
	}
	
	/**
	 * Determines the action part of an ECA rule for a given subformula.  
	 *   
	 * @param action
	 * @param iSubformula
	 * @return Returns the authorization action for a given subformula.
	 */
	@SuppressWarnings("unchecked")
	private AuthorizationActionType getAuthorizationAction(Object action, int iSubformula){
		AuthorizationActionType aAllowType=new AuthorizationActionType();
		AuthorizationAllowType aActionType = new AuthorizationAllowType(); 
		String sAuth=this.policyParams.get("policy_id")+"_"+"Authorization_"+String.valueOf(iSubformula+1);
		aAllowType.setName(sAuth);
		if(action!=null){			
			int iSel=alSubformula.get(iSubformula).getJcbAction();
			//hook up for automation
			if(iSel == 0){
				ArrayList<Object> actions = new ArrayList<Object>();
				for(Object o : (Object []) action){
					actions.add(o);
				}
				if (((String)actions.get(0)).equals("<inhibit/>"))
					iSel = 1;
				else if (((String)actions.get(0)).equals("<modify/>"))
					iSel = 2;
				else if (((String)actions.get(0)).equals("<execute/>"))
					iSel = 3;
				else if (((String)actions.get(0)).equals("<delay/>"))
					iSel = 4;				
				actions.remove(0);
				action = actions;
			}
			switch(iSel){
				case 1:
					//inhibit. I have to try this for now but its
					//unsatisfactory
					aAllowType.setInhibit(new AuthorizationInhibitType());
					break;
				case 2:
					//modify. There is a ModifyActionType to be prepared					
					ArrayList<Object> alModify=(ArrayList<Object>)action;					
					ModifyActionType mat=new ModifyActionType();
					if(alModify.size()>0){	
						for(int i=0; i<alModify.size(); ++i){
							String name = (String) alModify.get(i);
							String value = (String) alModify.get(++i);
							ParameterInstance paramObj=new ParameterInstance();
							paramObj.setName(name);
							paramObj.setValue(value);
							mat.getParameter().add(paramObj);
						}
					}
					aActionType.setModify(mat);		
					aAllowType.setAllow(aActionType);
					break;
				case 3:
					//execute. AuthorizationActionType provides we add execution actions
					ArrayList<SubformulaEvent> alAction=(ArrayList<SubformulaEvent>)action;						
					if(alAction.size()>0){						
						for(int i=0; i<alAction.size(); ++i){
							String sName="";
							ExecuteActionType eaType=new ExecuteActionType();
							SubformulaEvent eAction=alAction.get(i);
							//set name
							sName=eAction.getEventName();							
							if(sName!=null) eaType.setName(sName);
							
							//set the parameters for this action
							if(eAction.getParameters().size()>0){
								for(int j=0; j<eAction.getParameters().size(); ++j){
									String sParamName="",sParamValue="";
									sParamName=eAction.getParameters().get(j).getName();
									sParamValue=eAction.getParameters().get(j).getValue();
									ParameterInstance paramInstance=new ParameterInstance();
									if(sParamName!=null && sParamValue!=null)
									{
										paramInstance.setName(sParamName);
										paramInstance.setValue(sParamValue);
										if(sParamName.equals("pxpID")){
											eaType.setId(sParamValue);
										}
									}
									eaType.getParameter().add(paramInstance);
								}
							}
	
							aActionType.getExecuteAction().add(eaType);
						}
					}					
							
					aAllowType.setAllow(aActionType);					
					
					break;
				case 4:
					//delay. There is a DelayActionType object to be prepared					
					ArrayList<Object> alDelay=(ArrayList<Object>)action;
					aActionType=new AuthorizationAllowType();
					DelayActionType dat=new DelayActionType();
					if(alDelay.size()>0){
						String sAmount=(String)alDelay.get(1);
						if(sAmount.equals("")) dat.setAmount(0);
						else dat.setAmount(Integer.valueOf(sAmount));
						dat.setUnit(TimeUnitType.fromValue(alDelay.get(0).toString().toUpperCase()));
					}
					aActionType.setDelay(dat);	
					aAllowType.setAllow(aActionType);
					break;				
				case 5:
					//delete
					
					break;
			}								
		
		}		
		return aAllowType;
	}
	
	
	/**
	 * Recursive routine to find subformulas starting with node
	 * <or> passed in
	 * 
	 * @param node
	 */
	private void createSubFormulas(Node node){		
		//if this node is not an <or>
		//then we have a subformula
		//else we examine the children
		//of this <or> node
		String sName=node.getNodeName();
		if(!sName.equalsIgnoreCase("or")){
			Subformula sFormula= Subformula.createSubFormula(node);				
			alSubformula.add(sFormula);
		}
		else{
			NodeList nlChildren=node.getChildNodes();
			for(int i=0; i<nlChildren.getLength(); ++i)
				createSubFormulas(nlChildren.item(i));			
		}
		
	}
	
	/**
	 * Provide help with reading from xml strings and file paths 
	 * 
	 * @param input
	 * @param type
	 * @return Returns an xml document
	 */
	public static Document openXmlInput(String input){
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			Document doc=null;
			
			try {
				DocumentBuilder builder = factory.newDocumentBuilder();
				InputSource inputSource;
				inputSource = new InputSource(new StringReader(input));
				doc = builder.parse(inputSource);							
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
	 * 
	 * @param expression
	 * @param document
	 * @return Returns a node list of xml elements
	 */
	public static NodeList processXpathExpression(String expression, Object document){
		NodeList nodeList=null;				
		XPathFactory factory=XPathFactory.newInstance();
		XPath xpath=factory.newXPath();
		try {
			nodeList=(NodeList)xpath.evaluate(expression,document,XPathConstants.NODESET);
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}
		return nodeList;
	}

	@Override
	public FilterStatus getFilterStatus() {
		return fStatus;
	}

	@Override
	public String getMessage() {
		return sMessage;
	}

	public String getOutput(){
		return output;
	}
	
}
