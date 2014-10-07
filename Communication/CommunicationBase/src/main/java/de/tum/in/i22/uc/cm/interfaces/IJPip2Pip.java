package de.tum.in.i22.uc.cm.interfaces;

import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.thrift.generator.AThriftMethod;
import de.tum.in.i22.uc.thrift.generator.AThriftService;

/**
 * Interface defining methods a Java PIP can invoke on a PIP.
 * @author Lovat
 *
 */
@AThriftService(name="TJPip2Pip")
public interface IJPip2Pip {
	@AThriftMethod(signature="Types.TStatus addJPIPListener(1: string ip, 2: Types.int port, 3:string id, 4:string filter)")
	public IStatus addJPIPListener(String ip, int port, String id, String filter);
	
	@AThriftMethod(signature="Types.TStatus setUpdateFrequency(1: Types.int msec, 2:string id)")
	public IStatus setUpdateFrequency(int msec, String id);
}
