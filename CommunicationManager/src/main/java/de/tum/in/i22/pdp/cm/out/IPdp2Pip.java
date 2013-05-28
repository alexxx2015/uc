package de.tum.in.i22.pdp.cm.out;

import java.util.List;

import de.tum.in.i22.pdp.datatypes.IContainer;
import de.tum.in.i22.pdp.datatypes.IData;

public interface IPdp2Pip {
	public boolean evaluatePredicate(String predicate);
	public List<IContainer> getContainerForData(IData data);
	public List<IData> getDataInContainer(IContainer container);
}
