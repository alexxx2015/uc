package uctests;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;

import de.tum.in.i22.pdp.core.IIncoming;
import de.tum.in.i22.pdp.core.PdpHandlerMock;
import de.tum.in.i22.pip.core.PipHandlerMock;
import de.tum.in.i22.uc.cm.interfaces.IPdp2Pip;

public class PdpTestModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(IIncoming.class).to(PdpHandlerMock.class).in(Singleton.class);
		bind(IPdp2Pip.class).to(PipHandlerMock.class).in(Singleton.class);
	}
	
}
