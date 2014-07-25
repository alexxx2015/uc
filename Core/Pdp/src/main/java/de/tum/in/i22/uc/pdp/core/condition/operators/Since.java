package de.tum.in.i22.uc.pdp.core.condition.operators;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.datatypes.interfaces.IEvent;
import de.tum.in.i22.uc.pdp.core.condition.Operator;
import de.tum.in.i22.uc.pdp.core.mechanisms.Mechanism;
import de.tum.in.i22.uc.pdp.xsd.SinceType;

public class Since extends SinceType {
	private static Logger log = LoggerFactory.getLogger(Since.class);

	public Since() {
	}

	public Since(Operator operand1, Operator operand2) {
		this.getOperators().add(operand1);
		this.getOperators().add(operand2);
	}

	@Override
	public void initOperatorForMechanism(Mechanism mech) {
		super.initOperatorForMechanism(mech);
		((Operator) this.getOperators().get(0)).initOperatorForMechanism(mech);
		((Operator) this.getOperators().get(1)).initOperatorForMechanism(mech);
	}

	@Override
	public String toString() {
		return "SINCE (" + this.getOperators().get(0) + ", " + this.getOperators().get(1) + " )";
	}

	@Override
	public boolean evaluate(IEvent curEvent) { // A occurs, SINCE is satisfied
												// (LTL doesn't state anything
												// about B in the timestep when
												// A happens)
		Boolean operand1state = ((Operator) this.getOperators().get(0)).evaluate(curEvent);
		Boolean operand2state = ((Operator) this.getOperators().get(1)).evaluate(curEvent);

		if (operand1state) {
			log.debug("[SINCE] Subformula A satisfied this timestep => TRUE");
			this._state.value = true;
		} else {
			if (!this._state.immutable) { // until now B occurred every following
											// timestep

				if (this._state.counter == 1) {
					log.debug("[SINCE] Subformula A was satisfied any previous timestep");

					if (operand2state) {
						log.debug("[SINCE] Subformula B is satisfied this timestep => TRUE");
						this._state.value = true;
					} else {
						log.debug("[SINCE] Subformula B NOT satisfied this timestep => FALSE");
						this._state.value = false;
					}
				} else {
					log.debug("[SINCE] Subformula A NOT satisfied this timestep or any previous timestep");
					log.debug("[SINCE] Not yet immutable; check (ALWAYS B) part of since");

					if (operand2state) {
						log.debug("[SINCE] Subformula B is satisfied this timestep => TRUE");
						this._state.value = true;
					} else {
						log.debug("[SINCE] Subformula B NOT satisfied this timestep => FALSE");
						this._state.value = false;
					}
				}
			}
		}

		if (curEvent == null) {
			if (!this._state.value) {
				if (!this._state.immutable) { // immutable until next occurence
												// of subformula A
					log.debug("[SINCE] Evaluating current state value was FALSE =>  activating IMMUTABILITY");
					this._state.immutable = true;
				}
			}

			if (operand1state) {
				log.debug("[SINCE] Subformula A satisfied this timestep => setting counter flag");
				this._state.counter = 1;
				if (this._state.immutable) {
					log.debug("[SINCE] Deactivating immutability");
					this._state.immutable = false;
				}
			}

			if (!this._state.subEverTrue && !operand2state) {
				log.debug("[SINCE] Subformula B was previously always satisfied, but NOT this timestep => 2nd part of since can never be satisfied any more");
				log.debug("[SINCE] Setting subEverFalse flag and activating immutability");
				this._state.subEverTrue = true; // intention here subformula was
												// ever FALSE (in contrast to
												// name...)
				this._state.immutable = true;
			}
		}

		log.debug("eval SINCE [{}]", this._state.value);
		return this._state.value;
	}
}
