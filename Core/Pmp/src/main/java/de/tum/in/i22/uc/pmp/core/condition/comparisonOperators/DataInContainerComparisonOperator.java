package de.tum.in.i22.uc.pmp.core.condition.comparisonOperators;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.interfaces.IPmp2Pip;

public class DataInContainerComparisonOperator extends
		GenericComparisonOperator {
	private static Logger _logger = LoggerFactory
			.getLogger(DataInContainerComparisonOperator.class);

	private IPmp2Pip _pip;

	public DataInContainerComparisonOperator(IPmp2Pip pip) {
		super();
		_pip = pip;
	}

	public boolean compare(String cont, String data) {
		/**
		 * this operator or in general, any comparison operator does not make
		 * sense in this context.
		 */
		return false;
	}
}
