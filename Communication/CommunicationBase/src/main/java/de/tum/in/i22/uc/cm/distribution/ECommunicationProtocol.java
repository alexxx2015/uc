package de.tum.in.i22.uc.cm.distribution;


public enum ECommunicationProtocol {
	THRIFT;

	public static ECommunicationProtocol from(String s) {
		if (s == null) {
			return null;
		}

		switch (s.toLowerCase()) {
			case "thrift":
				return THRIFT;
		}

		throw new RuntimeException("No such communication protocol: " + s);
	}
}
