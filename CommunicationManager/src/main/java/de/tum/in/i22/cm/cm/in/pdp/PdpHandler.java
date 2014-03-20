package de.tum.in.i22.cm.cm.in.pdp;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Logger;

import testutil.DummyMessageGen;

import com.google.inject.Inject;

import de.tum.in.i22.cm.pdp.internal.Decision;
import de.tum.in.i22.cm.pdp.internal.Event;
import de.tum.in.i22.cm.pdp.internal.PolicyDecisionPoint;
import de.tum.in.i22.uc.cm.basic.ResponseBasic;
import de.tum.in.i22.uc.cm.basic.StatusBasic;
import de.tum.in.i22.uc.cm.datatypes.EConflictResolution;
import de.tum.in.i22.uc.cm.datatypes.EStatus;
import de.tum.in.i22.uc.cm.datatypes.IEvent;
import de.tum.in.i22.uc.cm.datatypes.IMechanism;
import de.tum.in.i22.uc.cm.datatypes.IPdpMechanism;
import de.tum.in.i22.uc.cm.datatypes.IPipDeployer;
import de.tum.in.i22.uc.cm.datatypes.IPxpSpec;
import de.tum.in.i22.uc.cm.datatypes.IResponse;
import de.tum.in.i22.uc.cm.datatypes.IStatus;
import de.tum.in.i22.uc.cm.interfaces.IPdpCore2PipCacher;
import de.tum.in.i22.uc.cm.interfaces.IPdpEngine2PipCacher;
import de.tum.in.i22.uc.cm.interfaces.IPdpIncoming;

/**
 * This contains some tests to run the PIP "inside" the PDP
 *
 * @author Lovat
 *
 */
public class PdpHandler implements IPdpIncoming {

	private static final Logger _logger = Logger.getLogger(PdpHandler.class);

	private final PolicyDecisionPoint _lpdp;

	public PdpHandler(PolicyDecisionPoint lpdp){
		_lpdp = lpdp;
		try {
			_logger.info("JavaPDP started");
//			_lpdp.deployPolicy(System.getProperty("user.dir")+"/../PdpCore/src/main/resources/testTUM.xml");
//			_logger.info("Test policy deployed");
		} catch (Exception e) {
			_logger.fatal("Could not load native PDP library! " + e.getMessage());
		}
	}


	@Override
	public IResponse notifyEvent(IEvent event) {
		if (event==null){
			return new ResponseBasic(new StatusBasic(EStatus.ERROR,"null event received"), null, null);
		}
		Decision d = _lpdp.notifyEvent(new Event(event));
		return d.getResponse();
	}

}
