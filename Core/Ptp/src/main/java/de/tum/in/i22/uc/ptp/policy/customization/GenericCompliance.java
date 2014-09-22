package de.tum.in.i22.uc.ptp.policy.customization;

import java.io.StringWriter;
import java.util.Map;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.tum.in.i22.uc.ptp.utilities.PublicMethods;

/**
 * @author Cipri
 *
 */
public class GenericCompliance {

	private static final Logger _logger = LoggerFactory.getLogger(GenericCompliance.class);
	
	
	/**
	 * <br> Add timesteps to policy
	 * <br> Change "eventually" to not.always.not
	 * @param xml
	 * @param parameters
	 * @return
	 */
	public static String makeCompliant(String xml, Map<String, String> parameters){
		
		//generic compliance
		String timestepType = parameters.get("timestepType");
		String timestepValue = parameters.get("timestepValue");
		xml = addTimeStepToPolicy(xml, timestepType, timestepValue);
		xml = replaceEventually(xml);
		
		//specific compliance
		xml = makePolicyCompliantToPep(xml, parameters);
		
		return xml;
	}
	
	
	private static String makePolicyCompliantToPep(String policyXML, Map<String, String> parameters){
		String result = policyXML;
		
		String pepType = parameters.get("pep");
		if(pepType == null){
			return result;
		}
		
		if(pepType.equals(FirefoxPepCompliance.id)){
			FirefoxPepCompliance ff = new FirefoxPepCompliance(policyXML, parameters);
			result = ff.getCompliantPolicy();
		}
		else if (pepType.equals(WindowsPepCompliance.id)){
			WindowsPepCompliance win = new WindowsPepCompliance(policyXML, parameters);
			result = win.getCompliantPolicy();
		}
		
		return result;
	}
	
	/**
	 * The syntax of eventually has changed.
	 * @param xml
	 * @return
	 */
	private static String replaceEventually(String xml){
 		Document doc = PublicMethods.openXmlInput(xml, "string");
 		if(doc==null)
 			return xml;
		String expression = "//eventually";
		XPathFactory factory=XPathFactory.newInstance();
		XPath xPath=factory.newXPath();
		NodeList nodeList = null;
		try {
			nodeList = (NodeList) xPath.compile(expression).evaluate(doc, XPathConstants.NODESET);
		} catch (XPathExpressionException e) {
			e.printStackTrace();
			String logMsg = "eventually: xml value parsing error";
			_logger.error(logMsg, e);
			return logMsg;
		}
		
		int eventuallyCounter = nodeList.getLength();
		//System.out.println("eventuallyCounter: "+ eventuallyCounter);
		for (int i = 0; i < eventuallyCounter; i++) {
			Node n = nodeList.item(i);
			if(n.getNodeType() != Node.ELEMENT_NODE)
				continue;
			
			Node parent = n.getParentNode();						
			PublicMethods.removeWhitespaceNodes(parent);
			
			Element not1 = doc.createElement("not");
			parent.appendChild(not1);
			Element always = doc.createElement("always");
			not1.appendChild(always);
			Element not2 = doc.createElement("not");
			always.appendChild(not2);
			
			NodeList children = n.getChildNodes();
			int childrenLength = children.getLength();
			for(int j=0; j<childrenLength; j++){
				Node c = children.item(j);
				if(c!=null)
					not2.appendChild(c);
				else{
					Element empty = doc.createElement("empty");
					not2.appendChild(empty);
				}
			}
			
			String pName = parent.getNodeName();
			String pValue = parent.getNodeValue();
			
			String value = n.getNodeValue();
			String name = n.getNodeName();
			System.out.println(i + " " + name + " " + value + "[" + pName + " " + pValue + "]");
			parent.removeChild(n);
		}
		
		String result = "";
		result = PublicMethods.convertDocumentToString(doc) + "";
		return result;
	}
	
