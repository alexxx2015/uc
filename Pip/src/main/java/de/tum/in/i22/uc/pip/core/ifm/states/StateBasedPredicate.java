package de.tum.in.i22.uc.pip.core.ifm.states;

import com.google.common.base.Objects;

import de.tum.in.i22.uc.pip.core.ifm.InformationFlowModel;
import de.tum.in.i22.uc.pip.interfaces.EStateBasedFormula;
import de.tum.in.i22.uc.pip.interfaces.IStateBasedPredicate;

public abstract class StateBasedPredicate implements IStateBasedPredicate {
	protected InformationFlowModel _ifModel = InformationFlowModel.getInstance();

	private final String _predicate;

	private final static String SEPARATOR1 = "\\|";
	protected final static String SEPARATOR2 = "#";


	public StateBasedPredicate(String predicate) {
		_predicate = predicate;
	}

	public static IStateBasedPredicate create(String predicate) {
		IStateBasedPredicate spredicate = null;

		RuntimeException rte = new RuntimeException("Predicate {" + predicate + "} is invalid.");

		String[] st = predicate.split(StateBasedPredicate.SEPARATOR1);
		if (st.length == 0) {
			throw rte;
		}

		switch (EStateBasedFormula.from(st[0])) {
			case IS_COMBINED_WITH:
				if (st.length >= 3)
					spredicate = new IsCombinedWith(predicate, st[1], st[2]);
				break;
			case IS_NOT_IN:
				if (st.length >= 3)
					spredicate = new IsNotIn(predicate, st[1], st[2]);
				break;
			case IS_ONLY_IN:
				if (st.length >= 3)
					spredicate = new IsOnlyIn(predicate, st[1], st[2]);
				break;
			default:
				throw rte;
		}

		if (spredicate == null) {
			throw rte;
		}

		return spredicate;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this)
				.add("_predicate", _predicate)
				.toString();
	}
}
