package de.tum.in.i22.uc.pdp.core;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Future;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.UnmarshalException;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.datatypes.basic.ResponseBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic.EStatus;
import de.tum.in.i22.uc.cm.datatypes.basic.XmlPolicy;
import de.tum.in.i22.uc.cm.datatypes.interfaces.AtomicOperator;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IEvent;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IOperator;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IResponse;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.cm.distribution.IDistributionManager;
import de.tum.in.i22.uc.cm.distribution.Threading;
import de.tum.in.i22.uc.cm.interfaces.IPdp2Pip;
import de.tum.in.i22.uc.cm.processing.dummy.DummyDistributionManager;
import de.tum.in.i22.uc.cm.processing.dummy.DummyPipProcessor;
import de.tum.in.i22.uc.cm.settings.Settings;
import de.tum.in.i22.uc.pdp.PxpManager;
import de.tum.in.i22.uc.pdp.core.AuthorizationAction.Authorization;
import de.tum.in.i22.uc.pdp.core.exceptions.InvalidMechanismException;
import de.tum.in.i22.uc.pdp.core.operators.ConditionParamMatchOperator;
import de.tum.in.i22.uc.pdp.core.operators.EventMatchOperator;
import de.tum.in.i22.uc.pdp.core.operators.StateBasedOperator;
import de.tum.in.i22.uc.pdp.xsd.DetectiveMechanismType;
import de.tum.in.i22.uc.pdp.xsd.MechanismBaseType;
import de.tum.in.i22.uc.pdp.xsd.PolicyType;
import de.tum.in.i22.uc.pdp.xsd.PreventiveMechanismType;

public class PolicyDecisionPoint implements Observer {
	private static final Logger _logger = LoggerFactory.getLogger(PolicyDecisionPoint.class);

	private final IPdp2Pip _pip;

	private final ActionDescriptionStore _actionDescriptionStore;

	/**
	 * Maps policy names to its set of mechanisms, where for each mechanism the
	 * mechanism name maps to the actual mechanism
	 */
	private final Map<String, Map<String, Mechanism>> _policyTable;

	private final PxpManager _pxpManager;

	private final IDistributionManager _distributionManager;

	private final Unmarshaller _unmarshaller;

	private final CompletionService<?> _csOperators;
	private final CompletionService<Pair<Mechanism,Boolean>> _csMechanisms;

	public PolicyDecisionPoint() {
		this(new DummyPipProcessor(), new PxpManager(), new DummyDistributionManager());
	}

	public PolicyDecisionPoint(IPdp2Pip pip, PxpManager pxpManager, IDistributionManager distributionManager) {
		_pip = pip;
		_pxpManager = pxpManager;
		_distributionManager = distributionManager;

		_policyTable = new HashMap<String, Map<String, Mechanism>>();
		_actionDescriptionStore = new ActionDescriptionStore();

		_csOperators = new ExecutorCompletionService<>(Threading.instance());
		_csMechanisms = new ExecutorCompletionService<>(Threading.instance());

		try {
			_unmarshaller = JAXBContext.newInstance(Settings.getInstance().getPdpJaxbContext()).createUnmarshaller();
		} catch (JAXBException e) {
			throw new RuntimeException("Unable to create Marshaller or Unmarshaller: " + e.getMessage());
		}
	}

	public boolean deployPolicyXML(XmlPolicy xmlPolicy) {
		_logger.debug("deployPolicyXML: " + xmlPolicy.getName());
		return deployXML(new ByteArrayInputStream(xmlPolicy.getXml().getBytes()));
	}

	public boolean deployPolicyURI(String policyFilename) {
		if (!policyFilename.endsWith(".xml")) {
			_logger.warn("Unsupported message format of policy [" + policyFilename + "]. Not deploying policy.");
			return false;
		}

		InputStream is = null;
		try {
			is = new FileInputStream(policyFilename);
		} catch (FileNotFoundException e) {
			_logger.warn("Policy file " + policyFilename + " not found.");
			e.printStackTrace();
			return false;
		}

		return deployXML(is);
	}

