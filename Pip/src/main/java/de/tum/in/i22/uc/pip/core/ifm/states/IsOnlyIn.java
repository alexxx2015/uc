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
		String[] containers;
		Set<IContainer> s;

		containers = _param2.split(separator2);
		Set<String> limit = new HashSet<String>(Arrays.asList(containers));
		s = _ifModel.getContainersForData(new DataBasic(_param1));
		// _logger.debug("size of s: "+s.size());
		for (IContainer cont : s) {
			NameBasic pname = new NameBasic(cont.getId());// TODO: not sure if
															// it is correct
			// _logger.debug("..in loop("+cont+")..");
			if (!(limit.contains(_ifModel.getContainerRelaxed(pname)))) {
				return false;
			}
		}
		// _logger.trace("..no match found, returning true");
		return true;
	}

}
