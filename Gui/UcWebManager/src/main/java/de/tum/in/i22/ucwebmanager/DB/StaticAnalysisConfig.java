package de.tum.in.i22.ucwebmanager.DB;

public class StaticAnalysisConfig {

	private int id, app_id;
	private String name,path;
	
	public StaticAnalysisConfig(int id, String name, String path, int app_id) {
		super();
		this.id = id;
		this.app_id = app_id;
		this.name = name;
		this.path = path;
	}
	
	public StaticAnalysisConfig( String name, String path, int app_id) {
		super();
		this.app_id = app_id;
		this.name = name;
		this.path = path;
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getApp_id() {
		return app_id;
	}
	public void setApp_id(int app_id) {
		this.app_id = app_id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	
}
