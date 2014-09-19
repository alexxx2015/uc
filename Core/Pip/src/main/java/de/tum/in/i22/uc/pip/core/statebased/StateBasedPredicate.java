package de.tum.in.i22.uc.pip.core.statebased;

import de.tum.in.i22.uc.cm.datatypes.basic.exceptions.InvalidStateBasedFormulaException;
import de.tum.in.i22.uc.cm.interfaces.informationFlowModel.IAnyInformationFlowModel;
import de.tum.in.i22.uc.cm.pip.interfaces.EStateBasedFormula;
import de.tum.in.i22.uc.cm.pip.interfaces.IStateBasedPredicate;
import de.tum.in.i22.uc.cm.settings.Settings;

public abstract class StateBasedPredicate implements IStateBasedPredicate {
	private final String _predicate;

	private final static String SEPARATOR1 = Settings.getInstance().getSeparator1();
	protected final static String SEPARATOR2 = Settings.getInstance().getSeparator2();

	protected final IAnyInformationFlowModel _informationFlowModel;


	public StateBasedPredicate(String predicate, IAnyInformationFlowModel informationFlowModel) {
		_predicate = predicate;
		_informationFlowModel = informationFlowModel;
	}

	public static IStateBasedPredicate create(String predicate, IAnyInformationFlowModel ifm) throws InvalidStateBasedFormulaException {
		IStateBasedPredicate spredicate = null;

		InvalidStateBasedFormulaException exc = new InvalidStateBasedFormulaException("Predicate {" + predicate + "} is invalid.");

		String[] st = predicate.split(StateBasedPredicate.SEPARATOR1);
		EStateBasedFormula pred = EStateBasedFormula.from(st[0]);

		if (st.length == 0 || pred == null) {
			throw exc;
		}

		switch (pred) {
			case IS_COMBINED_WITH:
				if (st.length >= 3)
					spredicate = new IsCombinedWith(predicate, st[1], st[2], ifm);
				break;
			case IS_NOT_IN:
				if (st.length >= 3)
					spredicate = new IsNotIn(predicate, st[1], st[2], ifm);
				break;
			case IS_ONLY_IN:
				if (st.length >= 3)
					spredicate = new IsOnlyIn(predicate, st[1], st[2], ifm);
				break;
			default:
				throw exc;
		}

		if (spredicate == null) {
			throw exc;
		}

		return spredicate;
	}

	@Override
	public String toString() {
		return com.google.common.base.MoreObjects.toStringHelper(this)
				.add("_predicate", _predicate)
				.toString();
	}
}
