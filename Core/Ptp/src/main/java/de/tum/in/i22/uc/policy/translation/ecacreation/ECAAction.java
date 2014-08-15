package de.tum.in.i22.uc.policy.translation.ecacreation;

/**
 * In configuring the action-part of ECA FTPRules, we have
 * different UI views and results for the actions inhibit,
 * modify, execute, and delay. This interface provides a
 * general contract for the UIs for the different views
 * so we can get the needed information from them
 * 
 * @author ELIJAH
 *
 */
public interface ECAAction {
	/**
	 * During forward and backward movement during ECA rules creation,
	 * we need to save the ECA rules and show what users had previously
	 * set.
	 */
	public void populateUI(Object action);
	/**
	 * Its important if we know the name of this action so
	 * we can cast the action result from getActionResult()
	 * into the appropriate object and use.
	 * 
	 * @return Returns any one of inhibit, modify, execute or delay
	 */
	public String getActionName();
	/**
	 * Returns the object associated with this action.
	 * Inhibit returns a string
	 * Modify returns an array list
	 * Delay returns an array list
	 * Execute returns a SubformulaEvent object
	 * 
	 * @return Returns an object for a particular action
	 */
	public Object getActionResult() throws ECAInputException;
}
