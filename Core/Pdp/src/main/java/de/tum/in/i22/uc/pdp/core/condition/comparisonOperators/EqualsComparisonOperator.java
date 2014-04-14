package de.tum.in.i22.uc.pdp.core.condition.comparisonOperators;

/**
 * Class for a generic Comparison operator.
 * @author moka
 *
 * @param <T1>
 * @param <T2>
 */
public class EqualsComparisonOperator extends GenericComparisonOperator {
	public boolean compare(String parameter1, String parameter2){
		return (parameter1!=null) && (parameter1.equals(parameter2));
	}
}
