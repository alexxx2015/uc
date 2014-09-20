package de.tum.in.i22.uc.ptp.ecajaxb;



public interface IOperatorType {
	/**
	 * 
	 * @return Returns the kind of operator type, i.e Unary, binary or 
	 * timeboundedunary, e.t.c
	 */
	public OperatorTypeValue tellOperatorType();
	
	/**
	 * Properly prepares an instance of an operator type.
	 * This differs for each kind of operator type.
	 * 
	 * @param parent
	 */
	public void configureMyself(String xmlNodeName,Object parent);
	
	public void connectToMyParent(Object parent);
	
	public enum OperatorTypeValue{
		UNARY,
		BINARY,
		STATEBASED,
		TIMEBOUNDEDUNARY,
		EVENT,
		OTHER
	}
}
