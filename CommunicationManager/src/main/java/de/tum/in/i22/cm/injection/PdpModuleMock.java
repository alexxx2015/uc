package de.tum.in.i22.cm.injection;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;

import de.tum.in.i22.pdp.core.PdpHandlerMock;
import de.tum.in.i22.uc.cm.interfaces.IPdpIncoming;

public class PdpModuleMock extends AbstractModule {

	@Override
	protected void configure() {
		//bind(IIncoming.class).to(PdpHandlerMockLinux.class).in(Singleton.class);
		
		bind(IPdpIncoming.class).to(PdpHandlerMock.class).in(Singleton.class);
		
		
//		bind(PolicyDecisionPoint.class).to(LinuxPolicyDecisionPoint.class).in(Singleton.class);
		
//		bind(PolicyDecisionPoint.class).to(Win64PolicyDecisionPoint.class).in(Singleton.class);
		
		
	}

}
