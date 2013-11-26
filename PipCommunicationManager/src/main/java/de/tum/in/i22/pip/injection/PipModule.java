package de.tum.in.i22.pip.injection;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;

import de.tum.in.i22.pip.core.IPdp2Pip;
import de.tum.in.i22.pip.core.PipHandler;

public class PipModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(IPdp2Pip.class).to(PipHandler.class).in(Singleton.class);
	}

}
