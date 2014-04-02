package de.tum.in.i22.uc.cm.basic;

import java.util.Objects;

import de.tum.in.i22.uc.cm.datatypes.EConflictResolution;
import de.tum.in.i22.uc.cm.datatypes.IConflictResolutionFlag;

public class ConflictResolutionFlagBasic implements IConflictResolutionFlag {
	private EConflictResolution _eConflictResolution = null;

	public ConflictResolutionFlagBasic(EConflictResolution value) {
		_eConflictResolution = value;
	}

	@Override
	public EConflictResolution getConflictResolution() {
		return _eConflictResolution;
	}


	@Override
	public boolean equals(Object obj) {
		boolean isEqual = false;
		if (obj instanceof ConflictResolutionFlagBasic) {
			isEqual = Objects.equals(_eConflictResolution, ((ConflictResolutionFlagBasic) obj).getConflictResolution());
		}
		return isEqual;
	}

	@Override
	public int hashCode() {
		return Objects.hash(_eConflictResolution);
	}

	@Override
	public String toString() {
		return com.google.common.base.Objects.toStringHelper(this)
				.add("_eConflictResolution", _eConflictResolution)
				.toString();
	}


}