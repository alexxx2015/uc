package de.tum.in.i22.uc.cm.datatypes;

import de.tum.in.i22.uc.cm.gpb.PdpProtos.GpStatus.GpEStatus;


public enum EStatus {
	ERROR1 {
		@Override
		public GpEStatus asGpEStatus() {
			return GpEStatus.ERROR1;
		}
	},
	OKAY {
		@Override
		public GpEStatus asGpEStatus() {
			return GpEStatus.OKAY;
		}
	},
	ERROR2 {
		@Override
		public GpEStatus asGpEStatus() {
			return GpEStatus.ERROR2;
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
	};
	
	
	public GpEStatus asGpEStatus() {
		return GpEStatus.ERROR1;
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
