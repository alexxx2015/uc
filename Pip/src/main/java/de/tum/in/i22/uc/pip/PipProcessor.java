package de.tum.in.i22.uc.pip;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.interfaces.IAny2Pdp;
import de.tum.in.i22.uc.cm.interfaces.IAny2Pip;
import de.tum.in.i22.uc.cm.interfaces.IAny2Pmp;
import de.tum.in.i22.uc.cm.processing.Processor;

/**
 *
 * @author Florian Kelbert
 *
 */
public abstract class PipProcessor implements Processor, IAny2Pip {

	protected static Logger _logger = LoggerFactory.getLogger(PipProcessor.class);

	private IAny2Pdp _pdp;
	private IAny2Pmp _pmp;

	private boolean _initialized = false;

	@Override
	public void init(IAny2Pdp pdp, IAny2Pmp pmp) {
		if (!_initialized) {
			_pdp = pdp;
			_pmp = pmp;
			_initialized = true;
		}
	}

	protected IAny2Pmp getPmp() {
		return _pmp;
	}

	protected IAny2Pdp getPdp() {
		return _pdp;
	}
}
