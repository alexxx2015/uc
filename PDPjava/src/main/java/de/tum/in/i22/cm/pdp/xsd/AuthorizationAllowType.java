//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.5-2 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2014.03.17 um 10:31:41 AM CET 
//


package de.tum.in.i22.cm.pdp.xsd;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import de.tum.in.i22.cm.pdp.xsd.action.ExecuteActionType;
import org.jvnet.jaxb2_commons.lang.JAXBToStringStrategy;
import org.jvnet.jaxb2_commons.lang.ToString;
import org.jvnet.jaxb2_commons.lang.ToStringStrategy;
import org.jvnet.jaxb2_commons.locator.ObjectLocator;


/**
 * <p>Java-Klasse für AuthorizationAllowType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="AuthorizationAllowType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="delay" type="{http://www.iese.fhg.de/pef/1.0/enforcementLanguage}DelayActionType" minOccurs="0"/>
 *         &lt;element name="modify" type="{http://www.iese.fhg.de/pef/1.0/enforcementLanguage}ModifyActionType" minOccurs="0"/>
 *         &lt;element name="executeSyncAction" type="{http://www.iese.fhg.de/pef/1.0/action}ExecuteActionType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AuthorizationAllowType", propOrder = {
    "delay",
    "modify",
    "executeSyncAction"
})
public class AuthorizationAllowType implements ToString
{

    protected DelayActionType delay;
    protected ModifyActionType modify;
    protected List<ExecuteActionType> executeSyncAction;

    /**
     * Ruft den Wert der delay-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link DelayActionType }
     *     
     */
    public DelayActionType getDelay() {
        return delay;
    }

    /**
     * Legt den Wert der delay-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link DelayActionType }
     *     
     */
    public void setDelay(DelayActionType value) {
        this.delay = value;
    }

    public boolean isSetDelay() {
        return (this.delay!= null);
    }

    /**
     * Ruft den Wert der modify-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link ModifyActionType }
     *     
     */
    public ModifyActionType getModify() {
        return modify;
    }

    /**
     * Legt den Wert der modify-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link ModifyActionType }
     *     
     */
    public void setModify(ModifyActionType value) {
        this.modify = value;
    }

    public boolean isSetModify() {
        return (this.modify!= null);
    }

    /**
     * Gets the value of the executeSyncAction property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the executeSyncAction property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getExecuteSyncAction().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ExecuteActionType }
     * 
     * 
     */
    public List<ExecuteActionType> getExecuteSyncAction() {
        if (executeSyncAction == null) {
            executeSyncAction = new ArrayList<ExecuteActionType>();
        }
        return this.executeSyncAction;
    }

    public boolean isSetExecuteSyncAction() {
        return ((this.executeSyncAction!= null)&&(!this.executeSyncAction.isEmpty()));
    }

    public void unsetExecuteSyncAction() {
        this.executeSyncAction = null;
    }

    public String toString() {
        final ToStringStrategy strategy = JAXBToStringStrategy.INSTANCE;
        final StringBuilder buffer = new StringBuilder();
        append(null, buffer, strategy);
        return buffer.toString();
    }

    public StringBuilder append(ObjectLocator locator, StringBuilder buffer, ToStringStrategy strategy) {
        strategy.appendStart(locator, this, buffer);
        appendFields(locator, buffer, strategy);
        strategy.appendEnd(locator, this, buffer);
        return buffer;
    }

    public StringBuilder appendFields(ObjectLocator locator, StringBuilder buffer, ToStringStrategy strategy) {
        {
            DelayActionType theDelay;
            theDelay = this.getDelay();
            strategy.appendField(locator, this, "delay", buffer, theDelay);
        }
        {
            ModifyActionType theModify;
            theModify = this.getModify();
            strategy.appendField(locator, this, "modify", buffer, theModify);
        }
        {
            List<ExecuteActionType> theExecuteSyncAction;
            theExecuteSyncAction = (this.isSetExecuteSyncAction()?this.getExecuteSyncAction():null);
            strategy.appendField(locator, this, "executeSyncAction", buffer, theExecuteSyncAction);
        }
        return buffer;
    }

}
