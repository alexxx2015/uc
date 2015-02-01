package de.tum.in.i22.uc.ptp.policy.translation.ecacreation;
/**
 * 
 * Represents an exceptional situation where configuring 
 * action input.
 * 
 * @author ELIJAH
 *
 */
public class ECAInputException extends Exception {
	/**
	 * Creates an instance of this exception object with
	 * a specific message.
	 * 
	 * @param eie
	 */
	public ECAInputException(String eie){
		super(eie);
	}
}
