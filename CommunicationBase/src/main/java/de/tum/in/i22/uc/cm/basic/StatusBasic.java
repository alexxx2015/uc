package de.tum.in.i22.uc.cm.basic;

import java.util.Objects;

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
		if (obj instanceof StatusBasic) {
			StatusBasic o = (StatusBasic)obj;
			isEqual = Objects.equals(_errorMessage, o._errorMessage)
					&& Objects.equals(_eStatus, o._eStatus);
		}
		return isEqual;
	}

	@Override
	public int hashCode() {
		return Objects.hash(_eStatus, _errorMessage);
	}

	@Override
	public boolean isSameStatus(IStatus status) {
		boolean isEqual = false;
		if (status instanceof StatusBasic) {
			StatusBasic o = (StatusBasic)status;
			isEqual = Objects.equals(_eStatus, o.getEStatus());
		}
		return isEqual;
	}



	@Override
	public String toString() {
		return com.google.common.base.Objects.toStringHelper(this)
				.add("_eStatus", _eStatus)
				.add("_errorMessage", _errorMessage)
				.toString();
	}

	@Override
	public void setErrorMessage(String error) {
		_errorMessage=error;
	}

}
