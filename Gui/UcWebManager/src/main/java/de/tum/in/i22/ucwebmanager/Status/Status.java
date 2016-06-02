package de.tum.in.i22.ucwebmanager.Status;
public enum  Status {
	UNDEFINED		("Undefined"),
	NONE			("None"),
	STATICANALYSIS	("StaticAnalysis"),
	INSTRUMENTATION	("Instrumentation");
	private final String stage;
	private Status(final String stage){
		this.stage = stage;
	}
	
	public String getStage(){
		return stage;
	}
}
