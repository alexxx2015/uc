package de.tum.in.i22.uc.policy.translation.ecacreation;

import org.w3c.dom.Node;

/**
 * Parameter associated with a subformula event 
 * 
 * @author ELIJAH
 *
 */
public class SubformulaEventParameter {
	
	/**
	 * Creates a trigger event parameter
	 * 
	 * @param name
	 * @param value
	 */
	public SubformulaEventParameter(String name, String value){
		this.sName=name;
		this.sValue=value;
	}
	
	/**
	 * Creates a trigger event parameter
	 * 
	 * @param name
	 * @param policyType
	 * @param containment
	 * @param value
	 */
	private SubformulaEventParameter(String name, String policyType, String containment, String value){
		this.sName=name;
		this.sPolicyType=policyType;
		this.sContainmentType=containment;
		this.sValue=value;
	}
	
	/**
	 * Create an instance of a subformula event parameter given
	 * an xml node
	 * 
	 * @param node
	 * @return Returns an instance of a sub formula event parameter
	 */
	public static SubformulaEventParameter createParameter(Node node){		
		String sName="", sPolicyType="", sContainment="", sValue="";
		Node nodeName=node.getAttributes().getNamedItem("name");
		if(nodeName!=null) sName=nodeName.getNodeValue();
		Node nodePolicyType=node.getAttributes().getNamedItem("policyType");
		if(nodePolicyType!=null) sPolicyType=nodePolicyType.getNodeValue();
		Node nodeContainment=node.getAttributes().getNamedItem("containment");
		if(nodeContainment!=null) sContainment=nodeContainment.getNodeValue();
		Node nodeValue=node.getAttributes().getNamedItem("value");
		if(nodeValue!=null) sValue=nodeValue.getNodeValue();
		return new SubformulaEventParameter(sName,sPolicyType,sContainment,sValue);
	}
	
	
	/**
	 * Parameter name
	 */
	private String sName;
	/**
	 * Parameter policy type
	 */
	private String sPolicyType;
	/**
	 * Parameter containment type.
	 * Usually for albums
	 */
	private String sContainmentType;
	/**
	 * Parameter Value
	 */
	private String sValue;
	
	/**
	 * 
	 * @return Returns name of parameter
	 */
	public String getName() {
		return sName;
	}
	/**
	 * Sets the parameter name
	 * 
	 * @param sName
	 */
	public void setName(String sName) {
		this.sName = sName;
	}
	/**
	 * 
	 * @return Returns the policy type
	 */
	public String getPolicyType() {
		return sPolicyType;
	}
	/**
	 * Sets the policy type
	 * 
	 * @param sPolicyType
	 */
	public void setPolicyType(String sPolicyType) {
		this.sPolicyType = sPolicyType;
	}
	/**
	 * 
	 * @return Returns the containment type
	 */
	public String getContainmentType() {
		return sContainmentType;
	}
	/**
	 * Sets the containment type
	 * 
	 * @param sContainmentType
	 */
	public void setContainmentType(String sContainmentType) {
		this.sContainmentType = sContainmentType;
	}
	/**
	 * 
	 * @return Returns the value
	 */
	public String getValue() {
		return sValue;
	}
	/**
	 * Sets the value
	 * 
	 * @param sValue
	 */
	public void setValue(String sValue) {
		this.sValue = sValue;
	}	
	
	public String toString(){
		String result = "";
		result += this.sName + ":" +this.sValue +":" + this.sPolicyType ; 
		return result;
	}
}
