package de.tum.in.i22.uc.cm.datatypes.interfaces;

public interface ICondition {

	boolean tick();

	boolean getValueAtLastTick();

	void startSimulation();

	void stopSimulation();
}
