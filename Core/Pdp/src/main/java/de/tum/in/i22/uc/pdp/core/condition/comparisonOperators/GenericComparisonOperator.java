package de.tum.in.i22.uc.pdp.core.condition.comparisonOperators;

/**
 * Class for a generic Comparison operator.
 * @author Enrico Lovat
 *
 * @param <T1>
 * @param <T2>
 */
public abstract class GenericComparisonOperator {
	public abstract boolean compare(String parameter1, String parameter2);
}
