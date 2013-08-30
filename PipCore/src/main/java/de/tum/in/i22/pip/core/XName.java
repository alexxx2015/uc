package de.tum.in.i22.pip.core;

public interface XName {
	public void updateInformationFlowSemantics(LayerOfAbstraction layerOfAbstraction, jar file, flagForTheConflictResolution);
	IGNORE_UPDATES,
	OVERWRITE,
	KEEP_ALL,
	TAKE_NEWEST,
	TAKE_OLDEST
}	
