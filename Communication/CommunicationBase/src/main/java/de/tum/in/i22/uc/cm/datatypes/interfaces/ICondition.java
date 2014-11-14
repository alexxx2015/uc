package de.tum.in.i22.uc.cm.datatypes.interfaces;

public interface ICondition {

	boolean tick(boolean endOfTimestep);

	void startSimulation();

	void stopSimulation();
}
