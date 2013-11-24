package de.tum.in.i22.pdp.injection;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;

import de.tum.in.i22.pdp.core.IIncoming;
import de.tum.in.i22.pdp.core.PdpHandlerPdpNative;

public class PdpModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(IIncoming.class).to(PdpHandlerPdpNative.class).in(Singleton.class);
	}

}
