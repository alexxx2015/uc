//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.0 in JDK 1.6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2013.05.26 at 12:09:43 PM CEST 
//


package de.tum.in.i22.uc.ecajaxb;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * 
 *         Events have a name as well parameters.
 *         Classes of events are: usage, signalling, and other.
 *         A try event represents an attempted usage event by a user.
 * 		
 * 
 * <p>Java class for EventType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="EventType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="parameter" type="{http://www.iese.fhg.de/pef/1.0/event}EventParameterType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="action" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="isTry" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="signallerComponent" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="subject" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="target" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="timestamp" use="required" type="{http://www.iese.fhg.de/pef/1.0/time}TimestampType" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "EventType", namespace = "http://www.iese.fhg.de/pef/1.0/event", propOrder = {
    "parameter"
})
public class EventType {

    @XmlElement(namespace = "http://www.iese.fhg.de/pef/1.0/event")
    protected List<EventParameterType> parameter;
    @XmlAttribute(required = true)
    protected String action;
    @XmlAttribute(required = true)
    protected boolean tryEvent;
    @XmlAttribute
    protected String signallerComponent;
    @XmlAttribute
    protected String subject;
    @XmlAttribute
    protected String target;
    @XmlAttribute(required = true)
    protected XMLGregorianCalendar timestamp;

    /**
     * Gets the value of the parameter property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the parameter property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getParameter().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link EventParameterType }
     * 
     * 
     */
    public List<EventParameterType> getParameter() {
        if (parameter == null) {
            parameter = new ArrayList<EventParameterType>();
        }
        return this.parameter;
    }

    /**
     * Gets the value of the action property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAction() {
        return action;
    }

    /**
     * Sets the value of the action property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAction(String value) {
        this.action = value;
    }

    /**
     * Gets the value of the isTry property.
     * 
     */
    public boolean isIsTry() {
        return tryEvent;
    }

    /**
     * Sets the value of the isTry property.
     * 
     */
    public void setIsTry(boolean value) {
        this.tryEvent = value;
    }

    /**
     * Gets the value of the signallerComponent property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSignallerComponent() {
        return signallerComponent;
    }

    /**
     * Sets the value of the signallerComponent property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSignallerComponent(String value) {
        this.signallerComponent = value;
    }

    /**
     * Gets the value of the subject property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSubject() {
        return subject;
    }

    /**
     * Sets the value of the subject property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSubject(String value) {
        this.subject = value;
    }

    /**
     * Gets the value of the target property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTarget() {
        return target;
    }

    /**
     * Sets the value of the target property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTarget(String value) {
        this.target = value;
    }

    /**
     * Gets the value of the timestamp property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getTimestamp() {
        return timestamp;
    }

    /**
     * Sets the value of the timestamp property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setTimestamp(XMLGregorianCalendar value) {
        this.timestamp = value;
    }

}
