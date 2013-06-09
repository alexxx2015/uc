package de.tum.in.i22.pdp.datatypes.basic;

import org.apache.log4j.Logger;

import de.tum.in.i22.pdp.datatypes.IOslFormula;
import de.tum.in.i22.pdp.gpb.PdpProtos.GpOslFormula;

public class OslFormulaBasic implements IOslFormula {
	private static Logger _logger = Logger.getLogger(OslFormulaBasic.class);
	private String _stringRepresentation;
	
	public OslFormulaBasic(String stringRepresentation) {
		super();
		_stringRepresentation = stringRepresentation;
	}
	
	public OslFormulaBasic(GpOslFormula condition) {
		if (condition == null)
			return;
		_stringRepresentation = condition.getFormula();
	}

	@Override
	public String getAsString() {
		return _stringRepresentation;
	}

	/**
	 * 
	 * @param e
	 * @return Google Protocol Buffer object corresponding to IOslFormula
	 */
	public static GpOslFormula createGpbOslFormula(IOslFormula formula) {
		if (formula == null)
			return null;
		_logger.trace("Build OslFormula");
		GpOslFormula.Builder gp = GpOslFormula.newBuilder();
		gp.setFormula(formula.getAsString());
		return gp.build();
	}
	
	@Override
	public boolean equals(Object obj) {
		boolean isEqual = false;
		if (obj != null && this.getClass() == obj.getClass()) {
			OslFormulaBasic o = (OslFormulaBasic)obj;
			isEqual = CompareUtil.areObjectsEqual(
					_stringRepresentation, o.getAsString());
		}
		return isEqual;
	}
	
}
