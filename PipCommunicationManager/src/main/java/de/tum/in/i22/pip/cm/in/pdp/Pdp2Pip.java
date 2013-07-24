package de.tum.in.i22.pip.cm.in.pdp;

import java.util.List;

import de.tum.in.i22.pdp.cm.out.IPdp2Pip;
import de.tum.in.i22.pdp.datatypes.IContainer;
import de.tum.in.i22.pdp.datatypes.IData;
import de.tum.in.i22.pdp.datatypes.IEvent;

public class Pdp2Pip implements IPdp2Pip {
	
	private static Pdp2Pip _instance = new Pdp2Pip();
	
	public static IPdp2Pip getInstance() {
		return _instance;
	}
	
	private Pdp2Pip() {
		super();
	}

	public Boolean evaluatePredicate(IEvent arg0, String arg1) {
		// TODO Auto-generated method stub
		return true;
	}

	public List<IContainer> getContainerForData(IData arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<IData> getDataInContainer(IContainer arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
