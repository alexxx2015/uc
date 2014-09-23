package de.tum.in.i22.uc.ptp.policy.customization;

import java.util.Map;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author Cipri
 *
 */
public class FirefoxPepCompliance {

	private static final Logger _logger = LoggerFactory.getLogger(FirefoxPepCompliance.class);
	
	private Document policy;
	private Map<String,String> param;
	
	public static final String id = "firefox";

	private String scope; 
	
	public FirefoxPepCompliance(Document doc, Map<String, String> parameters){
		this.policy = doc;
		this.param = parameters;
		
		this.scope = parameters.get("scope");
		if(scope==null)
			scope = "invalid-scope";
	}
	
	public void makeCompliantPolicy(){

		try {
			addEventParam(policy, scope);
		} catch (XPathExpressionException e) {
			_logger.error("add scope to policy", e);
		}
	}
	
	/**
	 * add to trigger
	 * <br> <paramMatch name="scope" value="53c50d767b07d"/>   
	 * @param doc
	 * @throws XPathExpressionException 
	 */
	private void addEventParam(Document doc, String scope) throws XPathExpressionException{
		XPathFactory xPathfactory = XPathFactory.newInstance();
		XPath xpath = xPathfactory.newXPath();
		XPathExpression expr = xpath.compile("//trigger");
		NodeList triggerList = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
		for(int i=0; i<triggerList.getLength(); i++){
			Node trigger = triggerList.item(i);
			
			Element el = doc.createElement("paramMatch");
			el.setAttribute("name", "scope");
			el.setAttribute("value", scope);
			
			trigger.appendChild(el);
			
		}
	}
	
}
