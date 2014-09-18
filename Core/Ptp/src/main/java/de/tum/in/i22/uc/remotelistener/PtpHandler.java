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

import de.tum.in.i22.uc.adaptation.engine.AdaptationController;
import de.tum.in.i22.uc.adaptation.engine.DomainMergeException;
import de.tum.in.i22.uc.adaptation.engine.InvalidDomainModelFormatException;
import de.tum.in.i22.uc.adaptation.engine.ModelLoader;
import de.tum.in.i22.uc.adaptation.model.DomainModel;
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
	
	private AdaptationController adaptationEngine ;
	
	public PtpHandler(){
		this.adaptationEngine = new AdaptationController();
	}
	
	/* (non-Javadoc)
	 * @see de.tum.in.i22.uc.cm.interfaces.IPmp2Ptp#translatePolicy(java.lang.String, java.util.Map, de.tum.in.i22.uc.cm.datatypes.basic.XmlPolicy)
	 * paramters must have keys: template_id, object_instance
	 */
	@Override
	public IPtpResponse translatePolicy(String requestId, Map<String, String> parameters, XmlPolicy xmlPolicy) {
		
		IStatus translationStatus = new StatusBasic(EStatus.ERROR);
		
		String policy = xmlPolicy.getXml();
		String policyId = xmlPolicy.getName();
		if(policyId == null || policyId.equals("")){
			policyId = "pTranslated"+policiesTranslatedTotalCounter++;
			xmlPolicy.setName(policyId);
		}
		
		String message = "" + "Req: "+requestId +"translateg policy: \n" + policy;
		_logger.info(message);
		
		if(policy == null || policy.equals("")){
			_logger.info("Invalid policy!");
			IPtpResponse response = new PtpResponseBasic(translationStatus, xmlPolicy);
			return response;
		}
		
		parameters.put("policy_id", policyId);
		parameters.put("template_id", xmlPolicy.getTemplateId());
		
		String outputPolicy = parameters.get("template_id")+"_"+parameters.get("object_instance")+"_"+"policytranslated.xml";
		
		TranslationController translationController ;
		translationController = new TranslationController(policy, parameters);
		FilterStatus status ;
		
		try{
			translationController.filter();
			status = translationController.getFilterStatus();
			outputPolicy = translationController.getFinalOutput();			
			
			outputPolicy = addTimeStepToPolicy(outputPolicy);
			outputPolicy = replaceEventually(outputPolicy);
			message = "Translation successful: " + translationController.getMessage();	
			
			xmlPolicy.setXml(outputPolicy);
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
		
		IPtpResponse response = new PtpResponseBasic(translationStatus, xmlPolicy);
		return response;
	}

	@Override
	public IPtpResponse updateDomainModel(String requestId,	Map<String, String> parameters, XmlPolicy xmlDomainModel) {
		_logger.info("updateDomainModel: " + requestId + " "+ xmlDomainModel.toString());
		
		DomainModel baseDm = null;
		try {
			baseDm = loadBaseDomainModel();
		} catch (InvalidDomainModelFormatException e) {
			_logger.error("Loading base domain failed.", e);
			StatusBasic adaptationStatus = new StatusBasic(EStatus.ERROR, "Loading base domain failed. See logs for further details.");
			IPtpResponse response = new PtpResponseBasic(adaptationStatus, xmlDomainModel);
			return response;
		}
		DomainModel newDm = null;
		try {
			newDm = loadNewDomainModel(xmlDomainModel);
		} catch (InvalidDomainModelFormatException e) {
			_logger.error("Loading new domain failed.", e);
			StatusBasic adaptationStatus = new StatusBasic(EStatus.ERROR, "Loading new domain failed. See logs for further details.");
			IPtpResponse response = new PtpResponseBasic(adaptationStatus, xmlDomainModel);
			return response;
		}
		
		adaptationEngine.setBaseDomainModel(baseDm);
		adaptationEngine.setNewDomainModel(newDm);
		int updates = 0;
		try {
			updates = adaptationEngine.mergeDomainModels();
		} catch (DomainMergeException e) {
			_logger.error("Merging domains failed.", e);
			StatusBasic adaptationStatus = new StatusBasic(EStatus.ERROR, "Merging domains failed. See logs for further details.");
			IPtpResponse response = new PtpResponseBasic(adaptationStatus, xmlDomainModel);
			return response;
		}
		
		ModelLoader modelHandler = new ModelLoader();
		modelHandler.backupBaseDomainModel();
		String xmlMergedDomain = "";
		try {
			xmlMergedDomain = modelHandler.storeXmlBaseDomainModel(baseDm);
		} catch (InvalidDomainModelFormatException e) {
			_logger.error("Merging domains failed.", e);
			StatusBasic adaptationStatus = new StatusBasic(EStatus.ERROR, "Storing merged domain failed. See logs for further details.");
			IPtpResponse response = new PtpResponseBasic(adaptationStatus, xmlDomainModel);
			return response;
		}
		
		StatusBasic adaptationStatus = new StatusBasic(EStatus.OKAY, "Adaptation success. Elements updated: "+ updates);
		XmlPolicy adaptedXmlDomainModel = new XmlPolicy(xmlDomainModel.getName(), "");
		
		IPtpResponse response = new PtpResponseBasic(adaptationStatus, adaptedXmlDomainModel);
		return response;
	}

	
	private DomainModel loadNewDomainModel(XmlPolicy xmlDomainModel) throws InvalidDomainModelFormatException {
		String xmlString = xmlDomainModel.getXml();
		ModelLoader modelHandler = new ModelLoader();
		DomainModel newDM = null;
		newDM = modelHandler.createDomainModel(xmlString);
		return newDM;
	}

	private DomainModel loadBaseDomainModel() throws InvalidDomainModelFormatException{
		ModelLoader modelHandler = new ModelLoader();
		DomainModel baseDM = null;
		baseDM = modelHandler.loadBaseDomainModel();
		return baseDM;
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
	 * @param policy
	 * @return
	 */
	private static String addTimeStepToPolicy(String policy){
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
