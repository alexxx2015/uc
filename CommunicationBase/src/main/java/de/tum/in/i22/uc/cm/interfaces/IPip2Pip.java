package de.tum.in.i22.uc.cm.interfaces;

import java.util.Collection;

import de.tum.in.i22.uc.cm.datatypes.IContainer;
import de.tum.in.i22.uc.cm.datatypes.IData;
import de.tum.in.i22.uc.cm.datatypes.IEvent;
import de.tum.in.i22.uc.cm.datatypes.IName;
import de.tum.in.i22.uc.cm.datatypes.IStatus;

public interface IPip2Pip {
    public boolean hasAllData(Collection<IData> data);
    public boolean hasAnyData(Collection<IData> data);
    public boolean hasAllContainers(Collection<IContainer> container);
    public boolean hasAnyContainer(Collection<IContainer> container);
    public IStatus notifyActualEvent(IEvent event);
}
