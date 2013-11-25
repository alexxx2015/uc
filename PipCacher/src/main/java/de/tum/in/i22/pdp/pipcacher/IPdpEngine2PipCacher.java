package de.tum.in.i22.pdp.pipcacher;

import de.tum.in.i22.uc.cm.datatypes.IEvent;
import de.tum.in.i22.uc.cm.datatypes.IKey;

public interface IPdpEngine2PipCacher {
    public boolean    eval(IKey key);
    public boolean    eval(IKey key, IEvent event2Simulate);
    public String     getScopeId(IEvent event);

}
