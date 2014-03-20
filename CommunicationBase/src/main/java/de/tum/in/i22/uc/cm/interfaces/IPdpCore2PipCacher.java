package de.tum.in.i22.uc.cm.interfaces;

import java.util.Map;
import java.util.Set;

import de.tum.in.i22.uc.cm.datatypes.IEvent;
import de.tum.in.i22.uc.cm.datatypes.IKey;
import de.tum.in.i22.uc.cm.datatypes.IStatus;

public interface IPdpCore2PipCacher {
    /**
     * If @param event is a desired event, simulates the new state in the PIP, update the cache, and then revert.
     * If @param event is an actual event, does the same, but the PIP remains in the new state.
     * @param event
     * @return
     */
	public IStatus     refresh(IEvent event);
    public IStatus     addPredicates(Map<String,IKey> predicates);
    public IStatus     revokePredicates(Set<IKey> keys);

}
