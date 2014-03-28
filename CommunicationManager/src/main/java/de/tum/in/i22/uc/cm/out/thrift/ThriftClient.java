package de.tum.in.i22.uc.cm.out.thrift;

/**
 *
 * @author Florian Kelbert
 *
 */
public interface ThriftClient {
	public void connect() throws Exception;
	public void disconnect();
}
