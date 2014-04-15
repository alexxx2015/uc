package de.tum.in.i22.uc.pmp;

import java.util.HashSet;
import java.util.Set;

import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic.EStatus;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IData;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.cm.distribution.Location;
import de.tum.in.i22.uc.cm.processing.PmpProcessor;
import de.tum.in.i22.uc.pmp.core.PmpDecisionPoint;
import de.tum.in.i22.uc.pmp.extensions.distribution.PmpDistributionManager;


public class PmpHandler extends PmpProcessor {

	private final PmpDistributionManager _distributedPmpManager;
	private final PmpDecisionPoint _pmp;

	public PmpHandler() {
		_distributedPmpManager = new PmpDistributionManager();
		_pmp = PmpDecisionPoint.getInstance();
	}


	@Override
	public IStatus receivePolicies(Set<String> policies) {
		for (String s : policies) {
			getPdp().deployPolicyXML(s);
		}

		// TODO: proper return value.
		return new StatusBasic(EStatus.OKAY);
	}

	@Override
	public IStatus informRemoteDataFlow(Location srcLocation, Location dstLocation, Set<IData> dataflow) {
		Set<IData> d = new HashSet<>();

		// TODO: Get policies for data and send them

		return new StatusBasic(EStatus.ERROR);
	}
}