	private boolean deployXML(InputStream is) {
		if (is == null) {
			return false;
		}

		try {
			JAXBElement<?> poElement = (JAXBElement<?>) _unmarshaller.unmarshal(is);
			PolicyType policy = (PolicyType) poElement.getValue();
			final String policyName = policy.getName();

			_logger.debug("Deploying policy [name={}]", policyName);

			/*
			 * Get the set of mechanisms of this policy (if any)
			 */
			Map<String, Mechanism> allMechanisms = _policyTable.get(policyName);
			if (allMechanisms == null) {
				allMechanisms = new HashMap<>();
				_policyTable.put(policyName, allMechanisms);
			}

			/*
			 * Loop over all mechanisms, add them to the set of mechanisms for
			 * this policy, and start the mechanism
			 */
			for (MechanismBaseType mech : policy.getDetectiveMechanismOrPreventiveMechanism()) {
				/*
				 * TODO Parallelize
				 * (Watch out to synchronize shared data structures such as allMechanisms).
				 */
				try {
					_logger.debug("Processing mechanism: {}", mech.getName());
					Mechanism curMechanism;

					if (mech instanceof PreventiveMechanismType) {
						curMechanism = new PreventiveMechanism(mech, policyName, this);
					}
					else if (mech instanceof DetectiveMechanismType) {
						curMechanism = new DetectiveMechanism(mech, policyName, this);
					}
					else {
						throw new InvalidMechanismException(mech.toString());
					}

					if (!allMechanisms.containsKey(mech.getName())) {
						allMechanisms.put(mech.getName(), curMechanism);
						Thread t = new Thread(curMechanism);
						curMechanism.setThread(t);
						t.start();
					} else {
						_logger.warn("Mechanism [{}] is already deployed for policy [{}]", curMechanism.getName(), policyName);
					}
				} catch (InvalidMechanismException e) {
					_logger.error("Invalid mechanism specified: {}", e.getMessage());
					return false;
				}
			}
		} catch (UnmarshalException e) {
			_logger.error("Syntax error in policy: " + e.getMessage());
			return false;
		} catch (JAXBException | ClassCastException e) {
			_logger.error("Error while deploying policy: " + e.getMessage());
			return false;
		}

		return true;
	}

	public void revokePolicy(String policyName) {
		_logger.debug("revokePolicy({}) invoked.", policyName);

		Map<String, Mechanism> mechanisms = _policyTable.remove(policyName);
		if (mechanisms != null) {
			for (Mechanism mech : mechanisms.values()) {
				_logger.info("Revoking mechanism: {}", mech.getName());
				mech.revoke();
			}
		}
	}

	private Mechanism findMechanism(String policyName, String mechName) {
		Mechanism mech = null;

		Map<String, Mechanism> mechanisms = _policyTable.get(policyName);
		if (mechanisms != null) {
			mech = mechanisms.get(mechName);
		}

		return mech;
	}

	public IStatus activateMechanism(String policyName, String mechName) {
		Mechanism mech = findMechanism(policyName, mechName);

		if (mech != null) {
			mech.unpause();
			return new StatusBasic(EStatus.OKAY);
		}

		return new StatusBasic(EStatus.ERROR);
	}

	public IStatus deactivateMechanism(String policyName, String mechName) {
		Mechanism mech = findMechanism(policyName, mechName);

		if (mech != null) {
			mech.pause();
			return new StatusBasic(EStatus.OKAY);
		}

		return new StatusBasic(EStatus.ERROR);
	}

	public boolean revokeMechanism(String policyName, String mechName) {
		_logger.info("revokeMechanism({}, {}) invoked.", policyName, mechName);

		Mechanism mech;
		Map<String, Mechanism> mechanisms = _policyTable.get(policyName);

		if (mechanisms == null || (mech = mechanisms.remove(mechName)) == null) {
			_logger.info("Mechanism [{}] did not exist for policy [{}] and could not be revoked.", mechName, policyName);
			return false;
		}

		_logger.info("Revoking mechanism: {}", mechName);
		mech.revoke();
		_actionDescriptionStore.removeMechanism(mech.getTriggerEvent().getAction());

		return true;
	}

