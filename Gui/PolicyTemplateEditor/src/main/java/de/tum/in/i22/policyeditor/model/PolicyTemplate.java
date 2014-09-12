package de.tum.in.i22.policyeditor.model;


import java.awt.Component;

import de.tum.in.i22.policyeditor.logger.EditorLogger;

public class PolicyTemplate {

	private static EditorLogger logger = EditorLogger.instance();
	
	/*
	 * The names defined here must match the names used in the json config file
	 * in order for the loader to work correctly. 
	 */
	
	private String templateId;
	private String policyName;
	private String[] classes;
	private String description;
	private String template;
	private String instance;
	
	public PolicyTemplate(String id, String[] classes, String description, String template){
		this.templateId = id;
		this.classes = classes;
		this.description = description;
		this.template = template;
		instance = "";
		policyName = "";
	}
	
	public PolicyTemplate(){
		instance = "";
		policyName = "";
		this.templateId = "";
		this.description = "";
		this.template = "";
		classes = new String[0];
	}
	
	/**
	 * This the id of the template from the configuration file.
	 * @return
	 */
	public String getTemplateId() {
		return templateId;
	}
	public void setTemplateId(String id) {
		this.templateId = id;
	}
	public String[] getClasses(){
		return classes;
	}
	public void setClasses(String[] classes){
		this.classes = classes;
	}
	
	/**
	 * Sets the name of the policy used by the PMP to identify a unique policy.
	 * The name must be unique for each policy.
	 * @param name
	 */
	public void setName(String name){
		this.policyName = name;
	}
	
	/** Returns the name of the policy used by the PMP to identify a unique policy.
	 * @return
	 */
	public String getName(){
		return this.policyName;
	}
	
	/**
	 * Returns the natural language description of the policy with the param. markers.
	 * @return
	 */
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	/** The template of the policy with the #param_tags# in place.
	 * @return
	 */
	public String getTemplate() {
		return template;
	}
	public void setTemplate(String template) {
		this.template = template;
	}
	/** The instantiated template with all the parameter tags replaced.
	 * @return
	 */
	public String getInstance() {
		return instance;
	}
	public void setInstance(String instance) {
		this.instance = instance;
	}
	
	public boolean isPolicyOfClass(String templateClass){
		if(this.classes == null)
			return false;
		for (int i = 0; i < this.classes.length; i++) {
			String element = this.classes[i];
			if(element.equals(templateClass))
				return true;
		}
		return false;
	}
	
	/**
	 * Returs the natural language description with all the param. markers removed.
	 * @return
	 */
	public String getClearDescription(){
		String clear = PolicyTemplateParser.filterData(description);
		return clear;		
	}
	
	public String toString(){
		return "["+templateId+"]"+description;
	}
	
	public String toStringExtended(){
		return "["+templateId+"]"+description + " "+"["+getDataClass()+"]"
				+ "\n" + instance;
	}
	
	public void setDataClass(String policyClass){
		this.classes = new String[]{policyClass};
	}
	
	/**
	 * Returs the data class of the policy.
	 * @return
	 */
	public String getDataClass(){
		if(this.classes.length > 0)
			return this.classes[0];
		else
			return "";
	}
	
	public void instantiatePolicyAttributes(Component c) {
		String instance = this.instance;
		String generic = "";
		String data = "";
		if (c instanceof UserAction) {
			generic = ((UserAction) c).ACTION;
			String action = ((UserAction) c).getValue();
			data = action;
		} else if (c instanceof UserNumber) {
			generic = ((UserNumber) c).getGenericRepresentation();
			String number = (Integer) ((UserNumber) c).getValue() + "";
			data = number;
		} else if (c instanceof UserSubject) {
			generic = ((UserSubject) c).SUBJECT;
			String subject = ((UserSubject) c).getValue();
			data = subject;
		} else if (c instanceof UserTime) {
			generic = ((UserTime) c).TIME;
			String time = ((UserTime) c).getValue();
			data = time;
		}

		if(generic.length() > 0){
			boolean contains = instance.contains(generic);
			while (contains) {
				instance = instance.replace(generic, data);
				contains = instance.contains(generic);
			}
		}
		
		this.instance = instance;
	}
	
	/**
	 * Instantiate a policy with its specific class.
	 */
	public void instantiatePolicyClass(){
		String instance = this.instance;
		String generic = UserClass.CLASS;
		String data = this.getDataClass();

		if(generic.length() > 0){
			boolean contains = instance.contains(generic);
			while (contains) {
				instance = instance.replace(generic, data);
				contains = instance.contains(generic);
			}
		}
		
		this.instance = instance;
	}
	
	/**
	 * Instantiate a policy with a specific container.
	 * @param obj - the container
	 */
	public void instantiatePolicyObject(UserObject obj){
		String instance = this.instance;
		String generic = UserObject.CLASS;
		String data = obj.getValue();

		if(generic.length() > 0){
			boolean contains = instance.contains(generic);
			while (contains) {
				instance = instance.replace(generic, data);
				contains = instance.contains(generic);
			}
		}
		
		this.instance = instance;
	}
	
	public boolean equals(Object object){
		if(! (object instanceof PolicyTemplate))
			return false;
		PolicyTemplate o = (PolicyTemplate) object;
		return this.templateId.equals(o.templateId);
	}
	
	public PolicyTemplate clone(){
		PolicyTemplate clone = new PolicyTemplate();
		clone.classes = classes;
		clone.description = description;
		clone.templateId = templateId;
		clone.instance = instance;
		clone.template = template;
		return clone;
	}
	
	/*
	 * *************************************************
	 */
//	public static void main(String[] args){
//		String instance = "#{<subject>}# must delete this data after #0[<number>]# @[<time>]@";
//		String generic = "@\\[<time>\\]@";
//		String subject = "@\\[seconds\\]@";
//		instance = instance.replaceAll(generic,subject );
//		System.out.println(instance);
//	}
	
}
