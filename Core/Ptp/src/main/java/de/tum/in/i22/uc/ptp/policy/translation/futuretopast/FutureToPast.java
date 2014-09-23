/**
 * 
 */
package de.tum.in.i22.uc.ptp.policy.translation.futuretopast;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import de.tum.in.i22.uc.ptp.PtpHandler;
import de.tum.in.i22.uc.ptp.policy.translation.Filter;
import de.tum.in.i22.uc.ptp.policy.translation.TranslationController;
import de.tum.in.i22.uc.ptp.utilities.PublicMethods;

/**
 * Main class of the translation package
 *
 */
public class FutureToPast implements Filter{
	
	private static final Logger _logger = LoggerFactory.getLogger(FutureToPast.class);
	
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
	 * DOM input from previous filter
	 */
	private Document domInput;

	/**
	 * Creates an instance of this class.
	 */
	public FutureToPast(Document input) {
		fStatus=FilterStatus.FAILURE;
		domInput=input;
	}	
	
	//Get an xml node as a string
	private String getNode(Node node, Transformer trans) throws TransformerException{			
		StringWriter sw = new StringWriter();
		StreamResult result = new StreamResult(sw);
		DOMSource source = new DOMSource(node);
		trans.transform(source, result);
		return sw.toString();
	}
	
	private String sFinalOutput;
	private String sTrimmedOutput;
	public String getFinalOutput(){
		return sFinalOutput;
	}
	
	/**
	 * Entrance into future-to-past processing.
	 */
	@Override
	public void filter(){
		
		
		String sXml = null;
				
		try{
						
			/* 13 April 2013
			 * This approach to get the obligation part of policy 
			 * is better than going through ParseFuturePolicy.java 
			 * so that we can retain the subject of our policy
			 */						
			Document doc = domInput;
			Node nodeObligation= doc.getElementsByTagName("Obligation").item(0);
			Node nodePolicy=doc.getElementsByTagName("Policy").item(0);
			
			TransformerFactory transfac = TransformerFactory.newInstance();
			Transformer trans = transfac.newTransformer();
			trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");			
			
			org.w3c.dom.NodeList nodeChildren=nodeObligation.getChildNodes();
			for(int i=0; i<nodeChildren.getLength(); ++i){			
					String sNode=getNode(nodeChildren.item(i), trans);
					if(sXml==null) sXml=sNode.toString();
					else sXml=sXml.concat(sNode.toString());					
			}		
		    
		    new FTPRules().getPastCondition(sXml);		    
		    fStatus = TranslationController.fStatus;
		    sMessage = TranslationController.sMessage;
		    if(!fStatus.equals(FilterStatus.SUCCESS)){
		    	sFinalOutput = sMessage;
		    	return;
		    }
		    
			String sNewXml="<?xml version=\"1.0\" encoding=\"UTF-8\"?><Policy Subject=\"";
			sNewXml += nodePolicy.getAttributes().getNamedItem("Subject").getNodeValue() +"\"><Obligations><Obligation>";

			sNewXml += FTPRules.buf.toString() + "</Obligation></Obligations></Policy>";
			
			/* Keeping result in memory instead of a file
			 * some final processing needed before that:
			 */
			
			//1. Trim the future to past translation result for nested nots etc.
			sTrimmedOutput=trimFtpResult(sNewXml);
			
			//2. Take care that this past from conforms to the deployment schema 
			sFinalOutput=makePolicySchemaCompliant(sTrimmedOutput); 
			
			//3. Set filter status
			fStatus=FilterStatus.SUCCESS;
			sMessage="- Future-to-past translation with action refinement was successful.";
			
			//System.out.println("*****************************************");
			//System.out.println("Result from combined translation stage:\n"+sFinalOutput);
			//System.out.println("*****************************************");
		
		    }   
		
		catch (Exception e) {
			
			fStatus=FilterStatus.FAILURE;
			sMessage="- Future-to-past was unsuccessful." ;
			_logger.error(sMessage, e);
		}
				
	}
	
