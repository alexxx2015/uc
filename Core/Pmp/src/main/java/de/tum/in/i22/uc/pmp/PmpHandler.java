package de.tum.in.i22.uc.pmp;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
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

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.UnmarshalException;
import javax.xml.bind.Unmarshaller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.datatypes.basic.DataBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.NameBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic.EStatus;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IData;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IMechanism;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.cm.distribution.Location;
import de.tum.in.i22.uc.cm.interfaces.IAny2Pip;
import de.tum.in.i22.uc.cm.processing.PmpProcessor;
import de.tum.in.i22.uc.cm.processing.dummy.DummyPdpProcessor;
import de.tum.in.i22.uc.cm.processing.dummy.DummyPipProcessor;
import de.tum.in.i22.uc.pmp.extensions.distribution.PmpDistributionManager;
import de.tum.in.i22.uc.pmp.xsd.ComparisonOperatorTypes;
import de.tum.in.i22.uc.pmp.xsd.MechanismBaseType;
import de.tum.in.i22.uc.pmp.xsd.ObjectFactory;
import de.tum.in.i22.uc.pmp.xsd.ParamMatchType;
import de.tum.in.i22.uc.pmp.xsd.PolicyType;

public class PmpHandler extends PmpProcessor {
	private static Logger log = LoggerFactory.getLogger(PmpHandler.class);

	private final PmpDistributionManager _distributedPmpManager;

	private final ObjectFactory of = new ObjectFactory();

	private final static String _DATAUSAGE = "dataUsage";
	private final static String _DATA = "data";

	public PmpHandler() {
		init(new DummyPipProcessor(), new DummyPdpProcessor());
		_distributedPmpManager = new PmpDistributionManager();
	}

	private PolicyType XMLtoPolicy(String XMLPolicy) {
		PolicyType curPolicy = null;
		log.debug("XMLtoPolicy");
		log.trace("Policyto be converted: " + XMLPolicy);
		InputStream inp = new ByteArrayInputStream(XMLPolicy.getBytes());
		try {
			JAXBContext jc = JAXBContext
					.newInstance("de.tum.in.i22.uc.pmp.xsd");
			Unmarshaller u = jc.createUnmarshaller();

			JAXBElement<?> poElement = (JAXBElement<?>) u.unmarshal(inp);

			curPolicy = (PolicyType) poElement.getValue();

			log.debug("curPolicy [name=" + curPolicy.getName() + ", "
					+ curPolicy.toString());

		} catch (UnmarshalException e) {
			log.error("Syntax error in policy: " + e.getMessage());
		} catch (JAXBException | ClassCastException e) {
			e.printStackTrace();
		}
		return curPolicy;
	}

	private String PolicyToXML(PolicyType policy) {
		String result = "";
		log.debug("PolicyToXML conversion...");
		log.trace("Policy to convert: " + policy);
		ObjectFactory of = new ObjectFactory();
		JAXBElement<PolicyType> pol = of.createPolicy(policy);
		try {
			JAXBContext jc = JAXBContext
					.newInstance("de.tum.in.i22.uc.pmp.xsd");
			Marshaller m = jc.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			StringWriter res = new StringWriter();
			m.marshal(pol, res);

			result = res.toString();

			log.trace("converted policy: " + result);

			// } catch (MarshalException e) {
			// log.error("Syntax error in policy: " + e.getMessage());
		} catch (JAXBException | ClassCastException e) {
			e.printStackTrace();
		}

		return result;
	}

	@Override
	public IStatus receivePolicies(Set<String> policies) {
		String output = convertPolicies(policies);

		// HERE GOES THE DEPLOYMENT TO THE PDP
		getPdp().deployPolicyXML(output);

		return new StatusBasic(EStatus.OKAY);
	}

	public String convertPolicies(Set<String> policies) {
		String output = "";

		IAny2Pip pip = getPip();

		if (pip == null) {
			log.error("PIP NOT AVAILABLE. Better crash now than living like this.");
			throw new RuntimeException(
					"PIP NOT AVAILABLE for policy conversion");
		}

		for (String ps : policies) {
			log.info("converting policy string into object");
			PolicyType policy = XMLtoPolicy(ps);

			List<MechanismBaseType> mechanisms = policy
					.getDetectiveMechanismOrPreventiveMechanism();

			for (MechanismBaseType mech : mechanisms) {
				List<ParamMatchType> paramList = mech.getTrigger().getParams();
				List<ParamMatchType> newParamList = new ArrayList<ParamMatchType>();
				for (ParamMatchType p : paramList) {
					if (p.getType().equals(_DATAUSAGE)) {
						String dataId = null;
						String value = p.getValue();
						String name = p.getName();
						String newValue = null;
						// ComparisonOperatorTypes cmpOp=p.getCmpOp();

						if (p.isSetDataID()) {
							dataId = p.getDataID();
							Set<IData> dataSet = createDataSetFromParamValue(dataId);
							IStatus status = getPip().initialRepresentation(
									new NameBasic(value), dataSet);
							newValue = dataId;
							if (status.isStatus(EStatus.ERROR)) {
								log.error("impossible to initialize representation for container "
										+ value + " with data id(s) " + dataSet);
								log.error(status.getErrorMessage());
								throw new RuntimeException(
										status.getErrorMessage());
							}
						} else {
							IData newData = getPip().newInitialRepresentation(new NameBasic(value));
							newValue = newData.getId();
						}

						ParamMatchType newP = of.createParamMatchType();
						newP.setName(name);
						newP.setValue(newValue);
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

			log.info("converting object into policy string");
			output = PolicyToXML(policy);
		}
		return output;
	}

	private Set<IData> createDataSetFromParamValue(String value) {
		if ((value == null) || (value.equals("")))
			return Collections.emptySet();

		Set<IData> res = new HashSet<IData>();
		StringTokenizer st = new StringTokenizer(value);
		while (st.hasMoreTokens()) {
			res.add(new DataBasic(st.nextToken()));
		}
		return res;
	}

	@Override
	public IStatus informRemoteDataFlow(Location srcLocation,
			Location dstLocation, Set<IData> dataflow) {
		Set<IData> d = new HashSet<>();

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
	public IStatus deployPolicyURIPmp(String policyFilePath) {
		if (policyFilePath.endsWith(".xml")) {
			try {
				return deployPolicyXMLPmp(com.google.common.io.Files.toString(new File(policyFilePath),Charset.defaultCharset()));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return new StatusBasic(EStatus.ERROR,"Error while loading policy file " + policyFilePath);
	}

	@Override
	public IStatus deployPolicyXMLPmp(String XMLPolicy) {
		return receivePolicies(Collections.singleton(XMLPolicy));
	}

	@Override
	public Map<String, List<String>> listMechanismsPmp() {
		return getPdp().listMechanisms();
	}
}
