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
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for DetectiveMechanismType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DetectiveMechanismType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.iese.fhg.de/pef/1.0/enforcementLanguage}MechanismBaseType">
 *       &lt;sequence>
 *         &lt;element name="executeAction" type="{http://www.iese.fhg.de/pef/1.0/action}ExecuteActionType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DetectiveMechanismType", propOrder = {
    "executeAction"
})
public class DetectiveMechanismType
    extends MechanismBaseType
{

    protected List<ExecuteActionType> executeAction;

    /**
     * Gets the value of the executeAction property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the executeAction property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getExecuteAction().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ExecuteActionType }
     * 
     * 
     */
    public List<ExecuteActionType> getExecuteAction() {
        if (executeAction == null) {
            executeAction = new ArrayList<ExecuteActionType>();
        }
        return this.executeAction;
    }

}
