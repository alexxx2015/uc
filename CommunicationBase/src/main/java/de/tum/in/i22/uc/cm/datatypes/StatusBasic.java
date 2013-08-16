package de.tum.in.i22.uc.cm.datatypes;

import de.tum.in.i22.uc.cm.gpb.PdpProtos.GpStatus;
import de.tum.in.i22.uc.cm.gpb.PdpProtos.GpStatus.GpEStatus;

public class StatusBasic implements IStatus {
	private EStatus _eStatus;
	private String _errorMessage;
	
	public StatusBasic(GpStatus gpStatus) {
		GpEStatus gpEStatus = gpStatus.getValue();
		_eStatus = EStatus.convertFromGpEStatus(gpEStatus);
		_errorMessage = gpStatus.getErrorMessage();
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
		gpStatus.setErrorMessage(status.getErrorMessage());
		return gpStatus.build();
	}
	
}
