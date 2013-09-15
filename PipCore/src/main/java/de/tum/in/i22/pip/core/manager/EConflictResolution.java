package de.tum.in.i22.pip.core.manager;

public enum EConflictResolution {
	OVERWRITE, // a (a' b) -> (a' b) - overwrite all
	
	// keep this flags but do not use them
	IGNORE_UPDATES, // a (a' b) -> (a b)
	
	
	KEEP_ALL, // a (a' b) -> (a a' b)
}
