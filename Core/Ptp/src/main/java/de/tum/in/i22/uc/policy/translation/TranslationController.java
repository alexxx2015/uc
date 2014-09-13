package de.tum.in.i22.uc.policy.translation;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import de.tum.in.i22.uc.policy.translation.ecacreation.ECARulesCreator;
import de.tum.in.i22.uc.policy.translation.futuretopast.FutureToPast;

/**
 * 09 April 2012
 * When a user issues the command for translation, a UI view is shown.
 * In the spirit of MVC, this controller class decouples the UI view from
 * pipe and filter logic of translation from OSL Policy -> ECA FTPRules.
 *
 * 
 */
public class TranslationController implements Filter{

	/**
	 * Represents a diagram or OSL policy file.
	 * The policy on which the translation process will be applied.
	 */
	private Document policy;
	
	/**
	 * Represents the translated file.
	 */
	private String sOutput;
	
	/**
	 * Status of pipe and filter operation
	 */
	public static FilterStatus fStatus;
	/**
	 * Messages during data movement through our
	 * pipe and filter system
	 */
	public static String sMessage;
	
	public static Map<String,String> params;
	/**
	 * Creates an instance of this controller.
	 * 
	 * @param inputFile - policy xml
	 * @param outputFile
	 * 
	 * not being used as of now
	 */
	public TranslationController(String inputStr){
		policy = policyCreator(inputStr);
		params = new HashMap<String,String>();
	}
	
	public TranslationController(String inputStr, Map<String,String> params){
		this.params = params;
		policy = policyCreator(inputStr);
	}
	
	public TranslationController(Document inputFile){
		policy = inputFile;
		params = new HashMap<String,String>();
	}
	
	public TranslationController(Document inputFile, Map<String,String> params){
		policy = inputFile;
		this.params = params;
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
	 * Performs translation through a pipe and filter process with
	 * error tracking between each stage.
	 *  
	 */
	@Override
	public void filter(){
		
		//1st: Future to past translation + action refinement
		if(policy==null){
			sMessage="Policy is null";
			fStatus=FilterStatus.FAILURE;
			return;
		}
			
		FutureToPast ftp=new FutureToPast(policy);
		ftp.filter();
		fStatus=ftp.getFilterStatus();
		sMessage=ftp.getMessage();
		if(fStatus==FilterStatus.FAILURE) return;
		
		String policyInput = ftp.getFinalOutput();
		
		//3rd: ECA rule creation
		ECARulesCreator ecaCreator=new ECARulesCreator(params, policyInput);
		ecaCreator.filter();
		fStatus=ecaCreator.getFilterStatus();
		sMessage=ecaCreator.getMessage();	
		sOutput = ecaCreator.getOutput();
		
	}
	
	@Override
	public FilterStatus getFilterStatus() {
		return fStatus;
	}

	@Override
	public String getMessage() {
		return sMessage;
	}
	
	public String getFinalOutput(){
		return sOutput;
	}
	
	
	/**************************************************************/
	
//	public static void main(String[] args){
//		String sSrc = "d:\\Users\\Cipri_L\\GitHub_cipri88\\policytranslation\\policy_ptp\\policyinput.xml";
//		String sDest = "d:\\Users\\Cipri_L\\GitHub_cipri88\\policytranslation\\policy_ptp\\policyinput2.xml";
//		//1st: Create a policy file from a diagram file
//		PolicyCreator policyCreator = new PolicyCreator(sSrc);
//		policyCreator.filter();	
//		FilterStatus fStatus=policyCreator.getFilterStatus();
//		String sMessage=policyCreator.getMessage();
//		if(fStatus==FilterStatus.FAILURE) return;
//		DOMResult policy = policyCreator.getOutput();
//        try {
//			System.out.println("policy stripped is:"+ PublicMethods.TransformDomresultToString(policy));
//		} catch (TransformerException e) {
//			e.printStackTrace();
//		}		
//		
//		TranslationController transController=new TranslationController((Document) policy.getNode());
//		transController.filter();
//	}
	
}
