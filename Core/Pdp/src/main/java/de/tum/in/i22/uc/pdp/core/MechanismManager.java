package de.tum.in.i22.uc.pdp.core;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic.EStatus;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;

class MechanismManager {
	protected static Logger _logger = LoggerFactory.getLogger(MechanismManager.class);

	/**
	 * Maps policy names to its set of mechanisms, where for each mechanism the
	 * mechanism name maps to the actual mechanism
	 */
	private final Map<String, Map<String, Mechanism>> _policyTable;

	MechanismManager() {
		_policyTable = new HashMap<String, Map<String, Mechanism>>();
	}

	void deploy(Mechanism mechanism) {
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
	 * Revokes the specified mechanism within the specified policy.
	 * Returns the revoked {@link Mechanism}, or null if no such
	 * mechanism existed.
	 *
	 * @param policyName
	 * @param mechName
	 * @return the revoked {@link Mechanism}, or null if no such
	 * mechanism existed.
	 */
	Mechanism revoke(String policyName, String mechName) {
		_logger.info("revoke({}, {}) invoked.", policyName, mechName);

		Mechanism mech = null;
		Map<String, Mechanism> mechanisms = _policyTable.get(policyName);

		if (mechanisms == null || (mech = mechanisms.remove(mechName)) == null) {
			_logger.info("Mechanism [{}] did not exist for policy [{}] and could not be revoked.", mechName, policyName);
		}
		else {
			_logger.info("Revoking mechanism: {}", mechName);
			mech.revoke();
		}

		return mech;
	}

	void revokePolicy(String policyName) {
		Map<String, Mechanism> mechanisms = _policyTable.remove(policyName);
		if (mechanisms != null) {
			mechanisms.values().forEach(m -> m.revoke());
		}
	}

	void revokeAll() {
		_policyTable.values().forEach(map ->
			map.values().forEach(m -> m.revoke())
		);

		_policyTable.clear();
	}

	private Mechanism findMechanism(String policyName, String mechName) {
		Mechanism mech = null;

		Map<String, Mechanism> mechanisms = _policyTable.get(policyName);
		if (mechanisms != null) {
			mech = mechanisms.get(mechName);
		}

		return mech;
	}

	IStatus activate(String policyName, String mechName) {
		Mechanism mech = findMechanism(policyName, mechName);

		if (mech != null) {
			mech.unpause();
			return new StatusBasic(EStatus.OKAY);
		}

		return new StatusBasic(EStatus.ERROR);
	}

	IStatus deactivate(String policyName, String mechName) {
		Mechanism mech = findMechanism(policyName, mechName);

		if (mech != null) {
			mech.pause();
			return new StatusBasic(EStatus.OKAY);
		}

		return new StatusBasic(EStatus.ERROR);
	}

	Map<String, Set<String>> listMechanisms() {
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
}
