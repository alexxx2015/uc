package de.tum.in.i22.uc.pip.extensions.statebased;

import java.util.HashSet;
import java.util.Set;

import de.tum.in.i22.uc.cm.datatypes.basic.DataBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.NameBasic;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IContainer;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IData;

public class IsNotIn extends StateBasedPredicate {
	private final String _param1;
	private final String _param2;

	public IsNotIn(String predicate, String param1, String param2) {
		super(predicate);
		_param1 = param1;
		_param2 = param2;
	}

	@Override
	public Boolean evaluate() {
		String[] containers;
		Set<IContainer> s;

		Set<IData> data =new HashSet<IData>();
		for (String d : _param1.split(SEPARATOR2)){
			data.add(new DataBasic(d));
		}
		 
		containers = _param2.split(SEPARATOR2);
		for (IData d : data) {
			s = _ifModel.getContainers(d);
			if (s.size() > 0) {
				for (String cont : containers) {
					NameBasic pname = new NameBasic(cont);
					if (s.contains(_ifModel.getContainer(pname))) {
						return false;
					}
				}
				// no match found, returning true
				return true;
			}
		}
		if (data.size() == 0) {
			return false;
		}

		return null;
	}

}
