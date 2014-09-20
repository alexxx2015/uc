package de.tum.in.i22.uc.cm.datatypes.interfaces;

import de.tum.in.i22.uc.cm.datatypes.basic.XmlPolicy;

/**
 * @author Cipri
 * The response type used by the PTP
 */
public interface IPtpResponse {
	public IStatus getStatus();
	public XmlPolicy getPolicy();
	public String getMessage();
}
