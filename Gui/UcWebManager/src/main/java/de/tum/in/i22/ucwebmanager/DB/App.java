package de.tum.in.i22.ucwebmanager.DB;

public class App {
	
	private int id;
	private String name, status, hashCode;
	public App(int id,String name, String hashCode, String status){
		this.id = id;
		this.name = name;
		this.hashCode = hashCode;
		this.status = status;
	}
	public App(String name, String hashCode, String status){
		this.name = name;
		this.hashCode = hashCode;
		this.status = status;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	public String getHashCode() {
		return hashCode;
	}
	public void setHashCode(String hashCode) {
		this.hashCode = hashCode;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
}
