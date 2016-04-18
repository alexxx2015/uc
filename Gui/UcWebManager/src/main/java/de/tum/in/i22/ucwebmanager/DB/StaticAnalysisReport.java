package de.tum.in.i22.ucwebmanager.DB;

public class StaticAnalysisReport {
	private int id, config_id;
	private String name,path;
	
	
	public StaticAnalysisReport(int id ,String name, String path, int app_id) {
		super();
		this.id = id;
		this.config_id = app_id;
		this.name = name;
		this.path = path;
	}
	
	public StaticAnalysisReport( String name, String path, int config_id) {
		super();
		this.config_id = config_id;
		this.name = name;
		this.path = path;
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}

	public int getConfig_id() {
		return config_id;
	}

	public void setConfig_id(int config_id) {
		this.config_id = config_id;
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
