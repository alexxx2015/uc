package de.tum.in.i22.uc.cm.processing;

import de.tum.in.i22.uc.cm.interfaces.IAny2Dmp;
import de.tum.in.i22.uc.cm.interfaces.IDmp2Pip;
import de.tum.in.i22.uc.cm.interfaces.IDmp2Pmp;


/**
 * 
 * @author Florian Kelbert
 *
 */
public abstract class DmpProcessor implements IAny2Dmp, IProcessor {

	public abstract void init(IDmp2Pip pip, IDmp2Pmp pmp);
}
