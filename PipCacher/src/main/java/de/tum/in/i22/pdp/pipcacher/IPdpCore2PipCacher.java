package de.tum.in.i22.pdp.pipcacher;

import java.util.Map;
import java.util.Set;

import de.tum.in.i22.uc.cm.datatypes.IEvent;
import de.tum.in.i22.uc.cm.datatypes.IKey;
import de.tum.in.i22.uc.cm.datatypes.IStatus;

public interface IPdpCore2PipCacher {
    public IStatus     refresh(IEvent desiredEvent);
    public IStatus     addPredicates(Map<String,IKey> predicates);
    public IStatus     revokePredicates(Set<IKey> keys);

}
