package de.tum.in.i22.pdp.injection;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;

import de.fraunhofer.iese.pef.pdp.LinuxPolicyDecisionPoint;
import de.fraunhofer.iese.pef.pdp.PolicyDecisionPoint;
import de.tum.in.i22.pdp.core.IIncoming;
import de.tum.in.i22.pdp.core.PdpHandlerPdpNative;

public class PdpModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(IIncoming.class).to(PdpHandlerPdpNative.class).in(Singleton.class);
		// for linux
		bind(PolicyDecisionPoint.class).to(LinuxPolicyDecisionPoint.class).in(Singleton.class);
		
		// for win 64
//		bind(PolicyDecisionPoint.class).to(Win64PolicyDecisionPoint.class).in(Singleton.class);
	}

}
