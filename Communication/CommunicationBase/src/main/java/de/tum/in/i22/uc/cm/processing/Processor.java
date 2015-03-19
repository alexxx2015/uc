package de.tum.in.i22.uc.cm.processing;

import de.tum.in.i22.uc.cm.distribution.Location;
import de.tum.in.i22.uc.cm.processing.dummy.IDummyProcessor;

/**
 * A abstract processor. Taking two interfaces to two other processors as an
 * argument.
 *
 * @author Florian Kelbert
 *
 */
public abstract class Processor<I1 extends IProcessor, I2 extends IProcessor, I3> implements IProcessor {
	
	/**
	 * These attributes use the default modifier on purpose,
	 * such that they can only be accessed from within this
	 * package.
	 */
	
	I1 _iface1;
	I2 _iface2;
	I3 _dmp;
	
	final Location _location;

	public Processor(Location location) {
		_location = location;
	}

	public Location getLocation(){
		return _location;
	}

//	public void init(I1 iface1, I2 iface2) {
//		init(iface1, iface2, new DummyDmpProcessor());
//	}

	public void init(I1 iface1, I2 iface2, I3 dmp) {
		if (_iface1 == null || _iface1 instanceof IDummyProcessor) {
			_iface1 = iface1;
		}

		if (_iface2 == null || _iface2 instanceof IDummyProcessor) {
			_iface2 = iface2;
		}

		if (_dmp == null || _dmp instanceof IDummyProcessor) {
			_dmp = dmp;
		}
	}
	
	protected I3 getDmp() {
		return _dmp;
	}
}
