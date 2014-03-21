//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.5-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2014.03.21 at 12:52:42 PM CET 
//


package de.tum.in.i22.uc.pdp.xsd.event;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for EventParameterDataTypes.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="EventParameterDataTypes">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="string"/>
 *     &lt;enumeration value="binary"/>
 *     &lt;enumeration value="int"/>
 *     &lt;enumeration value="long"/>
 *     &lt;enumeration value="bool"/>
 *     &lt;enumeration value="stringArray"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "EventParameterDataTypes")
@XmlEnum
public enum EventParameterDataTypes {

    @XmlEnumValue("string")
    STRING("string"),
    @XmlEnumValue("binary")
    BINARY("binary"),
    @XmlEnumValue("int")
    INT("int"),
    @XmlEnumValue("long")
    LONG("long"),
    @XmlEnumValue("bool")
    BOOL("bool"),
    @XmlEnumValue("stringArray")
    STRING_ARRAY("stringArray");
    private final String value;

    EventParameterDataTypes(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static EventParameterDataTypes fromValue(String v) {
        for (EventParameterDataTypes c: EventParameterDataTypes.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
