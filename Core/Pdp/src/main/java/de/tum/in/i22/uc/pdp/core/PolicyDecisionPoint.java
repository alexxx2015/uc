package de.tum.in.i22.uc.pdp.core;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Future;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
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
import de.tum.in.i22.uc.pdp.core.exceptions.InvalidPolicyException;
import de.tum.in.i22.uc.pdp.core.operators.ConditionParamMatchOperator;
import de.tum.in.i22.uc.pdp.core.operators.EventMatchOperator;
import de.tum.in.i22.uc.pdp.core.operators.StateBasedOperator;
import de.tum.in.i22.uc.pdp.xsd.PolicyType;

public class PolicyDecisionPoint implements Observer {
	private static final Logger _logger = LoggerFactory.getLogger(PolicyDecisionPoint.class);

	private final IPdp2Pip _pip;

	private final ObserverManager _observerManager;

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

	private final MechanismFactory _mechanismFactory;

	public PolicyDecisionPoint() {
		this(new DummyPipProcessor(), new PxpManager(), new DummyDistributionManager());
	}

	public PolicyDecisionPoint(IPdp2Pip pip, PxpManager pxpManager, IDistributionManager distributionManager) {
		_pip = pip;
		_pxpManager = pxpManager;
		_distributionManager = distributionManager;

		_policyTable = new ConcurrentHashMap<String, Map<String, Mechanism>>();
		_observerManager = new ObserverManager();

		_csOperators = new ExecutorCompletionService<>(Threading.instance());
		_csMechanisms = new ExecutorCompletionService<>(Threading.instance());

		_mechanismFactory = new MechanismFactory(this);

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


	/**
	 * Transforms the provided {@link InputStream} into
	 * a {@link PolicyType} and returns the corresponding
	 * result. If the provided stream can not be transformed,
	 * null is returned.
	 *
	 * @param is
	 * @return
	 */
	private PolicyType toPolicy(InputStream is) {
		PolicyType policy = null;

		if (is != null) {
			try {
				JAXBElement<?> poElement;
				synchronized (_unmarshaller) {
					poElement = (JAXBElement<?>) _unmarshaller.unmarshal(is);
				}
				policy = (PolicyType) poElement.getValue();

			} catch (JAXBException e) {
				_logger.error("Error while deploying policy: " + e.getMessage() + " (" + e.getClass() + ")");
			}
		}

		return policy;
	}

	private void deployMechanism(Mechanism mechanism) {
		String policyName = mechanism.getPolicyName();

		Map<String, Mechanism> deployedMechanisms = _policyTable.get(policyName);
		if (deployedMechanisms == null) {
			deployedMechanisms = new ConcurrentHashMap<>();
			_policyTable.put(policyName, deployedMechanisms);
		}

		if (!deployedMechanisms.containsKey(mechanism.getName())) {
			deployedMechanisms.put(mechanism.getName(), mechanism);
			Thread t = new Thread(mechanism);
			mechanism.setThread(t);
			t.start();
		} else {
			_logger.warn("Mechanism [{}] is already deployed for policy [{}]", mechanism.getName(), policyName);
		}
	}


	/**
	 * Deploys the policy provided as an {@link InputStream}.
	 * The provided stream is expected to be
	 * transformable into a {@link PolicyType}. If deployment
	 * is successful, true is returned. If an error occurs,
	 * false is returned.
	 *
	 * @param is the input stream to be deployed.
	 * @return true, if deployment was successful. False, if an error occurred.
	 */
	private boolean deployXML(InputStream is) {
		PolicyType policy = toPolicy(is);

		if (policy == null) {
			return false;
		}

		_logger.debug("Deploying policy {}.", policy.getName());

		Collection <Mechanism> newMechanisms;
		try {
			newMechanisms = _mechanismFactory.createMechanisms(policy);
		} catch (InvalidPolicyException e) {
			e.printStackTrace();
			return false;
		}

		CompletionService<?> cs = new ExecutorCompletionService<>(Threading.instance());
		newMechanisms.forEach(m -> cs.submit(() -> deployMechanism(m), null));
		Threading.waitFor(newMechanisms.size(), cs);

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
		_observerManager.removeMechanism(mech.getTriggerEvent().getAction());

		return true;
	}

	void addObservers(Collection<AtomicOperator> observers) {
		observers.forEach(o -> {
			switch(o.getOperatorType()) {
			case STATE_BASED:
				_observerManager.add((StateBasedOperator) o);
				break;
			case EVENT_MATCH:
				_observerManager.add((EventMatchOperator) o);
				break;
			case CONDITION_PARAM_MATCH:
				_observerManager.add((ConditionParamMatchOperator) o);
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
		Threading.waitFor(operators.size(), _csOperators);
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
		Future<Collection<Mechanism>> futureMechanisms = Threading.instance().submit(() -> _observerManager.getMechanisms(event.getName()));
		Collection<Mechanism> mechanisms;

		/*
		 * Get all relevant Operators that need to be updated. This is done in a new Thread.
		 */
		Future<Collection<AtomicOperator>> futureOperators = Threading.instance().submit(() -> _observerManager.getAtomicOperators(event.getName()));

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
			Threading.waitFor(mechanisms.size() + 1, _csMechanisms);

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
		_observerManager.add(mechanism);
	}

	@Override
	public void update(Observable o, Object arg) {
		if (o instanceof IOperator) {
			if (arg instanceof IEvent && ((IEvent) arg).isActual()) {
				_distributionManager.notify((IOperator) o, false);
			}
			else if (arg == Mechanism.END_OF_TIMESTEP) {
				_distributionManager.notify((IOperator) o, true);
			}
		}
	}

	public IDistributionManager getDistributionManager() {
		return _distributionManager;
	}
}
