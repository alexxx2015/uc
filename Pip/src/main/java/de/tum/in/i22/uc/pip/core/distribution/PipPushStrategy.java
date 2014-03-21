package de.tum.in.i22.uc.pip.core.distribution;

import java.util.Collection;

import de.tum.in.i22.uc.cm.basic.StatusBasic;
import de.tum.in.i22.uc.cm.datatypes.EStatus;
import de.tum.in.i22.uc.cm.datatypes.IContainer;
import de.tum.in.i22.uc.cm.datatypes.IData;
import de.tum.in.i22.uc.cm.datatypes.IEvent;
import de.tum.in.i22.uc.cm.datatypes.IName;
import de.tum.in.i22.uc.cm.datatypes.IStatus;
import de.tum.in.i22.uc.cm.out.Connector;
import de.tum.in.i22.uc.distribution.pip.EDistributedPipStrategy;

public class PipPushStrategy extends AbstractPipStrategy {

	public PipPushStrategy(EDistributedPipStrategy eStrategy) {
		super(eStrategy);
	}

	@Override
	public boolean hasAllData(Connector connector, Collection<IData> data) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean hasAnyData(Connector connector, Collection<IData> data) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean hasAllContainers(Connector connector,
			Collection<IContainer> containers) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean hasAnyContainer(Connector connector,
			Collection<IContainer> containers) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public IStatus notifyDataTransfer(Connector connector, IName containerName, Collection<IData> data) {
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
//
		return new StatusBasic(EStatus.OKAY);
	}

	@Override
	public IStatus notifyActualEvent(Connector connector, IEvent event) {
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
