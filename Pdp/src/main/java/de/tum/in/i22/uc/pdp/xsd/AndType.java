//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.5-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2014.03.21 at 12:52:42 PM CET 
//


package de.tum.in.i22.uc.pdp.xsd;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;
import de.tum.in.i22.uc.pdp.internal.condition.Operator;
import de.tum.in.i22.uc.pdp.internal.condition.operators.Always;
import de.tum.in.i22.uc.pdp.internal.condition.operators.Before;
import de.tum.in.i22.uc.pdp.internal.condition.operators.During;
import de.tum.in.i22.uc.pdp.internal.condition.operators.EvalOperator;
import de.tum.in.i22.uc.pdp.internal.condition.operators.EventMatchOperator;
import de.tum.in.i22.uc.pdp.internal.condition.operators.OSLAnd;
import de.tum.in.i22.uc.pdp.internal.condition.operators.OSLFalse;
import de.tum.in.i22.uc.pdp.internal.condition.operators.OSLImplies;
import de.tum.in.i22.uc.pdp.internal.condition.operators.OSLNot;
import de.tum.in.i22.uc.pdp.internal.condition.operators.OSLOr;
import de.tum.in.i22.uc.pdp.internal.condition.operators.OSLTrue;
import de.tum.in.i22.uc.pdp.internal.condition.operators.RepLim;
import de.tum.in.i22.uc.pdp.internal.condition.operators.RepMax;
import de.tum.in.i22.uc.pdp.internal.condition.operators.RepSince;
import de.tum.in.i22.uc.pdp.internal.condition.operators.Since;
import de.tum.in.i22.uc.pdp.internal.condition.operators.StateBasedOperator;
import de.tum.in.i22.uc.pdp.internal.condition.operators.Within;
import org.jvnet.jaxb2_commons.lang.JAXBToStringStrategy;
import org.jvnet.jaxb2_commons.lang.ToString;
import org.jvnet.jaxb2_commons.lang.ToStringStrategy;
import org.jvnet.jaxb2_commons.locator.ObjectLocator;


/**
 * <p>Java class for AndType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AndType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;group ref="{http://www.iese.fhg.de/pef/1.0/enforcementLanguage}Operators" maxOccurs="2" minOccurs="2"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AndType", propOrder = {
    "operators"
})
public abstract class AndType
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
    protected List<Object> operators;

    /**
     * Gets the value of the operators property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the operators property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getOperators().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TrueType }
     * {@link FalseType }
     * {@link NotType }
     * {@link OrType }
     * {@link AndType }
     * {@link ImpliesType }
     * {@link EventMatchingOperatorType }
     * {@link SinceType }
     * {@link AlwaysType }
     * {@link BeforeType }
     * {@link DuringType }
     * {@link WithinType }
     * {@link RepLimType }
     * {@link RepSinceType }
     * {@link RepMaxType }
     * {@link StateBasedOperatorType }
     * {@link EvalOperatorType }
     * 
     * 
     */
    public List<Object> getOperators() {
        if (operators == null) {
            operators = new ArrayList<Object>();
        }
        return this.operators;
    }

    public boolean isSetOperators() {
        return ((this.operators!= null)&&(!this.operators.isEmpty()));
    }

    public void unsetOperators() {
        this.operators = null;
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
            List<Object> theOperators;
            theOperators = (this.isSetOperators()?this.getOperators():null);
            strategy.appendField(locator, this, "operators", buffer, theOperators);
        }
        return buffer;
    }

}
