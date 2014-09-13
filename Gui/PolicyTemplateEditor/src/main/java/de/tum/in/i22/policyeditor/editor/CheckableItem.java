package de.tum.in.i22.policyeditor.editor;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;

import de.tum.in.i22.policyeditor.logger.EditorLogger;
import de.tum.in.i22.policyeditor.model.PolicyTemplate;
import de.tum.in.i22.policyeditor.model.PolicyTemplateParser;

public class CheckableItem {
	 private String str;
	 private PolicyTemplate policy;	 
	 private boolean isSelected;
	 
	 private boolean onInstanceList;
	 
	 private JList<CheckableItem> changeListener;
	 private ArrayList<Component> components;
	 
	 private static EditorLogger logger = EditorLogger.instance();
	 
	 public CheckableItem(String str) {
	   this.str = str;
	   components = new ArrayList<Component>();
	   this.policy = new PolicyTemplate();
	   isSelected = false;
	   onInstanceList = false;
	 }
	
	 public CheckableItem(PolicyTemplate policy){
		 components = new ArrayList<Component>();
		 this.policy = policy;
		 this.str = policy.getDescription();
		 isSelected = false;
		 onInstanceList = false;
	 }
	 
	 public PolicyTemplate getPolicy(){
		 return this.policy;
	 }
	 
	 public void setOnInstanceList(){
		 onInstanceList = true;
	 }
	 
	 public void setSelected(boolean b) {
	   isSelected = b;
	   this.changeListener.repaint();
	 }
	
	 public boolean isSelected() {
	   return isSelected;
	 }
	
	 public String toString() {
	   return str;
	 }
	 
	 public String getClearDescription(){
		 return policy.getClearDescription();
	 }
	 
	 public List<Component> getPolicyEditorComponents(){
		 String data = policy.getDescription();
		 if(!this.onInstanceList){
			 if(this.components.size() == 0){
				 List<Component> generatedComponents = PolicyTemplateParser.parsePolicyTemplate(data);
				 components.addAll(generatedComponents);
				 addSaveButton();
			 }else {
				for (Component c : components) {
					c.setEnabled(true);
				}
			 }
			 return this.components;
		 }
		components.clear();
		List<Component> generatedComponents = PolicyTemplateParser.parsePolicyTemplate(data);
		components.addAll(generatedComponents);
		addSaveButton();
		 
		return components;
	 }
	 
		
	
	 
	 private void addSaveButton(){
		 if(components.size() == 1)
			 return;
		 
			final JButton saveButton = new JButton("Save");
			saveButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					instantiatePolicy(saveButton);
				}
			});
			components.add(saveButton);
	}
	 
	 public void instantiatePolicy(Component saveButton){
		 String data = "";
			String instanceData = policy.getTemplate();
			policy.setInstance(instanceData);
			
			if(components.size()==0){
				this.getPolicyEditorComponents();
			}
			
			for(Component c: components){
				c.setEnabled(false);
				if(c.equals(saveButton) )
					continue;
				if(c instanceof JButton)
					continue;
				policy.instantiatePolicyAttributes(c);
				data += c.toString();
			}
			String description = data;
			logger.infoLog("Policy edited and saved: " + data, null);
			notifyEvent(description);
	 }
	 
	 public void registerListener(JList<CheckableItem> installedList){
		 this.changeListener = installedList;
	 }
	 
	 private void notifyEvent(String description){
		 if(this.onInstanceList == false){
			 CheckableItem clone = this.clone();
			 clone.policy.setDescription(description);
			 clone.setOnInstanceList();
			 clone.changeListener = this.changeListener;
			 if(!clone.getPolicy().getName().equals(""))
				 clone.setSelected(true);
			 DefaultListModel<CheckableItem> model =  (DefaultListModel<CheckableItem>) this.changeListener.getModel();
			 model.addElement(clone);
		 }
		 else {
			 this.policy.setDescription(description);
			 components.clear();
		 }
		 
		 this.changeListener.updateUI();
	 }
	 
	 public CheckableItem clone(){
		 PolicyTemplate policyClone = this.policy.clone();
		 CheckableItem clone = new CheckableItem(policyClone);
		 return clone;
	 }
	 
}