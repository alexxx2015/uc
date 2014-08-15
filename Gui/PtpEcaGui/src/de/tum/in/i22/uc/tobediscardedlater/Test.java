/**
 * 
 */
package de.tum.in.i22.uc.tobediscardedlater;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
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

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

//import de.tum.in.i22.uc.policy.translation.futuretopast.FTPRules;

/**
 * @author ladmin
 *
 */
public class Test {
	
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
	static String trimFtpResult(String rawPastForm) throws ParserConfigurationException, SAXException, IOException{
		
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
			return trimRecursively(doc.getDocumentElement());	
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
  static String trimRecursively(Node node) {
	  //print what you got
	  System.out.println(node);
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
	 */
    if(foundNode!=null && foundNode.hasChildNodes()){
    Document tempdoc = foundNode.getOwnerDocument();     
	if(foundNode.getFirstChild().getNodeName().equalsIgnoreCase("not")){
		Node secondNot = foundNode.getFirstChild();
		Node newChild = secondNot.getFirstChild();
		secondNot.getParentNode().replaceChild(newChild, secondNot);
		System.out.println("founf node is: "+foundNode.getNodeName()+" parent is: "+foundNode.getParentNode().getNodeName());
		foundNode.getParentNode().replaceChild(newChild, foundNode);
		// print recursively what is left
		
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
	static void removeWhitespaceNodes(Node node) {
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


	/**
	 * @param args
	 * @throws ParserConfigurationException 
	 * @throws IOException 
	 * @throws SAXException 
	 * @throws TransformerException 
	 */
	public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException, TransformerException {
		// TODO Auto-generated method stub
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse(new File("C:/test/test.xml"));
		
		TransformerFactory tfactory = TransformerFactory.newInstance();
		Transformer transformer = tfactory.newTransformer();
		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		StringWriter writer = new StringWriter();
		transformer.transform(new DOMSource(doc), new StreamResult(writer));
		String output = writer.getBuffer().toString().replaceAll("\n|\r", "");
		
       System.out.println(trimFtpResult(output));
	}

}
