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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ComparatorType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ComparatorType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="evaluator" type="{http://www.iese.fhg.de/pef/1.0/context}EvaluatorType"/>
 *         &lt;element name="constant" type="{http://www.iese.fhg.de/pef/1.0/context}ConstantType"/>
 *       &lt;/sequence>
 *       &lt;attribute name="op" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;enumeration value="less"/>
 *             &lt;enumeration value="less_eq"/>
 *             &lt;enumeration value="greater"/>
 *             &lt;enumeration value="greater_eq"/>
 *             &lt;enumeration value="eq"/>
 *             &lt;enumeration value="not_eq"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ComparatorType", namespace = "http://www.iese.fhg.de/pef/1.0/context", propOrder = {
    "evaluator",
    "constant"
})
public class ComparatorType {

    @XmlElement(namespace = "http://www.iese.fhg.de/pef/1.0/context", required = true)
    protected EvaluatorType evaluator;
    @XmlElement(namespace = "http://www.iese.fhg.de/pef/1.0/context", required = true)
    protected ConstantType constant;
    @XmlAttribute(required = true)
    protected String op;

    /**
     * Gets the value of the evaluator property.
     * 
     * @return
     *     possible object is
     *     {@link EvaluatorType }
     *     
     */
    public EvaluatorType getEvaluator() {
        return evaluator;
    }

    /**
     * Sets the value of the evaluator property.
     * 
     * @param value
     *     allowed object is
     *     {@link EvaluatorType }
     *     
     */
    public void setEvaluator(EvaluatorType value) {
        this.evaluator = value;
    }

    /**
     * Gets the value of the constant property.
     * 
     * @return
     *     possible object is
     *     {@link ConstantType }
     *     
     */
    public ConstantType getConstant() {
        return constant;
    }

    /**
     * Sets the value of the constant property.
     * 
     * @param value
     *     allowed object is
     *     {@link ConstantType }
     *     
     */
    public void setConstant(ConstantType value) {
        this.constant = value;
    }

    /**
     * Gets the value of the op property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOp() {
        return op;
    }

    /**
     * Sets the value of the op property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOp(String value) {
        this.op = value;
    }

}
