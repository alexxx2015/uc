package de.tum.in.i22.uc.pip.core.statebased;

import java.util.HashSet;
import java.util.Set;

import com.google.common.collect.Sets;

import de.tum.in.i22.uc.cm.datatypes.basic.DataBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.exceptions.InvalidStateBasedFormulaException;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IContainer;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IData;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IName;
import de.tum.in.i22.uc.cm.factories.MessageFactory;
import de.tum.in.i22.uc.cm.pip.ifm.IAnyInformationFlowModel;

public class IsNotIn extends StateBasedPredicate {
	private final Set<IData> _data;
	private final Set<IName> _forbiddenContainerNames;

	public IsNotIn(String predicate, String param1, String param2, IAnyInformationFlowModel ifm) throws InvalidStateBasedFormulaException {
		super(predicate, ifm);

		if (param1 == null || param2 == null || param1.isEmpty() || param2.isEmpty()) {
			throw new InvalidStateBasedFormulaException(
					"Impossible to evaluate a formula where parameters have not been initialized [ param1 = " + param1
							+ ", param2 = " + param2 + "]");
		}

		_data = new HashSet<>();
		for (String d : param1.split(SEPARATOR2)) {
			_data.add(new DataBasic(d));
		}

		_forbiddenContainerNames = new HashSet<>();
		for (String name : param2.split(SEPARATOR2)) {
			_forbiddenContainerNames.add(MessageFactory.createName(name));
		}
	}

	@Override
	public boolean evaluate() {
		Set<IContainer> forbiddenContainers = new HashSet<>();
		for (IName name : _forbiddenContainerNames) {
			forbiddenContainers.add(_informationFlowModel.getContainer(name));
		}

		for (IData d : _data) {
			Set<IContainer> contForData = _informationFlowModel.getContainers(d);
			if (!Sets.intersection(contForData, forbiddenContainers).isEmpty()) {
				return false;
			}
		}

		return true;
	}
}
