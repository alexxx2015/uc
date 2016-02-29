package de.tum.in.i22.uc.thrift.client;

import de.tum.in.i22.uc.cm.distribution.IPLocation;
import de.tum.in.i22.uc.cm.distribution.Location;
import de.tum.in.i22.uc.cm.distribution.client.Any2PdpClient;
import de.tum.in.i22.uc.cm.distribution.client.Any2PipClient;
import de.tum.in.i22.uc.cm.distribution.client.Any2PmpClient;
import de.tum.in.i22.uc.cm.distribution.client.Prp2PrpClient;
import de.tum.in.i22.uc.cm.distribution.client.Any2PxpClient;
import de.tum.in.i22.uc.cm.distribution.client.Dmp2DmpClient;
import de.tum.in.i22.uc.cm.distribution.client.JPip2PipClient;
import de.tum.in.i22.uc.cm.distribution.client.Pdp2PepClient;import de.tum.in.i22.uc.cm.distribution.client.Pdp2PipClient;
import de.tum.in.i22.uc.cm.distribution.client.Pdp2PxpClient;
import de.tum.in.i22.uc.cm.distribution.client.Pep2PdpClient;
import de.tum.in.i22.uc.cm.distribution.client.Pep2PipClient;
import de.tum.in.i22.uc.cm.distribution.client.Pip2JPipClient;
import de.tum.in.i22.uc.cm.distribution.client.Pip2PipClient;
import de.tum.in.i22.uc.cm.distribution.client.Pmp2PdpClient;
import de.tum.in.i22.uc.cm.distribution.client.Pmp2PipClient;
import de.tum.in.i22.uc.cm.distribution.client.Pmp2PmpClient;
import de.tum.in.i22.uc.cm.distribution.client.Pxp2PdpClient;
import de.tum.in.i22.uc.cm.factories.IClientFactory;


public class ThriftClientFactory implements IClientFactory {

	@Override
	public Any2PdpClient createAny2PdpClient(Location location) {
		if (location instanceof IPLocation) {
			return new ThriftAny2PdpClient((IPLocation) location);
		}
		throw new RuntimeException("Thrift client depends on " + IPLocation.class + ", but got " + location.getClass());
	}

	@Override
	public Any2PipClient createAny2PipClient(Location location) {
		if (location instanceof IPLocation) {
			return new ThriftAny2PipClient((IPLocation) location);
		}
		throw new RuntimeException("Thrift client depends on " + IPLocation.class + ", but got " + location.getClass());
	}

	@Override
	public Any2PmpClient createAny2PmpClient(Location location) {
		if (location instanceof IPLocation) {
			return new ThriftAny2PmpClient((IPLocation) location);
		}
		throw new RuntimeException("Thrift client depends on " + IPLocation.class + ", but got " + location.getClass());
	}

	@Override
	public Any2PxpClient createAny2PxpClient(Location location) {
		if (location instanceof IPLocation) {
			return new ThriftAny2PxpClient((IPLocation) location);
		}
		throw new RuntimeException("Thrift client depends on " + IPLocation.class + ", but got " + location.getClass());
	}

	@Override
	public JPip2PipClient createJPip2PipClient(Location location) {
		if (location instanceof IPLocation) {
			return new ThriftJPip2PipClient((IPLocation) location);
		}
		throw new RuntimeException("Thrift client depends on " + IPLocation.class + ", but got " + location.getClass());
	}


	@Override
	public Pip2JPipClient createPip2JPipClient(Location location) {
		if (location instanceof IPLocation) {
			return new ThriftPip2JPipClient((IPLocation) location);
		}
		throw new RuntimeException("Thrift client depends on " + IPLocation.class + ", but got " + location.getClass());
	}
	
	
	@Override
	public Pep2PipClient createPep2PipClient(Location location) {
		if (location instanceof IPLocation) {
			return new ThriftPep2PipClient((IPLocation) location);
		}
		throw new RuntimeException("Thrift client depends on " + IPLocation.class + ", but got " + location.getClass());
	}

	@Override
	public Pdp2PipClient createPdp2PipClient(Location location) {
		if (location instanceof IPLocation) {
			return new ThriftPdp2PipClient((IPLocation) location);
		}
		throw new RuntimeException("Thrift client depends on " + IPLocation.class + ", but got " + location.getClass());
	}

	@Override
	public Pip2PipClient createPip2PipClient(Location location) {
		if (location instanceof IPLocation) {
			return new ThriftPip2PipClient((IPLocation) location);
		}
		throw new RuntimeException("Thrift client depends on " + IPLocation.class + ", but got " + location.getClass());
	}

	@Override
	public Pmp2PipClient createPmp2PipClient(Location location) {
		if (location instanceof IPLocation) {
			return new ThriftPmp2PipClient((IPLocation) location);
		}
		throw new RuntimeException("Thrift client depends on " + IPLocation.class + ", but got " + location.getClass());
	}

	@Override
	public Pep2PdpClient createPep2PdpClient(Location location) {
		if (location instanceof IPLocation) {
			return new ThriftPep2PdpClient((IPLocation) location);
		}
		throw new RuntimeException("Thrift client depends on " + IPLocation.class + ", but got " + location.getClass());
	}

	@Override
	public Pmp2PdpClient createPmp2PdpClient(Location location) {
		if (location instanceof IPLocation) {
			return new ThriftPmp2PdpClient((IPLocation) location);
		}
		throw new RuntimeException("Thrift client depends on " + IPLocation.class + ", but got " + location.getClass());
	}

	@Override
	public Pmp2PmpClient createPmp2PmpClient(Location location) {
		if (location instanceof IPLocation) {
			return new ThriftPmp2PmpClient((IPLocation) location);
		}
		throw new RuntimeException("Thrift client depends on " + IPLocation.class + ", but got " + location.getClass());
	}

	@Override
	public Pdp2PxpClient createPdp2PxpClient(Location location) {
		if (location instanceof IPLocation) {
			return new ThriftPdp2PxpClient((IPLocation) location);
		}
		throw new RuntimeException("Thrift client depends on " + IPLocation.class + ", but got " + location.getClass());
	}

	@Override
	public Pxp2PdpClient createPxp2PdpClient(Location location) {
		if (location instanceof IPLocation) {
			return new ThriftPxp2PdpClient((IPLocation) location);
		}
		throw new RuntimeException("Thrift client depends on " + IPLocation.class + ", but got " + location.getClass());
	}

	@Override
	public Pdp2PepClient createPdp2PepClient(Location location) {
		if (location instanceof IPLocation) {
			return new ThriftPdp2PepClient((IPLocation) location);
		}
		throw new RuntimeException("Thrift client depends on " + IPLocation.class + ", but got " + location.getClass());
	}

	@Override
	public Dmp2DmpClient createDmp2DmpClient(Location location) {
		if (location instanceof IPLocation) {
			return new ThriftDmp2DmpClient((IPLocation) location);
		}
		throw new RuntimeException("Thrift client depends on " + IPLocation.class + ", but got " + location.getClass());
	}

	@Override
	public Prp2PrpClient createAny2PrpClient(Location location) {
		// TODO Auto-generated method stub
		if (location instanceof IPLocation) {
			return new ThriftPrp2PrpClient((IPLocation) location);
		}
		throw new RuntimeException("Thrift client depends on " + IPLocation.class + ", but got " + location.getClass());
	}
}
