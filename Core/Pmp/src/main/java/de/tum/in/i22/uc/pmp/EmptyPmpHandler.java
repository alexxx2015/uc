package de.tum.in.i22.uc.pmp;

import java.util.Set;

import de.tum.in.i22.uc.cm.datatypes.IData;
import de.tum.in.i22.uc.cm.datatypes.IStatus;
import de.tum.in.i22.uc.cm.distribution.Location;
import de.tum.in.i22.uc.cm.server.PmpProcessor;


public class EmptyPmpHandler extends PmpProcessor {

	@Override
	public IStatus informRemoteDataFlow(Location location, Set<IData> dataflow) {
		throw new UnsupportedOperationException("No PmpHandler deployed.");
	}

	@Override
	public IStatus receivePolicies(Set<String> policies) {
		throw new UnsupportedOperationException("No PmpHandler deployed.");
	}
}
