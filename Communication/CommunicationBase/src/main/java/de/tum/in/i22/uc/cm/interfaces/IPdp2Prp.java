package de.tum.in.i22.uc.cm.interfaces;

import java.nio.ByteBuffer;

import de.tum.in.i22.uc.thrift.generator.AThriftMethod;

//@AThriftService(name="TPdp2Prp")
public interface IPdp2Prp {
	
	@AThriftMethod(signature="oneway void deployReleaseMechanism(1: binary mechanism)")
	public void deployReleaseMechanism(ByteBuffer mechanism);
}
