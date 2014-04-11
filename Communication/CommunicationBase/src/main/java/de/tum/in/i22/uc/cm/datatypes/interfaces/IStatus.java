package de.tum.in.i22.uc.cm.datatypes;

public interface IStatus {
	public EStatus getEStatus();
	public String getErrorMessage();
	boolean isStatus(EStatus status);
}
