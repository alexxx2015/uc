package de.tum.in.i22.uc.cm.processing;

import de.tum.in.i22.uc.cm.distribution.IDistributionManager;
import de.tum.in.i22.uc.cm.distribution.Location;
import de.tum.in.i22.uc.cm.processing.dummy.DummyDistributionManager;
import de.tum.in.i22.uc.cm.processing.dummy.IDummyProcessor;

/**
 * A abstract processor. Taking two interfaces to two other processors as an
 * argument.
 * 
 * @author Florian Kelbert
 * 
 */
public abstract class Processor<I1 extends Processor<?, ?>, I2 extends Processor<?, ?>> {
	protected I1 _iface1;
	protected I2 _iface2;
	protected IDistributionManager _distributionManager;
	protected final Location _location;

	public Processor(Location location) {
		_location = location;
	}

	public Location getLocation(){
		return _location;
	}

	public void init(I1 iface1, I2 iface2) {
		init(iface1, iface2, new DummyDistributionManager());
	}

	public void init(I1 iface1, I2 iface2, IDistributionManager distributionManager) {
		if (_iface1 == null || _iface1 instanceof IDummyProcessor) {
			_iface1 = iface1;
		}

		if (_iface2 == null || _iface2 instanceof IDummyProcessor) {
			_iface2 = iface2;
		}

		if (_distributionManager == null || _distributionManager instanceof DummyDistributionManager) {
			_distributionManager = distributionManager;
		}
	}
}
