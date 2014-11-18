package de.tum.in.i22.uc.pip.core.statebased;

import java.util.HashMap;
import java.util.Map;

import de.tum.in.i22.uc.cm.datatypes.basic.exceptions.InvalidStateBasedFormulaException;
import de.tum.in.i22.uc.cm.pip.ifm.IAnyInformationFlowModel;
import de.tum.in.i22.uc.cm.pip.interfaces.EStateBasedFormulaType;
import de.tum.in.i22.uc.cm.pip.interfaces.IStateBasedPredicate;
import de.tum.in.i22.uc.cm.settings.Settings;


/**
 * This class manages {@link IStateBasedPredicate}s.
 *
 * It will create {@link IStateBasedPredicate}s given a
 * String (method {@link StateBasedPredicateManager#get(String, IAnyInformationFlowModel)}.
 * and buffer them, such that they are not re-created every time the
 * {@link IStateBasedPredicate} is used.
 *
 * 2014/11/18 FK
 *
 * @author Florian Kelbert
 *
 */
public class StateBasedPredicateManager {

	public final static String SEPARATOR1 = Settings.getInstance().getSeparator1();
	public final static String SEPARATOR2 = Settings.getInstance().getSeparator2();

	private final Map<String,IStateBasedPredicate> _predicates;

	public StateBasedPredicateManager() {
		_predicates = new HashMap<>();
	}

	/**
	 * Creates an {@link IStateBasedPredicate} out of the specified predicate (String).
	 * If the predicate was created before, a cached copy is returned.
	 *
	 * 2014/11/18 FK
	 *
	 * @param predicate the predicate to transform.
	 * @param ifm the Information Flow Model upon which the {@link IStateBasedPredicate} will be evaluated.
	 * @return an {@link IStateBasedPredicate} reflecting the specified predicate.
	 * @throws InvalidStateBasedFormulaException in case the specified predicate was malformed.
	 */
	public IStateBasedPredicate get(String predicate, IAnyInformationFlowModel ifm) throws InvalidStateBasedFormulaException {
		IStateBasedPredicate spredicate = _predicates.get(predicate);

		if (spredicate != null) {
			return spredicate;
		}

		InvalidStateBasedFormulaException exc = new InvalidStateBasedFormulaException("Predicate {" + predicate + "} is invalid.");

		String[] st = predicate.split(SEPARATOR1);
		EStateBasedFormulaType pred = EStateBasedFormulaType.from(st[0]);

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

		_predicates.put(predicate, spredicate);
		return spredicate;
	}
}
