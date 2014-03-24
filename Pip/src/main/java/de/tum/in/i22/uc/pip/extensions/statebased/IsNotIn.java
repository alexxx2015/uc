package de.tum.in.i22.uc.pip.extensions.statebased;

import java.util.Set;

import de.tum.in.i22.uc.cm.basic.NameBasic;
import de.tum.in.i22.uc.cm.datatypes.IContainer;
import de.tum.in.i22.uc.cm.datatypes.IData;

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

		// TODO This is for sure optimizable. -FK-

		IContainer container = _ifModel.getContainerRelaxed(new NameBasic(_param1));
		Set<IData> data = _ifModel.getDataInContainer(container);

		containers = _param2.split(SEPARATOR2);
		for (IData d : data) {
			s = _ifModel.getContainersForData(d);
			// _logger.debug("size of s: "+s.size());
			if (s.size() > 0) {
				for (String cont : containers) {
					NameBasic pname = new NameBasic(cont);
					// _logger.debug("..in loop("+cont+")..");
					if (s.contains(_ifModel.getContainerRelaxed(pname))) {
						return false;
					}
				}
				// _logger.trace("..no match found, returning true");
				return true;
			}
		}
		if (data.size() == 0) {
			return false;
		}

		return null;

		// par1 is data, par2 is list of containers
		// containers= par2.split(separator2);
		// s= _ifModel.getContainersForData(new DataBasic(par1));
		// //_logger.debug("size of s: "+s.size());
		// if(s.size() > 0){
		// for (String cont : containers){
		// NameBasic pname= new NameBasic(cont);
		// //_logger.debug("..in loop("+cont+")..");
		// if (s.contains(_ifModel.getContainerRelaxed(pname))) {
		// _logger.trace(out+"=false");
		// return false;
		// }
		// }
		// //_logger.trace("..no match found, returning true");
		// _logger.trace(out+"=true");
		// return true;
		// } else{
		// return false;
		// }
	}

}
