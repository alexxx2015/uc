package de.tum.in.i22.uc.policy.translation;

import java.io.File;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;

import org.w3c.dom.Node;

import de.tum.in.i22.uc.policy.translation.Filter.FilterStatus;

/**
 * 20 April 2013
 * Create a policy file as a result of filtering a diagram
 * file saved by the user of our Usage Control Policy Editor
 */
public class PolicyCreator implements Filter{
	
	/**
	 * Status of pipe and filter operation
	 */
	private FilterStatus fStatus;
	/**
	 * Messages during data movement through our
	 * pipe and filter system
	 */
	private String sMessage;
	
	public PolicyCreator(String inputFile){
		sInput=inputFile;
		fStatus=FilterStatus.FAILURE;
	}
	
	//Get an xml node as a string
	private String getNode(Node node, Transformer trans) throws TransformerException{			
		StringWriter sw = new StringWriter();
		StreamResult result = new StreamResult(sw);
		DOMSource source = new DOMSource(node);
		trans.transform(source, result);
		return sw.toString();
	}
	
	/**
	 * Does the filtering.
	 */
	@Override
	public void filter(){
		try{						    							 	
			
			//Required for building DOMResults both as temporary storage and
			//output from filter, in RAM, instead of files on hard disk
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();												
			
			//temporary output in memory
			DOMResult oTempDOMResult=new DOMResult(builder.newDocument());

            TransformerFactory tFactory = TransformerFactory.newInstance();
            Transformer transformer;
				
			StreamSource src = new StreamSource(new File("support/firstStyle.xsl"));
			transformer = tFactory.newTransformer(src);
				
	        StreamSource strs=new StreamSource(new File(sInput));
	        
	        //leave temporary result in memory
	        transformer.transform(strs,oTempDOMResult);				        

	        TransformerFactory tfFactory = TransformerFactory.newInstance();
	        Transformer tTransformer = tfFactory.newTransformer(new StreamSource(new File("support/secondStyle.xsl"))); 
	        
	        //output DOMResult in xml	        	
			DOMResult oDOMResult=new DOMResult(builder.newDocument());	        
	        tTransformer.transform(new DOMSource(oTempDOMResult.getNode()),oDOMResult);	        	       
	        oOutput=oDOMResult;
	        
	        fStatus=FilterStatus.SUCCESS;
	        sMessage="- Policy creation was successful.";
		}	    
		catch(Exception e){
			fStatus=FilterStatus.FAILURE;
			sMessage="- Policy transformation was unsuccessful.";
			e.printStackTrace();
		}
	}
	
	private String sInput;
	private DOMResult oOutput;
	/**
	 * Sets the input file to this policy creator.
	 * 
	 * @param input
	 */
	public void setInput(String input){
		this.sInput=input;
	}
	
	/**
	 * 
	 * @return Returns a DOMResult output of created policy.
	 */
	public DOMResult getOutput(){
		return oOutput;
	}

	public String getOutputPolicy(){
		String result = "";
		return result ;
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
