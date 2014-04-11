package de.tum.in.i22.uc.cm.factories;
import java.util.Map;

import de.tum.in.i22.uc.cm.datatypes.basic.PxpSpec;
import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic.EStatus;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IContainer;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IData;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IEvent;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;


public interface IMessageFactory {
	public IEvent createEvent(String name, Map<String, String> map);
	public IEvent createActualEvent(String name, Map<String, String> map);
	public IStatus createStatus(EStatus status);
	public IStatus createStatus(EStatus status, String errorMessage);
	public IContainer createContainer();
	public IData createData();
	public IData createData(String id);
	public IEvent createDesiredEvent(String name, Map<String, String> map);
	public IContainer createContainer(String classValue, String id);

	public PxpSpec createPxpSpec();
}
