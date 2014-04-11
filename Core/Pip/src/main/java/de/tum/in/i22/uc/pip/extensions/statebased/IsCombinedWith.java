package de.tum.in.i22.uc.pip.extensions.statebased;

import java.util.Set;

import de.tum.in.i22.uc.cm.datatypes.basic.DataBasic;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IContainer;

public class IsCombinedWith extends StateBasedPredicate {
	private final String _param1;
	private final String _param2;

	public IsCombinedWith(String predicate, String param1, String param2) {
		super(predicate);
		_param1 = param1;
		_param2 = param2;
	}

	@Override
	public Boolean evaluate() {
		Set<IContainer> s1= _ifModel.getContainers(new DataBasic(_param1));
		Set<IContainer> s2= _ifModel.getContainers(new DataBasic(_param2));

		Set<IContainer> small;
		Set<IContainer> large;

		if (s1.size() > s2.size()) {
			small = s2;
			large = s1;
		}
		else {
			small = s1;
			large = s2;
		}

		return large.containsAll(small);
	}

}
