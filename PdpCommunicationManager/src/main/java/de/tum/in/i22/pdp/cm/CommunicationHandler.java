package de.tum.in.i22.pdp.cm;

import org.apache.log4j.Logger;

import testutil.DummyMessageGen;
import de.tum.in.i22.pdp.cm.in.IIncoming;
import de.tum.in.i22.uc.cm.datatypes.IEvent;
import de.tum.in.i22.uc.cm.datatypes.IMechanism;
import de.tum.in.i22.uc.cm.datatypes.IResponse;
import de.tum.in.i22.uc.cm.gpb.PdpProtos.GpStatus.EStatus;

/**
 * This is just a stub for now
 * @author Stoimenov
 *
 */
public class CommunicationHandler
		implements IIncoming {
	
	
	private static final Logger _logger = Logger
			.getLogger(CommunicationHandler.class);
	
	private static CommunicationHandler _instance;

	
	public static CommunicationHandler getInstance() {
		if (_instance == null) {
			_instance = new CommunicationHandler();
		}
		return _instance;
	}
	
	private CommunicationHandler() {
		
	}

	@Override
	public EStatus deployMechanism(IMechanism mechanism) {
		// TODO implement
		_logger.debug("Deploy mechanism called");
		return EStatus.OKAY;
	}

	@Override
	public IMechanism exportMechanism(String par) {
		// TODO implement
		_logger.debug("Export mechanism called");
		return DummyMessageGen.createMechanism();
	}

	@Override
	public EStatus revokeMechanism(String par) {
		_logger.debug("Revoke mechanism called");
		//TODO implement
		return EStatus.OKAY;
	}

	@Override
	public IResponse notifyEvent(IEvent event) {
		//TODO implement
		_logger.debug("Notify event called");
		return DummyMessageGen.createResponse();
	}
}
