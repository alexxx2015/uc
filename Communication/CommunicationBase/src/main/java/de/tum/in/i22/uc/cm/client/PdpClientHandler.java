package de.tum.in.i22.uc.cm.client;

import de.tum.in.i22.uc.cm.server.PdpProcessor;

/**
 * This class represents the client side of a remote {@link PdpProcessor}
 * and is thus {@link IConnectable} to that remote point.
 *
 * @author Florian Kelbert
 *
 */
public abstract class PdpClientHandler extends PdpProcessor implements IConnectable {
}
