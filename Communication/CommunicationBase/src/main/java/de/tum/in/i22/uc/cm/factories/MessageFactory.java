package de.tum.in.i22.uc.cm.factories;

import java.util.Map;

import de.tum.in.i22.uc.cm.datatypes.basic.ContainerBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.DataBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.EventBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.PxpSpec;
import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic.EStatus;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IContainer;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IData;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IEvent;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;

/**
 * MessageFactory
 * @author Stoimenov
 *
 */
public class MessageFactory implements IMessageFactory {

	@Override
	public IEvent createEvent(String name, Map<String, String> map) {
		IEvent e =  new EventBasic(name, map);
		return e;
	}

	@Override
	public IEvent createActualEvent(String name, Map<String, String> map) {
		IEvent e =  new EventBasic(name, map, true);
		return e;
	}

	@Override
	public IEvent createDesiredEvent(String name, Map<String, String> map) {
		IEvent e =  new EventBasic(name, map, false);
		return e;
	}

	@Override
	public IContainer createContainer() {
		IContainer container = new ContainerBasic();
		return container;
	}

	@Override
	public IData createData() {
		IData data = new DataBasic();
		return data;
	}

	@Override
	public IData createData(String id) {
		IData data = new DataBasic(id);
		return data;
	}


	@Override
	public IStatus createStatus(EStatus status) {
		return new StatusBasic(status);
	}

	@Override
	public IStatus createStatus(EStatus status, String errorMessage) {
		return new StatusBasic(status, errorMessage);
	}

	@Override
	public IContainer createContainer(String id) {
		return new ContainerBasic(id);
	}

	@Override
	public PxpSpec createPxpSpec() {
		// TODO Auto-generated method stub
		return new PxpSpec(null,0,null,null);
	}
}
