package de.tum.in.i22.uc.cm.datatypes.basic;

public class PxpSpec {
	private final String ip;
	private final int port;
	private final String description;
	private final String id;

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
}
