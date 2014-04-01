package de.tum.in.i22.uc.cm.interfaces;

import java.util.Set;

import de.tum.in.i22.uc.cm.datatypes.IContainer;
import de.tum.in.i22.uc.cm.datatypes.IData;
import de.tum.in.i22.uc.cm.datatypes.IEvent;
import de.tum.in.i22.uc.cm.datatypes.IStatus;

public interface IPip2Pip {
    public boolean hasAllData(Set<IData> data);
    public boolean hasAnyData(Set<IData> data);
    public boolean hasAllContainers(Set<IContainer> container);
    public boolean hasAnyContainer(Set<IContainer> container);
    public IStatus notifyActualEvent(IEvent event);
}
