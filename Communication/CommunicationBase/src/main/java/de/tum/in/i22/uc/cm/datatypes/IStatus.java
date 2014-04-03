package de.tum.in.i22.uc.cm.datatypes;

public interface IStatus {
	public EStatus getEStatus();
	public String getErrorMessage();
	public void setErrorMessage(String error);
	boolean isSameStatus(EStatus status);
}
