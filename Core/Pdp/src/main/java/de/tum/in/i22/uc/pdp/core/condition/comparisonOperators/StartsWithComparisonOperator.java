package de.tum.in.i22.uc.pdp.core.condition.comparisonOperators;

/**
 * Class for a generic Comparison operator.
 * @author Enrico Lovat
 *
 * @param <T1>
 * @param <T2>
 */
public class StartsWithComparisonOperator extends GenericComparisonOperator{
	public boolean compare(String parameter1, String parameter2){
		return (parameter1!=null) && (parameter2 != null) && (parameter1.startsWith(parameter2));
	}
}
