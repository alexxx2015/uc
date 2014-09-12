package de.tum.in.i22.uc.pmp;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.Files;

import de.tum.in.i22.uc.cm.datatypes.basic.DataBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.PtpResponseBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic.EStatus;
import de.tum.in.i22.uc.cm.datatypes.basic.XmlPolicy;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IContainer;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IData;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IMechanism;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IName;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IPtpResponse;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.cm.distribution.IPLocation;
import de.tum.in.i22.uc.cm.distribution.LocalLocation;
import de.tum.in.i22.uc.cm.factories.MessageFactory;
import de.tum.in.i22.uc.cm.interfaces.IPmp2Ptp;
import de.tum.in.i22.uc.cm.processing.PmpProcessor;
import de.tum.in.i22.uc.cm.processing.dummy.DummyPdpProcessor;
import de.tum.in.i22.uc.cm.processing.dummy.DummyPipProcessor;
import de.tum.in.i22.uc.cm.settings.Settings;
import de.tum.in.i22.uc.pmp.policies.PolicyManager;
import de.tum.in.i22.uc.pmp.xsd.ComparisonOperatorTypes;
import de.tum.in.i22.uc.pmp.xsd.ContainerType;
import de.tum.in.i22.uc.pmp.xsd.InitialRepresentationType;
import de.tum.in.i22.uc.pmp.xsd.MechanismBaseType;
import de.tum.in.i22.uc.pmp.xsd.ObjectFactory;
import de.tum.in.i22.uc.pmp.xsd.ParamMatchType;
import de.tum.in.i22.uc.pmp.xsd.PolicyType;
import de.tum.in.i22.uc.remotelistener.PtpHandler;

public class PmpHandler extends PmpProcessor {
	private static final Logger _logger = LoggerFactory.getLogger(PmpHandler.class);

	/**
	 * Maps data to the XML policies in which it occurs. Used for fast lookup of
	 * policies for a given data.
	 */
	private final Map<IData, Set<XmlPolicy>> _dataToPolicies;

	private final static String _DATAUSAGE = "dataUsage";
	private final static String _DATA = "data";

	private static final String JAXB_CONTEXT = Settings.getInstance().getPmpJaxbContext();

	private IPmp2Ptp _ptp;
	private PolicyManager _policymanager;
	
	//private final Set<String> _deployedPolicies;

	
	public PmpHandler() {
		super(LocalLocation.getInstance());
		init(new DummyPipProcessor(), new DummyPdpProcessor());
		_dataToPolicies = new ConcurrentHashMap<>();
		_ptp = new PtpHandler();
		_policymanager = new PolicyManager();
	}

	private PolicyType xmlToPolicy(String xml) {
		_logger.debug("xmlToPolicy: " + xml);

		PolicyType policy = null;
		InputStream inp = new ByteArrayInputStream(xml.getBytes());

		try {
			Unmarshaller u = JAXBContext.newInstance(JAXB_CONTEXT).createUnmarshaller();
			policy = (PolicyType) ((JAXBElement<?>) u.unmarshal(inp)).getValue();
		} catch (JAXBException | ClassCastException e) {
			_logger.error(e.getMessage());
		}

		_logger.debug("curPolicy [name=" + policy.getName() + ", " + policy.toString());
		return policy;
	}

	private String policyToXML(PolicyType policy) {
		_logger.debug("PolicyToXML({}) invoked.", policy);

		StringWriter res = new StringWriter();

		try {
			Marshaller m = JAXBContext.newInstance(JAXB_CONTEXT).createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			m.marshal(new ObjectFactory().createPolicy(policy), res);
		} catch (JAXBException | ClassCastException e) {
			_logger.error(e.getMessage());
		}

		_logger.trace("converted policy: " + res.toString());
		return res.toString();
	}

	/**
	 * Initializes the initial representations specified
	 * within the given policy.
	 *
	 * @param policy
	 */
	private void initInitialRepresentations(PolicyType policy) {
		if (policy.isSetInitialRepresentations()) {
			InitialRepresentationType ir = policy.getInitialRepresentations();

			if (ir.isSetContainer()) {
				for (ContainerType c : ir.getContainer()) {

					if (c.isSetDataId()) {
						Set<IData> dataSet = new HashSet<IData>();
						for (String dataId : c.getDataId()) {
							if (dataId != null && !dataId.equals("")) {
								dataSet.add(new DataBasic(dataId.trim()));
							}
						}

						IName contName = MessageFactory.createName(c.getName());
						IStatus status = getPip().initialRepresentation(contName, dataSet);

						if (status.isStatus(EStatus.ERROR)) {
							_logger.error("impossible to initialize representation for container " + contName + " with data id(s) " + dataSet + ": " + status.getErrorMessage());
							throw new RuntimeException(status.getErrorMessage());
						}
					}
				}
			}
		}
	}