	/**
	 * This method 
	 * a) replaces each <start> node by an <event> node 
	 * where name of the event = activation; object parameter = mechanismID
	 * b) makes the resultant output "schema compliant" by removing "timestep" and "num" nodes
	 * 
	 * @param sTrimmedOutput2
	 * @return schemaConformantPolicy
	 */
	private String makePolicySchemaCompliant(String inputStr) {
		Document doc = new PublicMethods().convertStringToDocument(inputStr);
		
		// 1. replace start by an activate event
		NodeList startNodes = doc.getElementsByTagName("start");
		int counter=startNodes.getLength()-1;
		while(counter >= 0 ) {
		  doc.renameNode(startNodes.item(counter), null, "event");

		  Element ele = (Element) startNodes.item(counter);
		  
		  ele.setAttribute("name", "activateMechanism");
		  	  
	  
		  Element paramEle = doc.createElement("Param");
		  paramEle.setAttribute("name", "object");
		  paramEle.setAttribute("value", "mechanismID");
		  paramEle.setAttribute("policyType", "dataUsage");
		  
		  ele.appendChild(paramEle);
		  counter--;
		}
		
		//2. make the resultant output schema compliant
		//Search for all the "num" nodes 
		 NodeList numNodes = doc.getElementsByTagName("num");
		 for(int i=0; i<numNodes.getLength(); i++){
			// pick one num node
			 Node numNode = numNodes.item(i);
			 if(numNode==null)
				 continue;
			 // get its parent to check if it is a timestep node or (else) an OSL operator
			 Node numsParent = numNode.getParentNode();
			 NodeList parentChildren = numsParent.getChildNodes();
			 if(numsParent.getNodeName().equalsIgnoreCase("Timesteps")){
				 // then numsParentNode = timestepsNode, so
				 Node timestepsParentNode = numsParent.getParentNode();
				 // now create amount and unit attributes in the parent of "timestep" node with appropriate values
				 ((Element)timestepsParentNode).setAttribute("amount",((Element)numNode).getAttribute("value"));
				 ((Element)timestepsParentNode).setAttribute("unit",numsParent.getFirstChild().getNextSibling().getNodeName());
				 // now that we don't need them, delete one by one all the children of the timestep node
//				 numNode.removeChild(numNode.getFirstChild());
//				 numsParent.removeChild(numNode);
				 timestepsParentNode.removeChild(numsParent);
				 //for some reason the numNodes length reajusts and the counter has to start again from the previous
				 i--;
			 }
			 else{ // the parent of num node is an OSL operator. so just create the attribute limit with appropriate value
				 ((Element)numsParent).setAttribute("limit", ((Element)numNode).getAttribute("value"));
				 // and again, delete the num node as it is no longer needed
				 numsParent.removeChild(numNode);
				 //for some reason the numNodes length reajusts and the counter has to start again from the previous
				 i--;
			 }
		 }

		return new PublicMethods().convertDocumentToString(doc);
	}

	/**
	 * This method handles cases of nested "not" nodes 
	 * and not(true)=false / not(false)=true 
	 * 
	 * @param rawPastForm
	 * @return trimmedString
	 * @throws ParserConfigurationException 
	 * @throws IOException 
	 * @throws SAXException 
	 */
	String trimFtpResult(String rawPastForm) throws ParserConfigurationException, SAXException, IOException{
		
	    // if the input is not null
		if(!rawPastForm.equalsIgnoreCase(null)){			

		/*convert the input string into xml format
		 * 
		 */
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setValidating(false);
			DocumentBuilder builder = factory.newDocumentBuilder();
			
			InputSource inputSource = new InputSource( new StringReader( rawPastForm ) );
			org.w3c.dom.Document doc = builder.parse(inputSource);
			
			/* So we got a string input, we converted it into 
			 * xml format and send it forward for processing.
			 * For the first time, we pass on the entire document
			 * instead of a node 
			 */
			return trimRecursively(doc);	
	}
		else return rawPastForm;
}

