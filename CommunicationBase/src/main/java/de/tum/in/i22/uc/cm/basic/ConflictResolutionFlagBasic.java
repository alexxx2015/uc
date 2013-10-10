package de.tum.in.i22.uc.cm.basic;

import de.tum.in.i22.uc.cm.datatypes.EConflictResolution;
import de.tum.in.i22.uc.cm.datatypes.IConflictResolutionFlag;
import de.tum.in.i22.uc.cm.gpb.PdpProtos.GpConflictResolutionFlag;
import de.tum.in.i22.uc.cm.gpb.PdpProtos.GpConflictResolutionFlag.GpEConflictResolution;

public class ConflictResolutionFlagBasic implements IConflictResolutionFlag {
	private EConflictResolution _eConflictResolution = null;
	
	public ConflictResolutionFlagBasic(GpConflictResolutionFlag gpConflictResolutionFlag) {
		if (gpConflictResolutionFlag.hasValue()) {
			GpEConflictResolution gpEConflictResolution = gpConflictResolutionFlag.getValue();
			_eConflictResolution = EConflictResolution.convertFromGpEConflictResolution(gpEConflictResolution);
		}
	}
	
	public ConflictResolutionFlagBasic(EConflictResolution value) {
		_eConflictResolution = value;
	}

	@Override
	public EConflictResolution getConflictResolution() {
		return _eConflictResolution;
	}
	
	public static GpConflictResolutionFlag createGpbConflictResolutionFlag(IConflictResolutionFlag flag) {
		GpConflictResolutionFlag.Builder gp = GpConflictResolutionFlag.newBuilder();
		gp.setValue(flag.getConflictResolution().asGpEConflictResolution());
		return gp.build();
	}
	
	@Override
	public boolean equals(Object obj) {
		boolean isEqual = false;
		if (obj != null && this.getClass() == obj.getClass()) {
			ConflictResolutionFlagBasic o = (ConflictResolutionFlagBasic)obj;
			isEqual = CompareUtil.areObjectsEqual(_eConflictResolution, o.getConflictResolution());
		}
		return isEqual;
	}

	@Override
	public String toString() {
		return "ConflictResolutionFlagBasic [_eConflictResolution="
				+ _eConflictResolution + "]";
	}
	
	
}
