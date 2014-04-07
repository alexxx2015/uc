package de.tum.in.i22.uc.pmp;

import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import de.tum.in.i22.uc.cm.basic.StatusBasic;
import de.tum.in.i22.uc.cm.datatypes.EStatus;
import de.tum.in.i22.uc.cm.datatypes.IData;
import de.tum.in.i22.uc.cm.datatypes.IName;
import de.tum.in.i22.uc.cm.datatypes.IStatus;
import de.tum.in.i22.uc.cm.distribution.Location;
import de.tum.in.i22.uc.cm.server.PmpProcessor;
import de.tum.in.i22.uc.pmp.extensions.distribution.DistributedPmpManager;


public class PmpHandler extends PmpProcessor {

	private final DistributedPmpManager _distributedPmpManager;

	public PmpHandler() {
		_distributedPmpManager = new DistributedPmpManager();
	}

	@Override
	public IStatus informRemoteDataFlow(Map<Location, Map<IName, Set<IData>>> dataflow) {
		for (Entry<Location, Map<IName, Set<IData>>> locEntry : dataflow.entrySet()) {

			Set<IData> data = new HashSet<>();

			for (Entry<IName, Set<IData>> nameEntry : locEntry.getValue().entrySet()) {
				data.addAll(nameEntry.getValue());
			}

			if (data.size() > 0) {
				// TODO get policies from PDP.
//				_distributedPmpManager.remotePolicyTransfer(locEntry.getKey(), policy);
			}
		}

		return new StatusBasic(EStatus.ERROR);
	}

	@Override
	public IStatus remotePolicyTransfer(Set<String> policies) {
		// TODO Auto-generated method stub
		return null;
	}
}
