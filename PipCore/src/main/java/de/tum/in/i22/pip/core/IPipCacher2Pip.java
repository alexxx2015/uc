package de.tum.in.i22.pip.core;

import java.util.List;
import java.util.Map;

import de.tum.in.i22.pip.core.manager.IPipManager;
import de.tum.in.i22.uc.cm.datatypes.ICacheUpdate;
import de.tum.in.i22.uc.cm.datatypes.IEvent;
import de.tum.in.i22.uc.cm.datatypes.IKey;
import de.tum.in.i22.uc.cm.datatypes.IStatus;

public interface IPipCacher2Pip extends IPipManager {

    public IStatus        startSimulation();
    public IStatus        stopSimulation();
    public ICacheUpdate   update(IEvent e);
    public IStatus        addPredicates(Map<IKey,String> predicates);
    public IStatus        revokePredicates(List<IKey> keys);
}
