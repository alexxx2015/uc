package de.tum.in.i22.ucwebmanager.DB;

public class Instrument {
	private int id, report_id;
	private String name;
	
	public Instrument(int id, String name,  int report_id) {
		super();
		this.id = id;
		this.report_id = report_id;
		this.name = name;
	}
	public Instrument(String name, int report_id) {
		super();
		this.report_id = report_id;
		this.name = name;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getReport_id() {
		return report_id;
	}
	public void setReport_id(int report_id) {
		this.report_id = report_id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
}
