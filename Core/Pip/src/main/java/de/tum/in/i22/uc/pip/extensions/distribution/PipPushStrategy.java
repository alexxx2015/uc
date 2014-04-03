package de.tum.in.i22.uc.pip.extensions.distribution;

import java.util.Set;

import de.tum.in.i22.uc.cm.basic.StatusBasic;
import de.tum.in.i22.uc.cm.client.PipClientHandler;
import de.tum.in.i22.uc.cm.datatypes.EStatus;
import de.tum.in.i22.uc.cm.datatypes.IContainer;
import de.tum.in.i22.uc.cm.datatypes.IData;
import de.tum.in.i22.uc.cm.datatypes.IEvent;
import de.tum.in.i22.uc.cm.datatypes.IName;
import de.tum.in.i22.uc.cm.datatypes.IStatus;
import de.tum.in.i22.uc.cm.distribution.Location;
import de.tum.in.i22.uc.cm.distribution.pip.EDistributedPipStrategy;
import de.tum.in.i22.uc.thrift.ThriftConverter;

public class PipPushStrategy extends DistributedPipStrategy {

	public PipPushStrategy(EDistributedPipStrategy eStrategy) {
		super(eStrategy);
	}

	@Override
	public boolean hasAllData(Location location, Set<IData> data) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean hasAnyData(Location location, Set<IData> data) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean hasAllContainers(Location location, Set<IContainer> containers) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean hasAnyContainer(Location location, Set<IContainer> containers) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public IStatus initialRepresentation(Location location, IName containerName, Set<IData> data) {
//		Pip2PipImp pip2pip = new Pip2PipImp(connector);
//
//		try {
//			ConnectionManager.MAIN.obtain(pip2pip);
//		} catch (IOException e) {
//			return new StatusBasic(EStatus.ERROR, "Unable to connect to " + pip2pip);
//		}
//
////		pip2pip.notifyDataTransfer(containerName, data);
//		System.out.println("XXXXXXXXXXXXXX Notify " + containerName);
//
//		ConnectionManager.MAIN.release(pip2pip);

		PipClientHandler<?> pip = _clientHandlerFactory.createPipClientHandler(location);
		try {
			pip.connect();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		pip.initialRepresentation(containerName, data);
		pip.disconnect();

		return new StatusBasic(EStatus.OKAY);
	}

	@Override
	public IStatus notifyActualEvent(Location location, IEvent event) {
//		Pip2PipImp pip2pip = new Pip2PipImp(connector);
//
//		try {
//			ConnectionManager.MAIN.obtain(pip2pip);
//		} catch (IOException e) {
//			return new StatusBasic(EStatus.ERROR, "Unable to connect to " + pip2pip);
//		}
//
////		pip2pip.notifyActualEvent(event);
//		System.out.println("XXXXXXXXXXXXXX Notify " +event);
//
//		ConnectionManager.MAIN.release(pip2pip);
//
		return new StatusBasic(EStatus.OKAY);
	}
}
