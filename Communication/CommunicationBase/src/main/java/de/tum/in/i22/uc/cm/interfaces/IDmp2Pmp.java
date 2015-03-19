package de.tum.in.i22.uc.cm.interfaces;

import de.tum.in.i22.uc.cm.datatypes.basic.XmlPolicy;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.cm.processing.DmpProcessor;

/**
 * Methods that the {@link DmpProcessor} may invoke on the PMP. 
 * 
 * The {@link DmpProcessor} is not expected to contact remote PMPs
 * directly, which is why this interface and its methods is not exposed
 * vis Thrift.
 * 
 * @author Florian Kelbert
 *
 */
public interface IDmp2Pmp {
	public IStatus incomingPolicyTransfer(XmlPolicy xml, String from);
}
