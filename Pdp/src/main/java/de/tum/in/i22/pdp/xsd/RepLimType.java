//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.5-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2014.03.21 at 11:00:29 AM CET 
//


package de.tum.in.i22.pdp.xsd;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;
import de.tum.in.i22.pdp.internal.condition.Operator;
import de.tum.in.i22.pdp.internal.condition.operators.Always;
import de.tum.in.i22.pdp.internal.condition.operators.Before;
import de.tum.in.i22.pdp.internal.condition.operators.During;
import de.tum.in.i22.pdp.internal.condition.operators.EvalOperator;
import de.tum.in.i22.pdp.internal.condition.operators.EventMatchOperator;
import de.tum.in.i22.pdp.internal.condition.operators.OSLAnd;
import de.tum.in.i22.pdp.internal.condition.operators.OSLFalse;
import de.tum.in.i22.pdp.internal.condition.operators.OSLImplies;
import de.tum.in.i22.pdp.internal.condition.operators.OSLNot;
import de.tum.in.i22.pdp.internal.condition.operators.OSLOr;
import de.tum.in.i22.pdp.internal.condition.operators.OSLTrue;
import de.tum.in.i22.pdp.internal.condition.operators.RepLim;
import de.tum.in.i22.pdp.internal.condition.operators.RepMax;
import de.tum.in.i22.pdp.internal.condition.operators.RepSince;
import de.tum.in.i22.pdp.internal.condition.operators.Since;
import de.tum.in.i22.pdp.internal.condition.operators.StateBasedOperator;
import de.tum.in.i22.pdp.internal.condition.operators.Within;
import de.tum.in.i22.pdp.xsd.time.TimeUnitType;
import org.jvnet.jaxb2_commons.lang.JAXBToStringStrategy;
import org.jvnet.jaxb2_commons.lang.ToString;
import org.jvnet.jaxb2_commons.lang.ToStringStrategy;
import org.jvnet.jaxb2_commons.locator.ObjectLocator;