	/**
	 * The PDP expects a policy which has a "timestep" node with a value.
	 * This method adds a default timestep node.
	 * Without it, the deployment fails.
	 * <br> default: SECONDS 60
	 * @param policy
	 * @param timestepType SECONDS MINUTES HOURS DAYS MONTHS YEARS
	 * @param timestepValue number
	 * @return
	 */
	private static String addTimeStepToPolicy(String policy, String timestepType, String timestepValue){
		if(policy == null)
			return "";
		if(policy.length()==0)
			return "";

		if(timestepType == null)
			timestepType = "SECONDS";
		if(timestepValue == null)
			timestepValue ="60";
		
		Document doc = PublicMethods.openXmlInput(policy, "string");
		if(doc==null)
			return policy;
		XPathFactory factory=XPathFactory.newInstance();
		XPath xpath=factory.newXPath();
		
		/* process name of the layers */
		String nodeNames = "//preventiveMechanism";
		NodeList mechanisms;
		try {
			mechanisms = (NodeList)xpath.evaluate(nodeNames, doc, XPathConstants.NODESET);
			int mechsCounter = mechanisms.getLength();
			for(int i=0; i<mechsCounter; i++){
				Node n = mechanisms.item(i);
				Element timestep = doc.createElement("timestep");
				timestep.setAttribute("amount", timestepValue);
				timestep.setAttribute("unit", timestepType);
				n.appendChild(timestep);
			}
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}
		
		policy = PublicMethods.convertDocumentToString(doc) + "";
		return policy;
	}
	
	
	private String modifyDetectiveMechanisms(String xml){
		Document doc = PublicMethods.convertStringToDocument(xml);
		XPath xPath = XPathFactory.newInstance().newXPath();
		String expression = "//preventiveMechanism[contains(@name,'_detective')]";
		NodeList nodeList = null;
		try {
			nodeList = (NodeList) xPath.compile(expression).evaluate(doc, XPathConstants.NODESET);
			_logger.info(nodeList.toString());
		} catch (XPathExpressionException e) {
			e.printStackTrace();
			String logMsg = "preventiveMechanism: xml value parsing error";
			_logger.error(logMsg, e);
			return logMsg;
		}
		
		int preventiveCounter = nodeList.getLength();
		System.out.println("preventiveCounter: "+ preventiveCounter);
		for (int i = 0; i < preventiveCounter; i++) {
			Node n = nodeList.item(i);
			if(n.getNodeType() != Node.ELEMENT_NODE)
				continue;
			
			System.out.println(n.getNodeName() + " "+n.getAttributes().getNamedItem("name").getNodeValue());
			doc.renameNode(n, null, "detectiveMechanism");
			
			NodeList children = n.getChildNodes();
			int childrenCounter = children.getLength();
			for(int j=0; j<childrenCounter; j++){
				Node child = children.item(j);
				if(child == null)
					continue;
				if(child.getNodeType() != Node.ELEMENT_NODE)
					continue;
				String name = child.getNodeName();
				if(name.equals("trigger")){
					//n.removeChild(child);
				}
				else if(name.equals("authorizationAction")){
					boolean operationFinished = false;
					NodeList atChildren = child.getChildNodes();
					for(int k=0; k<atChildren.getLength(); k++){
						Node atChild = atChildren.item(k);
						if(atChild.getNodeType() != Node.ELEMENT_NODE)
							continue;
						NodeList allowChildren = atChild.getChildNodes();
						for(int c=0; c<allowChildren.getLength(); c++){
							Node aChild = allowChildren.item(c);
							if(aChild.getNodeType() != Node.ELEMENT_NODE)
								continue;
							String renamedNode = aChild.getNodeName();
							((Element) aChild).setAttribute("processor", "pxp");
							doc.renameNode(aChild, null, "executeAsyncAction");
							n.removeChild(child);
							n.appendChild(aChild);
							operationFinished = true;
							break;
						}							
						if(operationFinished)
							break;
					}
					
				}
			}
			
		}
		
		String result = "";
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer transformer;
		try {
			transformer = tf.newTransformer();
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			StringWriter writer = new StringWriter();
			transformer.transform(new DOMSource(doc), new StreamResult(writer));
			result = writer.getBuffer().toString().replaceAll("\n|\r", "");
		} catch (TransformerException e) {
			_logger.error("modifyDetectiveMechanism", e);
		}
		
		return result;
	}
	
}
