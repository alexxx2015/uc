package de.tum.in.i22.uc.ptp.policy.translation.ecacreation;

import java.util.ArrayList;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * A subformula event is an essential part of every ECA rule.
 * When an event happens in a system, the Usage Control Policy
 * System checks for the particular trigger event
 *
 */
public class SubformulaEvent {
	
	/**
	 * Creates an instance of a subformula event given evet name
	 * and array of parameter objects
	 * 
	 * @param eventName
	 * @param parameters
	 */
	public SubformulaEvent(String eventName, ArrayList<SubformulaEventParameter> parameters){
		this.sEventName=eventName;
		this.teParameters=parameters;
	}
	
	/**
	 * Creates a subformula event from an event name.
	 * 
	 * @param eventName
	 */
	public SubformulaEvent(String eventName){
		this.sEventName=eventName;
		this.teParameters=new ArrayList<SubformulaEventParameter>();
	}
	
	/**
	 * Creates an instance of a subformula event given an xml node
	 * 
	 * @param node
	 * @return Returns an instance of a subformula event
	 */
	public static SubformulaEvent createTriggerEvent(Node node){		
		ArrayList<SubformulaEventParameter> altep=new ArrayList<SubformulaEventParameter>();
		//name of event
		String sName="";
		Node nodeName=node.getAttributes().getNamedItem("name");
		if(nodeName!=null) sName=nodeName.getNodeValue();
		//parameters
		NodeList nlParameter=processXpathExpression("Param",node);
		if(nlParameter!=null){
			if(nlParameter.getLength()>0)
			{
				for(int i=0; i<nlParameter.getLength(); ++i)
				altep.add(SubformulaEventParameter.createParameter(nlParameter.item(i)));
			}
		}		
				
		return new SubformulaEvent(sName,altep);
	}
	
	/**
	 * Name of the event
	 */
	private String sEventName;
	/**
	 * Trigger event parameters for this trigger event
	 */
	private ArrayList<SubformulaEventParameter> teParameters;
	/**
	 * 
	 * @return Returns event name
	 */
	public String getEventName() {
		return sEventName;
	}
	/**
	 * Sets the event name
	 * 
	 * @param sEventName
	 */
	public void setEventName(String sEventName) {
		this.sEventName = sEventName;
	}
	/**
	 * 
	 * @return Returns an array list of trigger event parameters
	 */
	public ArrayList<SubformulaEventParameter> getParameters() {
		return teParameters;
	}
	/**
	 * Sets the trigger event parameters
	 * 
	 * @param teParameters
	 */
	public void setParameters(ArrayList<SubformulaEventParameter> parameters) {
		this.teParameters = parameters;
	}
	
	/**
	 * 
	 * @param expression
	 * @param document
	 * @return returns a node list of xml elements
	 */
	public static NodeList processXpathExpression(String expression, Object document){
		NodeList nodeList=null;				
		XPathFactory factory=XPathFactory.newInstance();
		XPath xpath=factory.newXPath();
		try {
			nodeList=(NodeList)xpath.evaluate(expression,document,XPathConstants.NODESET);
		} catch (XPathExpressionException e) {
			
			e.printStackTrace();
		}
		return nodeList;
	}
	

	@Override
	  public boolean equals(Object obj )  
    {  
        System.out.println("Equals invoked");  
        boolean flag = false;  
        SubformulaEvent subf = (SubformulaEvent) obj;  
        if (subf.getEventName().equalsIgnoreCase(sEventName))  
            flag = true;  
        return flag;  
    }
	  
	  @Override
	  public int hashCode(){
		  int result =1;
		  result += 31*result + sEventName.hashCode() ;
		  return result;
	  }
	  
	  public String toString(){
		  String result = "";
		  result += this.sEventName + this.teParameters;
		  return result ;
	  }
	
}
