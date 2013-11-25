package de.tum.in.i22.pdp.pipcacher;

import java.util.List;
import java.util.Map;

import de.tum.in.i22.uc.cm.datatypes.IEvent;
import de.tum.in.i22.uc.cm.datatypes.IKey;
import de.tum.in.i22.uc.cm.datatypes.IStatus;

public interface IPdpCore2PipCacher {
    public IStatus     refresh(IEvent desiredEvent);
    public IStatus     addPredicates(Map<IKey,String> predicates);
    public IStatus     revokePredicates(List<IKey> keys);

}
