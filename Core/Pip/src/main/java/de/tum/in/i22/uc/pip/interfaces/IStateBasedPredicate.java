package de.tum.in.i22.uc.pip.interfaces;

import de.tum.in.i22.uc.pip.extensions.statebased.InvalidStateBasedFormula;

public interface IStateBasedPredicate {
	public Boolean evaluate() throws InvalidStateBasedFormula;
}
