package de.tum.in.i22.cm.injection;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;

import de.tum.in.i22.cm.cm.in.pdp.PdpHandler;
import de.tum.in.i22.uc.cm.interfaces.IPdpIncoming;

public class PdpModuleMockTestPip extends AbstractModule {

	@Override
	protected void configure() {
		bind(IPdpIncoming.class).to(PdpHandler.class).in(Singleton.class);
	}

}
