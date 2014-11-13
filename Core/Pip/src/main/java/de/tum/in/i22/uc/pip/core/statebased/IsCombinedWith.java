package de.tum.in.i22.uc.pip.core.statebased;

import java.util.Set;

import com.google.common.collect.Sets;

import de.tum.in.i22.uc.cm.datatypes.basic.DataBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.exceptions.InvalidStateBasedFormulaException;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IContainer;
import de.tum.in.i22.uc.cm.pip.ifm.IAnyInformationFlowModel;

public class IsCombinedWith extends StateBasedPredicate {
	private final String _param1;
	private final String _param2;

	public IsCombinedWith(String predicate, String param1, String param2, IAnyInformationFlowModel ifm) throws InvalidStateBasedFormulaException {
		super(predicate, ifm);

		if (param1 == null || param2 == null || param1.isEmpty() || param2.isEmpty()) {
			throw new InvalidStateBasedFormulaException(
					"Impossible to evaluate a formula where parameters have not been initialized [ param1 = " + param1
							+ ", param2 = " + param2 + "]");
		}

		_param1 = param1;
		_param2 = param2;
	}

	@Override
	public boolean evaluate() {
		Set<IContainer> s1 = _informationFlowModel.getContainers(new DataBasic(_param1));
		Set<IContainer> s2 = _informationFlowModel.getContainers(new DataBasic(_param2));

		return Sets.intersection(s1, s2).size() > 0;
	}

}
