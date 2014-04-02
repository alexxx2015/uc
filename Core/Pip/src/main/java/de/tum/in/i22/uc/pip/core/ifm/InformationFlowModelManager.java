package de.tum.in.i22.uc.pip.core.ifm;

import java.util.HashMap;
import java.util.Map;

import de.tum.in.i22.uc.cm.basic.StatusBasic;
import de.tum.in.i22.uc.cm.client.ConnectionManager;
import de.tum.in.i22.uc.cm.datatypes.EStatus;
import de.tum.in.i22.uc.cm.datatypes.IStatus;
import de.tum.in.i22.uc.cm.pip.EInformationFlowModel;
import de.tum.in.i22.uc.cm.settings.Settings;
import de.tum.in.i22.uc.pip.extensions.crosslayer.ScopeManager;

public final class InformationFlowModelManager {
	private static InformationFlowModelManager _instance;

	private final Map<EInformationFlowModel,IInformationFlowModelExtension> _ifModelExtensions;

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
					InformationFlowModelExtensionManager sm = new ScopeManager();
					_ifModelExtensions.put(eifm, sm.getExtension());
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
	public <T extends IInformationFlowModelExtension> T getExtension(EInformationFlowModel ifm) {
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
		for (IInformationFlowModelExtension ifme : _ifModelExtensions.values()) {
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
		for (IInformationFlowModelExtension ifme : _ifModelExtensions.values()) {
			ifme.pop();
		}

		return new StatusBasic(EStatus.OKAY);
	}

	public void reset() {
		_basicIfModel.reset();
		for (IInformationFlowModelExtension ifme : _ifModelExtensions.values()) {
			ifme.reset();
		}
	}

	public String niceString() {
		StringBuilder sb = new StringBuilder(_basicIfModel.niceString());

		for (IInformationFlowModelExtension ifme : _ifModelExtensions.values()) {
			sb.append(ifme.niceString());
		}

		sb.append("-----------------------------------------------");

		return sb.toString();
	}
}
