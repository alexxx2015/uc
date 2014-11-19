package de.tum.in.i22.uc.pdp;

import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Stopwatch;

import de.tum.in.i22.uc.cm.datatypes.basic.EventBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.PxpSpec;
import de.tum.in.i22.uc.cm.datatypes.basic.ResponseBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic.EStatus;
import de.tum.in.i22.uc.cm.datatypes.basic.XmlPolicy;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IEvent;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IMechanism;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IResponse;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.cm.distribution.IDistributionManager;
import de.tum.in.i22.uc.cm.distribution.LocalLocation;
import de.tum.in.i22.uc.cm.processing.PdpProcessor;
import de.tum.in.i22.uc.cm.processing.PipProcessor;
import de.tum.in.i22.uc.cm.processing.PmpProcessor;
import de.tum.in.i22.uc.cm.processing.dummy.DummyPipProcessor;
import de.tum.in.i22.uc.cm.processing.dummy.DummyPmpProcessor;
import de.tum.in.i22.uc.pdp.core.PolicyDecisionPoint;

public class PdpHandler extends PdpProcessor {

	private static Logger _logger = LoggerFactory.getLogger(PdpHandler.class);

	private PolicyDecisionPoint _pdp;

	private final PxpManager _pxpManager;


	public PdpHandler() {
		super(LocalLocation.getInstance());
		_pxpManager = new PxpManager();
		init(new DummyPipProcessor(), new DummyPmpProcessor());
	}

	@Override
	public IMechanism exportMechanism(String par) {
		// TODO: functionality not yet implemented in the pdp
		return null;
	}

	@Override
	public IStatus revokePolicy(String policyName) {
		_pdp.revokePolicy(policyName);
		return new StatusBasic(EStatus.OKAY);
	}

	@Override
	public IStatus revokeMechanism(String policyName, String mechName) {
		// TODO: sanitize inputs
		boolean b = _pdp.revokeMechanism(policyName, mechName);
		return b == true ? new StatusBasic(EStatus.OKAY) : new StatusBasic(
				EStatus.ERROR, "revokeMechanism failed");
	}

	@Override
	public IStatus deployPolicyXML(XmlPolicy XMLPolicy) {
		return _pdp.deployPolicyXML(XMLPolicy)
				? new StatusBasic(EStatus.OKAY)
				: new StatusBasic(EStatus.ERROR, "deploy policy failed");
	}

	@Override
	public Map<String, Set<String>> listMechanisms() {
		return _pdp.listDeployedMechanisms();
	}

	@Override
	public boolean registerPxp(PxpSpec pxp) {
		return _pxpManager.registerPxp(pxp);
	}

	Stopwatch async = Stopwatch.createUnstarted();
	Stopwatch sync = Stopwatch.createUnstarted();

	@Override
	public void notifyEventAsync(IEvent event) {
//		async.start();
		_pdp.notifyEvent(event, false);
//		_logger.debug("Stopwatch: time spent in notifyEventAsync: {}ms.", async.stop().elapsed(TimeUnit.MILLISECONDS));
	}

	@Override
	public IResponse notifyEventSync(IEvent event) {
//		sync.start();
		if (event == null) {
			return new ResponseBasic(new StatusBasic(EStatus.ERROR, "null event received"), null, null);
		}
		IResponse res = _pdp.notifyEvent(event, true);

		/*
		 * If the event is *not* actual AND if the event was allowed by the
		 * PDP AND if for this event allowance implies that the event is to be
		 * considered as actual event, then we create the corresponding actual
		 * event and signal it to both the PIP and the PDP as actual event.
		 */


		if (!event.isActual() && res.isAuthorizationAction(EStatus.ALLOW) && event.allowImpliesActual()) {
			IEvent ev2 = new EventBasic(event.getName(), event.getParameters(), true);
			notifyEventAsync(ev2);
		}

//		_logger.debug("Stopwatch: time spent in notifyEventSync: {}ms.", sync.stop().elapsed(TimeUnit.MILLISECONDS));
		return res;
	}

	@Override
	public void init(PipProcessor iface1, PmpProcessor iface2, IDistributionManager distributionManager) {
		super.init(iface1, iface2, distributionManager);

		_logger.debug("Initializing PDP. Pip reference is " + (iface1 != null ? "not " : "") + "NULL");
		_pdp = new PolicyDecisionPoint(iface1, _pxpManager, distributionManager);
	}

	@Override
	public void processEventAsync(IEvent pepEvent) {
		notifyEventAsync(pepEvent);
	}

	@Override
	public IResponse processEventSync(IEvent pepEvent) {
		return notifyEventSync(pepEvent);
	}
}
