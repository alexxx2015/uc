package de.tum.in.i22.uc.pdp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.interfaces.IAny2Pdp;
import de.tum.in.i22.uc.cm.interfaces.IAny2Pip;
import de.tum.in.i22.uc.cm.interfaces.IAny2Pmp;
import de.tum.in.i22.uc.cm.processing.Processor;

public abstract class PdpProcessor implements Processor, IAny2Pdp {

	protected static Logger _logger = LoggerFactory.getLogger(PdpProcessor.class);

	private IAny2Pmp _pmp;
	private IAny2Pip _pip;

	private boolean _initialized = false;

	@Override
	public void init(IAny2Pip pip, IAny2Pmp pmp) {
		if (!_initialized) {
			_initialized = true;
			_pmp = pmp;
			_pip = pip;
		}
	}

	public IAny2Pmp getPmp() {
		return _pmp;
	}

	public IAny2Pip getPip() {
		return _pip;
	}
}
