package de.tum.in.i22.uc.remotelistener;

import java.util.Map;

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

import de.tum.in.i22.uc.cm.datatypes.basic.PtpResponseBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic.EStatus;
import de.tum.in.i22.uc.cm.datatypes.basic.XmlPolicy;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IPtpResponse;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.cm.interfaces.IPmp2Ptp;
import de.tum.in.i22.uc.policy.translation.Filter.FilterStatus;
import de.tum.in.i22.uc.policy.translation.TranslationController;
import de.tum.in.i22.uc.utilities.PublicMethods;

public class PtpHandler implements IPmp2Ptp {

	private static final Logger _logger = LoggerFactory.getLogger(PtpHandler.class);
	
	private int policiesTranslatedTotalCounter = 0;
	
	/* (non-Javadoc)
	 * @see de.tum.in.i22.uc.cm.interfaces.IPmp2Ptp#translatePolicy(java.lang.String, java.util.Map, de.tum.in.i22.uc.cm.datatypes.basic.XmlPolicy)
	 * paramters must have keys: template_id, object_instance
	 */
	@Override
	public IPtpResponse translatePolicy(String requestId, Map<String, String> parameters, XmlPolicy xmlPolicy) {
		
		String policy = xmlPolicy.getXml();
		
		String mechanismId = parameters.get("policy_id");
		if(mechanismId == null){
			mechanismId = "p"+policiesTranslatedTotalCounter++;
		}
		else{
			mechanismId += "_p"+policiesTranslatedTotalCounter++;
		}
		parameters.put("policy_id", mechanismId);
		
		String message = "" + "Req: "+requestId +"translateg policy: \n" + policy;
		System.out.println(message);
		_logger.info(message);
		
		String outputPolicy = parameters.get("template_id")+"_"+parameters.get("object_instance")+"_"+"policytranslated.xml";
		
		TranslationController translationController ;
		translationController = new TranslationController(policy, parameters);
		FilterStatus status ;
		IStatus translationStatus = new StatusBasic(EStatus.ERROR);
		try{
			translationController.filter();
			status = translationController.getFilterStatus();
			outputPolicy = translationController.getFinalOutput();
			message = "Translation successful: " + translationController.getMessage();			
		} catch (Exception ex){
			status = FilterStatus.FAILURE;
			message = translationController.getMessage();
			System.out.println(message);
			_logger.error("translation exception", ex);
		}		
		
		if(status == FilterStatus.SUCCESS){			
			_logger.info(message);
			translationStatus = new StatusBasic(EStatus.OKAY, message);
		}
		else{
			message += "\nTranslation failed: " + status.name();
			_logger.error(message);
			translationStatus = new StatusBasic(EStatus.ERROR, message);
			outputPolicy = "";
		}
		
		outputPolicy = addTimeStepToPolicy(outputPolicy);
		outputPolicy = replaceEventually(outputPolicy);
		
		XmlPolicy translatedXmlPolicy = new XmlPolicy(mechanismId+"_"+xmlPolicy.getName(), outputPolicy);
		IPtpResponse response = new PtpResponseBasic(translationStatus, translatedXmlPolicy);
		
		return response;
	}

	@Override
	public IPtpResponse updateDomainModel(String requestId,	Map<String, String> parameters, XmlPolicy xmlDomainModel) {
		_logger.info("updateDomainModel: " + requestId + " "+ xmlDomainModel.toString());
		
		
		StatusBasic adaptationStatus = new StatusBasic(EStatus.OKAY, "adaptation success");
		XmlPolicy adaptedXmlDomainModel = new XmlPolicy(xmlDomainModel.getName()+"_adapted", "");
		
		IPtpResponse response = new PtpResponseBasic(adaptationStatus, adaptedXmlDomainModel);
		return response;
	}

	/**
	 * The syntax of eventually has changed.
	 * @param xml
	 * @return
	 */
	private String replaceEventually(String xml){
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
			System.out.println( logMsg );
			return logMsg;
		}
		
		int eventuallyCounter = nodeList.getLength();
		System.out.println("eventuallyCounter: "+ eventuallyCounter);
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
	 * @param policy
	 * @return
	 */
	private String addTimeStepToPolicy(String policy){
		if(policy == null)
			return "";
		if(policy.length()==0)
			return "";
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
				timestep.setAttribute("amount", "60");
				timestep.setAttribute("unit", "SECONDS");
				n.appendChild(timestep);
			}
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}
		
		policy = PublicMethods.convertDocumentToString(doc) + "";
		return policy;
	}
	
}
