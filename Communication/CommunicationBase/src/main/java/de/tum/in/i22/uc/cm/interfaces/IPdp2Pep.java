package de.tum.in.i22.uc.cm.interfaces;

import de.tum.in.i22.uc.cm.distribution.IPLocation;
import de.tum.in.i22.uc.thrift.generator.AThriftMethod;
import de.tum.in.i22.uc.thrift.generator.AThriftService;

@AThriftService(name="TPdp2Pep")
public interface IPdp2Pep {
	/**
	 * A PDP might wonder which other PDP is responsible for a specific PEP.
	 * Using this method, a PEP is supposed to respond to such a query with
	 * the {@link IPLocation} of its responsible PDP (i.e. the PDP to which
	 * it signals its events).
	 *
	 * @return
	 */
	@AThriftMethod(signature="string getResponsiblePdpLocation()")
	public IPLocation getResponsiblePdpLocation();
}
