package de.tum.in.i22.pdp.injection;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;

import de.tum.in.i22.pdp.core.IIncoming;
import de.tum.in.i22.pdp.core.PdpHandlerMock;

public class PdpModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(IIncoming.class).to(PdpHandlerMock.class).in(Singleton.class);
	}

}
