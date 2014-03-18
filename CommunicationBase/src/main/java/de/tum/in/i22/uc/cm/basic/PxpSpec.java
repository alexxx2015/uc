package de.tum.in.i22.uc.cm.basic;

import de.tum.in.i22.uc.cm.datatypes.IPxpSpec;

public class PxpSpec implements IPxpSpec {
	private String ip;
	private int port;
	private String description;
	private String id;
	
	public PxpSpec(String ip, int port, String id, String description){
		this.id = id;
		this.ip = ip;
		this.description = description;
		this.port = port;
	}

	public String getIp() {
		return ip;
	}

	public int getPort() {
		return port;
	}

	public String getDescription() {
		return description;
	}

	public String getId() {
		return id;
	}
	
	

//	@Override
//	public void setIp(String ip) {
//		// TODO Auto-generated method stub
//		this.ip = ip;
//	}
//
//	@Override
//	public void setPort(int port) {
//		// TODO Auto-generated method stub
//		this.port = port;
//	}
//
//	@Override
//	public void setDesc(String description) {
//		// TODO Auto-generated method stub
//		this.description = description;
//	}
//
//	@Override
//	public void setId(String id) {
//		// TODO Auto-generated method stub
//		this.id = id;
//	}

}
