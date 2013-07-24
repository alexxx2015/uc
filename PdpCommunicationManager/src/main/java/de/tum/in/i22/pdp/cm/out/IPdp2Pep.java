package de.tum.in.i22.pdp.cm.out;

import java.util.List;

import de.tum.in.i22.pdp.datatypes.IEvent;
import de.tum.in.i22.pdp.gpb.PdpProtos.GpStatus.EStatus;

public interface IPdp2Pep {
	public EStatus execute(List<IEvent> event);
}
