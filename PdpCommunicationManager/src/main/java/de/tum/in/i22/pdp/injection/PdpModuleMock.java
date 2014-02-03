package de.tum.in.i22.pdp.injection;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;

import de.fraunhofer.iese.pef.pdp.LinuxPolicyDecisionPoint;
import de.fraunhofer.iese.pef.pdp.PolicyDecisionPoint;
import de.tum.in.i22.pdp.core.IIncoming;
import de.tum.in.i22.pdp.core.PdpHandlerMock;

public class PdpModuleMock extends AbstractModule {

	@Override
	protected void configure() {
		//bind(IIncoming.class).to(PdpHandlerMockLinux.class).in(Singleton.class);
		
		bind(IIncoming.class).to(PdpHandlerMock.class).in(Singleton.class);
		
		
		bind(PolicyDecisionPoint.class).to(LinuxPolicyDecisionPoint.class).in(Singleton.class);
		
//		bind(PolicyDecisionPoint.class).to(Win64PolicyDecisionPoint.class).in(Singleton.class);
		
		
	}

}
