package de.tum.in.i22.uc.remotelistener;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.datatypes.basic.XmlPolicy;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IPtpResponse;
import de.tum.in.i22.uc.cm.interfaces.IPmp2Ptp;

public class PtpHandler implements IPmp2Ptp {

	private static final Logger _logger = LoggerFactory.getLogger(PtpHandler.class);
	
	@Override
	public IPtpResponse translatePolicy(String requestId,
			Map<String, String> parameters, XmlPolicy xmlPolicy) {
		_logger.info("translate policy");
		return null;
	}

	@Override
	public IPtpResponse updateDomainModel(String requestId,
			Map<String, String> parameters, XmlPolicy xmlDomainModel) {
		_logger.info("updateDomainModel");
		return null;
	}

}
