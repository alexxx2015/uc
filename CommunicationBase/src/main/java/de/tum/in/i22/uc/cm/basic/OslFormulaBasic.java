package de.tum.in.i22.uc.cm.basic;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.datatypes.IOslFormula;

public class OslFormulaBasic implements IOslFormula {
	private static Logger _logger = LoggerFactory.getLogger(OslFormulaBasic.class);
	private final String _stringRepresentation;

	public OslFormulaBasic(String stringRepresentation) {
		super();
		_stringRepresentation = stringRepresentation;
	}

	@Override
	public String getAsString() {
		return _stringRepresentation;
	}


	@Override
	public boolean equals(Object obj) {
		boolean isEqual = false;
		if (obj instanceof OslFormulaBasic) {
			OslFormulaBasic o = (OslFormulaBasic)obj;
			isEqual = Objects.equals(_stringRepresentation, o._stringRepresentation);
		}
		return isEqual;
	}

	@Override
	public int hashCode() {
		return Objects.hash(_stringRepresentation);
	}

}