	/**
	 * Maps each of the specified {@link IData} elements to the
	 * specified {@link XmlPolicy} using the internal map {@link PmpHandler#_dataToPolicies}.
	 *
	 * 2014/09/05. FK.
	 *
	 * @param data
	 * @param policy
	 */
	private void mapDataToPolicy(Set<IData> data, XmlPolicy policy) {
		for (IData d : data) {
			Set<XmlPolicy> policies = _dataToPolicies.get(d);
			if (policies == null) {
				policies = new HashSet<>();
				_dataToPolicies.put(d, policies);
			}
			policies.add(policy);
		}
	}

	/**
	 * This methods converts DATAUSAGE parameters within the specified policy,
	 * such that a {@link ComparisonOperatorTypes#DATA_IN_CONTAINER} is used
	 * for later comparisons of the parameter.
	 *
	 * This method returns a {@link Pair}. It's first component
	 * ({@link Pair#getLeft()}) is the converted policy, i.e. the specified
	 * policy with some changed parameters. It's second component is a set
	 * of all {@link IData} elements referred to by this policy.
	 *
	 * 2014/09/05. FK.
	 *
	 * @param policy the policy to be converted
	 * @return as described above.
	 */
	private Pair<PolicyType,Set<IData>> convertDatausageParameters(PolicyType policy) {

		// gathers all data this policy is about
		Set<IData> allData = new HashSet<>();

		for (MechanismBaseType mech : policy.getDetectiveMechanismOrPreventiveMechanism()) {
			for (ParamMatchType p : mech.getTrigger().getParams()) {
				if (p.getType().equals(_DATAUSAGE)) {
					String value = p.getValue();
					String dataIds = p.getDataID();
					IName contName = MessageFactory.createName(value);

					if (dataIds != null) {
						// in this case there was a data id within the policy.
						// Let's use it.

						Set<IData> dataSet = createDataSetFromParamValue(dataIds);
						IStatus status = getPip().initialRepresentation(contName, dataSet);

						if (status.isStatus(EStatus.ERROR)) {
							_logger.error("Impossible to initialize representation for container " + contName + " with data id(s) " + dataSet);
							_logger.error(status.getErrorMessage());
							throw new RuntimeException(status.getErrorMessage());
						}

						allData.addAll(dataSet);
					} else {
						// there was no data id, so let's create one
						IData newData = getPip().newInitialRepresentation(contName);
						dataIds = newData.getId();
						allData.add(newData);
					}

					p.setValue(dataIds);
					p.setCmpOp(ComparisonOperatorTypes.DATA_IN_CONTAINER);
					p.setType(_DATA);
				}
			}
		}

		return Pair.of(policy, allData);
	}

	/**
	 * Returns an unmodifiable view onto the set of policies that 'talk' about
	 * the specified data.
	 *
	 * @param data
	 * @return
	 */
	@Override
	public Set<XmlPolicy> getPolicies(IData data) {
		Set<XmlPolicy> res = _dataToPolicies.get(data);

		if (res == null) {
			return Collections.emptySet();
		}

		return Collections.unmodifiableSet(res);
	}

	@Override
	public Set<XmlPolicy> listPoliciesPmp() {
		Set<XmlPolicy> res = new HashSet<XmlPolicy>();
		Collection<XmlPolicy> c = this._policymanager.getPolicies();
		for (Iterator<XmlPolicy> iterator = c.iterator(); iterator.hasNext();) {
			XmlPolicy xmlPolicy = (XmlPolicy) iterator.next();
			res.add(xmlPolicy);
		}
		return Collections.unmodifiableSet(res);
	}
	
	/**
	 * Tokenizes the specified string at whitespaces, interprets the tokens as
	 * data ids, and returns the corresponding set of {@link IData} objects
	 * created out of those tokenized data ids.
	 *
	 * @param value
	 *            the string to be transformed
	 * @return the set of {@link IData} corresponding to the tokenized data ids.
	 */
	private Set<IData> createDataSetFromParamValue(String value) {
		if (value == null || value.equals(""))
			return Collections.emptySet();

		Set<IData> res = new HashSet<>();
		StringTokenizer st = new StringTokenizer(value);
		while (st.hasMoreTokens()) {
			res.add(new DataBasic(st.nextToken()));
		}
		return res;
	}

	@Override
	public IMechanism exportMechanismPmp(String par) {
		return getPdp().exportMechanism(par);
	}

	@Override
	public IStatus revokePolicyPmp(String policyName) {
		_logger.debug("revokePolicyPmp({}) invoked.", policyName);

		IStatus status;

		if (this._policymanager.removePolicy(policyName)) {
			_distributionManager.unregisterPolicy(policyName, IPLocation.localIpLocation);
			status = getPdp().revokePolicy(policyName);
		}
		else {
			status = new StatusBasic(EStatus.OKAY);
		}

		_logger.debug("revokePolicyPmp({}) result: {}", policyName, status);

		return status;
	}

