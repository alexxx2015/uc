//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.5-2 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2014.03.17 um 10:31:41 AM CET 
//


package de.tum.in.i22.cm.pdp.xsd;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;
import de.tum.in.i22.cm.pdp.internal.ParamMatch;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the de.tum.in.i22.cm.pdp.xsd package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _Policy_QNAME = new QName("http://www.iese.fhg.de/pef/1.0/enforcementLanguage", "policy");
    private final static QName _Condition_QNAME = new QName("http://www.iese.fhg.de/pef/1.0/enforcementLanguage", "condition");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: de.tum.in.i22.cm.pdp.xsd
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link ConditionType }
     * 
     */
    public ConditionType createConditionType() {
        return new ConditionType();
    }

    /**
     * Create an instance of {@link PolicyType }
     * 
     */
    public PolicyType createPolicyType() {
        return new PolicyType();
    }

    /**
     * Create an instance of {@link AuthorizationInhibitType }
     * 
     */
    public AuthorizationInhibitType createAuthorizationInhibitType() {
        return new AuthorizationInhibitType();
    }

    /**
     * Create an instance of {@link PreventiveMechanismType }
     * 
     */
    public PreventiveMechanismType createPreventiveMechanismType() {
        return new PreventiveMechanismType();
    }

    /**
     * Create an instance of {@link ModifyActionType }
     * 
     */
    public ModifyActionType createModifyActionType() {
        return new ModifyActionType();
    }

    /**
     * Create an instance of {@link DelayActionType }
     * 
     */
    public DelayActionType createDelayActionType() {
        return new DelayActionType();
    }

    /**
     * Create an instance of {@link AuthorizationActionType }
     * 
     */
    public AuthorizationActionType createAuthorizationActionType() {
        return new AuthorizationActionType();
    }

    /**
     * Create an instance of {@link DetectiveMechanismType }
     * 
     */
    public DetectiveMechanismType createDetectiveMechanismType() {
        return new DetectiveMechanismType();
    }

    /**
     * Create an instance of {@link MechanismBaseType }
     * 
     */
    public MechanismBaseType createMechanismBaseType() {
        return new MechanismBaseType();
    }

    /**
     * Create an instance of {@link ParamMatchType }
     * 
     */
    public ParamMatchType createParamMatchType() {
        return new ParamMatch();
    }

    /**
     * Create an instance of {@link AuthorizationAllowType }
     * 
     */
    public AuthorizationAllowType createAuthorizationAllowType() {
        return new AuthorizationAllowType();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PolicyType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.iese.fhg.de/pef/1.0/enforcementLanguage", name = "policy")
    public JAXBElement<PolicyType> createPolicy(PolicyType value) {
        return new JAXBElement<PolicyType>(_Policy_QNAME, PolicyType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ConditionType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.iese.fhg.de/pef/1.0/enforcementLanguage", name = "condition")
    public JAXBElement<ConditionType> createCondition(ConditionType value) {
        return new JAXBElement<ConditionType>(_Condition_QNAME, ConditionType.class, null, value);
    }

}
