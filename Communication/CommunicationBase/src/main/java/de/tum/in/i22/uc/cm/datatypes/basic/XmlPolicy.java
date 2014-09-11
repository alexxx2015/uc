package de.tum.in.i22.uc.cm.datatypes.basic;

import java.util.Objects;

/**
 * This class represents a XML policy. The intuition behind this class
 * was to not pass around strings whenever we talk about an XML policy,
 * in order to get some kind of type safety. With the help of this class,
 * we actually know that we talk about a xml policy (which we wouldn't know when
 * passing around strings).
 *
 * @author Florian Kelbert
 *
 */
public class XmlPolicy {
	private final String _name;
	private final String _xml;

	private String _description;
	private String _templateId;
	private String _templateXml;
	private String _dataClass;
	
	public XmlPolicy(String name, String xml) {
		if (name == null || xml == null) {
			throw new RuntimeException("Parameters might not be null.");
		}

		_name = name;
		_xml = xml;
		_description = "";
		_templateId = "";
		_templateXml = "";
		_dataClass = "";
	}
	
	public XmlPolicy(String name, String xml, String description, String templateId, String templateXML, String dataClass) {
		this(name,xml);
		_description = description;
		_templateId = templateId;
		_templateXml = templateXML;
		_dataClass = dataClass;
	}
	
	public String getName() {
		return _name;
	}

	public String getXml() {
		return _xml;
	}

	
	public String getDescription() {
		return _description;
	}

	public void setDescription(String description) {
		if(description == null)
			description = "";
		this._description = description;
	}

	public String getTemplateId() {
		return _templateId;
	}

	public void setTemplateId(String templateId) {
		if(templateId == null)
			templateId = "";
		this._templateId = templateId;
	}

	public String getTemplateXml() {
		return _templateXml;
	}

	public void setTemplateXml(String templateXml) {
		if(templateXml == null)
			templateXml = "";
		this._templateXml = templateXml;
	}

	public String getDataClass() {
		return _dataClass;
	}

	public void setDataClass(String dataClass) {
		if(dataClass == null)
			dataClass = "";
		this._dataClass = dataClass;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof XmlPolicy) {
			XmlPolicy other = (XmlPolicy) obj;
			return Objects.equals(_name, other._name)
					&& Objects.equals(_xml, other._xml);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return _name.hashCode();
	}

	@Override
	public String toString() {
		return _name;
	}
}
