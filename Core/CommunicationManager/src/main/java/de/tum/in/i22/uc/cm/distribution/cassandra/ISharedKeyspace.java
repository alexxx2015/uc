package de.tum.in.i22.uc.cm.distribution.cassandra;

import de.tum.in.i22.uc.cm.datatypes.interfaces.AtomicOperator;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IData;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IOperator;
import de.tum.in.i22.uc.cm.distribution.IPLocation;

public interface ISharedKeyspace {

	int howOftenNotifiedSinceTimestep(AtomicOperator operator, long timestep);

	int howOftenNotifiedAtTimestep(AtomicOperator operator, long timestep);

	boolean wasNotifiedAtTimestep(AtomicOperator operator, long timestep);

	long getFirstTick(String mechanismName);

	void notify(IOperator op, boolean endOfTimestep);

	void addData(IData d, IPLocation dstLocation);

	void setFirstTick(String mechanismName, long firstTick);

	boolean diminishBy(IPLocation location);

	boolean enlargeBy(IPLocation location);

}
