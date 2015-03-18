package de.tum.in.i22.uc.pmp;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
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
import de.tum.in.i22.uc.ptp.PtpHandler;

public class PmpHandler extends PmpProcessor {
	private static final Logger _logger = LoggerFactory.getLogger(PmpHandler.class);

	/**
	 * Maps data to the XML policies in which it occurs.
	 * Used for fast lookup of
	 * policies for a given data.
	 */
	private final Map<IData, Set<XmlPolicy>> _dataToPolicies;

	private final static String _DATAUSAGE = "dataUsage";
	private final static String _DATA = "data";

	private final Marshaller _marshaller;
	private final Unmarshaller _unmarshaller;

	private final IPmp2Ptp _ptp;
	private final PolicyManager _policymanager;

	public PmpHandler() {
		super(LocalLocation.getInstance());
		init(new DummyPipProcessor(), new DummyPdpProcessor());
		_dataToPolicies = new ConcurrentHashMap<>();
		_ptp = new PtpHandler();
		_policymanager = new PolicyManager();

		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(Settings.getInstance().getPmpJaxbContext());
			_marshaller = jaxbContext.createMarshaller();
			_marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			_unmarshaller = jaxbContext.createUnmarshaller();
		} catch (JAXBException e) {
			throw new RuntimeException("Unable to create Marshaller or Unmarshaller: " + e.getMessage());
		}
	}

	private PolicyType xmlToPolicy(String xml) {
		_logger.debug("xmlToPolicy: " + xml);

		PolicyType policy = null;
		InputStream inp = new ByteArrayInputStream(xml.getBytes());

		try {
			policy = (PolicyType) ((JAXBElement<?>) _unmarshaller.unmarshal(inp)).getValue();
		} catch (JAXBException e) {
			_logger.error("Unable to unmarshal policy: " + e.getMessage());
			throw new IllegalArgumentException("Policy could not be parsed.");
		}

		_logger.debug("curPolicy [name=" + policy.getName() + ", " + policy.toString());
		return policy;
	}

	private String policyToXML(PolicyType policy) {
		_logger.debug("PolicyToXML({}) invoked.", policy);

		StringWriter res = new StringWriter();

		try {
			_marshaller.marshal(new ObjectFactory().createPolicy(policy), res);
		} catch (JAXBException e) {
			_logger.error(e.getMessage());
		}

		_logger.trace("converted policy: " + res.toString());
		return res.toString();
	}

	/**
	 * Initializes the initial representations specified
	 * within the given policy. This method returns a set of data,
	 * namely all {@link IData}s that have been identified within
	 * the policy's initial representations.
	 *
	 * 2014/11/06. Extended by FK.
	 *
	 * @param policy the policy of which the initial representations are
	 * 		initialized.
	 * @return the set of identified {@link IData}s.
	 */
	private Set<IData> initInitialRepresentations(PolicyType policy) {
		if (!policy.isSetInitialRepresentations()) {
			return Collections.emptySet();
		}

		InitialRepresentationType ir = policy.getInitialRepresentations();

		if (!ir.isSetContainer()) {
			return Collections.emptySet();
		}

		Set<IData> allData = new HashSet<>();

		for (ContainerType c : ir.getContainer()) {

			if (c.isSetDataId()) {
				Set<IData> dataSet = new HashSet<>();
				for (String dataId : c.getDataId()) {
					dataId = dataId.trim();
					if (!dataId.isEmpty()) {
						dataSet.add(new DataBasic(dataId));
					}
				}

				IName contName = MessageFactory.createName(c.getName());
				IStatus status = getPip().initialRepresentation(contName, dataSet);

				allData.addAll(dataSet);

				if (status.isStatus(EStatus.ERROR)) {
					_logger.error("Unable to initialize representation for container " + contName + " with data id(s) "
							+ dataSet + ": " + status.getErrorMessage());
					throw new RuntimeException(status.getErrorMessage());
				}
			}
		}

		return allData;
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
					String dataIds;
					IName contName = MessageFactory.createName(value);

					if (p.isSetDataID()) {
						// in this case there was a data id within the policy.
						// Let's use it.

						dataIds = p.getDataID();

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
		return new HashSet<>(_policymanager.getPolicies());
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

		IStatus status;

		if (_policymanager.removePolicy(policyName)) {
			_distributionManager.deregister(policyName, IPLocation.localIpLocation);
			status = getPdp().revokePolicy(policyName);
		}
		else {
			_logger.debug("revokePolicyPmp({}) result: {}", policyName, "Not Found!");
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
	public IStatus deployPolicyRawXMLPmp(String xml) {
		_logger.debug("deployPolicyRawXMLPmp invoked [{}]", xml);

		XmlPolicy xmlPolicy = new XmlPolicy("", xml);
		return deployPolicyXMLPmp(xmlPolicy);
	}

	@Override
	public IStatus remotePolicyTransfer(XmlPolicy xml, String from) {
		_logger.debug("remotePolicyTransfer invoked [{}, {}]", xml, from);

//		XmlPolicy xmlPolicy = new XmlPolicy("", xml);
		return deployPolicyXMLPmp(xml, from);
	}

	@Override
	public IStatus deployPolicyXMLPmp(XmlPolicy xmlPolicy) {
		return deployPolicyXMLPmp(xmlPolicy, null);
	}

	private IStatus deployPolicyXMLPmp(XmlPolicy xmlPolicy, String from) {
		Pair<IStatus,XmlPolicy> status = deployPolicyXMLPmp_impl(xmlPolicy);

		if (status.getLeft().isStatus(EStatus.OKAY)
				&& status.getRight() != null
				&& Settings.getInstance().isDistributionEnabled()) {
			_distributionManager.register(status.getRight(), from);
		}

		return status.getLeft();
	}

	private Pair<IStatus,XmlPolicy> deployPolicyXMLPmp_impl(XmlPolicy xmlPolicy) {

		String xml = xmlPolicy.getXml();
		PolicyType policy;

		// Convert the string xml to a PolicyType
		try {
			policy = xmlToPolicy(xml);
		} catch (IllegalArgumentException e) {
			_logger.error(e.getMessage());
			return Pair.of(new StatusBasic(EStatus.ERROR), null);
		}

		// When you receive a raw xml policy, the object created is without a name.
		xmlPolicy.setName(policy.getName());

		if (_policymanager.addPolicy(xmlPolicy)) {

			Set<IData> allData = new HashSet<>();

			// Initialize the initial representations specified in the policy
			// and retrieve the corresponding data IDs.
			allData.addAll(initInitialRepresentations(policy));

			// Convert DATAUSAGE parameters within the policy
			Pair<PolicyType,Set<IData>> convertedPolicy = convertDatausageParameters(policy);

			// create an XmlPolicy out of the converted policy;
			// the policy's name remains the same
			XmlPolicy convertedXmlPolicy = new XmlPolicy(policy.getName(), policyToXML(convertedPolicy.getLeft()),
														xmlPolicy.getDescription(), xmlPolicy.getTemplateId(), xmlPolicy.getTemplateXml(),
														xmlPolicy.getDataClass());

			// We got some further data IDs. Add them to the set of all data IDs.
			allData.addAll(convertedPolicy.getRight());

			// Map all data IDs to the new XmlPolicy
			mapDataToPolicy(allData, convertedXmlPolicy);

			// Deploy at the PDP
			return Pair.of(getPdp().deployPolicyXML(convertedXmlPolicy), convertedXmlPolicy);
		}
		else {
			_logger.debug("Policy was deployed before. Not deploying again.");
			return Pair.of(new StatusBasic(EStatus.OKAY), null);
		}

	}

	@Override
	public Map<String, Set<String>> listMechanismsPmp() {
		return getPdp().listMechanisms();
	}


	public void stop() {
		// TODO Auto-generated method stub

	}


	/**
	 * Policies are translated and then deployed on the PDP.
	 * If a policies submitted to be translated already exists,
	 * it is revoked and deployed again.
	 */
	@Override
	public IPtpResponse translatePolicy(String requestId, Map<String, String> parameters, XmlPolicy xmlPolicy) {

		String policyname = xmlPolicy.getName();
		String policyTemplate = xmlPolicy.getTemplateXml();
		String log = "TranslatePolicy request: " + policyname + " "+ policyTemplate;
		_logger.info(log);

		Map<String, String> policyParam = this._policymanager.getPolicyParam(xmlPolicy);
		policyParam.putAll(parameters);

		IPtpResponse translationResponse = _ptp.translatePolicy(requestId, policyParam, xmlPolicy);
		log = "Translated policy status: "+translationResponse.getStatus().getEStatus()+" " + policyname + " "+ xmlPolicy.getXml();
		_logger.info(log);
		if(translationResponse.getStatus().isStatus(EStatus.ERROR))
			return translationResponse;

		//TODO: used only for debugging - to be removed
		String PMP_MODE =  parameters.get("PMP_MODE");
		if(PMP_MODE == null)
			PMP_MODE = "NORMAL";
		if(PMP_MODE.equals("TRANSLATE_ONLY"))
			return translationResponse;

		XmlPolicy translatedPolicy = translationResponse.getPolicy();
		this._policymanager.addPolicyParam(xmlPolicy, policyParam);

		/* revoke the policy in case it was deployed before. */
		revokePolicyPmp(translatedPolicy.getName());

		IStatus deploymentStatus = deployPolicyXMLPmp(translatedPolicy);

		PtpResponseBasic deploymentResponse = new PtpResponseBasic(deploymentStatus, translatedPolicy, deploymentStatus.getErrorMessage()+"");
		return deploymentResponse;
	}

	@Override
	public IPtpResponse updateDomainModel(String requestId,	Map<String, String> parameters, XmlPolicy xmlDomainModel) {
		IPtpResponse updateResponse = _ptp.updateDomainModel(requestId, parameters, xmlDomainModel);
		//response status: MODIFY - the domain model was changed
		//response status: OKAY - no changes made
		//response status: ERROR - error while adapting

		//do not retranslate if there are no changes
		if(!updateResponse.getStatus().getEStatus().equals(EStatus.MODIFY)){
			_logger.info("no retranslation of policies after update");
			return updateResponse;
		}

		//the following lines where used only for testing purposes
		//if PMP_MODE:ADAPTATION_ONLY then
		//the method simply returns without retranslating the policies
		String MODE = parameters.get("PMP_MODE");
		if(MODE != null){
			if(MODE.equals("ADAPTATION_ONLY"))
				return updateResponse;
		}

		//retranslate all policies
		Map<String,String> param = new HashMap<String, String>();
		List<XmlPolicy> policies = this._policymanager.getPolicies();
		for(XmlPolicy p : policies){
			String requestIdentifier = p.getName();
			param = this._policymanager.getPolicyParam(p);
			this.translatePolicy(requestIdentifier, param, p);
		}
		_logger.info("UpdateDomainModel response: "+ updateResponse.getStatus());
		return updateResponse;
	}


}