/**
 * <p>Java class for RepLimType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="RepLimType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;group ref="{http://www.iese.fhg.de/pef/1.0/enforcementLanguage}Operators"/>
 *       &lt;/sequence>
 *       &lt;attGroup ref="{http://www.iese.fhg.de/pef/1.0/time}TimeAmountAttributeGroup"/>
 *       &lt;attribute name="lowerLimit" use="required" type="{http://www.w3.org/2001/XMLSchema}long" />
 *       &lt;attribute name="upperLimit" use="required" type="{http://www.w3.org/2001/XMLSchema}long" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RepLimType", propOrder = {
    "operators"
})
public abstract class RepLimType
    extends Operator
    implements ToString
{

    @XmlElements({
        @XmlElement(name = "true", type = OSLTrue.class),
        @XmlElement(name = "false", type = OSLFalse.class),
        @XmlElement(name = "not", type = OSLNot.class),
        @XmlElement(name = "or", type = OSLOr.class),
        @XmlElement(name = "and", type = OSLAnd.class),
        @XmlElement(name = "implies", type = OSLImplies.class),
        @XmlElement(name = "eventMatch", type = EventMatchOperator.class),
        @XmlElement(name = "since", type = Since.class),
        @XmlElement(name = "always", type = Always.class),
        @XmlElement(name = "before", type = Before.class),
        @XmlElement(name = "during", type = During.class),
        @XmlElement(name = "within", type = Within.class),
        @XmlElement(name = "repLim", type = RepLim.class),
        @XmlElement(name = "repSince", type = RepSince.class),
        @XmlElement(name = "repMax", type = RepMax.class),
        @XmlElement(name = "stateBasedFormula", type = StateBasedOperator.class),
        @XmlElement(name = "eval", type = EvalOperator.class)
    })
    protected Object operators;
    @XmlAttribute(name = "lowerLimit", required = true)
    protected long lowerLimit;
    @XmlAttribute(name = "upperLimit", required = true)
    protected long upperLimit;
    @XmlAttribute(name = "amount", required = true)
    protected long amount;
    @XmlAttribute(name = "unit")
    protected TimeUnitType unit;

    /**
     * Gets the value of the operators property.
     * 
     * @return
     *     possible object is
     *     {@link TrueType }
     *     {@link FalseType }
     *     {@link NotType }
     *     {@link OrType }
     *     {@link AndType }
     *     {@link ImpliesType }
     *     {@link EventMatchingOperatorType }
     *     {@link SinceType }
     *     {@link AlwaysType }
     *     {@link BeforeType }
     *     {@link DuringType }
     *     {@link WithinType }
     *     {@link RepLimType }
     *     {@link RepSinceType }
     *     {@link RepMaxType }
     *     {@link StateBasedOperatorType }
     *     {@link EvalOperatorType }
     *     
     */
    public Object getOperators() {
        return operators;
    }

    /**
     * Sets the value of the operators property.
     * 
     * @param value
     *     allowed object is
     *     {@link TrueType }
     *     {@link FalseType }
     *     {@link NotType }
     *     {@link OrType }
     *     {@link AndType }
     *     {@link ImpliesType }
     *     {@link EventMatchingOperatorType }
     *     {@link SinceType }
     *     {@link AlwaysType }
     *     {@link BeforeType }
     *     {@link DuringType }
     *     {@link WithinType }
     *     {@link RepLimType }
     *     {@link RepSinceType }
     *     {@link RepMaxType }
     *     {@link StateBasedOperatorType }
     *     {@link EvalOperatorType }
     *     
     */
    public void setOperators(Object value) {
        this.operators = value;
    }

    public boolean isSetOperators() {
        return (this.operators!= null);
    }

    /**
     * Gets the value of the lowerLimit property.
     * 
     */
    public long getLowerLimit() {
        return lowerLimit;
    }

    /**
     * Sets the value of the lowerLimit property.
     * 
     */
    public void setLowerLimit(long value) {
        this.lowerLimit = value;
    }

    public boolean isSetLowerLimit() {
        return true;
    }

    /**
     * Gets the value of the upperLimit property.
     * 
     */
    public long getUpperLimit() {
        return upperLimit;
    }

    /**
     * Sets the value of the upperLimit property.
     * 
     */
    public void setUpperLimit(long value) {
        this.upperLimit = value;
    }

    public boolean isSetUpperLimit() {
        return true;
    }

    /**
     * Gets the value of the amount property.
     * 
     */
    public long getAmount() {
        return amount;
    }

    /**
     * Sets the value of the amount property.
     * 
     */
    public void setAmount(long value) {
        this.amount = value;
    }

    public boolean isSetAmount() {
        return true;
    }

    /**
     * Gets the value of the unit property.
     * 
     * @return
     *     possible object is
     *     {@link TimeUnitType }
     *     
     */
    public TimeUnitType getUnit() {
        if (unit == null) {
            return TimeUnitType.TIMESTEPS;
        } else {
            return unit;
        }
    }

    /**
     * Sets the value of the unit property.
     * 
     * @param value
     *     allowed object is
     *     {@link TimeUnitType }
     *     
     */
    public void setUnit(TimeUnitType value) {
        this.unit = value;
    }

    public boolean isSetUnit() {
        return (this.unit!= null);
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
            Object theOperators;
            theOperators = this.getOperators();
            strategy.appendField(locator, this, "operators", buffer, theOperators);
        }
        {
            long theLowerLimit;
            theLowerLimit = (this.isSetLowerLimit()?this.getLowerLimit(): 0L);
            strategy.appendField(locator, this, "lowerLimit", buffer, theLowerLimit);
        }
        {
            long theUpperLimit;
            theUpperLimit = (this.isSetUpperLimit()?this.getUpperLimit(): 0L);
            strategy.appendField(locator, this, "upperLimit", buffer, theUpperLimit);
        }
        {
            long theAmount;
            theAmount = (this.isSetAmount()?this.getAmount(): 0L);
            strategy.appendField(locator, this, "amount", buffer, theAmount);
        }
        {
            TimeUnitType theUnit;
            theUnit = this.getUnit();
            strategy.appendField(locator, this, "unit", buffer, theUnit);
        }
        return buffer;
    }

}
