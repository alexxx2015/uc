package de.tum.in.i22.uc.pmp;

import java.util.HashSet;
import java.util.Set;

import de.tum.in.i22.uc.cm.basic.StatusBasic;
import de.tum.in.i22.uc.cm.datatypes.EStatus;
import de.tum.in.i22.uc.cm.datatypes.IData;
import de.tum.in.i22.uc.cm.datatypes.IStatus;
import de.tum.in.i22.uc.cm.distribution.Location;
import de.tum.in.i22.uc.cm.server.PmpProcessor;
import de.tum.in.i22.uc.pmp.extensions.distribution.PmpDistributionManager;


public class PmpHandler extends PmpProcessor {

	private final PmpDistributionManager _distributedPmpManager;

	public PmpHandler() {
		_distributedPmpManager = new PmpDistributionManager();
	}

	@Override
	public IStatus informRemoteDataFlow(Location location, Set<IData> data) {
		Set<IData> d = new HashSet<>();

		// TODO: Get policies for data and send them

		return new StatusBasic(EStatus.ERROR);
	}

	@Override
	public IStatus receivePolicies(Set<String> policies) {
		// TODO: Do sth. cool with the policies
		// - manage them
		// - deploy them at PDP.

		for (String s : policies) {
			getPdp().deployPolicyXML(s);
		}

		// TODO: proper return value.
		return new StatusBasic(EStatus.OKAY);
	}
}
