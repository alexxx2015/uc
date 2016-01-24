package de.tum.in.i22.uc.cm.interfaces;

import java.nio.ByteBuffer;

import de.tum.in.i22.uc.thrift.generator.AThriftMethod;

//@AThriftService(name="TPep2Prp")
public interface IPep2Prp {

	@AThriftMethod(signature="binary getMechanism(1: string mechanismName)")
	public ByteBuffer getMechanism(String mechanismName);
}
