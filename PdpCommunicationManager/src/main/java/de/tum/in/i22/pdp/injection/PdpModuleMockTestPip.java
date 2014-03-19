package de.tum.in.i22.pdp.injection;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;

import de.tum.in.i22.pdp.core.PdpHandlerTestPip;
import de.tum.in.i22.uc.cm.interfaces.IPdpIncoming;

public class PdpModuleMockTestPip extends AbstractModule {

	@Override
	protected void configure() {
		bind(IPdpIncoming.class).to(PdpHandlerTestPip.class).in(Singleton.class);
	}

}
