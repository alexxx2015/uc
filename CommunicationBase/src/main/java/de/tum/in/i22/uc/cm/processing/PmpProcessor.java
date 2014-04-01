package de.tum.in.i22.uc.cm.processing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.interfaces.IAny2Pdp;
import de.tum.in.i22.uc.cm.interfaces.IAny2Pip;
import de.tum.in.i22.uc.cm.interfaces.IAny2Pmp;

public abstract class PmpProcessor implements Processor, IAny2Pmp {
	protected static Logger _logger = LoggerFactory.getLogger(PmpProcessor.class);

	private IAny2Pip _pip;
	private IAny2Pdp _pdp;
	private boolean _initialized = false;

	@Override
	public void init(IAny2Pip pip, IAny2Pdp pdp) {
		if (!_initialized) {
			_initialized = true;
			_pip = pip;
			_pdp = pdp;
		}
	}

	public IAny2Pdp getPdp() {
		return _pdp;
	}

	public IAny2Pip getPip() {
		return _pip;
	}
}
