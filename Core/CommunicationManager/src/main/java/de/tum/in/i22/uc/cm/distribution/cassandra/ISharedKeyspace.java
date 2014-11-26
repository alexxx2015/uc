package de.tum.in.i22.uc.cm.distribution.cassandra;

import de.tum.in.i22.uc.cm.datatypes.interfaces.AtomicOperator;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IData;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IOperator;
import de.tum.in.i22.uc.cm.distribution.IPLocation;

public interface ISharedKeyspace {

	int howOftenNotifiedSinceTimestep(AtomicOperator operator, long timestep);

	int howOftenNotifiedInBetween(AtomicOperator operator, long from, long to);

	int howOftenNotifiedAtTimestep(AtomicOperator operator, long timestep);

	boolean wasNotifiedInBetween(AtomicOperator operator, long from, long to);

	boolean wasNotifiedAtTimestep(AtomicOperator operator, long timestep);

	long getFirstTick(String mechanismName);

	void notify(IOperator op, boolean endOfTimestep);

	void addData(IData d, IPLocation dstLocation);

	boolean adjust(String name, IPLocation pmpLocation, boolean b);

	void setFirstTick(String mechanismName, long firstTick);

}
