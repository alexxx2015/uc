package de.tum.in.i22.ucwebmanager.Status;
public enum  Status {
	UNDEFINED		("Undefined"),
	NONE			("None"),
	STATISTIC		("Statistic"),
	INSTRUMENTATION	("Instrumentation");
	private final String stage;
	private Status(final String stage){
		this.stage = stage;
	}
	
	public String getStage(){
		return stage;
	}
}
