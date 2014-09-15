//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.0 in JDK 1.6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2013.05.26 at 12:09:43 PM CEST 
//


package de.tum.in.i22.uc.ecajaxb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for AuthorizationActionType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AuthorizationActionType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice>
 *         &lt;element name="allow" type="{http://www.iese.fhg.de/pef/1.0/enforcementLanguage}AuthorizationAllowType"/>
 *         &lt;element name="inhibit" type="{http://www.iese.fhg.de/pef/1.0/enforcementLanguage}AuthorizationInhibitType"/>
 *       &lt;/choice>
 *       &lt;attribute name="fallback" type="{http://www.w3.org/2001/XMLSchema}string" default="inhibit" />
 *       &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="start" type="{http://www.w3.org/2001/XMLSchema}string" default="false" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AuthorizationActionType", propOrder = {
    "allow",
    "inhibit"
})
public class AuthorizationActionType {

    protected AuthorizationAllowType allow;
    protected AuthorizationInhibitType inhibit;
    @XmlAttribute
    protected String fallback;
    @XmlAttribute(required = true)
    protected String name;
    @XmlAttribute
    protected String start;

    /**
     * Gets the value of the allow property.
     * 
     * @return
     *     possible object is
     *     {@link AuthorizationAllowType }
     *     
     */
    public AuthorizationAllowType getAllow() {
        return allow;
    }

    /**
     * Sets the value of the allow property.
     * 
     * @param value
     *     allowed object is
     *     {@link AuthorizationAllowType }
     *     
     */
    public void setAllow(AuthorizationAllowType value) {
        this.allow = value;
    }

    /**
     * Gets the value of the inhibit property.
     * 
     * @return
     *     possible object is
     *     {@link AuthorizationInhibitType }
     *     
     */
    public AuthorizationInhibitType getInhibit() {
        return inhibit;
    }

    /**
     * Sets the value of the inhibit property.
     * 
     * @param value
     *     allowed object is
     *     {@link AuthorizationInhibitType }
     *     
     */
    public void setInhibit(AuthorizationInhibitType value) {
        this.inhibit = value;
    }

    /**
     * Gets the value of the fallback property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFallback() {
        if (fallback == null) {
            return "inhibit";
        } else {
            return fallback;
        }
    }

    /**
     * Sets the value of the fallback property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFallback(String value) {
        this.fallback = value;
    }

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the start property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStart() {
        if (start == null) {
            return "false";
        } else {
            return start;
        }
    }

    /**
     * Sets the value of the start property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStart(String value) {
        this.start = value;
    }

}