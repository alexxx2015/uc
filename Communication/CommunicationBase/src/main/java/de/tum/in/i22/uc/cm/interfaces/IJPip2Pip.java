package de.tum.in.i22.uc.cm.interfaces;

import java.util.Map;
import java.util.Set;

import de.tum.in.i22.uc.cm.datatypes.interfaces.IContainer;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IData;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IEvent;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IName;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.cm.distribution.Location;
import de.tum.in.i22.uc.thrift.generator.AThriftMethod;
import de.tum.in.i22.uc.thrift.generator.AThriftService;

/**
 * Interface defining methods a Java PIP can invoke on a PIP.
 * @author Lovat
 *
 */
@AThriftService(name="TJPip2Pip")
public interface IJPip2Pip {
	@AThriftMethod(signature="Types.TStatus addListener(1:string ip, 2:i32 port, 3:string id, 4:string filter)")
	public IStatus addListener(String ip, int port, String id, String filter);
	
	@AThriftMethod(signature="Types.TStatus setUpdateFrequency(1:i32 msec, 2:string id)")
	public IStatus setUpdateFrequency(int msec, String id);
}
