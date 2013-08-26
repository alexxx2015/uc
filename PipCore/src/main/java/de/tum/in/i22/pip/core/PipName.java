package de.tum.in.i22.pip.core;

public class PipName {
	/**
	 * the ID of the process, the representation belongs to (if representation
	 * is system-wide unique: PID = -1)
	 */
	public int _processId;

	/**
	 * the name = representation of a data container
	 */
	public String _dataContainerName;

	/**
	 * 
	 */
	public PipName(int processId, String dataContainerName) {
		this._processId = processId;
		this._dataContainerName = dataContainerName;
	}
	
	public String getDataContainerName() {
		return _dataContainerName;
	}
	
	public int getProcessId() {
		return _processId;
	}

}
