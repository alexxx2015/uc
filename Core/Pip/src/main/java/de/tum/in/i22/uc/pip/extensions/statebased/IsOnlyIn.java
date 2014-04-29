package de.tum.in.i22.uc.pip.extensions.statebased;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import de.tum.in.i22.uc.cm.datatypes.basic.DataBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.exceptions.InvalidStateBasedFormulaException;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IContainer;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IName;
import de.tum.in.i22.uc.cm.interfaces.informationFlowModel.IInformationFlowModel;

public class IsOnlyIn extends StateBasedPredicate {
	private final String _param1;
	private final String _param2;

	public IsOnlyIn(String predicate, String param1, String param2, IInformationFlowModel ifm) {
		super(predicate, ifm);
		_param1 = (param1 == null ? "" : param1);
		_param2 = (param2 == null ? "" : param2);
	}

	@Override
	public Boolean evaluate() throws InvalidStateBasedFormulaException {

		if ((_param1==null) || (_param2==null)){
			throw new InvalidStateBasedFormulaException("Impossible to evaluate a formula where parameters have not been initialized [ param1 = "+_param1+" , param2 = "+_param2+"]");
		}


		// if one of the data in param1 is stored in a container that is not in
		// param2 list, then return false.
		// otherwise return true.

		// notice that data in param1 could be stored in a container that has more than one name.
		// at least one of the names must exists in param1 list, otherwise param1 data is not only in param2 list.

		if (_param1==null) return true;
		String[] dataList=_param1.split(SEPARATOR2);

		String[] contList = new String[0];
		if (_param2!=null) contList=_param2.split(SEPARATOR2);

		Set<String> contSet = new HashSet<String>(Arrays.asList(contList));

		for (String d : dataList) {
			Set<IContainer> dataLocations = _informationFlowModel.getContainers(new DataBasic(d));
			for (IContainer c : dataLocations){
				boolean oneOfTheNamesMatches = false;
				for (IName n : _informationFlowModel.getAllNames(c)){
					if (contSet.contains(n.getName())) {
						oneOfTheNamesMatches=true;
						break;
					}
				}
				if (!oneOfTheNamesMatches) return false;
			}
		}
		return true;
	}
}