package de.tum.in.i22.cm.in.pdp;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Logger;

import de.tum.in.i22.pdp.internal.Decision;
import de.tum.in.i22.pdp.internal.Event;
import de.tum.in.i22.pdp.internal.PolicyDecisionPoint;
import de.tum.in.i22.uc.cm.basic.ResponseBasic;
import de.tum.in.i22.uc.cm.basic.StatusBasic;
import de.tum.in.i22.uc.cm.datatypes.EStatus;
import de.tum.in.i22.uc.cm.datatypes.IEvent;
import de.tum.in.i22.uc.cm.datatypes.IMechanism;
import de.tum.in.i22.uc.cm.datatypes.IPdpMechanism;
import de.tum.in.i22.uc.cm.datatypes.IPxpSpec;
import de.tum.in.i22.uc.cm.datatypes.IResponse;
import de.tum.in.i22.uc.cm.datatypes.IStatus;
import de.tum.in.i22.uc.cm.interfaces.IAny2Pdp;

/**
 * This contains some tests to run the PIP "inside" the PDP
 *
 * @author Lovat
 *
 */
public class PdpHandler implements IAny2Pdp {

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


	@Override
	public IStatus deployMechanism(IMechanism mechanism) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public IMechanism exportMechanism(String par) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public IStatus revokeMechanism(String policyName) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public IStatus revokeMechanism(String policyName, String mechName) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public IStatus deployPolicy(String policyFilePath) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public HashMap<String, ArrayList<IPdpMechanism>> listMechanisms() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public boolean registerPxp(IPxpSpec pxp) {
		// TODO Auto-generated method stub
		return false;
	}

}
