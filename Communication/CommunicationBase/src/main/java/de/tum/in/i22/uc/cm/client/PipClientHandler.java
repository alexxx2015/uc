package de.tum.in.i22.uc.cm.client;

import de.tum.in.i22.uc.cm.server.PipProcessor;

/**
 * This class represents the client side of a remote {@link PipProcessor}
 * and is thus {@link IConnectable} to that remote point.
 *
 * @author Florian Kelbert
 *
 */
public abstract class PipClientHandler extends PipProcessor implements IConnectable {
}
