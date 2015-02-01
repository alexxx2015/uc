package de.tum.in.i22.uc.ptp.policy.translation.ecacreation;

import java.util.ArrayList;

import javax.swing.JRadioButton;

/**
 * There is tight coupling between our ECARulesCreatorView
 * and the model that does logic of ECA FTPRules creation: Subformula
 * This class is only a decoupler and will help enhancements in future.
 * As the user moves forward or backwards through the ECARulesCreatorView
 * for each subformula, calls pass through this class.
 * 
 * @author ELIJAH
 *
 */
public class ECARulesCreatorController {
	
	private Subformula subformula;
	
	/**
	 * Sets the current subformula.
	 * 
	 * @param subformula
	 */
	public void setCurrentSubformula(Subformula subformula){
		this.subformula=subformula;
	}
	
	/**
	 * Sets the radio button for the selected trigger event. This
	 * is useful to show the user's previous settings moving back and
	 * forth the UI.
	 * 
	 * @param button
	 */
	public void setButtonForTriggerEvent(JRadioButton button){
		subformula.setRbTriggerEvent(button);
	}
	
	/**
	 * Sets the radio button for the selected conditon. This
	 * is useful to show the user's previous settings moving back and
	 * forth the UI.
	 * 
	 * @param button
	 */
	public void setButtonForCondition(JRadioButton button){
		subformula.setRbCondition(button);
	}
	
	/**
	 * Sets the index of selected ECA action. This
	 * is useful to show the user's previous settings moving back and
	 * forth the UI.
	 * 
	 * @param index
	 */
	public void setIndexSelectedAction(int index){
		subformula.setJcbAction(index);
	}
	
	/**
	 *  
	 * @return Returns the previously selected trigger event radio button.
	 */
	public Object getButtonForTriggerEvent(){
		return subformula.getRbTriggerEvent();
	}
	
	/**
	 * 
	 * @return Returns the previously selected condition radio button.
	 */
	public Object getButtonForCondition(){
		return subformula.getRbCondition();
	}
	
	/**
	 * 
	 * @return Returns the previously set action index.
	 */
	public int getIndexSelectedAction(){
		return subformula.getJcbAction();
	}
	
	/**
	 * Updates an ECA rule for a given subformula
	 * 
	 * @param event
	 * @param condition
	 * @param action
	 */
	public void updateECARule(Object event, Object condition, Object action){
		subformula.updateECARule(event, condition, action);
	}
	
	/**
	 * 
	 * @return Returns the ECA rule for this subformula.
	 */
	public ECARule getECARule(){
		return subformula.getECARule();
	}
	
	/**
	 * Set the event a user defined in a table. The user defined event is
	 * the last option in the trigger event part of the UI.
	 * 
	 * @param event
	 */
	public void setUserDefinedEvent(SubformulaEvent event){
		subformula.setUserDefinedEvent(event);
	}
	
	/**
	 * 
	 * @return Returns the previously set user defined event.
	 */
	public SubformulaEvent getUserDefinedEvent(){
		return subformula.getUserDefinedEvent();
	}
	
	/**
	 * This updates the action in the list.
	 * 
	 * @param index
	 * @param event
	 */
	public void modifyExecuteAction(int index, SubformulaEvent event){
		subformula.modifyExecuteAction(index, event);
	}
	
	/**
	 * This adds a new action for an execute option
	 * 
	 * @param event
	 */
	public void addExecuteAction(SubformulaEvent event){
		subformula.addExecuteAction(event);	
	}
	
	/**
	 * The action part of an ECA rule has execute as one of its options. It means
	 * executing a number of actions in response to detected event and satisfied condition.
	 *  
	 * @param index
	 * @return Returns a specific execute action from a list of actions.
	 */
	public SubformulaEvent getExecuteAction(int index){
		return subformula.getExecuteActions().get(index);
	}
	
	/**
	 * 
	 * @return Returns the list of previously set execute actions.
	 */
	public ArrayList<SubformulaEvent> getExecuteActions(){
		return subformula.getExecuteActions();
	}
	
}
