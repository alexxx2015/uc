package de.tum.in.i22.uc.pip.core.ifm;

import java.util.Observable;
import java.util.Observer;

import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic.EStatus;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.cm.pip.ifm.IInformationFlowModel;

public abstract class InformationFlowModel implements IInformationFlowModel {
	private boolean _changed = false;
	private boolean _changedBackup = false;

	protected final Observer _observer;

	public InformationFlowModel() {
		_observer = new InternalObserver();
	}

	@Override
	public boolean hasChanged() {
		return _changed;
	}

	@Override
	public void clearChanged() {
		_changed = false;
	}

	@Override
	public void reset() {
		_changed = false;
	}

	@Override
	public IStatus startSimulation() {
		_changedBackup = _changed;
		return new StatusBasic(EStatus.OKAY);
	}

	@Override
	public IStatus stopSimulation() {
		_changed = _changedBackup;
		return new StatusBasic(EStatus.OKAY);
	}

	private class InternalObserver implements Observer {
		@Override
		public void update(Observable o, Object arg) {
			_changed = true;
		}
	}
}
