package de.tum.in.i22.uc.cm.datatypes.basic;

import de.tum.in.i22.uc.cm.datatypes.interfaces.IPolicy;

public class XmlPolicy implements IPolicy {
	private final String _name;
	private final String _xml;

	public XmlPolicy(String name, String xml) {
		_name = name;
		_xml = xml;
	}

	public String getName() {
		return _name;
	}

	public String getXml() {
		return _xml;
	}
}
