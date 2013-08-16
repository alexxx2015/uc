package de.tum.in.i22.pdp.cm.out;

import java.util.List;

import de.tum.in.i22.uc.cm.datatypes.IEvent;
import de.tum.in.i22.uc.cm.datatypes.IStatus;

public interface IPdp2Pep {
	public IStatus execute(List<IEvent> event);
}
