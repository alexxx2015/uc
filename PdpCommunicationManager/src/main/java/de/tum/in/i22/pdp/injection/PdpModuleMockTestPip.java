package de.tum.in.i22.pdp.injection;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;

import de.tum.in.i22.pdp.core.IIncoming;
import de.tum.in.i22.pdp.core.PdpHandlerMock;
import de.tum.in.i22.pdp.core.PdpHandlerMockLinux;
import de.tum.in.i22.pdp.core.PdpHandlerPdpNative;
import de.tum.in.i22.pdp.core.PdpHandlerTestPip;

public class PdpModuleMockTestPip extends AbstractModule {

	@Override
	protected void configure() {
//		bind(IIncoming.class).to(PdpHandlerMockLinux.class).in(Singleton.class);

		bind(IIncoming.class).to(PdpHandlerTestPip.class).in(Singleton.class);
	}

}
