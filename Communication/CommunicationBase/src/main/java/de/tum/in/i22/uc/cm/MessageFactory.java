package de.tum.in.i22.uc.cm;

import java.util.Map;

import de.tum.in.i22.uc.cm.basic.ContainerBasic;
import de.tum.in.i22.uc.cm.basic.DataBasic;
import de.tum.in.i22.uc.cm.basic.EventBasic;
import de.tum.in.i22.uc.cm.basic.PxpSpec;
import de.tum.in.i22.uc.cm.basic.StatusBasic;
import de.tum.in.i22.uc.cm.datatypes.EStatus;
import de.tum.in.i22.uc.cm.datatypes.IContainer;
import de.tum.in.i22.uc.cm.datatypes.IData;
import de.tum.in.i22.uc.cm.datatypes.IEvent;
import de.tum.in.i22.uc.cm.datatypes.IPxpSpec;
import de.tum.in.i22.uc.cm.datatypes.IStatus;

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
	public IContainer createContainer(String classValue, String id) {
		IContainer cont = new ContainerBasic(classValue, id);
		return cont;
	}

	@Override
	public IPxpSpec createPxpSpec() {
		// TODO Auto-generated method stub
		return new PxpSpec(null,0,null,null);
	}
}