  /**
   * This method does the actual processing and is called by
   * the method "trimFtpResult".
   * 
   * This method recursively eliminates cases of nested "nots" and 
   * also the cases of "tru" and "false" nodes nested inside "not"
   * 	
   * @param node
   * @return nextchildnode
   */
   String trimRecursively(Node node) {
	  
    String nodeName, destStr = null;
    if(node!=null){
	try {
		
	org.w3c.dom.Node outerNode = node.getFirstChild(); 
	if(outerNode!=null){
		
	/*
	 * Remove whitespaces to make sure
	 * every type of xml formating works as input	
	 */
	removeWhitespaceNodes(outerNode);
	
	nodeName = outerNode.getNodeName();
	
	/*
	 *Search for the "not" node 
	 */
	XPath xPath = XPathFactory.newInstance().newXPath();
	XPathExpression compiledExp = xPath.compile("//not");
	Node foundNode = (Node)compiledExp.evaluate(node, XPathConstants.NODE);
		
	/*
	 * We have three cases for children of any "not" node
	 * Case 1: the child is also a "not" node
	 * In this case we must remove both the not nodes
	 */
    if(foundNode!=null && foundNode.hasChildNodes()){
    Document tempdoc = foundNode.getOwnerDocument();     
	if(foundNode.getFirstChild().getNodeName().equalsIgnoreCase("not")){
		// a. then get the second not node
		Node secondNot = foundNode.getFirstChild();
		// b. get the second not node's child
		Node newChild = secondNot.getFirstChild();
		// c. replace this second not node with its child under its parent
		secondNot.getParentNode().replaceChild(newChild, secondNot);
		// d. now replace the first not node with its new child (which was until sometime back, its grand child) under its parent
		foundNode.getParentNode().replaceChild(newChild, foundNode);
			
	}	
	// Case 2: the child is a "true" node
	else 
		if(foundNode.getFirstChild().getNodeName().equalsIgnoreCase("true")){		
		Node trueNode = foundNode.getFirstChild();
		tempdoc.renameNode(trueNode, null, "false");
		foundNode.getParentNode().replaceChild(foundNode.getFirstChild(), foundNode);
	}
	// Case 3: the child is a "false" node
	else 
		if(foundNode.getFirstChild().getNodeName().equalsIgnoreCase("false")){
		Node falseNode = foundNode.getFirstChild();
		tempdoc.renameNode(falseNode, null, "true");
		foundNode.getParentNode().replaceChild(foundNode.getFirstChild(), foundNode);	
		
	}
}
    
        //Now process the next nesting
        trimRecursively(outerNode);
	    
        /*
         * Now that all nodes have been covered for trimming,
         * convert the result into string to return from this method
         * 1. start with the transformer 
         */
    	TransformerFactory tranFactory = TransformerFactory.newInstance(); 
	    Transformer aTransformer = tranFactory.newTransformer(); 	
	
        Source src = new DOMSource(outerNode);
	    StringWriter writer = new StringWriter();
	    Result dest = new javax.xml.transform.stream.StreamResult(writer);
	    aTransformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		   
	    // 2. now we transform into a bare string
	    aTransformer.transform(src, dest);
	    destStr=writer.toString();		 
	}
	
} catch (TransformerConfigurationException e) {
	e.printStackTrace();
} catch (XPathExpressionException e) {
	e.printStackTrace();
} catch (TransformerException e) {
	e.printStackTrace();
}
}
    // return the result as a string
    return destStr;
}
	
  
  /**
   * This method just removes the whitespaces from an xml document/node
   * 
   * @param node
   */
	void removeWhitespaceNodes(Node node) {
		if(node!=null){
		NodeList children = node.getChildNodes();
		for (int i = children.getLength() - 1; i >= 0; i--) {
		Node child = children.item(i);
		if (child instanceof Text && ((Text) child).getData().trim().length() == 0) {
		node.removeChild(child);
		}
		else if (child instanceof Element) {
		removeWhitespaceNodes((Element) child);
		}
		}
		}
			
	}

	@Override
	public FilterStatus getFilterStatus() {
		return fStatus;
	}

	@Override
	public String getMessage() {
		return sMessage;
	} 

}
