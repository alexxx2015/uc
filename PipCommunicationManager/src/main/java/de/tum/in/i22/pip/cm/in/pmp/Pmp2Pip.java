package de.tum.in.i22.pip.cm.in.pmp;

import testutil.DummyMessageGen;
import de.tum.in.i22.uc.cm.datatypes.IContainer;
import de.tum.in.i22.uc.cm.datatypes.IData;
import de.tum.in.i22.uc.cm.gpb.PdpProtos.GpStatus.EStatus;

public class Pmp2Pip implements IPmp2Pip {

	private static final IPmp2Pip _instance = new Pmp2Pip();
	
	private Pmp2Pip() {
		
	}
	
	public static IPmp2Pip getInstance() {
		return _instance;
	}
	
	@Override
	public EStatus initialRepresentation(IContainer container, IData data) {
		// TODO implement
		return DummyMessageGen.createStatus();
	}

}
