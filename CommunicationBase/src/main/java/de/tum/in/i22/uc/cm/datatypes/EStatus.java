package de.tum.in.i22.uc.cm.datatypes;

import de.tum.in.i22.uc.cm.gpb.PdpProtos.GpStatus.GpEStatus;


public enum EStatus {
	OKAY {
		@Override
		public GpEStatus asGpEStatus() {
			return GpEStatus.OKAY;
		}
	},
	ERROR {
		@Override
		public GpEStatus asGpEStatus() {
			return GpEStatus.ERROR;
		}
	},
	INHIBIT {
		@Override
		public GpEStatus asGpEStatus() {
			return GpEStatus.INHIBIT;
		}
	},
	ALLOW {
		@Override
		public GpEStatus asGpEStatus() {
			return GpEStatus.ALLOW;
		}
	},
	MODIFY {
		@Override
		public GpEStatus asGpEStatus() {
			return GpEStatus.MODIFY;
		}
	},
	ERROR_EVENT_PARAMETER_MISSING {
		@Override
		public GpEStatus asGpEStatus() {
			return GpEStatus.ERROR_EVENT_PARAMETER_MISSING;
		}
	}
	;
	
	
	public GpEStatus asGpEStatus() {
		return GpEStatus.ERROR;
	}
	
	public static EStatus convertFromGpEStatus(GpEStatus gpEStatus) {
		EStatus[] values = EStatus.values();
		EStatus result = null;
		for (EStatus value: values) {
			if (value.toString().equals(gpEStatus.toString())) {
				result = value;
				break;
			}
		}
		return result;
	}
}
