package de.tum.in.i22.ucwebmanager.DB;

public class App {
	public enum Dir{
		CODE	("code"),
		REPORT  ("staticAnalysis/reports"),
		CONFIG 	("staticAnalysis/configurations"),
		INSTRUMENTATION ("instrumentations"),
		RUNTIME ("runtime");
		private String dir;
		private Dir(String s){
			dir = s;
		}
		public String getDir(){
			return dir;
		}
	};
	private int id, hashCode;
	private String name, path, status;
	public App(int id,String name, int hashCode, String path, String status){
		this.id = id;
		this.name = name;
		this.hashCode = hashCode;
		this.path = path;
		this.status = status;
	}
	public App(String name, int hashCode, String path, String status){
		this.name = name;
		this.hashCode = hashCode;
		this.path = path;
		this.status = status;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getHashCode() {
		return hashCode;
	}
	public void setHashCode(int hashCode) {
		this.hashCode = hashCode;
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
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
}
