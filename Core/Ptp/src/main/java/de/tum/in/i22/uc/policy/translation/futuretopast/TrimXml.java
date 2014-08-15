/**
 * 
 */
package de.tum.in.i22.uc.policy.translation.futuretopast;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.ObjectInputStream.GetField;

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
import org.apache.commons.io.*;

/**
 * @author ladmin
 *
 */
public class TrimXml {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws ParserConfigurationException 
	 * @throws SAXException 
	 */
	public static void main(String[] args) throws IOException, ParserConfigurationException, SAXException {
		
     String orig = FileUtils.readFileToString(new File("C:/result.xml"));
		
     		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setValidating(false);
			DocumentBuilder builder = factory.newDocumentBuilder();
			
			InputSource inputSource = new InputSource( new StringReader( orig ) );
			org.w3c.dom.Document doc = builder.parse(inputSource);
			System.out.println(trimRecursively(doc));	
	}
	
  public static String trimRecursively(Node node) {
    String nodeName, destStr = null;
    if(node!=null){
	try {
		
	org.w3c.dom.Node outerNode = node.getFirstChild(); 
	if(outerNode!=null){
	removeWhitespaceNodes(outerNode);
	nodeName = outerNode.getNodeName();
	
	XPath xPath = XPathFactory.newInstance().newXPath();
	XPathExpression compiledExp = xPath.compile("//not");
	Node foundNode = (Node)compiledExp.evaluate(node, XPathConstants.NODE);
		
    if(foundNode!=null && foundNode.hasChildNodes()){
    Document tempdoc = foundNode.getOwnerDocument();     
	if(foundNode.getFirstChild().getNodeName().equalsIgnoreCase("not")){
		Node secondNot = foundNode.getFirstChild();
		Node newChild = secondNot.getFirstChild();
		secondNot.getParentNode().replaceChild(newChild, secondNot);
		foundNode.getParentNode().replaceChild(newChild, foundNode);
	}	
	else 
		if(foundNode.getFirstChild().getNodeName().equalsIgnoreCase("true")){		
		Node trueNode = foundNode.getFirstChild();
		tempdoc.renameNode(trueNode, null, "false");
		foundNode.getParentNode().replaceChild(foundNode.getFirstChild(), foundNode);
	}
	else 
		if(foundNode.getFirstChild().getNodeName().equalsIgnoreCase("false")){
		Node falseNode = foundNode.getFirstChild();
		tempdoc.renameNode(falseNode, null, "true");
		foundNode.getParentNode().replaceChild(foundNode.getFirstChild(), foundNode);	
		
	}
}
	    trimRecursively(outerNode);
	    
    	TransformerFactory tranFactory = TransformerFactory.newInstance(); 
	    Transformer aTransformer = tranFactory.newTransformer(); 	
	
        Source src = new DOMSource(outerNode);
	    StringWriter writer = new StringWriter();
	    Result dest = new javax.xml.transform.stream.StreamResult(writer);
	    aTransformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		   
	    // now we transform into a bare string
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
    return destStr;
}
	
	public static void removeWhitespaceNodes(Node e) {
		if(e!=null){
		NodeList children = e.getChildNodes();
		for (int i = children.getLength() - 1; i >= 0; i--) {
		Node child = children.item(i);
		if (child instanceof Text && ((Text) child).getData().trim().length() == 0) {
		e.removeChild(child);
		}
		else if (child instanceof Element) {
		removeWhitespaceNodes((Element) child);
		}
		}
		}
	}
}
