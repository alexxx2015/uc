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
 * extended by Ciprian Lucaci
 */
public class XmlPolicy {
	private String _name;
	private String _xml;

	/**
	 * The natural language description with all the param. markers removed.
	 */
	private String _description;
	/**
	 * Id of the template from the configuration file or database of templates.
	 */
	private String _templateId;
	/**
	 * Each policy can have a template which is translated by the PTP 
	 * and instantiated by actual values and ECA rules.
	 */
	private String _templateXml;
	/**
	 * Each policy is applied to a particular data class.
	 */
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
	public void setName(String name){
		if(name == null)
			name = "";
		this._name = name;
	}
	
	public String getXml() {
		return _xml;
	}
	public void setXml(String xml){
		if(xml == null)
			xml = "";
		this._xml = xml;
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
	
	public XmlPolicy clone(){
		XmlPolicy clone = new XmlPolicy(_name, _xml, _description, _templateId, _templateXml, _dataClass);
		return clone;
	}
}
