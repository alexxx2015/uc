package de.tum.in.i22.uc.pip.core.statebased;

import java.util.HashSet;
import java.util.Set;

import com.google.common.collect.Sets;

import de.tum.in.i22.uc.cm.datatypes.basic.DataBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.NameBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.exceptions.InvalidStateBasedFormulaException;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IContainer;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IData;
import de.tum.in.i22.uc.cm.interfaces.informationFlowModel.IInformationFlowModel;

public class IsNotIn extends StateBasedPredicate {
	private final String _param1;
	private final String _param2;

	public IsNotIn(String predicate, String param1, String param2, IInformationFlowModel ifm) {
		super(predicate, ifm);
		_param1 = (param1 == null ? "" : param1);
		_param2 = (param2 == null ? "" : param2);
	}

	@Override
	public Boolean evaluate() throws InvalidStateBasedFormulaException {

		if ((_param1==null) || (_param2==null)){
			throw new InvalidStateBasedFormulaException("Impossible to evaluate a formula where parameters have not been initialized [ param1 = "+_param1+" , param2 = "+_param2+"]");
		}

		Set<IData> data = new HashSet<IData>();
		String[] forbiddenContainerNames = _param2.split(SEPARATOR2);
		Set<IContainer> forbiddenContainers = new HashSet<IContainer>();

		for (String d : _param1.split(SEPARATOR2)) {
			data.add(new DataBasic(d));
		}

		for (String forbiddenContainerName : forbiddenContainerNames) {
			forbiddenContainers.add(_informationFlowModel.getContainer(new NameBasic(
					forbiddenContainerName)));
		}

		for (IData d : data) {
			Set<IContainer> contForData = _informationFlowModel.getContainers(d);
			if (Sets.intersection(contForData, forbiddenContainers).size() != 0)
				return false;
		}

		return true;
	}
}