	void addObservers(Collection<AtomicOperator> observers) {
		observers.forEach(o -> {
			switch(o.getOperatorType()) {
			case STATE_BASED:
				_actionDescriptionStore.add((StateBasedOperator) o);
				break;
			case EVENT_MATCH:
				_actionDescriptionStore.add((EventMatchOperator) o);
				break;
			case CONDITION_PARAM_MATCH:
				_actionDescriptionStore.add((ConditionParamMatchOperator) o);
				break;
			default:
				break;
			}
		});
	}

	/**
	 * Update all specified {@link AtomicOperator}s with the specified event.
	 *
	 * @param operators the operators to be updated
	 * @param event the event
	 */
	private void updateAll(Collection<AtomicOperator> operators, IEvent event) {
		/*
		 * Updating is done in parallel. The first forEach instruction starts
		 * the actual update threads, while the second forEach waits for their
		 * completion.
		 */
		operators.forEach(o -> _csOperators.submit(() -> o.update(event), null));
		operators.forEach(o -> Threading.take(_csOperators));
	}


	public IResponse notifyEvent(IEvent event) {
		return notifyEvent(event, true);
	}

	/**
	 * Notifies an event to this {@link PolicyDecisionPoint}.
	 *
	 * @param event the event to notify
	 * @param syncCall whether the original call was synchronous or asynchronous
	 * @return
	 */
	public synchronized IResponse notifyEvent(IEvent event, boolean syncCall) {
		_logger.info("notifyEvent: {}", event);

		if (!syncCall && !event.isActual()) {
			/*
			 * The notified event is desired and it was
			 * notified asynchronously. In this case there
			 * is nothing to do, because the event is actually
			 * not happening and the caller is not interested
			 * in an evaluation result.
			 */
			return new ResponseBasic(new StatusBasic(EStatus.ALLOW));
		}

		/*
		 * Prepare a decision object.
		 */
		Decision decision = new Decision(new AuthorizationAction("default", Authorization.ALLOW), _pxpManager);

		/*
		 * Get all relevant Mechanisms to evaluate. This is done in a new Thread.
		 */
		Future<Collection<Mechanism>> futureMechanisms = Threading.instance().submit(() -> _actionDescriptionStore.getMechanisms(event.getName()));
		Collection<Mechanism> mechanisms;

		/*
		 * Get all relevant Operators that need to be updated. This is done in a new Thread.
		 */
		Future<Collection<AtomicOperator>> futureOperators = Threading.instance().submit(() -> _actionDescriptionStore.getAtomicOperators(event.getName()));

		if (event.isActual()) {
			/*
			 * This is an actual event and can not be undone.
			 */

			/*
			 * First thing to do is to update the PIP with the event,
			 * such that it updates its state accordingly.
			 */
			_pip.update(event);

			/*
			 * Wait for the relevant-Operators-thread to finish. Once
			 * the Operators are retrieved, update them.
			 */
			updateAll(Threading.resultOf(futureOperators), event);

//			/*
//			 * If operators changed, let the
//			 * DistributionManager take care of them.
//			 */
//			if (Settings.getInstance().getDistributionEnabled() && !_changedOperators.isEmpty()) {
//				_distributionManager.update(_changedOperators, false);
//			}

			/*
			 * Notify the event to all Mechanisms,
			 * effectively evaluating their condition.
			 * This is done while simulating, because
			 * this is not the end of a timestep. Rather,
			 * we 'simulate' the end of a timestep in
			 * order to get an evaluation result.
			 *
			 * However, we only need to perform this evaluation
			 * if the event has been notified synchronously.
			 * If the event was notified asynchronously, the
			 * caller is not interested in a decision anyway.
			 */
			if (syncCall) {
				mechanisms = Threading.resultOf(futureMechanisms);

				/*
				 * Do the actual start/stop of simulation and notification
				 * to Mechanism. Start a new Thread for each Mechanism.
				 */
				mechanisms.forEach(m -> _csMechanisms.submit(() -> {
					m.startSimulation();
					boolean result = m.notifyEvent(event);
					m.stopSimulation();
					return Pair.of(m,result);
				}));

				/*
				 * Wait for all the threads to be done and accumulate
				 * the decision.
				 */
				mechanisms.forEach(m -> {
					Pair<Mechanism,Boolean> res = Threading.takeResult(_csMechanisms);
					if (res.getRight()) {
						decision.processMechanism(res.getLeft());
					}
				});
			}
		}
		else if (syncCall) {
			/*
			 * This is a desired event and it was
			 * notified synchronously. In this case
			 * the event is only simulated and the
			 * caller is actually interested in an
			 * evaluation result.
			 */

			/* The first thing to do is to start the
			 * simulation of the PIP and the Mechanisms.
			 * This is done in parallel using a
			 * ExecutorCompletionService.
			 */
			_csMechanisms.submit(() -> _pip.startSimulation(), null);
			mechanisms = Threading.resultOf(futureMechanisms);
			mechanisms.forEach(m -> _csMechanisms.submit(() -> m.startSimulation(), null));
			mechanisms.forEach(m -> Threading.take(_csMechanisms));
			Threading.take(_csMechanisms);

			/*
			 * Then, notify the event to the PIP and to all
			 * observers that registered for it (just as above
			 * for the actual events). Since we started
			 * simulation before, this will be undone later.
			 *
			 * Different from the case above, where the
			 * DistributionManager takes care about changed
			 * operators, they are ignored here, because the
			 * event is actually not happening.
			 */
			_pip.update(event);
			updateAll(Threading.resultOf(futureOperators), event);

			/*
			 * Notify the event to all Mechanisms,
			 * effectively simulating the end of a timestep.
			 * After getting the evaluation result, stop
			 * the simulation.
			 */
			mechanisms.forEach(m -> _csMechanisms.submit(() -> {
				boolean result = m.notifyEvent(event);
				m.stopSimulation();
				return Pair.of(m,result);
			}));

			_pip.stopSimulation();

			mechanisms.forEach(m -> {
				Pair<Mechanism,Boolean> res = Threading.takeResult(_csMechanisms);
				if (res.getRight()) {
					decision.processMechanism(res.getLeft());
				}
			});
		}


//		// prepare for next iteration
//		_changedOperators.clear();

		return decision.toResponse();
	}

