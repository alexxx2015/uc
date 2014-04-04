package de.tum.in.i22.uc.cm.client;

import de.tum.in.i22.uc.cm.server.PmpProcessor;

/**
 * This class represents the client side of a remote {@link PmpProcessor}
 * and is thus {@link IConnectable} to that remote point.
 *
 * @author Florian Kelbert
 *
 */
public abstract class PmpClientHandler extends PmpProcessor implements IConnectable {
}
