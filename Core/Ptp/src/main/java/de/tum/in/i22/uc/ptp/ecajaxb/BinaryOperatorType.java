//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.0 in JDK 1.6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2013.05.25 at 01:44:24 PM CEST 
//


package de.tum.in.i22.uc.ptp.ecajaxb;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for BinaryOperatorType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="BinaryOperatorType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;group ref="{http://www.iese.fhg.de/pef/1.0/enforcementLanguage}OperatorsGroup" maxOccurs="2" minOccurs="2"/>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BinaryOperatorType", propOrder = {
    "operatorsGroup"
})
public class BinaryOperatorType implements IOperatorType{

    @XmlElementRefs({
        @XmlElementRef(name = "during", namespace = "http://www.iese.fhg.de/pef/1.0/enforcementLanguage", type = JAXBElement.class),
        @XmlElementRef(name = "not", namespace = "http://www.iese.fhg.de/pef/1.0/enforcementLanguage", type = JAXBElement.class),
        @XmlElementRef(name = "xPathEval", namespace = "http://www.iese.fhg.de/pef/1.0/enforcementLanguage", type = JAXBElement.class),
        @XmlElementRef(name = "always", namespace = "http://www.iese.fhg.de/pef/1.0/enforcementLanguage", type = JAXBElement.class),
        @XmlElementRef(name = "stateBasedFormula", namespace = "http://www.iese.fhg.de/pef/1.0/enforcementLanguage", type = JAXBElement.class),
        @XmlElementRef(name = "implies", namespace = "http://www.iese.fhg.de/pef/1.0/enforcementLanguage", type = JAXBElement.class),
        @XmlElementRef(name = "or", namespace = "http://www.iese.fhg.de/pef/1.0/enforcementLanguage", type = JAXBElement.class),
        @XmlElementRef(name = "true", namespace = "http://www.iese.fhg.de/pef/1.0/enforcementLanguage", type = JAXBElement.class),
        @XmlElementRef(name = "eventMatch", namespace = "http://www.iese.fhg.de/pef/1.0/enforcementLanguage", type = JAXBElement.class),
        @XmlElementRef(name = "within", namespace = "http://www.iese.fhg.de/pef/1.0/enforcementLanguage", type = JAXBElement.class),
        @XmlElementRef(name = "false", namespace = "http://www.iese.fhg.de/pef/1.0/enforcementLanguage", type = JAXBElement.class),
        @XmlElementRef(name = "before", namespace = "http://www.iese.fhg.de/pef/1.0/enforcementLanguage", type = JAXBElement.class),
        @XmlElementRef(name = "repMax", namespace = "http://www.iese.fhg.de/pef/1.0/enforcementLanguage", type = JAXBElement.class),
        @XmlElementRef(name = "eventually", namespace = "http://www.iese.fhg.de/pef/1.0/enforcementLanguage", type = JAXBElement.class),
        @XmlElementRef(name = "repLim", namespace = "http://www.iese.fhg.de/pef/1.0/enforcementLanguage", type = JAXBElement.class),
        @XmlElementRef(name = "since", namespace = "http://www.iese.fhg.de/pef/1.0/enforcementLanguage", type = JAXBElement.class),
        @XmlElementRef(name = "repSince", namespace = "http://www.iese.fhg.de/pef/1.0/enforcementLanguage", type = JAXBElement.class),
        @XmlElementRef(name = "and", namespace = "http://www.iese.fhg.de/pef/1.0/enforcementLanguage", type = JAXBElement.class)
    })
    protected List<JAXBElement<?>> operatorsGroup;

    /**
     * Gets the value of the operatorsGroup property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the operatorsGroup property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getOperatorsGroup().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link JAXBElement }{@code <}{@link StateBasedOperatorType }{@code >}
     * {@link JAXBElement }{@code <}{@link TimeBoundedUnaryOperatorType }{@code >}
     * {@link JAXBElement }{@code <}{@link UnaryOperatorType }{@code >}
     * {@link JAXBElement }{@code <}{@link String }{@code >}
     * {@link JAXBElement }{@code <}{@link UnaryOperatorType }{@code >}
     * {@link JAXBElement }{@code <}{@link StateBasedOperatorType }{@code >}
     * {@link JAXBElement }{@code <}{@link BinaryOperatorType }{@code >}
     * {@link JAXBElement }{@code <}{@link EmptyOperatorType }{@code >}
     * {@link JAXBElement }{@code <}{@link BinaryOperatorType }{@code >}
     * {@link JAXBElement }{@code <}{@link StateBasedOperatorType }{@code >}
     * {@link JAXBElement }{@code <}{@link TimeBoundedUnaryOperatorType }{@code >}
     * {@link JAXBElement }{@code <}{@link EventMatchingOperatorType }{@code >}
     * {@link JAXBElement }{@code <}{@link TimeBoundedUnaryOperatorType }{@code >}
     * {@link JAXBElement }{@code <}{@link EmptyOperatorType }{@code >}
     * {@link JAXBElement }{@code <}{@link UnaryOperatorType }{@code >}
     * {@link JAXBElement }{@code <}{@link TimeBoundedUnaryOperatorType.RepMax }{@code >}
     * {@link JAXBElement }{@code <}{@link BinaryOperatorType }{@code >}
     * {@link JAXBElement }{@code <}{@link TimeBoundedUnaryOperatorType.RepLim }{@code >}
     * {@link JAXBElement }{@code <}{@link BinaryOperatorType }{@code >}
     * {@link JAXBElement }{@code <}{@link TimeBoundedUnaryOperatorType.RepSince }{@code >}
     * 
     * 
     */
    public List<JAXBElement<?>> getOperatorsGroup() {
        if (operatorsGroup == null) {
            operatorsGroup = new ArrayList<JAXBElement<?>>();
        }
        return this.operatorsGroup;
    }
    
