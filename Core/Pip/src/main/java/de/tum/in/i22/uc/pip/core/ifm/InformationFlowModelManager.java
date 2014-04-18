package de.tum.in.i22.uc.pip.core.ifm;

import java.util.HashMap;
import java.util.Map;

import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic.EStatus;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.cm.pip.EInformationFlowModel;
import de.tum.in.i22.uc.cm.settings.Settings;
import de.tum.in.i22.uc.pip.extensions.crosslayer.ScopeInformationFlowModel;

public final class InformationFlowModelManager {
	private static InformationFlowModelManager _instance;

	private final Map<EInformationFlowModel,InformationFlowModelExtension> _ifModelExtensions;

	private final BasicInformationFlowModel _basicIfModel;

	private boolean _simulating;

	private InformationFlowModelManager() {
		_ifModelExtensions = new HashMap<>();
		_simulating = false;
		_basicIfModel = BasicInformationFlowModel.getInstance();

		for (EInformationFlowModel eifm : Settings.getInstance().getEnabledInformationFlowModels()) {
			switch (eifm) {
				case QUANTITIES:
					break;
				case SCOPE:
					_ifModelExtensions.put(eifm, new ScopeInformationFlowModel());
					break;
				default:
					break;
			}
		}
	}

	public static InformationFlowModelManager getInstance() {
		/*
		 * This implementation may seem odd, overengineered, redundant, or all of it.
		 * Yet, it is the best way to implement a thread-safe singleton, cf.
		 * http://www.journaldev.com/171/thread-safety-in-java-singleton-classes-with-example-code
		 * -FK-
		 */
		if (_instance == null) {
			synchronized (InformationFlowModelManager.class) {
				if (_instance == null) _instance = new InformationFlowModelManager();
			}
		}
		return _instance;
	}

	public BasicInformationFlowModel getBasicInformationFlowModel() {
		return _basicIfModel;
	}

	public boolean isEnabled(EInformationFlowModel ifm) {
		return _ifModelExtensions.containsKey(ifm);
	}

	@SuppressWarnings("unchecked")
	public <T extends InformationFlowModelExtension> T getExtension(EInformationFlowModel ifm) {
		return (T) _ifModelExtensions.get(ifm);
	}

	public boolean isSimulating() {
		return _simulating;
	}

	public IStatus startSimulation() {
		if (_simulating) {
			return new StatusBasic(EStatus.ERROR, "Already simulating.");
		}
		_simulating = true;

		_basicIfModel.push();
		for (InformationFlowModelExtension ifme : _ifModelExtensions.values()) {
			ifme.push();
		}

		return new StatusBasic(EStatus.OKAY);
	}

	public IStatus stopSimulation() {
		if (!_simulating) {
			return new StatusBasic(EStatus.ERROR, "Not simulating.");
		}
		_simulating = false;

		_basicIfModel.pop();
		for (InformationFlowModelExtension ifme : _ifModelExtensions.values()) {
			ifme.pop();
		}

		return new StatusBasic(EStatus.OKAY);
	}

	public void reset() {
		_basicIfModel.reset();
		for (InformationFlowModelExtension ifme : _ifModelExtensions.values()) {
			ifme.reset();
		}
	}

	public String niceString() {
		StringBuilder sb = new StringBuilder(_basicIfModel.niceString());

		for (InformationFlowModelExtension ifme : _ifModelExtensions.values()) {
			sb.append(ifme.niceString());
		}

		sb.append("-----------------------------------------------");

		return sb.toString();
	}
}
