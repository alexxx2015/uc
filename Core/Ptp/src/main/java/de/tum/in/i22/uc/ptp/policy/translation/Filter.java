package de.tum.in.i22.uc.ptp.policy.translation;

/**
 * The various stages in translation are usually filters
 * in a pipe-filter architecture. Each filter implements this
 * interface as it provides a way to track errors in stages
 * and get error messages.
 *  *
 */
public interface Filter {

	/**
	 * When filtering is in progress, errors can result so
	 * that the process can be interrupted. It can also be
	 * successful.
	 *
	 */
	public enum FilterStatus{
		SUCCESS,
		FAILURE,
	}
	
	/**
	 * Starts processing in a filter component as data flows
	 * through the pipe and filter system.
	 */
	public void filter();
	
	/**
	 * 
	 * @return Returns the status of a filter after it has been
	 * called to work, if the operation within this filter was
	 * successful or failed with java exception or application-
	 * specific message.
	 */
	public FilterStatus getFilterStatus();
	
	/**
	 * 
	 * @return Returns the message associated with the
	 * operation of this filter.
	 */
	public String getMessage();
	
}
