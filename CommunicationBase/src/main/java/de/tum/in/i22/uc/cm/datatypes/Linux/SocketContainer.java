package de.tum.in.i22.uc.cm.datatypes.Linux;

import de.tum.in.i22.uc.cm.basic.ContainerBasic;

public class SocketContainer extends ContainerBasic {
	private Domain _domain;
	private Type _type;
	
	public SocketContainer(Domain domain, Type type) {
		super();
		this._domain = domain;
		this._type = type;
	}
	
	public Domain getDomain() {
		return _domain;
	}
	
	public Type getType() {
		return _type;
	}

	public enum Domain {
		AF_INET,
		AF_INET6,
		AF_UNIX,
		AF_LOCAL;
		
		public static Domain from(String s) {
			switch (s) {
				case "AF_INET":
				case "PF_INET":
					return AF_INET;
				case "AF_INET6":
				case "PF_INET6":
					return AF_INET6;
				case "AF_UNIX":
				case "PF_UNIX":
					return AF_UNIX;
				case "AF_LOCAL":
				case "PF_LOCAL":
					return AF_LOCAL;
				default: 
					return null;
			}
		}
	}
	
	public enum Type {
		SOCK_STREAM,
		SOCK_DGRAM,
		SOCK_RAW;
		
		public static Type from(String s) {
			switch(s) {
				case "SOCK_STREAM":
					return SOCK_STREAM;
				case "SOCK_DGRAM":
					return SOCK_DGRAM;
				case "SOCK_RAW":
					return SOCK_RAW;
				default:
					return null;
			}
		}
	}
}
