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
	
	@Override
	public IPtpResponse translatePolicy(String requestId, Map<String, String> parameters, XmlPolicy xmlPolicy) {
		
		String policy = xmlPolicy.getXml();
		
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
		//TODO: add also "eventually" replacement operator
		
		XmlPolicy translatedXmlPolicy = new XmlPolicy(xmlPolicy.getName()+"_translated", outputPolicy);
		IPtpResponse response = new PtpResponseBasic(translationStatus, translatedXmlPolicy);
		
		return response;
	}

	@Override
	public IPtpResponse updateDomainModel(String requestId,	Map<String, String> parameters, XmlPolicy xmlDomainModel) {
		_logger.info("updateDomainModel");
		
		
		return null;
	}

	private String addTimeStepToPolicy(String policy){
		if(policy == null)
			return "";
		if(policy.length()==0)
			return "";
		Document doc = PublicMethods.openXmlInput(policy, "string");
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
