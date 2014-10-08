package de.tum.in.i22.uc.ptp.adaptation.statebased;

/**
 * @author Cipri
 * 
 * <complexType name="StateBasedOperatorType">
 *		<attribute name="operator" type="string" use="required" />
 *		<attribute name="param1" type="string" use="required" />
 *		<attribute name="param2" type="string" use="optional" />
 *		<attribute name="param3" type="string" use="optional" />
 *	</complexType>
 *
 */
public class StateBasedFormula {

	private String operator;
	private String param1;
	private String param2;
	private String param3;
	
	public StateBasedFormula(String operator, String param1){
		this.setOperator(operator);
		this.setParam1(param1);
	}

	/**
	 * @return the operator
	 */
	public String getOperator() {
		return operator;
	}

	/**
	 * @param operator the operator to set
	 */
	public void setOperator(String operator) {
		this.operator = operator;
	}

	/**
	 * @return the param1
	 */
	public String getParam1() {
		return param1;
	}

	/**
	 * @param param1 the param1 to set
	 */
	public void setParam1(String param1) {
		this.param1 = param1;
	}

	/**
	 * @return the param2
	 */
	public String getParam2() {
		return param2;
	}

	/**
	 * @param param2 the param2 to set
	 */
	public void setParam2(String param2) {
		this.param2 = param2;
	}

	/**
	 * @return the param3
	 */
	public String getParam3() {
		return param3;
	}

	/**
	 * @param param3 the param3 to set
	 */
	public void setParam3(String param3) {
		this.param3 = param3;
	}
	
	public String toString(){
		String result = "";
		result += operator;
		result +=" [" + this.param1;
		result +=" " + this.param2;
		result +=" " + this.param3;
		result +=" ]";
		return result;
	}
	
	public int hashCode(){
		return (this.operator+this.param1).hashCode();
	}
	
	public boolean equals(Object o){
		if (o == null)
			return false;
		if (!(o instanceof StateBasedFormula)){
			return false;
		}
		StateBasedFormula obj = (StateBasedFormula) o;
		if( !this.operator.equals(obj.operator))
			return false;
		if( !this.param1.equals(obj.param1))
			return false;
		return true;
	}
}
