package de.tum.in.i22.uc.ptp.utilities;


import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
<<<<<<< HEAD:Core/Ptp/src/main/java/de/tum/in/i22/uc/ptp/utilities/PublicMethods.java
=======





>>>>>>> 34241d9247322206d6bbc20a064b95ba0d3a6264:Core/Ptp/src/main/java/de/tum/in/i22/uc/utilities/PublicMethods.java
//import de.tum.in.i22.uc.blocks.controller.Config;
import de.tum.in.i22.uc.ptp.policy.translation.ecacreation.SubformulaEvent;


public class PublicMethods {

	public static String readFile(String path, Charset encoding) throws IOException {
	  byte[] encoded = Files.readAllBytes(Paths.get(path));
	  return encoding.decode(ByteBuffer.wrap(encoded)).toString();
	}
	
	public static void writeFile(String path, String data) throws IOException {
		String msg = data;
		Files.write(Paths.get(path), msg.getBytes());
	}
	
	/**
<<<<<<< HEAD:Core/Ptp/src/main/java/de/tum/in/i22/uc/ptp/utilities/PublicMethods.java
	 * timestamp format YYYYMMDD-HHMMSS
	 * @return
	 */
	public static String timestamp(){
		//generate a timestamp
		Calendar cal = Calendar.getInstance();
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH) + 1;
		int day = cal.get(Calendar.DAY_OF_MONTH);
		int hour = cal.get(Calendar.HOUR_OF_DAY) ;
		int min = cal.get(Calendar.MINUTE) ;
		int sec = cal.get(Calendar.SECOND);
		String timestamp = year
				+""+(month<10?"0"+month:month)
				+""+(day<10?"0"+day:day)
				+"-" 
				+""+(hour<10?"0"+hour:hour)
				+""+(min<10?"0"+min:min)
				+""+(sec<10?"0"+sec:sec);
		return timestamp;
	}
	
	/**
=======
>>>>>>> 34241d9247322206d6bbc20a064b95ba0d3a6264:Core/Ptp/src/main/java/de/tum/in/i22/uc/utilities/PublicMethods.java
	 * removes duplicates from a string list
	 * 
	 */
	@SuppressWarnings("rawtypes")
	public ArrayList<SubformulaEvent> removeDuplicates(ArrayList<SubformulaEvent> inputList){
		Set<SubformulaEvent> noDups = new HashSet<SubformulaEvent>();
		for(int i=0;i<inputList.size();i++)
		noDups.add(inputList.get(i));
		ArrayList<SubformulaEvent> noDupsList= new ArrayList<SubformulaEvent>();
		noDupsList.addAll(noDups);
		
		return noDupsList;
	}
	
	
	/**
	 * removes duplicates from a string list
	 * 
	 */
	@SuppressWarnings("rawtypes")
	public ArrayList removeDuplicates_generic(ArrayList inputList){
		HashSet noDups = new HashSet();
		noDups.addAll(inputList);
		inputList.clear();
		inputList.addAll(noDups);
		
		return inputList;
	}
	
	
	/**
	 * coverts a string into an XML document 
	 * @param xmlStr
	 * @return doc
	 */
	public static Document convertStringToDocument(String xmlStr) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();  
        DocumentBuilder builder;  
        try 
        {  
            builder = factory.newDocumentBuilder();  
            Document doc = builder.parse( new InputSource( new StringReader( xmlStr ) ) ); 
            return doc;
        } catch (Exception e) {  
            e.printStackTrace();  
        } 
        return null;
    }
	
	/**
	 * 
	 * @param doc
	 * @return strOutput
	 */
	public static String convertDocumentToString(Document doc) {
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer;
        try {
            transformer = tf.newTransformer();
            // below code to remove XML declaration
            // transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            StringWriter writer = new StringWriter();
            transformer.transform(new DOMSource(doc), new StreamResult(writer));
            String strOutput = writer.getBuffer().toString();
            return strOutput;
        } catch (Exception e) {
            e.printStackTrace();
        }
         
        return null;
    }
	
	public static String TransformDomresultToString(DOMResult dom) throws TransformerException
	{

		Node node = dom.getNode();
		StringWriter writer = new StringWriter();
		Transformer transformer = TransformerFactory.newInstance().newTransformer(); 
		transformer.transform(new DOMSource(node), new StreamResult(writer));
	    return writer.toString();
	}
	
	/**
	 * returns true if a string is found in a list
	 */
	public boolean stringIsInList(String string, java.util.List<String> list) {
		boolean result = false;
		for (int i = 0; i < list.size(); i++) {
			if (string.equalsIgnoreCase(list.get(i))) {
				result = true;
				break;
			}
		}
		return result;
	}

	/**
	 * 
	 * @param eventname
	 * @param paramname
	 * @return paramValueList
	 * given a paramname, returns a list of valid paramvalues
	 */
