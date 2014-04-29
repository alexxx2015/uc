package de.tum.in.i22.uc.pip.extensions.statebased;

import java.util.Set;

import com.google.common.collect.Sets;

import de.tum.in.i22.uc.cm.datatypes.basic.DataBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.exceptions.InvalidStateBasedFormulaException;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IContainer;
import de.tum.in.i22.uc.cm.interfaces.informationFlowModel.IInformationFlowModel;

public class IsCombinedWith extends StateBasedPredicate {
	private final String _param1;
	private final String _param2;

	public IsCombinedWith(String predicate, String param1, String param2, IInformationFlowModel ifm) {
		super(predicate, ifm);
		_param1 = (param1 == null ? "" : param1);
		_param2 = (param2 == null ? "" : param2);
	}

	@Override
	public Boolean evaluate() throws InvalidStateBasedFormulaException {

		if ((_param1==null) || (_param2==null)){
			throw new InvalidStateBasedFormulaException("Impossible to evaluate a formula where parameters have not been initialized [ param1 = "+_param1+" , param2 = "+_param2+"]");
		}

		Set<IContainer> s1= _informationFlowModel.getContainers(new DataBasic(_param1));
		Set<IContainer> s2= _informationFlowModel.getContainers(new DataBasic(_param2));

		return Sets.intersection(s1, s2).size()>0;
	}

}
