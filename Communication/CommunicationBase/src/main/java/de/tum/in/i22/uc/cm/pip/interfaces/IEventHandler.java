package de.tum.in.i22.uc.cm.pip.interfaces;

import de.tum.in.i22.uc.cm.datatypes.interfaces.IEvent;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.cm.distribution.IDistributionManager;
import de.tum.in.i22.uc.cm.pip.ifm.IAnyInformationFlowModel;

public interface IEventHandler {
	public IStatus performUpdate();

	/**
	 * Sets the event of this event handler and returns the event handler
	 * @param event the event to set
	 * @return the event handler itself
	 */
	public IEventHandler setEvent(IEvent event);

	public void setInformationFlowModel(IAnyInformationFlowModel ifm);

	/**
	 * Resets the event, i.e. all of its attributes, such that
	 * the object can be reused.
	 */
	public void reset();

	public void setDistributionManager(IDistributionManager _distributionManager);
}