    @XmlTransient
	private boolean isAnd;
	@XmlTransient
	private boolean isOr;
	@XmlTransient
	private boolean isImplies;
	@XmlTransient
	private boolean isSince;
	@XmlTransient
	private BinaryOperatorType oParentToPass;

	/**
	 * 
	 * @return Returns if this operator is an <and> or not.
	 */
	public boolean isAnd() {
		return isAnd;
	}

	/**
	 * Sets if this operator is an <and> or not.
	 * 
	 * @param isAnd
	 */
	public void setAnd(boolean isAnd) {
		this.isAnd = isAnd;
	}

	/**
	 * 
	 * @return Returns if this operator is an <or> or not.
	 */
	public boolean isOr() {
		return isOr;
	}

	/**
	 * Sets if this operator is an <or> or not.
	 * 
	 * @param isOr
	 */
	public void setOr(boolean isOr) {
		this.isOr = isOr;
	}

	/**
	 * 
	 * @return Returns if this operator is a <implies> or not. 
	 */
	public boolean isImplies() {
		return isImplies;
	}

	/**
	 * Sets if this operator is a <implies> or not.
	 * 
	 * @param isImplies
	 */
	public void setImplies(boolean isImplies) {
		this.isImplies = isImplies;
	}

	/**
	 * 
	 * @return Returns if this operator is a <since> or not.
	 */
	public boolean isSince() {
		return isSince;
	}

	/**
	 * Sets if this operator is a <since> or not.
	 * 
	 * @param isSince
	 */
	public void setSince(boolean isSince) {
		this.isSince = isSince;
	}
	
	/**
	 * 
	 * @return Returns the parent to pass to the next recursive process.
	 */
	public Object getParentToPass(){
		return oParentToPass;
	}

	@Override
	public OperatorTypeValue tellOperatorType() {
		
		return OperatorTypeValue.BINARY;
	}

	@Override
	public void configureMyself(String xmlNodeName, Object parent) {
		
		if(xmlNodeName.equalsIgnoreCase("or")){				
			setOr(true);					
		}
		else if(xmlNodeName.equalsIgnoreCase("and")){			
			setAnd(true);					
		}
		else if(xmlNodeName.equalsIgnoreCase("implies")){				
			setImplies(true);						
		}
		else if(xmlNodeName.equalsIgnoreCase("since")){				
			setSince(true);						
		}
		connectToMyParent(parent);
		oParentToPass=this;
	}

	@Override
	public void connectToMyParent(Object parent) {
				
		ObjectFactory factory=new ObjectFactory();
		
		if(parent!=null){
			if(parent instanceof UnaryOperatorType){
				UnaryOperatorType uotParent=(UnaryOperatorType) parent;
				if(isAnd()) uotParent.setAnd(this);
				else if(isImplies()) uotParent.setImplies(this);
				else if(isOr()) uotParent.setOr(this);
				else if(isSince()) uotParent.setSince(this);					
			}
			else if(parent instanceof BinaryOperatorType){
				BinaryOperatorType botParent=(BinaryOperatorType) parent;					
				JAXBElement<BinaryOperatorType> el;
				if(isAnd()) {
					el=factory.createBinaryOperatorTypeAnd(this);
					botParent.getOperatorsGroup().add(el);
				}
				else if(isImplies()) {
					el=factory.createBinaryOperatorTypeImplies(this);
					botParent.getOperatorsGroup().add(el);
				}
				else if(isOr()) {
					el=factory.createBinaryOperatorTypeOr(this);
					botParent.getOperatorsGroup().add(el);
				}
				else if(isSince()) {
					el=factory.createBinaryOperatorTypeSince(this);
					botParent.getOperatorsGroup().add(el);
				}
				
			}
			else if(parent instanceof TimeBoundedUnaryOperatorType){
			 TimeBoundedUnaryOperatorType tbotParent = (TimeBoundedUnaryOperatorType) parent;
			 if(isAnd()) tbotParent.setAnd(this);
				else if(isImplies()) tbotParent.setImplies(this);
				else if(isOr()) tbotParent.setOr(this);
				else if(isSince()) tbotParent.setSince(this);	
			}
	  }
		
	}

}
