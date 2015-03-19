package de.tum.in.i22.uc.cm.processing;

import de.tum.in.i22.uc.cm.interfaces.IAny2Dmp;


/**
 * 
 * @author Florian Kelbert
 *
 */
public abstract class DmpProcessor implements IAny2Dmp, IProcessor {

	public abstract void init(PdpProcessor _pdp, PipProcessor _pip, PmpProcessor _pmp);
}
