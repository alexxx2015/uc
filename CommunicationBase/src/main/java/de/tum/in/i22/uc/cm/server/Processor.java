package de.tum.in.i22.uc.cm.server;

/**
 * A abstract processor. Taking two interfaces to two other processors as an argument.
 *
 * @author Florian Kelbert
 *
 */
public abstract class Processor<I1 extends Processor<?, ?>, I2 extends Processor<?, ?>> {
	protected I1 _iface1;
	protected I2 _iface2;

	private boolean _initialized = false;

	public void init(I1 iface1, I2 iface2) {
		if (!_initialized) {
			_initialized = true;
			_iface1 = iface1;
			_iface2 = iface2;
		}
	}
}
