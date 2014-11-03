package de.tum.in.i22.uc.ptp;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.datatypes.basic.PtpResponseBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic.EStatus;
import de.tum.in.i22.uc.cm.datatypes.basic.XmlPolicy;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IPtpResponse;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.cm.interfaces.IPmp2Ptp;
import de.tum.in.i22.uc.ptp.adaptation.domainmodel.DomainModel;
import de.tum.in.i22.uc.ptp.adaptation.engine.AdaptationController;
import de.tum.in.i22.uc.ptp.adaptation.engine.DomainMergeException;
import de.tum.in.i22.uc.ptp.adaptation.engine.InvalidDomainModelFormatException;
import de.tum.in.i22.uc.ptp.adaptation.engine.ModelLoader;
import de.tum.in.i22.uc.ptp.policy.customization.GenericCompliance;
import de.tum.in.i22.uc.ptp.policy.translation.Filter.FilterStatus;
import de.tum.in.i22.uc.ptp.policy.translation.TranslationController;
import de.tum.in.i22.uc.ptp.utilities.PublicMethods;

public class PtpHandler implements IPmp2Ptp {

	private static final Logger _logger = LoggerFactory.getLogger(PtpHandler.class);
	
	private int policiesTranslatedTotalCounter = 0;
	
	private AdaptationController adaptationEngine ;
	
	public PtpHandler(){
		this.adaptationEngine = new AdaptationController();
	}
	
