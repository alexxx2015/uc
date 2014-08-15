package de.tum.in.i22.uc.cm.interfaces;

import java.util.Map;

import de.tum.in.i22.uc.cm.datatypes.basic.XmlPolicy;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IPtpResponse;
import de.tum.in.i22.uc.thrift.generator.AThriftMethod;
import de.tum.in.i22.uc.thrift.generator.AThriftService;

/**
 * Interface defining methods a PMP can invoke on a PTP.
 * @author Kelbert & Lovat
 *
 */
@AThriftService(name="TPmp2Ptp")
public interface IPmp2Ptp {
		
	@AThriftMethod(signature="Types.TPtpResponse translatePolicy(1: string requestId, 2: map<string,string> parameters, 3: Types.TXmlPolicy xmlPolicy)")
	public IPtpResponse translatePolicy(String requestId,	Map<String, String> parameters, XmlPolicy xmlPolicy);
		
	@AThriftMethod(signature="Types.TPtpResponse updateDomainModel(1: string requestId, 2: map<string,string> parameters, 3: Types.TXmlPolicy xmlDomainModel)")
	public IPtpResponse updateDomainModel(String requestId,	Map<String, String> parameters, XmlPolicy xmlDomainModel);
}
