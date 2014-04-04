package de.tum.in.i22.uc.thrift.server;

public interface IThriftServer extends Runnable {
	public void stop();
	public boolean started();
}
