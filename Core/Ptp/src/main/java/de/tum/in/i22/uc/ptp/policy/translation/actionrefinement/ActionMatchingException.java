package de.tum.in.i22.uc.ptp.policy.translation.actionrefinement;


/**
 * Thrown when we cannot find a complete match
 * for an action and its associated data in our
 * sns.xml domain file, for a given specification-level
 * policy.
 *
 */
public class ActionMatchingException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7846669462480958648L;

	/**
	 * Creates an instance of this exception with a specific
	 * message.
	 * 
	 * @param message
	 */
	public ActionMatchingException(String message){
		super(message);
		printStackTrace();
	}
}
