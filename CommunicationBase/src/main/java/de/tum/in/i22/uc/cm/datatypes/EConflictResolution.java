package de.tum.in.i22.uc.cm.datatypes;

import de.tum.in.i22.uc.cm.gpb.PdpProtos.GpConflictResolutionFlag.GpEConflictResolution;

public enum EConflictResolution {
	OVERWRITE {
		@Override
		public GpEConflictResolution asGpEConflictResolution() {
			return GpEConflictResolution.OVERWRITE;
		}
	},
	IGNORE_UPDATES {
		@Override
		public GpEConflictResolution asGpEConflictResolution() {
			return GpEConflictResolution.IGNORE_UPDATES;
		}
	}, //currently not used
	KEEP_ALL {
		@Override
		public GpEConflictResolution asGpEConflictResolution() {
			return GpEConflictResolution.KEEP_ALL;
		}
	}; // currently not used
	
	
	public GpEConflictResolution asGpEConflictResolution() {
		return GpEConflictResolution.OVERWRITE;
	}
	
	public static EConflictResolution convertFromGpEConflictResolution(GpEConflictResolution gpEConflictResolution) {
		EConflictResolution[] values = EConflictResolution.values();
		EConflictResolution result = null;
		for (EConflictResolution value: values) {
			if (value.toString().equals(gpEConflictResolution.toString())) {
				result = value;
				break;
			}
		}
		return result;
	}
}