	public Map<String, Set<String>> listDeployedMechanisms() {
		Map<String, Set<String>> map = new TreeMap<>();

		for (String policyName : _policyTable.keySet()) {
			Set<String> mechanisms = new TreeSet<>();
			for (String mechName : _policyTable.get(policyName).keySet()) {
				mechanisms.add(mechName);
			}
			map.put(policyName, mechanisms);
		}

		return map;
	}

	public IPdp2Pip getPip() {
		return _pip;
	}

	boolean executeAction(ExecuteAction execAction, boolean synchronous) {
		return _pxpManager.execute(execAction, synchronous);
	}

	public void stop() {
		for (Map<String, Mechanism> map : _policyTable.values()) {
			map.values().forEach(m -> m.revoke());
		}

		_policyTable.clear();
	}

	public void addMechanism(Mechanism mechanism) {
		_actionDescriptionStore.add(mechanism);
	}

	@Override
	public void update(Observable o, Object arg) {
		if (o instanceof IOperator) {
			if (arg instanceof IEvent && ((IEvent) arg).isActual()) {
				_distributionManager.update((IOperator) o, false);
			}
			else if (arg == Mechanism.END_OF_TIMESTEP) {
				_distributionManager.update((IOperator) o, true);
			}
		}
//
//
//		if ((o instanceof Operator) && (arg instanceof State)) {
//			_logger.info("Got update about Operator: {}. New state: {}.", o, arg);meat -d
//			_changedOperators.add((Operator) o);
//		}
//		else if ((o instanceof Mechanism) && (arg == Mechanism.END_OF_TIMESTEP)) {
//			if (!_changedOperators.isEmpty() && Settings.getInstance().getDistributionEnabled()) {
//				_distributionManager.update(_changedOperators, true);
//			}
//
//			// Prepare for next
//			_changedOperators.clear();
//		}
	}

	public IDistributionManager getDistributionManager() {
		return _distributionManager;
	}
}
