package de.tum.in.i22.pdp.datatypes.basic;

import de.tum.in.i22.pdp.datatypes.IOslFormula;

public class OslFormulaBasic implements IOslFormula {
	private String _stringRepresentation;
	
	public OslFormulaBasic(String stringRepresentation) {
		super();
		_stringRepresentation = stringRepresentation;
	}
	
	@Override
	public String getAsString() {
		return _stringRepresentation;
	}
	
}