	@Override
	public IStatus revokeMechanismPmp(String policyName, String mechName) {
		return getPdp().revokeMechanism(policyName, mechName);
	}

	@Override
	public IStatus deployPolicyRawXMLPmp(String xml) {
		_logger.debug("deployPolicyRawXMLPmp invoked [" + xml + "]");

		XmlPolicy xmlPolicy = new XmlPolicy("", xml);
		return deployPolicyXMLPmp(xmlPolicy);
	}

	@Override
	public IStatus deployPolicyURIPmp(String policyFilePath) {
		if (policyFilePath.endsWith(".xml")) {
			try {
				return deployPolicyRawXMLPmp(Files.toString(new File(policyFilePath), Charset.defaultCharset()));
			} catch (Exception e) {
				return new StatusBasic(EStatus.ERROR, e.getMessage());
			}
		}
		return new StatusBasic(EStatus.ERROR, "Error while loading policy file " + policyFilePath);
	}

	@Override
	public IStatus deployPolicyXMLPmp(XmlPolicy xmlPolicy) {
		
		String xml = xmlPolicy.getXml();
		// Convert the string xml to a PolicyType
		PolicyType policy = xmlToPolicy(xml);
		
		if (this._policymanager.addPolicy(xmlPolicy)) {

			// Initialize the initial representations specified in the policy
			initInitialRepresentations(policy);

			// Convert DATAUSAGE parameters within the policy
			Pair<PolicyType,Set<IData>> convertedPolicy = convertDatausageParameters(policy);

			// create an XmlPolicy out of the converted policy; the policy's name remains the same
			final XmlPolicy convertedXmlPolicy = new XmlPolicy(policy.getName(), policyToXML(convertedPolicy.getLeft()), 
														xmlPolicy.getDescription(), xmlPolicy.getTemplateId(), xmlPolicy.getTemplateXml(), 
														xmlPolicy.getDataClass());

			// map all data IDs to the new XmlPolicy
			mapDataToPolicy(convertedPolicy.getRight(), convertedXmlPolicy);

			if (Settings.getInstance().getDistributionEnabled()) {
				/*
				 * As registering the policy for remote
				 * purposes might take a while, we start
				 * the registration process in a new thread and go on.
				 */
				new Thread() {
					@Override
					public void run() {
						_distributionManager.registerPolicy(convertedXmlPolicy);
					}
				}.start();
			}

			// finally, deploy at the PDP
			return getPdp().deployPolicyXML(convertedXmlPolicy);
		}
		else {
			_logger.debug("Policy was deployed before. Not deploying again.");
			return new StatusBasic(EStatus.OKAY);
		}
		
	}

	@Override
	public Map<String, Set<String>> listMechanismsPmp() {
		return getPdp().listMechanisms();
	}

	
	public void stop() {
		// TODO Auto-generated method stub

	}

	@Override
	
	public IStatus specifyPolicyFor(Set<IContainer> representations, String dataClass) {
		// TODO Here goes Prachi & Cipri's code
		_logger.debug("Here goes Prachi's and Cipri's code");
		_logger.debug("the String value for the dataClass that matches any dataclass is " + Settings.getInstance().getPolicySpecificationStarDataClass());

		_logger.debug("specifyPolicyFor method invoked for containers " + representations + " and dataclass " + dataClass);
		if (representations==null||"".equals(dataClass)) return new StatusBasic(EStatus.ERROR);
		return new StatusBasic(EStatus.OKAY);
	}

	/**
	 * Policies are translated and then deployed on the PDP.
	 */
	@Override
	public IPtpResponse translatePolicy(String requestId, Map<String, String> parameters, XmlPolicy xmlPolicy) {

		String policyname = xmlPolicy.getName();
		String policyTemplate = xmlPolicy.getXml();
		
		IPtpResponse translationResponse = _ptp.translatePolicy(requestId, parameters, xmlPolicy);
		if(translationResponse.getStatus().equals(EStatus.ERROR))
			return translationResponse;

		XmlPolicy translatedPolicy = translationResponse.getPolicy();

		IStatus revocationStatus = this.revokePolicyPmp(translatedPolicy.getName());
		
		IStatus deploymentStatus = this.deployPolicyXMLPmp(translatedPolicy);

		PtpResponseBasic deploymentResponse = new PtpResponseBasic(deploymentStatus, translatedPolicy);
		return deploymentResponse;
	}

	@Override
	public IPtpResponse updateDomainModel(String requestId,	Map<String, String> parameters, XmlPolicy xmlDomainModel) {
		return _ptp.updateDomainModel(requestId, parameters, xmlDomainModel);
	}

	
}
