package de.tum.in.i22.pip2pip;

import java.util.Collection;

import de.tum.in.i22.uc.cm.datatypes.IContainer;
import de.tum.in.i22.uc.cm.datatypes.IData;
import de.tum.in.i22.uc.cm.datatypes.IEvent;
import de.tum.in.i22.uc.cm.datatypes.IStatus;

public interface IPip2Pip {
    public boolean hasData(Collection<IData> data);
    public boolean hasContainer(Collection<IContainer> container);
    public IStatus notifyEventToPip(IEvent event);
}