//	public java.util.List<String> returnValuesByParam(String eventname, String paramname) {
//		java.util.List<String> paramValueList = new ArrayList<String>();
//
//		try {
//
//			DocumentBuilderFactory factory = DocumentBuilderFactory
//					.newInstance();
//			DocumentBuilder builder;
//			builder = factory.newDocumentBuilder();
//
//			Config cfg = new Config();
//
//			// build DOMs
//			org.w3c.dom.Document sourceFile = builder.parse(new File(cfg
//					.getProperty("eventDecl")));
//
//			// now get all action names
//			NodeList eventNode = sourceFile.getElementsByTagName("event");
//			for (int i = 0; i < eventNode.getLength(); i++) {
//				Node n = eventNode.item(i); 
//				if (eventname.equalsIgnoreCase(n.getFirstChild().getNextSibling()
//						.getTextContent())) {
//
//					// children of "event" node where event name is the string
//					// given in func param
//					NodeList childList = n.getChildNodes();
//					for (int j = 0; j < childList.getLength(); j++) {
//						Node childnode = childList.item(j);
//
//						// grandchildren of event node
//						NodeList paramChildNodeList = childnode.getChildNodes();
//						for (int k = 0; k < paramChildNodeList.getLength(); k++) {
//							Node grandchild = paramChildNodeList.item(k);
//
//							if (grandchild instanceof Element) {
//								Element ele = (Element) grandchild;
//								if (!(ele.getAttribute("paramName").equalsIgnoreCase("")) && ele.getAttribute("paramName").equalsIgnoreCase(paramname)) {
//								NodeList paramvalues=ele.getElementsByTagName("paramValue");
//								for(int x=0; x<paramvalues.getLength(); x++){
//								if(!(paramvalues.item(x).getTextContent().equalsIgnoreCase("")))
//								paramValueList.add(paramvalues.item(x).getTextContent());
//								}
//								
//								}
//								
//							}
//						}
//					}
//				}
//			}	
//			
//		} catch (ParserConfigurationException e) {
//			
//			e.printStackTrace();
//		} catch (IOException e) {
//			
//			e.printStackTrace();
//		} catch (SAXException e) {
//			
//			e.printStackTrace();
//		}
//		return paramValueList;
//		}
//	
	/**@param action
	 * @return paramnamelist
	 * get param names for an action name
	 */
