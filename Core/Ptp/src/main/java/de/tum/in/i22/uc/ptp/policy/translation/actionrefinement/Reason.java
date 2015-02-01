package de.tum.in.i22.uc.ptp.policy.translation.actionrefinement;

/**
 * This Enumeration helps to determine the nature of xml output
 * while doing action refinement on policies
 * 
 * @author ELIJAH
 *
 */
public enum Reason {
	/**
	 * create <or>
	 */
	SET_PROCESSING,
	/**
	 * create </or>
	 */
	SET_END_PROCESSING,
	/**
	 * create <and><eventually>
	 */
	SEQUENCE_START,
	/**
	 * create </eventually></and>
	 */
	SEQUENCE_END,
	/**
	 * We have reached the last node in our 
	 * graph but this transformer is in a set
	 */
	A_FINAL_TRANSFORMER_IN_SET,
	/**
	 * We have reached the last node in our 
	 * graph but this transformer is in a sequence
	 */
	A_FINAL_TRANSFORMER_IN_SEQ,
	/**
	 * A final transformer. This is the case when
	 * we have reached a node without children and
	 * being the only child of its parent.
	 */
	A_FINAL_TRANSFORMER,
}
