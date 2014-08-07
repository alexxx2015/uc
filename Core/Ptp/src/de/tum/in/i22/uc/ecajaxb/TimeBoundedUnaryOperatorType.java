//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.0 in JDK 1.6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2013.05.25 at 01:44:24 PM CEST 
//


package de.tum.in.i22.uc.ecajaxb;

import java.math.BigInteger;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.w3c.dom.Node;

import de.tum.in.i22.uc.policy.translation.ecacreation.ECARulesCreatorView;


/**
 * <p>Java class for TimeBoundedUnaryOperatorType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="TimeBoundedUnaryOperatorType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;group ref="{http://www.iese.fhg.de/pef/1.0/enforcementLanguage}OperatorsGroup"/>
 *       &lt;attGroup ref="{http://www.iese.fhg.de/pef/1.0/time}TimeAmountAttributeGroup"/>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TimeBoundedUnaryOperatorType", propOrder = {
    "_true",
    "_false",
    "eventMatch",
    "not",
    "and",
    "or",
    "implies",
    "xPathEval",
    "eventually",
    "stateBasedFormula",
    "since",
    "always",
    "before",
    "during",
    "within",
    "repLim",
    "repSince",
    "repMax"
})
public class TimeBoundedUnaryOperatorType implements IOperatorType{

    @XmlElement(name = "true")
    protected EmptyOperatorType _true;
    @XmlElement(name = "false")
    protected EmptyOperatorType _false;
    protected EventMatchingOperatorType eventMatch;
    protected UnaryOperatorType not;
    protected BinaryOperatorType and;
    protected BinaryOperatorType or;
    protected BinaryOperatorType implies;
    protected String xPathEval;
    protected UnaryOperatorType eventually;
    protected StateBasedOperatorType stateBasedFormula;
    protected BinaryOperatorType since;
    protected UnaryOperatorType always;
    protected TimeBoundedUnaryOperatorType before;
    protected TimeBoundedUnaryOperatorType during;
    protected TimeBoundedUnaryOperatorType within;
    protected TimeBoundedUnaryOperatorType.RepLim repLim;
    protected TimeBoundedUnaryOperatorType.RepSince repSince;
    protected TimeBoundedUnaryOperatorType.RepMax repMax;
    @XmlAttribute(required = true)
    protected int amount;
    @XmlAttribute
    protected TimeUnitType unit;

    /**
     * Gets the value of the true property.
     * 
     * @return
     *     possible object is
     *     {@link EmptyOperatorType }
     *     
     */
    public EmptyOperatorType getTrue() {
        return _true;
    }

    /**
     * Sets the value of the true property.
     * 
     * @param value
     *     allowed object is
     *     {@link EmptyOperatorType }
     *     
     */
    public void setTrue(EmptyOperatorType value) {
        this._true = value;
    }

    /**
     * Gets the value of the false property.
     * 
     * @return
     *     possible object is
     *     {@link EmptyOperatorType }
     *     
     */
    public EmptyOperatorType getFalse() {
        return _false;
    }

    /**
     * Sets the value of the false property.
     * 
     * @param value
     *     allowed object is
     *     {@link EmptyOperatorType }
     *     
     */
    public void setFalse(EmptyOperatorType value) {
        this._false = value;
    }

    /**
     * Gets the value of the eventMatch property.
     * 
     * @return
     *     possible object is
     *     {@link EventMatchingOperatorType }
     *     
     */
    public EventMatchingOperatorType getEventMatch() {
        return eventMatch;
    }

    /**
     * Sets the value of the eventMatch property.
     * 
     * @param value
     *     allowed object is
     *     {@link EventMatchingOperatorType }
     *     
     */
    public void setEventMatch(EventMatchingOperatorType value) {
        this.eventMatch = value;
    }

    /**
     * Gets the value of the not property.
     * 
     * @return
     *     possible object is
     *     {@link UnaryOperatorType }
     *     
     */
    public UnaryOperatorType getNot() {
        return not;
    }

    /**
     * Sets the value of the not property.
     * 
     * @param value
     *     allowed object is
     *     {@link UnaryOperatorType }
     *     
     */
    public void setNot(UnaryOperatorType value) {
        this.not = value;
    }

    /**
     * Gets the value of the and property.
     * 
     * @return
     *     possible object is
     *     {@link BinaryOperatorType }
     *     
     */
    public BinaryOperatorType getAnd() {
        return and;
    }

    /**
     * Sets the value of the and property.
     * 
     * @param value
     *     allowed object is
     *     {@link BinaryOperatorType }
     *     
     */
    public void setAnd(BinaryOperatorType value) {
        this.and = value;
    }

    /**
     * Gets the value of the or property.
     * 
     * @return
     *     possible object is
     *     {@link BinaryOperatorType }
     *     
     */
    public BinaryOperatorType getOr() {
        return or;
    }

    /**
     * Sets the value of the or property.
     * 
     * @param value
     *     allowed object is
     *     {@link BinaryOperatorType }
     *     
     */
    public void setOr(BinaryOperatorType value) {
        this.or = value;
    }

    /**
     * Gets the value of the implies property.
     * 
     * @return
     *     possible object is
     *     {@link BinaryOperatorType }
     *     
     */
    public BinaryOperatorType getImplies() {
        return implies;
    }

    /**
     * Sets the value of the implies property.
     * 
     * @param value
     *     allowed object is
     *     {@link BinaryOperatorType }
     *     
     */
    public void setImplies(BinaryOperatorType value) {
        this.implies = value;
    }

    /**
     * Gets the value of the xPathEval property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getXPathEval() {
        return xPathEval;
    }

    /**
     * Sets the value of the xPathEval property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setXPathEval(String value) {
        this.xPathEval = value;
    }

    /**
     * Gets the value of the eventually property.
     * 
     * @return
     *     possible object is
     *     {@link UnaryOperatorType }
     *     
     */
    public UnaryOperatorType getEventually() {
        return eventually;
    }

    /**
     * Sets the value of the eventually property.
     * 
     * @param value
     *     allowed object is
     *     {@link UnaryOperatorType }
     *     
     */
    public void setEventually(UnaryOperatorType value) {
        this.eventually = value;
    }

    /**
     * Gets the value of the stateBasedFormula property.
     * 
     * @return
     *     possible object is
     *     {@link StateBasedOperatorType }
     *     
     */
    public StateBasedOperatorType getStateBasedFormula() {
        return stateBasedFormula;
    }

    /**
     * Sets the value of the stateBasedFormula property.
     * 
     * @param value
     *     allowed object is
     *     {@link StateBasedOperatorType }
     *     
     */
    public void setStateBasedFormula(StateBasedOperatorType value) {
        this.stateBasedFormula = value;
    }

     
    /**
     * Gets the value of the since property.
     * 
     * @return
     *     possible object is
     *     {@link BinaryOperatorType }
     *     
     */
    public BinaryOperatorType getSince() {
        return since;
    }

    /**
     * Sets the value of the since property.
     * 
     * @param value
     *     allowed object is
     *     {@link BinaryOperatorType }
     *     
     */
    public void setSince(BinaryOperatorType value) {
        this.since = value;
    }

    /**
     * Gets the value of the always property.
     * 
     * @return
     *     possible object is
     *     {@link UnaryOperatorType }
     *     
     */
    public UnaryOperatorType getAlways() {
        return always;
    }

    /**
     * Sets the value of the always property.
     * 
     * @param value
     *     allowed object is
     *     {@link UnaryOperatorType }
     *     
     */
    public void setAlways(UnaryOperatorType value) {
        this.always = value;
    }

    /**
     * Gets the value of the before property.
     * 
     * @return
     *     possible object is
     *     {@link TimeBoundedUnaryOperatorType }
     *     
     */
    public TimeBoundedUnaryOperatorType getBefore() {
        return before;
    }

    /**
     * Sets the value of the before property.
     * 
     * @param value
     *     allowed object is
     *     {@link TimeBoundedUnaryOperatorType }
     *     
     */
    public void setBefore(TimeBoundedUnaryOperatorType value) {
        this.before = value;
    }

    /**
     * Gets the value of the during property.
     * 
     * @return
     *     possible object is
     *     {@link TimeBoundedUnaryOperatorType }
     *     
     */
    public TimeBoundedUnaryOperatorType getDuring() {
        return during;
    }

    /**
     * Sets the value of the during property.
     * 
     * @param value
     *     allowed object is
     *     {@link TimeBoundedUnaryOperatorType }
     *     
     */
    public void setDuring(TimeBoundedUnaryOperatorType value) {
        this.during = value;
    }

    /**
     * Gets the value of the within property.
     * 
     * @return
     *     possible object is
     *     {@link TimeBoundedUnaryOperatorType }
     *     
     */
    public TimeBoundedUnaryOperatorType getWithin() {
        return within;
    }

    /**
     * Sets the value of the within property.
     * 
     * @param value
     *     allowed object is
     *     {@link TimeBoundedUnaryOperatorType }
     *     
     */
    public void setWithin(TimeBoundedUnaryOperatorType value) {
        this.within = value;
    }

    /**
     * Gets the value of the repLim property.
     * 
     * @return
     *     possible object is
     *     {@link TimeBoundedUnaryOperatorType.RepLim }
     *     
     */
    public TimeBoundedUnaryOperatorType.RepLim getRepLim() {
        return repLim;
    }

    /**
     * Sets the value of the repLim property.
     * 
     * @param value
     *     allowed object is
     *     {@link TimeBoundedUnaryOperatorType.RepLim }
     *     
     */
    public void setRepLim(TimeBoundedUnaryOperatorType.RepLim value) {
        this.repLim = value;
    }

    /**
     * Gets the value of the repSince property.
     * 
     * @return
     *     possible object is
     *     {@link TimeBoundedUnaryOperatorType.RepSince }
     *     
     */
    public TimeBoundedUnaryOperatorType.RepSince getRepSince() {
        return repSince;
    }

    /**
     * Sets the value of the repSince property.
     * 
     * @param value
     *     allowed object is
     *     {@link TimeBoundedUnaryOperatorType.RepSince }
     *     
     */
    public void setRepSince(TimeBoundedUnaryOperatorType.RepSince value) {
        this.repSince = value;
    }

    /**
     * Gets the value of the repMax property.
     * 
     * @return
     *     possible object is
     *     {@link TimeBoundedUnaryOperatorType.RepMax }
     *     
     */
    public TimeBoundedUnaryOperatorType.RepMax getRepMax() {
        return repMax;
    }

    /**
     * Sets the value of the repMax property.
     * 
     * @param value
     *     allowed object is
     *     {@link TimeBoundedUnaryOperatorType.RepMax }
     *     
     */
    public void setRepMax(TimeBoundedUnaryOperatorType.RepMax value) {
        this.repMax = value;
    }

    /**
     * Gets the value of the amount property.
     * 
     */
    public int getAmount() {
        return amount;
    }

    /**
     * Sets the value of the amount property.
     * 
     */
    public void setAmount(int value) {
        this.amount = value;
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


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;extension base="{http://www.iese.fhg.de/pef/1.0/enforcementLanguage}TimeBoundedUnaryOperatorType">
     *       &lt;attribute name="lowerLimit" use="required" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" />
     *       &lt;attribute name="upperLimit" use="required" type="{http://www.w3.org/2001/XMLSchema}positiveInteger" />
     *     &lt;/extension>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class RepLim
        extends TimeBoundedUnaryOperatorType
    {

        @XmlAttribute(required = true)
        protected BigInteger lowerLimit;
        @XmlAttribute(required = true)
        protected BigInteger upperLimit;

        /**
         * Gets the value of the lowerLimit property.
         * 
         * @return
         *     possible object is
         *     {@link BigInteger }
         *     
         */
        public BigInteger getLowerLimit() {
            return lowerLimit;
        }

        /**
         * Sets the value of the lowerLimit property.
         * 
         * @param value
         *     allowed object is
         *     {@link BigInteger }
         *     
         */
        public void setLowerLimit(BigInteger value) {
            this.lowerLimit = value;
        }

        /**
         * Gets the value of the upperLimit property.
         * 
         * @return
         *     possible object is
         *     {@link BigInteger }
         *     
         */
        public BigInteger getUpperLimit() {
            return upperLimit;
        }

        /**
         * Sets the value of the upperLimit property.
         * 
         * @param value
         *     allowed object is
         *     {@link BigInteger }
         *     
         */
        public void setUpperLimit(BigInteger value) {
            this.upperLimit = value;
        }

    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;extension base="{http://www.iese.fhg.de/pef/1.0/enforcementLanguage}UnaryOperatorType">
     *       &lt;attribute name="limit" use="required" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" />
     *     &lt;/extension>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class RepMax
        extends UnaryOperatorType
    {

        @XmlAttribute(required = true)
        protected BigInteger limit;

        /**
         * Gets the value of the limit property.
         * 
         * @return
         *     possible object is
         *     {@link BigInteger }
         *     
         */
        public BigInteger getLimit() {
            return limit;
        }

        /**
         * Sets the value of the limit property.
         * 
         * @param value
         *     allowed object is
         *     {@link BigInteger }
         *     
         */
        public void setLimit(BigInteger value) {
            this.limit = value;
        }

    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;extension base="{http://www.iese.fhg.de/pef/1.0/enforcementLanguage}BinaryOperatorType">
     *       &lt;attribute name="limit" use="required" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" />
     *     &lt;/extension>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class RepSince
        extends BinaryOperatorType
    {

        @XmlAttribute(required = true)
        protected BigInteger limit;

        /**
         * Gets the value of the limit property.
         * 
         * @return
         *     possible object is
         *     {@link BigInteger }
         *     
         */
        public BigInteger getLimit() {
            return limit;
        }

        /**
         * Sets the value of the limit property.
         * 
         * @param value
         *     allowed object is
         *     {@link BigInteger }
         *     
         */
        public void setLimit(BigInteger value) {
            this.limit = value;
        }

    }
    
    @XmlTransient
	private boolean isBefore;
	@XmlTransient
	private boolean isDuring;
	@XmlTransient
	private boolean isWithin;
	@XmlTransient
	private boolean isReplim;
	@XmlTransient
	private boolean isRepsince;
	@XmlTransient
	private boolean isRepmax;
	@XmlTransient
	private Object oParentToPass;

	/**
	 * 
	 * @return Returns if this operator is <before> or not
	 */
	public boolean isBefore() {
		return isBefore;
	}

	/**
	 * Sets that this operator is <before> or not
	 * 
	 * @param isBefore
	 */
	public void setBefore(boolean isBefore) {
		this.isBefore = isBefore;
	}

	/**
	 * 
	 * @return Returns if this operator is <during> or not
	 */
	public boolean isDuring() {
		return isDuring;
	}

	/**
	 * Sets that this operator is a <during> or not
	 * 
	 * @param isDuring
	 */
	public void setDuring(boolean isDuring) {
		this.isDuring = isDuring;
	}

	/**
	 * 
	 * @return Returns if this operator is a <within> or not
	 */
	public boolean isWithin() {
		return isWithin;
	}

	/**
	 * Sets if this operator is a <within> or not
	 * 
	 * @param isWithin
	 */
	public void setWithin(boolean isWithin) {
		this.isWithin = isWithin;
	}

	/**
	 * 
	 * @return Returns if this operator is a <replim> or not
	 */
	public boolean isReplim() {
		return isReplim;
	}

	/**
	 * Sets if this operator is a <replim> or not
	 * 
	 * @param isReplim
	 */
	public void setReplim(boolean isReplim) {
		this.isReplim = isReplim;
	}

	/**
	 * 
	 * @return Returns if this operator is a <repsince> or not 
	 */
	public boolean isRepsince() {
		return isRepsince;
	}

	/**
	 * Sets if this operator is a <repsince> or not.
	 * 
	 * @param isRepsince
	 */
	public void setRepsince(boolean isRepsince) {
		this.isRepsince = isRepsince;
	}

	/**
	 * 
	 * @return Returns if this operator is a <repmax> or not.
	 */
	public boolean isRepmax() {
		return isRepmax;
	}

	/**
	 * Sets if this operator is a <repmax> or not.
	 * 
	 * @param isRepmax
	 */
	public void setRepmax(boolean isRepmax) {
		this.isRepmax = isRepmax;
	}
	
	/**
	 * 
	 * @return Returns the parent to pass to the next recursive step
	 */
	public Object getParentToPass(){
		return oParentToPass;
	}


	@Override
	public OperatorTypeValue tellOperatorType() {
		
		return OperatorTypeValue.TIMEBOUNDEDUNARY;
	}

	/**
	 * First set the type of temporal operator. Then,
	 * derive matching temporal operator and set its attribute values
	 * like amount, unit, limit etc.
	 * for persisting in an xml file
	 * 
	 * @param event
	 * @return Returns an EventMatchingOperatorType for a trigger event
	 */
	 public TimeBoundedUnaryOperatorType getMatchingTemporalOperator(Node node){
		TimeBoundedUnaryOperatorType matchingOperator=new TimeBoundedUnaryOperatorType();
		String xmlNodeName = node.getNodeName();
		if(xmlNodeName.equalsIgnoreCase("before")) matchingOperator.setBefore(true);
		else if(xmlNodeName.equalsIgnoreCase("within")) matchingOperator.setWithin(true);
		else if(xmlNodeName.equalsIgnoreCase("during")) matchingOperator.setDuring(true);
		else if(xmlNodeName.equalsIgnoreCase("replim")) {
			matchingOperator.setReplim(true);
		}
		else if(xmlNodeName.equalsIgnoreCase("repmax")) {
			matchingOperator.setRepmax(true);
		}
		else if(xmlNodeName.equalsIgnoreCase("repsince")) {
			matchingOperator.setRepsince(true);
		}
		
		String unitName="";
		int amount=0;
		BigInteger limit= new BigInteger("0");
		BigInteger upperLimit = new BigInteger("0");
		BigInteger lowerLimit= new BigInteger("0") ;
		if(node!=null){
			if(node.getAttributes().getLength()>0){
				if(matchingOperator.isBefore() || matchingOperator.isDuring() || matchingOperator.isReplim() || matchingOperator.isWithin()){
				if(node.getAttributes().getNamedItem("amount")!=null)
				amount=Integer.parseInt(node.getAttributes().getNamedItem("amount").getNodeValue());
				if(node.getAttributes().getNamedItem("unit")!=null)
					unitName=node.getAttributes().getNamedItem("unit").getNodeValue();
				
				matchingOperator.setAmount(amount);
				// doesn't work and causes exception if unitName.toUpperCase() is removed
				matchingOperator.setUnit(TimeUnitType.fromValue(unitName.toUpperCase()));
				}
				
				if(matchingOperator.isRepmax()){
					TimeBoundedUnaryOperatorType.RepMax repmaxOp = new TimeBoundedUnaryOperatorType.RepMax();
					if(node.getAttributes().getNamedItem("limit")!=null)
					limit = new BigInteger(node.getAttributes().getNamedItem("limit").getNodeValue());
					repmaxOp.setLimit(limit);
		            matchingOperator.setRepMax(repmaxOp);
				}
				if(matchingOperator.isRepsince()){
					TimeBoundedUnaryOperatorType.RepSince repsinceOp = new TimeBoundedUnaryOperatorType.RepSince();
						if(node.getAttributes().getNamedItem("limit")!=null)
						limit = new BigInteger(node.getAttributes().getNamedItem("limit").getNodeValue());
						repsinceOp.setLimit(limit);
						matchingOperator.setRepSince(repsinceOp);
				}
				if (matchingOperator.isReplim()) {
				// this part of the code wont be executed and might as well cause exception
		        // because lower limit and upper limit have not been set in the replim operator
			    // To make this part work, set upper and lower limit for replim as done for replim and 
			    // other operators with setting of amount, unit and limit in FutureToPast.java
					if(node.getAttributes().getNamedItem("lowerlimit")!=null){
						limit = new BigInteger(node.getAttributes().getNamedItem("lowerlimit").getNodeValue());
						matchingOperator.getRepLim().setLowerLimit(lowerLimit);
				}
					if(node.getAttributes().getNamedItem("upperlimit")!=null){
						limit = new BigInteger(node.getAttributes().getNamedItem("upperlimit").getNodeValue());
						matchingOperator.getRepLim().setUpperLimit(upperLimit);
				}
					}
					
				
			}


		}		
		
		return matchingOperator;
	}
	
	@Override
	public void configureMyself(String xmlNodeName, Object parent) {
		
		if(xmlNodeName.equalsIgnoreCase("before")) setBefore(true);
		else if(xmlNodeName.equalsIgnoreCase("within")) setWithin(true);
		else if(xmlNodeName.equalsIgnoreCase("during")) {
			setDuring(true);
		}
		else if(xmlNodeName.equalsIgnoreCase("replim")) {
			setReplim(true);
			oParentToPass=this.repLim; 
		}
		else if(xmlNodeName.equalsIgnoreCase("repmax")) {
			setRepmax(true);
			oParentToPass=this.repMax;
		}
		else if(xmlNodeName.equalsIgnoreCase("repsince")) {
			setRepsince(true);
			oParentToPass=this.repSince;
		}
				
		connectToMyParent(parent);
		if(oParentToPass==null)
			oParentToPass=this;
	}

	@Override
	public void connectToMyParent(Object parent) {
		ObjectFactory factory=new ObjectFactory();
		if(parent!=null){
			if(parent instanceof UnaryOperatorType){
				UnaryOperatorType uotParent=(UnaryOperatorType) parent;
				if(isBefore()) uotParent.setBefore(this);
				else if(isDuring()) uotParent.setDuring(this);
				else if(isReplim()) uotParent.setRepLim(this.repLim);
				else if(isRepmax()) uotParent.setRepMax(this.repMax);
				else if(isRepsince()) uotParent.setRepSince(this.repSince);
				else if(isWithin()) uotParent.setWithin(this);
			}
			else if(parent instanceof BinaryOperatorType){
				BinaryOperatorType botParent=(BinaryOperatorType) parent;					
				JAXBElement<TimeBoundedUnaryOperatorType> el;
				if(isBefore()) {
					el=factory.createBinaryOperatorTypeBefore(this);
					botParent.getOperatorsGroup().add(el);
				}
				else if(isDuring()) {
					el=factory.createBinaryOperatorTypeDuring(this);
					botParent.getOperatorsGroup().add(el);
				}
			
				else if(isReplim()) {
					JAXBElement<TimeBoundedUnaryOperatorType.RepLim> el1;
					el1=factory.createBinaryOperatorTypeRepLim(this.repLim);
					botParent.getOperatorsGroup().add(el1);
				}
				else if(isRepmax()) {
					JAXBElement<TimeBoundedUnaryOperatorType.RepMax> el1;
					el1=factory.createBinaryOperatorTypeRepMax(this.repMax);
					botParent.getOperatorsGroup().add(el1);
				}
				else if(isRepsince()) {
					JAXBElement<TimeBoundedUnaryOperatorType.RepSince> el1;
					el1=factory.createBinaryOperatorTypeRepSince(this.repSince);
					botParent.getOperatorsGroup().add(el1);
				}
				else if(isWithin()) {
					el=factory.createBinaryOperatorTypeWithin(this);
					botParent.getOperatorsGroup().add(el);
				}
				
				}
			else if(parent instanceof TimeBoundedUnaryOperatorType){
				TimeBoundedUnaryOperatorType tbotParent=(TimeBoundedUnaryOperatorType) parent;					
				if(isBefore()) tbotParent.setBefore(this);
				else if(isDuring()) tbotParent.setDuring(this);
				else if(isReplim()) tbotParent.setRepLim(this.repLim);
				else if(isRepmax()) tbotParent.setRepMax(this.repMax);
				else if(isRepsince()) tbotParent.setRepSince(this.repSince);
				else if(isWithin()) tbotParent.setWithin(this);
				
				}
			}
	}

}