//	public java.util.List<String> returnParamsByAction(String action) {
//		java.util.List<String> paramNameList = new ArrayList<String>();
//
//		try {
//
//			DocumentBuilderFactory factory = DocumentBuilderFactory
//					.newInstance();
//			DocumentBuilder builder;
//			builder = factory.newDocumentBuilder();
//
//			Config cfg = new Config();
//
//			// build DOMs
//			org.w3c.dom.Document sourceFile = builder.parse(new File(cfg
//					.getProperty("eventDecl")));
//
//			// now get all action names
//			NodeList eventNode = sourceFile.getElementsByTagName("event");
//			for (int i = 0; i < eventNode.getLength(); i++) {
//				Node n = eventNode.item(i); 
//				if (action.equalsIgnoreCase(n.getFirstChild().getNextSibling()
//						.getTextContent())) {
//
//					// children of "event" node where event name is the string
//					// given in func param
//					NodeList childList = n.getChildNodes();
//					for (int j = 0; j < childList.getLength(); j++) {
//						Node childnode = childList.item(j);
//
//						// grandchildren of event node
//						NodeList paramChildNodeList = childnode.getChildNodes();
//						for (int k = 0; k < paramChildNodeList.getLength(); k++) {
//							Node grandchild = paramChildNodeList.item(k);
//
//							if (grandchild instanceof Element) {
//								Element ele = (Element) grandchild;
//								if (!(ele.getAttribute("paramName")
//										.equalsIgnoreCase(""))) {
//									paramNameList.add(ele
//											.getAttribute("paramName"));
//								}
//								
//							}
//						}
//					}
//				}
//			}
//
//		} catch (ParserConfigurationException e) {
//			
//			e.printStackTrace();
//		} catch (IOException e) {
//			
//			e.printStackTrace();
//		} catch (SAXException e) {
//			
//			e.printStackTrace();
//		}
//		return paramNameList;
//
//	}

	/**
	 * when (matchingAttribName,matchingAttribValue) matches in node, then, get
	 * the attribute value from the same node, for attribute name
	 * queryAttribName. the xml text is passed as string xmltext
	 */
	public String returnAttributeByAttrib(String matchingAttribName,
			String matchingAttribValue, String queryAttribName, String node,
			String xmlFile) {
		String retAttrib = "";
		
		// now parse this xml file
		try {

			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder builder;
			builder = factory.newDocumentBuilder();

			// build DOMs
			org.w3c.dom.Document sourceFile = builder.parse(new File(xmlFile));

			// now get all block names
			NodeList nodeList = sourceFile.getElementsByTagName(node);
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node n = nodeList.item(i);
				if (n instanceof Element) {
					Element ele = (Element) n;
					if (!(ele.getAttribute(matchingAttribName)
							.equalsIgnoreCase(matchingAttribValue))) {
						retAttrib = ele.getAttribute(queryAttribName);
					}
					// System.out.println(childList.item(j).getTextContent());
				}
			}
		} catch (ParserConfigurationException e) {
			
			e.printStackTrace();
		} catch (IOException e) {
			
			e.printStackTrace();
		} catch (SAXException e) {
			
			e.printStackTrace();
		}

		return retAttrib;
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
				return null;
			} catch (SAXException e) {
				e.printStackTrace();
				return null;
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
			
			Node root1 = doc.getFirstChild();
			removeWhitespaceNodes(root1);
			Node root2 = doc.getParentNode();
			removeWhitespaceNodes(root2);
			Node root3 = doc.getNextSibling();
			removeWhitespaceNodes(root3);
			
			return doc;
	}
	
	/**
	   * This method just removes the whitespaces from an xml document/node
	   * 
	   * @param node
	   */
	public static void removeWhitespaceNodes(Node node) {
		if (node != null) {
			NodeList children = node.getChildNodes();
			for (int i = children.getLength() - 1; i >= 0; i--) {
				Node child = children.item(i);
				if (child instanceof Text
						&& ((Text) child).getData().trim().length() == 0) {
					node.removeChild(child);
				} else if (child instanceof Element) {
					removeWhitespaceNodes((Element) child);
				}
			}
		}
	}
	
	public static String prettyPrint(Document xml) throws Exception {
		Transformer tf = TransformerFactory.newInstance().newTransformer();
		tf.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		tf.setOutputProperty(OutputKeys.INDENT, "yes");
		Writer out = new StringWriter();
		tf.transform(new DOMSource(xml), new StreamResult(out));
		String prettyXML = out.toString();
		return prettyXML;
	}
	
	
	/* a testing block for functions defined here */
//	public static void main(String args[]) {
//		List<String> list = new PublicMethods().returnValuesByParam("play", "device");
//		for(int i=0;i<list.size();i++)
//		System.out.println(list.get(i));
//	}
}
