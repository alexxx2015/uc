package de.tum.in.i22.pdp.core;

import de.tum.in.i22.pdp.pipcacher.IPdpCore2PipCacher;
import de.tum.in.i22.pdp.pipcacher.IPdpEngine2PipCacher;
import de.tum.in.i22.uc.cm.datatypes.IStatus;
/***
 * we use this interface to pass the pip-reference (via pipcahcer)to the pdp
 * @author uc
 *
 */
public interface IPdpConf {
	public IStatus setPdpCore2PipCacher (IPdpCore2PipCacher core2cacher);
	public IStatus setPdpEngine2PipCacher (IPdpEngine2PipCacher engine2cacher);
}
