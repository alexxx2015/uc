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
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.tum.in.i22.uc.ptp.utilities.PublicMethods;

/**
 * @author Cipri
 *
 */
public class WindowsPepCompliance {

	private static final Logger _logger = LoggerFactory.getLogger(WindowsPepCompliance.class);
	
	public static final String id = "windows";
	
	private Document policy;
	private Map<String,String> param;
	
	public WindowsPepCompliance(Document doc, Map<String, String> parameters){
		this.policy = doc;
		this.param = parameters;
	}
	
	public void getCompliantPolicy(){
		Document doc =policy;
		
		String file = param.get("pictureFileName");
		String cache = param.get("InFileName");
		String cacheType = param.get("cacheType");
		String processName = param.get("ProcessName");
		
		try {
			addEventParam(doc);
			addConditionParam(doc, processName);
		} catch (XPathExpressionException e) {
			_logger.error("adding condition param match", e);
		}
		
	}
	
	
	
	/**
	 * <paramMatch name="InFileName" value="C:\Users\Cipri\AppData\Local\Mozilla\Firefox\Profiles\knsq1snj.default\Cache\_CACHE_MAP_"/>   
	 * @param doc
	 * @throws XPathExpressionException 
	 */
	private void addEventParam(Document doc) throws XPathExpressionException{
		XPathFactory xPathfactory = XPathFactory.newInstance();
		XPath xpath = xPathfactory.newXPath();
		XPathExpression expr = xpath.compile("//trigger");
		NodeList triggerList = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
		for(int i=0; i<triggerList.getLength(); i++){
			Node trigger = triggerList.item(i);
			NodeList children = trigger.getChildNodes();
			int childrenCounter = children.getLength();
			for(int j=0; j<childrenCounter; j++){
				Node child = children.item(j);
				if(child == null)
					continue;
				if(child.getNodeType() != Node.ELEMENT_NODE)
					continue;
				String childName = child.getNodeName();
				if(childName.equals("paramMatch")){
					NamedNodeMap attributes = child.getAttributes();
					if(attributes!=null){
						Node name = attributes.getNamedItem("name");
						Node value = attributes.getNamedItem("value");
						if(name!=null && value!=null){
							String fileValue = value.getTextContent();
							((Element)child).setAttribute("name", "InFileName");
						}
					}
				}
			}
		}
	}
	
	/**
	 * <condition>   
	 *		<conditionParamMatch name="PNAME" value="C:\Program Files (x86)\Mozilla Firefox\firefox.exe"/>	
  	 *	</condition>
	 * @param doc
	 * @param file
	 * @param cache
	 * @throws XPathExpressionException
	 */
	private void addConditionParam(Document doc, String processName) throws XPathExpressionException{
		XPathFactory xPathfactory = XPathFactory.newInstance();
		XPath xpath = xPathfactory.newXPath();
		XPathExpression expr = xpath.compile("//condition");
		NodeList conditionList = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
		for(int i=0; i<conditionList.getLength(); i++){
			Node condition = conditionList.item(i);
			PublicMethods.removeWhitespaceNodes(condition);
			Node oldChildren = condition.getFirstChild();
			if(oldChildren == null)
				continue;
			if(oldChildren.getNodeType() != Node.ELEMENT_NODE)
				continue;
			condition.removeChild(oldChildren);
			
			Element conditionParamMatch = doc.createElement("conditionParamMatch");
			conditionParamMatch.setAttribute("name", "PNAME");
			conditionParamMatch.setAttribute("value", processName);
			conditionParamMatch.setAttribute("cmpOp", "notEquals");
			
			Element and = doc.createElement("and");
			and.appendChild(oldChildren);
			and.appendChild(conditionParamMatch);
			
			condition.appendChild(and);
		}
	}
}
