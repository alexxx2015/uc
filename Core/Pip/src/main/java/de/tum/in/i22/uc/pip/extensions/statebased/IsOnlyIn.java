package de.tum.in.i22.uc.pip.extensions.statebased;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import de.tum.in.i22.uc.cm.datatypes.basic.ContainerBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.DataBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.NameBasic;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IContainer;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IData;

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

		// if one of the data in param1 is stored in a container that is not in
		// param2 list, then return false.
		// otherwise return true.

		Set<String> limit = new HashSet<String>(Arrays.asList(_param2
				.split(SEPARATOR2)));

		for (String d : _param1.split(SEPARATOR2)) {
			Set<IContainer> dataLocations = _ifModel
					.getContainers(new DataBasic(d));
			for (String cs : limit) {
				if (!(dataLocations.contains(new ContainerBasic(cs)))) {
					return false;
				}
			}
		}
		return true;
	}
}