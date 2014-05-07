package de.tum.in.i22.uc.cm.distribution;

import de.tum.in.i22.uc.cm.pip.RemoteDataFlowInfo;
import de.tum.in.i22.uc.cm.processing.PdpProcessor;
import de.tum.in.i22.uc.cm.processing.PipProcessor;
import de.tum.in.i22.uc.cm.processing.PmpProcessor;

public interface IDistributionManager {

	void dataTransfer(RemoteDataFlowInfo dataflow);

	void init(PdpProcessor _pdp, PipProcessor _pip, PmpProcessor _pmp);
}
