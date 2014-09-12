package de.tum.in.i22.policyeditor.editor;

//Example from http://www.crionics.com/products/opensource/faq/swing_ex/SwingExamples.html

//File:CheckListExample2.java
/* (swing1.1.1beta2) */


import java.util.ArrayList;

import javax.swing.JList;

import de.tum.in.i22.policyeditor.model.PolicyTemplate;
import de.tum.in.i22.policyeditor.model.TemplatesLoader;


class CheckList {

	private ArrayList<CheckableItem> items ;
	
	public CheckList(String policyClass) {
		initialize(policyClass);
	}
	
	private JList<CheckableItem> changeListener ;
	
	public CheckList(JList<CheckableItem> list){
		items = new  ArrayList<CheckableItem>();
		changeListener = list;
	}
	
	private void initialize(String policyClass){
		items = new  ArrayList<CheckableItem>();
		ArrayList<PolicyTemplate> templates = new ArrayList<PolicyTemplate>();
		PolicyTemplate[] templatesArray = TemplatesLoader.loadPolicyTemplates().getTemplates();
		for (int i = 0; i < templatesArray.length; i++) {
			PolicyTemplate policyTemplate = templatesArray[i];
			if(policyTemplate.isPolicyOfClass(policyClass)){
				policyTemplate.setDataClass(policyClass);
				templates.add(policyTemplate);
			}
		}
		templatesArray = new PolicyTemplate[templates.size()];
		templatesArray = templates.toArray(templatesArray);
		items.addAll( createData(templatesArray));
	}

	
	private ArrayList<CheckableItem> createData(PolicyTemplate[] templates) {
		 int n = templates.length;
		 ArrayList<CheckableItem> items = new ArrayList<CheckableItem>(n);
		 for (int i = 0; i < n; i++) {
		   CheckableItem item = new CheckableItem(templates[i]);
		   item.registerListener(changeListener);
		   items.add( item );
		 }
		 return items;
	}

	public void addPolicy(PolicyTemplate policy){
		CheckableItem item = new CheckableItem(policy);
		item.registerListener(changeListener);
		items.add( item );
	}
	
	public CheckableItem[] getItems(){
		int size = items.size();
		CheckableItem[] result = new CheckableItem[size];
		return items.toArray(result);
	}
	
	public void clear(){
		items.clear();
	}
	
}
