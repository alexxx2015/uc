package de.tum.in.i22.uc.cm.basic;

import de.tum.in.i22.uc.cm.datatypes.EStatus;
import de.tum.in.i22.uc.cm.datatypes.IStatus;
import de.tum.in.i22.uc.cm.gpb.PdpProtos.GpStatus;
import de.tum.in.i22.uc.cm.gpb.PdpProtos.GpStatus.GpEStatus;

public class StatusBasic implements IStatus {
	private EStatus _eStatus = null;
	private String _errorMessage  = null;
	
	public StatusBasic() {
	}
	
	public StatusBasic(GpStatus gpStatus) {
		if (gpStatus.hasValue()) {
			GpEStatus gpEStatus = gpStatus.getValue();
			_eStatus = EStatus.convertFromGpEStatus(gpEStatus);
		}
		
		if (gpStatus.hasErrorMessage())
			_errorMessage = gpStatus.getErrorMessage();
	}
	
	public StatusBasic(EStatus eStatus, String errorMessage) {
		_eStatus = eStatus;
		_errorMessage = errorMessage;
	}
	
	public StatusBasic(EStatus eStatus) {
		this(eStatus, null);
	}

	public void seteStatus(EStatus eStatus) {
		_eStatus = eStatus;
	}
	
	@Override
	public EStatus getEStatus() {
		return _eStatus;
	}

	@Override
	public String getErrorMessage() {
		return _errorMessage;
	}
	
	/**
	 * 
	 * @param status
	 * @return Google Protocol Buffer object corresponding to IStatus
	 */
	public static GpStatus createGpbStatus(IStatus status) {
		GpStatus.Builder gpStatus = GpStatus.newBuilder();
		gpStatus.setValue(status.getEStatus().asGpEStatus());
		
		if (status.getErrorMessage() != null)
			gpStatus.setErrorMessage(status.getErrorMessage());
		return gpStatus.build();
	}
	
	@Override
	public boolean equals(Object obj) {
		boolean isEqual = false;
		if (obj != null && this.getClass() == obj.getClass()) {
			StatusBasic o = (StatusBasic)obj;
			isEqual = CompareUtil.areObjectsEqual(_errorMessage, o.getErrorMessage())
					&& CompareUtil.areObjectsEqual(_eStatus, o.getEStatus());
		}
		return isEqual;
	}

	@Override
	public String toString() {
		return "StatusBasic [_eStatus=" + _eStatus + ", _errorMessage="
				+ _errorMessage + "]";
	}
	
}
