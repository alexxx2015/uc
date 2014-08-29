package de.tum.in.i22.uc.pmp;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
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
import javax.xml.bind.UnmarshalException;
import javax.xml.bind.Unmarshaller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.Files;

import de.tum.in.i22.uc.cm.datatypes.basic.DataBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.NameBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.PtpResponseBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic.EStatus;
import de.tum.in.i22.uc.cm.datatypes.basic.XmlPolicy;
import de.tum.in.i22.uc.cm.datatypes.excel.CellName;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IContainer;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IData;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IMechanism;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IName;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IPtpResponse;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.cm.distribution.LocalLocation;
import de.tum.in.i22.uc.cm.distribution.Location;
import de.tum.in.i22.uc.cm.interfaces.IAny2Pip;
import de.tum.in.i22.uc.cm.interfaces.IPmp2Ptp;
import de.tum.in.i22.uc.cm.processing.PmpProcessor;
import de.tum.in.i22.uc.cm.processing.dummy.DummyPdpProcessor;
import de.tum.in.i22.uc.cm.processing.dummy.DummyPipProcessor;
import de.tum.in.i22.uc.cm.settings.Settings;
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

	private final ObjectFactory of = new ObjectFactory();

	/**
	 * Maps data to the XML policies in which it occurs. Used for fast lookup of
	 * policies for a given data.
	 */
	private final Map<IData, Set<XmlPolicy>> _dataToPolicies;

	private final static String _DATAUSAGE = "dataUsage";
	private final static String _DATA = "data";

	private static final String JAXB_CONTEXT = "de.tum.in.i22.uc.pmp.xsd";

	private IPmp2Ptp _ptp;

	public PmpHandler() {
		super(LocalLocation.getInstance());
		init(new DummyPipProcessor(), new DummyPdpProcessor());
		_dataToPolicies = new ConcurrentHashMap<>();
		_ptp = new PtpHandler();
	}

	private PolicyType xmlToPolicy(String XMLPolicy) {
		PolicyType curPolicy = null;
		_logger.debug("XMLtoPolicy");
		_logger.trace("Policy to be converted: " + XMLPolicy);
		InputStream inp = new ByteArrayInputStream(XMLPolicy.getBytes());
		try {
			JAXBContext jc = JAXBContext.newInstance(JAXB_CONTEXT);
			Unmarshaller u = jc.createUnmarshaller();

			JAXBElement<?> poElement = (JAXBElement<?>) u.unmarshal(inp);

			curPolicy = (PolicyType) poElement.getValue();

			_logger.debug("curPolicy [name=" + curPolicy.getName() + ", "
					+ curPolicy.toString());

		} catch (UnmarshalException e) {
			_logger.error("Syntax error in policy: " + e.getMessage());
		} catch (JAXBException | ClassCastException e) {
			e.printStackTrace();
		}
		return curPolicy;
	}

	private String policyToXML(PolicyType policy) {
		String result = "";
		_logger.debug("PolicyToXML conversion...");
		ObjectFactory of = new ObjectFactory();
		JAXBElement<PolicyType> pol = of.createPolicy(policy);
		try {
			JAXBContext jc = JAXBContext.newInstance(JAXB_CONTEXT);
			Marshaller m = jc.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			StringWriter res = new StringWriter();
			m.marshal(pol, res);

			result = res.toString();

			_logger.trace("converted policy: " + result);

		} catch (JAXBException | ClassCastException e) {
			e.printStackTrace();
		}

		return result;
	}

	// TODO To whoever wrote this: Please add a comment what is happening.
	// Also because it seems that it is not just 'conversion' (e.g.
	// a new initial representation is created)
	// Also, please document of which format which strings are (XML?!)
	private XmlPolicy convertPolicy(XmlPolicy xmlPolicy) {
		IAny2Pip pip = getPip();

		if (pip == null) {
			_logger.error("PIP NOT AVAILABLE. Better crash now than living like this.");
			throw new RuntimeException(
					"PIP NOT AVAILABLE for policy conversion");
		}

		_logger.info("converting policy string into object");
		PolicyType policy = xmlToPolicy(xmlPolicy.getXml());

		if (policy.isSetInitialRepresentations()) {
			InitialRepresentationType ir = policy.getInitialRepresentations();
			if (ir.isSetContainer()) {
				List<ContainerType> conts = ir.getContainer();
				for (ContainerType c : conts) {
					String name=c.getName();
					IName contName;

					//TODO: make it generic
					if (name.startsWith("EXCEL-"))
						contName = new CellName(c.getName().substring(c.getName().indexOf('-')+1));
					else
						contName = new NameBasic(c.getName());
					if (c.isSetDataId()) {
						Set<IData> dataSet = new HashSet<IData>();
						for (String dataId : c.getDataId()) {
							if (dataId != null && !dataId.equals(""))
								dataSet.add(new DataBasic(dataId.trim()));
						}
						IStatus status = getPip().initialRepresentation(
								contName, dataSet);
						if (status.isStatus(EStatus.ERROR)) {
							_logger.error("impossible to initialize representation for container "
									+ contName + " with data id(s) " + dataSet);
							_logger.error(status.getErrorMessage());
							throw new RuntimeException(status.getErrorMessage());
						}
					}

				}
			}
		}

		// gathers all data this policy is about
		Set<IData> allData = new HashSet<>();

		for (MechanismBaseType mech : policy.getDetectiveMechanismOrPreventiveMechanism()) {
			List<ParamMatchType> paramList = mech.getTrigger().getParams();
			List<ParamMatchType> newParamList = new ArrayList<>();

			for (ParamMatchType p : paramList) {
				//TODO: make it generic
				boolean isExcelContainer=false;
				if (p.getType().equals(_DATAUSAGE)) {
					String dataIds = null;
					String value = p.getValue();
					//TODO: make it generic
					if (value.startsWith("EXCEL-")){
						isExcelContainer=true;
						value=value.substring(value.indexOf('-')+1);
					}

					String name = p.getName();
					// ComparisonOperatorTypes cmpOp=p.getCmpOp();

					if (p.isSetDataID()) {
						// in this case there was a data id within the policy.
						// Let's use it.

						dataIds = p.getDataID();
						Set<IData> dataSet = createDataSetFromParamValue(dataIds);
						IStatus status;

						if (isExcelContainer) status= getPip().initialRepresentation(
								new CellName(value), dataSet);
						else status= getPip().initialRepresentation(
								new NameBasic(value), dataSet);

						if (status.isStatus(EStatus.ERROR)) {
							_logger.error("impossible to initialize representation for container "
									+ value + " with data id(s) " + dataSet);
							_logger.error(status.getErrorMessage());
							throw new RuntimeException(status.getErrorMessage());
						}

						allData.addAll(dataSet);
					} else {
						// there was no data id, so let's create one
						IData newData;
						if (isExcelContainer) newData = getPip().newInitialRepresentation(
								new CellName(value));
						else newData = getPip().newInitialRepresentation(
								new NameBasic(value));
						dataIds = newData.getId();
						allData.add(newData);
					}

					ParamMatchType newP = of.createParamMatchType();
					newP.setName(name);
					newP.setValue(dataIds);
					newP.setCmpOp(ComparisonOperatorTypes.DATA_IN_CONTAINER);
					newP.setType(_DATA);

					newParamList.add(newP);
				} else {
					newParamList.add(p);
				}
			}
			mech.getTrigger().getParams().clear();
			mech.getTrigger().getParams().addAll(newParamList);
		}

		_logger.info("converting object into policy string");
		XmlPolicy convertedXmlPolicy = new XmlPolicy(xmlPolicy.getName(),
				policyToXML(policy));

		// map each gathered data to this converted policy.
		for (IData data : allData) {
			Set<XmlPolicy> pol = _dataToPolicies.get(data);
			if (pol == null) {
				pol = new HashSet<>();
				_dataToPolicies.put(data, pol);
			}
			pol.add(convertedXmlPolicy);
		}

		return convertedXmlPolicy;
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
	public IStatus informRemoteDataFlow(Location srcLocation,
			Location dstLocation, Set<IData> dataflow) {

		// TODO: Get policies for data and send them

		return new StatusBasic(EStatus.ERROR);
	}

	@Override
	public IMechanism exportMechanismPmp(String par) {
		return getPdp().exportMechanism(par);
	}

	@Override
	public IStatus revokePolicyPmp(String policyName) {
		return getPdp().revokePolicy(policyName);
	}

	@Override
	public IStatus revokeMechanismPmp(String policyName, String mechName) {
		return getPdp().revokeMechanism(policyName, mechName);
	}

	@Override
	public IStatus deployPolicyRawXMLPmp(String xml) {
		_logger.debug("deployPolicyRawXMLPmp invoked [" + xml + "]");
		return deployPolicyXMLPmp(new XmlPolicy(xmlToPolicy(xml).getName(), xml));
	}

	@Override
	public IStatus deployPolicyURIPmp(String policyFilePath) {
		if (policyFilePath.endsWith(".xml")) {
			try {
				return deployPolicyRawXMLPmp(Files.toString(new File(policyFilePath), Charset.defaultCharset()));
			} catch (Exception e) {
				_logger.warn("Error deploying policy {}: {}", policyFilePath, e);
			}
		}
		return new StatusBasic(EStatus.ERROR, "Error while loading policy file " + policyFilePath);
	}

	@Override
	public IStatus deployPolicyXMLPmp(XmlPolicy xmlPolicy) {
		XmlPolicy convertedPolicy = convertPolicy(xmlPolicy);
		return getPdp().deployPolicyXML(convertedPolicy);
	}

	@Override
	public Map<String, Set<String>> listMechanismsPmp() {
		return getPdp().listMechanisms();
	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub

	}

	@Override
	public IStatus specifyPolicyFor(Set<IContainer> representations,
			String dataClass) {
		// TODO Here goes Prachi & Cipri's code
		_logger.debug("Here goes Prachi's and Cipri's code");
		_logger.debug("the String value for the dataClass that matches any dataclass is " + Settings.getInstance().getPolicySpecificationStarDataClass());

		_logger.debug("specifyPolicyFor method invoked for containers " + representations + " and dataclass " + dataClass);
		if (representations==null||"".equals(dataClass)) return new StatusBasic(EStatus.ERROR);
		return new StatusBasic(EStatus.OKAY);
	}

	/* (non-Javadoc)
	 * @see de.tum.in.i22.uc.cm.interfaces.IPmp2Ptp#translatePolicy(java.lang.String, java.util.Map, de.tum.in.i22.uc.cm.datatypes.basic.XmlPolicy)
	 * Policies are translated and then deployed on the PDP.
	 */
	@Override
	public IPtpResponse translatePolicy(String requestId, Map<String, String> parameters, XmlPolicy xmlPolicy) {
		
		IPtpResponse translationResponse = _ptp.translatePolicy(requestId, parameters, xmlPolicy); 
		if(translationResponse.getStatus().equals(EStatus.ERROR))
			return translationResponse;
		
		XmlPolicy translatedPolicy = translationResponse.getPolicy();
		
		IStatus deploymentStatus = this.deployPolicyXMLPmp(translatedPolicy);
		
		PtpResponseBasic deploymentResponse = new PtpResponseBasic(deploymentStatus, translatedPolicy);
		return deploymentResponse;
	}

	@Override
	public IPtpResponse updateDomainModel(String requestId,	Map<String, String> parameters, XmlPolicy xmlDomainModel) {
		return _ptp.updateDomainModel(requestId, parameters, xmlDomainModel);
	}
}
