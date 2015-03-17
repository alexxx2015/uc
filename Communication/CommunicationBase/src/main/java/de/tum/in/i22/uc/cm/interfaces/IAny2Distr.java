package de.tum.in.i22.uc.cm.interfaces;

import de.tum.in.i22.uc.cm.distribution.IDistributionManagerPublic;
import de.tum.in.i22.uc.thrift.generator.AThriftService;


/**
 * Interface defining methods to be invoked on an {@link IDistributionManagerPublic}.
 * @author Kelbert
 *
 */
@AThriftService(name="TAny2Distr")
public interface IAny2Distr extends IDistr2Distr {

}
