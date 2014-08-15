package de.tum.in.i22.uc.policy.translation.ecacreation;

/**
 * Event-Condition-Action rule
 * 
 * @author ELIJAH
 *
 */
public class ECARule {
	/**
	 * Create an instance of ECA rule
	 * 
	 * @param triggerEvent
	 * @param condition
	 * @param action
	 */
	public ECARule(SubformulaEvent event, Object condition, Object action){
		this.tEvent=event;
		this.oCondition=condition;
		this.oAction=action;
	}
	
	private SubformulaEvent tEvent;
	private Object oCondition;
	private Object oAction;
	
	private String type;
	
	public void setType(String type){
		this.type = type;
	}
	
	public String getType(){
		return this.type;
	}
	
	/**
	 * 
	 * @return Returns the trigger event
	 */
	public SubformulaEvent getEvent() {
		return tEvent;
	}
	/**
	 * Sets the trigger event
	 * 
	 * @param tEvent
	 */
	public void setEvent(SubformulaEvent tEvent) {
		this.tEvent = tEvent;
	}
	/**
	 * 
	 * @return Returns the condition of this ECA rule
	 */
	public Object getCondition() {
		return oCondition;
	}
	/**
	 * Sets the condition of this rule
	 * 
	 * @param sCondition
	 */
	public void setCondition(Object oCondition) {
		this.oCondition = oCondition;
	}
	/**
	 * 
	 * @return Returns the action of this ECA rule
	 */
	public Object getAction() {
		return oAction;
	}
	/**
	 * Sets the action of this ECA rule
	 * 
	 * @param sAction
	 */
	public void setAction(Object oAction) {
		this.oAction = oAction;
	}
	
	public String toString(){
		String result = "";
		result += tEvent +":"+ oCondition + ":" + oAction;
		return result ;
	}
	
}
