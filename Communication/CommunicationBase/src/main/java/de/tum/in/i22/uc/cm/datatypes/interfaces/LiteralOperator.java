package de.tum.in.i22.uc.cm.datatypes.interfaces;

import org.apache.derby.iapi.util.Operator;

/**
 * Tagging interface for basic {@link Operator}s, i.e.
 * operators that do not 'nest' other {@link Operator}s inside them.
 *
 * @author Florian Kelbert
 *
 */
public interface LiteralOperator extends IOperator {
	public boolean isPositive();
}
