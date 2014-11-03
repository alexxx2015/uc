package de.tum.in.i22.uc.pdp.core;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.MoreObjects;

import de.tum.in.i22.uc.pdp.core.operators.comparison.DataInContainerComparisonOperator;
import de.tum.in.i22.uc.pdp.core.operators.comparison.ElementInListComparisonOperator;
import de.tum.in.i22.uc.pdp.core.operators.comparison.EndsWithComparisonOperator;
import de.tum.in.i22.uc.pdp.core.operators.comparison.EqualsComparisonOperator;
import de.tum.in.i22.uc.pdp.core.operators.comparison.EqualsIgnoreCaseComparisonOperator;
import de.tum.in.i22.uc.pdp.core.operators.comparison.GeComparisonOperator;
import de.tum.in.i22.uc.pdp.core.operators.comparison.GenericComparisonOperator;
import de.tum.in.i22.uc.pdp.core.operators.comparison.GtComparisonOperator;
import de.tum.in.i22.uc.pdp.core.operators.comparison.LeComparisonOperator;
import de.tum.in.i22.uc.pdp.core.operators.comparison.ListInListComparisonOperator;
import de.tum.in.i22.uc.pdp.core.operators.comparison.LtComparisonOperator;
import de.tum.in.i22.uc.pdp.core.operators.comparison.NotEqualsComparisonOperator;
import de.tum.in.i22.uc.pdp.core.operators.comparison.StartsWithComparisonOperator;
import de.tum.in.i22.uc.pdp.core.operators.comparison.SubstringComparisonOperator;
import de.tum.in.i22.uc.pdp.xsd.ComparisonOperatorTypes;
import de.tum.in.i22.uc.pdp.xsd.ParamMatchType;

public class ParamMatch extends ParamMatchType {
	private static Logger _logger = LoggerFactory.getLogger(ParamMatch.class);
	private PolicyDecisionPoint _pdp;

	private final static Map<ComparisonOperatorTypes,GenericComparisonOperator> _comparisonOperators;

	static {
		_comparisonOperators = new HashMap<>();
		_comparisonOperators.put(ComparisonOperatorTypes.DATA_IN_CONTAINER, new DataInContainerComparisonOperator(null));
		_comparisonOperators.put(ComparisonOperatorTypes.ELEMENT_IN_LIST, new ElementInListComparisonOperator());
		_comparisonOperators.put(ComparisonOperatorTypes.ENDS_WITH, new EndsWithComparisonOperator());
		_comparisonOperators.put(ComparisonOperatorTypes.EQUALS, new EqualsComparisonOperator());
		_comparisonOperators.put(ComparisonOperatorTypes.EQUALS_IGNORE_CASE, new EqualsIgnoreCaseComparisonOperator());
		_comparisonOperators.put(ComparisonOperatorTypes.GE, new GeComparisonOperator());
		_comparisonOperators.put(ComparisonOperatorTypes.GT, new GtComparisonOperator());
		_comparisonOperators.put(ComparisonOperatorTypes.LE, new LeComparisonOperator());
		_comparisonOperators.put(ComparisonOperatorTypes.LIST_IN_LIST, new ListInListComparisonOperator());
		_comparisonOperators.put(ComparisonOperatorTypes.LT, new LtComparisonOperator());
		_comparisonOperators.put(ComparisonOperatorTypes.NOT_EQUALS, new NotEqualsComparisonOperator());
		_comparisonOperators.put(ComparisonOperatorTypes.STARTS_WITH, new StartsWithComparisonOperator());
		_comparisonOperators.put(ComparisonOperatorTypes.SUBSTRING, new SubstringComparisonOperator());
	}

	public ParamMatch() {
	}

	public ParamMatch(String name, String value, ComparisonOperatorTypes cmpOp, PolicyDecisionPoint pdp) {
		this.name = name;
		this.value = value;
		this.cmpOp = cmpOp;
		_pdp = pdp;
	}

	public static ParamMatch convertFrom(ParamMatchType p, PolicyDecisionPoint pdp) {
		if (p instanceof ParamMatch) {
			ParamMatch newp = (ParamMatch) p;
			newp._pdp = pdp;
			return newp;
		}
		throw new IllegalArgumentException(p + " is not of type " + ParamMatch.class);
	}


	/**
	 * Returns true if this element matches the specified parameter name and value.
	 * @param paramName
	 * @param paramValue
	 * @return
	 */
	public boolean matches(String paramName, String paramValue) {
		if (paramName == null || paramValue == null) {
			_logger.trace("Parameter [{}] not present", paramName);
			return false;
		}

		if (!paramName.equals(name)) {
			_logger.trace("param name [" + name + "] does NOT match");
			return false;
		}

		_logger.trace("param name [" + name + "] matches");

		GenericComparisonOperator comparer = _comparisonOperators.get(cmpOp);
		if (comparer == null) {
			// default comparison is equality
			comparer = _comparisonOperators.get(ComparisonOperatorTypes.EQUALS);
		}
		else if (comparer instanceof DataInContainerComparisonOperator) {
			((DataInContainerComparisonOperator) comparer).setPip(_pdp.getPip());
		}

		boolean matches = comparer.compare(paramValue, this.getValue());
		_logger.trace("param value [" + paramValue + "] does " + (matches ? "" : "NOT ") + "match ["
				+ this.getValue() + "]");
		return matches;

	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(getClass())
				.add("name", name)
				.add("value", value)
				.add("type", type)
				.add("cmpOp", cmpOp)
				.add("dataID", dataID)
				.toString();
	}
}