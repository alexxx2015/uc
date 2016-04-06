package de.tum.in.i22.uc.cm.processing;

import java.nio.ByteBuffer;

import de.tum.in.i22.uc.cm.distribution.Location;
import de.tum.in.i22.uc.cm.interfaces.IAny2Pdp;
import de.tum.in.i22.uc.cm.interfaces.IAny2Prp;

public abstract class PrpProcessor implements IAny2Prp, IProcessor {

	@Override
	public void deployReleaseMechanism(ByteBuffer mechanism) {
		// TODO Auto-generated method stub
		System.out.println("Prp.deployreleasemech");
	}

	@Override
	public ByteBuffer getMechanism(String mechanismName) {
		// TODO Auto-generated method stub
		System.out.println("Prp.getmechanism");
		return null;
	}
	
	public abstract void init(IAny2Pdp pdp);

}
