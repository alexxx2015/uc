package de.tum.in.i22.uc.pip.core.ifm.states;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import de.tum.in.i22.uc.cm.basic.DataBasic;
import de.tum.in.i22.uc.cm.basic.NameBasic;
import de.tum.in.i22.uc.cm.datatypes.IContainer;

public class IsOnlyIn extends StateBasedPredicate {
	private final String _param1;
	private final String _param2;

	public IsOnlyIn(String predicate, String param1, String param2) {
		super(predicate);
		_param1 = param1;
		_param2 = param2;
	}

	@Override
	public Boolean evaluate() {
		// TODO FK: This implementation seems to be completely wrong.

		Set<String> limit = new HashSet<String>(Arrays.asList(_param2.split(SEPARATOR2)));

		Set<IContainer> containers = _ifModel.getContainersForData(new DataBasic(_param1));

		for (IContainer cont : containers) {
			NameBasic pname = new NameBasic(cont.getId());

			if (!(limit.contains(_ifModel.getContainerRelaxed(pname)))) {
				return false;
			}
		}

		return true;
	}

}