	/* (non-Javadoc)
	 * @see de.tum.in.i22.uc.cm.interfaces.IPmp2Ptp#translatePolicy(java.lang.String, java.util.Map, de.tum.in.i22.uc.cm.datatypes.basic.XmlPolicy)
	 * 
	 * PARAMETERS must have KEYS: 
	 * template_id - template used by the eca 
	 * object_instance - value used to replace in the template the value of object=<objectInstance>
	 * timestepType - SECONDS MINUTES ...
	 * timestepValue - one number as a string: 60, 102, ...
	 * e.g template_id : 102
	 */
	@Override
	public IPtpResponse translatePolicy(String requestId, Map<String, String> param, XmlPolicy xmlPolicy) {
		
		IStatus translationStatus = new StatusBasic(EStatus.ERROR);
		Map<String, String> parameters = new HashMap<String, String>(param);
		
		String policy = xmlPolicy.getTemplateXml();
		String policyId = xmlPolicy.getName();
		if(policyId == null || policyId.equals("")){
			policyId = "pTranslated"+policiesTranslatedTotalCounter++;
			xmlPolicy.setName(policyId);
		}
		
		String message = "" + "Req: "+requestId +" templateId: "+ xmlPolicy.getTemplateId() +" translate policy: \n" + policy;
		_logger.info(message);
		
		if(policy == null || policy.equals("")){
			_logger.info("Invalid policy!");
			IPtpResponse response = new PtpResponseBasic(translationStatus, xmlPolicy, "Invalid policy!");
			return response;
		}
		
		parameters.put("policy_id", policyId);
		parameters.put("template_id", xmlPolicy.getTemplateId());
		
		String outputPolicy = "";
		
		TranslationController translationController ;
		translationController = new TranslationController(policy, parameters);
		FilterStatus status ;
		
		try{
			translationController.filter();
			status = translationController.getFilterStatus();
			outputPolicy = translationController.getFinalOutput();			
		
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
		
		if(status == FilterStatus.SUCCESS){	
			outputPolicy = GenericCompliance.makeCompliant(xmlPolicy, parameters);
			if(outputPolicy.equals("")){
				translationStatus = new StatusBasic(EStatus.ERROR, message);
			}
			else{
				xmlPolicy.setXml(outputPolicy);
			}
		}
		
		//storePolicyForDebugging(xmlPolicy.getXml());
		
		IPtpResponse response = new PtpResponseBasic(translationStatus, xmlPolicy, message);
		return response;
	}

	
	
	
	private void storePolicyForDebugging(String xml){
		
		String destination = System.getProperty("user.dir")
				+File.separator+"logging"
				+File.separator+PublicMethods.timestamp()+(policiesTranslatedTotalCounter++)
				+ ".xml";
		
		try {
			PublicMethods.writeFile(destination, xml);
		} catch (IOException e) {
			_logger.error("Error writing policy to debug file", e);
		}
		
	}
	
	@Override
	public IPtpResponse updateDomainModel(String requestId,	Map<String, String> parameters, XmlPolicy xmlDomainModel) {
		_logger.info("updateDomainModel: " + requestId + " "+ xmlDomainModel.toString());
		
		//PTP_MODE is used to force the adaptation engine to overwrite the base domain model
		//it is mainly used for testing when one wants to reset the domain model to a known initial one
		String mergeMODE = parameters.get("PTP_MODE");
		if(mergeMODE == null){
			mergeMODE = "NORMAL";
		}
		
		DomainModel baseDm = null;
		int baseDomainModelElements = -1;
		try {
			baseDm = loadBaseDomainModel();
		} catch (InvalidDomainModelFormatException e) {
			_logger.error("Loading base domain failed.", e);
			StatusBasic adaptationStatus = new StatusBasic(EStatus.ERROR, "Loading base domain failed. See logs for further details.");
			IPtpResponse response = new PtpResponseBasic(adaptationStatus, xmlDomainModel, "Loading base domain failed.");
			return response;
		}
		baseDomainModelElements = baseDm.getElementsSize();
		
		DomainModel newDm = null;
		int newDomainModelElements = -1;
		try {
			newDm = loadNewDomainModel(xmlDomainModel);
		} catch (InvalidDomainModelFormatException e) {
			_logger.error("Loading new domain failed.", e);
			StatusBasic adaptationStatus = new StatusBasic(EStatus.ERROR, "Loading new domain failed. See logs for further details.");
			IPtpResponse response = new PtpResponseBasic(adaptationStatus, xmlDomainModel, "Loading new domain failed.");
			return response;
		}
		newDomainModelElements = newDm.getElementsSize();
		
		int updatedAttributes = -1;
		int mergedDomainModelElements = -1;
		if(mergeMODE.equals("NORMAL")){
			adaptationEngine.setBaseDomainModel(baseDm);
			adaptationEngine.setNewDomainModel(newDm);
			try {
				updatedAttributes = adaptationEngine.mergeDomainModels();
			} catch (DomainMergeException e) {
				_logger.error("Merging domains failed.", e);
				StatusBasic adaptationStatus = new StatusBasic(EStatus.ERROR, "Merging domains failed. See logs for further details.");
				IPtpResponse response = new PtpResponseBasic(adaptationStatus, xmlDomainModel,"Merging domains failed.");
				return response;
			}
		}
		mergedDomainModelElements = baseDm.getElementsSize();
		
		ModelLoader modelHandler = new ModelLoader();
		modelHandler.backupBaseDomainModel();
		
		DomainModel toStore = baseDm;
		if(mergeMODE.equals("RESET")){
			toStore = newDm;
		}
		
		String xmlMergedDomain = "";
		try {
			xmlMergedDomain = modelHandler.storeXmlBaseDomainModel(toStore);
		} catch (InvalidDomainModelFormatException e) {
			_logger.error("Merging domains failed.", e);
			StatusBasic adaptationStatus = new StatusBasic(EStatus.ERROR, "Storing merged domain failed. See logs for further details.");
			IPtpResponse response = new PtpResponseBasic(adaptationStatus, xmlDomainModel, "Merging domains failed.");
			return response;
		}
		
		//used only as debugging information
		String message = "Elements updated: "+ updatedAttributes 
				+" baseE: "+ baseDomainModelElements
				+" newE: "+ newDomainModelElements
				+" mergedE: "+ mergedDomainModelElements
				+" MODE: "+ mergeMODE;
		StatusBasic adaptationStatus = new StatusBasic(EStatus.MODIFY, message);
		if(updatedAttributes == 0){
			adaptationStatus = new StatusBasic(EStatus.OKAY, message);
		}
				
		XmlPolicy adaptedXmlDomainModel = new XmlPolicy(xmlDomainModel.getName(), "");
		
		IPtpResponse response = new PtpResponseBasic(adaptationStatus, adaptedXmlDomainModel, message);		
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
	
	
	
}
