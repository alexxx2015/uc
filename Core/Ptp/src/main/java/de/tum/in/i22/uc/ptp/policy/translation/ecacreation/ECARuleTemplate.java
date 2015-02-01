package de.tum.in.i22.uc.ptp.policy.translation.ecacreation;

/**
 * @author Cipri
 * This class represents an instance of an ECA rule template.
 */
public class ECARuleTemplate {

	private String event;
	private String condition;
	private String[] action ;
	private String type ;
	
	public ECARuleTemplate(String event, String condition, String[] action, String type){
		this.event = event;
		this.condition = condition;
		this.action = action;
		this.type = type;
	}
	
	public String getType(){
		return type;
	}
	public void setType(String type){
		this.type = type;
	}
	public String getEvent() {
		return event;
	}
	public void setEvent(String event) {
		this.event = event;
	}
	public String getCondition() {
		return condition;
	}
	public void setCondition(String condition) {
		this.condition = condition;
	}
	public String[] getAction() {
		return action;
	}
		
	public void setAction(String[] action) {
		this.action = action;
	}
	
	public String toString(){
		String a = "";
		for(String act : action)
			a += act +"-";
		String s = "{" + event + ":" + condition + ":" + a +":"+ type+ "}";
		return s ;
	}
	
}
