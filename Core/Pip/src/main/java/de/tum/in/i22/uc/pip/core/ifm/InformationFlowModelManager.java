package de.tum.in.i22.uc.pip.core.ifm;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic.EStatus;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IContainer;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IData;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IEvent;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IName;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IScope;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.cm.interfaces.informationFlowModel.IBasicInformationFlowModel;
import de.tum.in.i22.uc.cm.interfaces.informationFlowModel.IInformationFlowModel;
import de.tum.in.i22.uc.cm.pip.EInformationFlowModel;
import de.tum.in.i22.uc.cm.pip.interfaces.EBehavior;
import de.tum.in.i22.uc.cm.pip.interfaces.EScopeState;
import de.tum.in.i22.uc.cm.pip.interfaces.IEventHandler;
import de.tum.in.i22.uc.cm.settings.Settings;
import de.tum.in.i22.uc.pip.extensions.crosslayer.ScopeInformationFlowModel;
import de.tum.in.i22.uc.pip.extensions.structured.StructuredInformationFlowModel;

public final class InformationFlowModelManager implements
		IInformationFlowModel, IBasicInformationFlowModel {
	private final Map<EInformationFlowModel, InformationFlowModelExtension> _ifModelExtensions;

	private final BasicInformationFlowModel _basicIfModel;

	private boolean _simulating;

	/**
	 * 
	 */
	public InformationFlowModelManager() {
		_ifModelExtensions = new HashMap<>();
		_simulating = false;
		_basicIfModel = new BasicInformationFlowModel();

		DummyInformationFlowModel dummy = new DummyInformationFlowModel(null);

		for (EInformationFlowModel eifm : EInformationFlowModel.values()) {
			_ifModelExtensions.put(eifm, dummy);
		}

		for (EInformationFlowModel eifm : Settings.getInstance()
				.getEnabledInformationFlowModels()) {
			switch (eifm) {
			case QUANTITIES:
				break;
			case SCOPE:
				_ifModelExtensions.put(eifm,
						new ScopeInformationFlowModel(this));
				break;
			case STRUCTURE:
				_ifModelExtensions.put(eifm,
						new StructuredInformationFlowModel(this));
				break;
			default:
				break;
			}
		}
	}

	public IBasicInformationFlowModel getBasicInformationFlowModel() {
		return _basicIfModel;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.tum.in.i22.uc.pip.core.ifm.IInformationFlowModel#isEnabled(de.tum.
	 * in.i22.uc.cm.pip.EInformationFlowModel)
	 */
	@Override
	public boolean isEnabled(EInformationFlowModel ifm) {
		return _ifModelExtensions.containsKey(ifm);
	}

	@SuppressWarnings("unchecked")
	public <T extends InformationFlowModelExtension> T getExtension(
			EInformationFlowModel ifm) {
		return (T) _ifModelExtensions.get(ifm);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.tum.in.i22.uc.pip.core.ifm.IInformationFlowModel#isSimulating()
	 */
	@Override
	public boolean isSimulating() {
		return _simulating;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.tum.in.i22.uc.pip.core.ifm.IInformationFlowModel#startSimulation()
	 */
	@Override
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.tum.in.i22.uc.pip.core.ifm.IInformationFlowModel#stopSimulation()
	 */
	@Override
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.tum.in.i22.uc.pip.core.ifm.IInformationFlowModel#reset()
	 */
	@Override
	public void reset() {
		_basicIfModel.reset();
		for (InformationFlowModelExtension ifme : _ifModelExtensions.values()) {
			ifme.reset();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.tum.in.i22.uc.pip.core.ifm.IInformationFlowModel#niceString()
	 */
	@Override
	public String niceString() {
		StringBuilder sb = new StringBuilder();
		String nl = System.getProperty("line.separator");
		sb.append("-----------------------------------------------"+nl);
		sb.append(_basicIfModel.niceString());

		for (InformationFlowModelExtension ifme : _ifModelExtensions.values()) {
			String model = ifme.niceString(); 
			if (model != null) sb.append(model);
		}

		sb.append(nl+"-----------------------------------------------"+nl);

		return sb.toString();
	}

	@Override
	public void remove(IData data) {
		_basicIfModel.remove(data);
	}

	@Override
	public void remove(IContainer cont) {
		_basicIfModel.remove(cont);
	}

	@Override
	public void emptyContainer(IContainer container) {
		_basicIfModel.emptyContainer(container);
	}

	@Override
	public void emptyContainer(IName containerName) {
		_basicIfModel.emptyContainer(containerName);
	}

	@Override
	public void addAlias(IContainer fromContainer, IContainer toContainer) {
		_basicIfModel.addAlias(fromContainer, toContainer);
	}

	@Override
	public void addAlias(IName fromContainerName, IName toContainerName) {
		_basicIfModel.addAlias(fromContainerName, toContainerName);
	}

	@Override
	public void removeAlias(IContainer fromContainer, IContainer toContainer) {
		_basicIfModel.removeAlias(fromContainer, toContainer);

	}

	@Override
	public Collection<IContainer> getAliasesFrom(IContainer container) {
		return _basicIfModel.getAliasesFrom(container);
	}

	@Override
	public Set<IContainer> getAliasTransitiveReflexiveClosure(
			IContainer container) {
		return _basicIfModel.getAliasTransitiveReflexiveClosure(container);
	}

	@Override
	public void removeAllAliasesFrom(IContainer fromContainer) {
		_basicIfModel.removeAllAliasesFrom(fromContainer);
	}

	@Override
	public void removeAllAliasesTo(IContainer toContainer) {
		_basicIfModel.removeAllAliasesTo(toContainer);
	}

	@Override
	public Set<IContainer> getAliasesTo(IContainer container) {
		return _basicIfModel.getAliasesTo(container);
	}

	@Override
	public Set<IContainer> getAliasTransitiveClosure(IContainer container) {
		return _basicIfModel.getAliasTransitiveClosure(container);
	}

	@Override
	public void addData(IData data, IContainer container) {
		_basicIfModel.addData(data, container);
	}

	@Override
	public void removeData(IData data, IContainer container) {
		_basicIfModel.removeData(data, container);
	}

	@Override
	public Set<IData> getData(IContainer container) {
		return _basicIfModel.getData(container);
	}

	@Override
	public Set<IData> getData(IName containerName) {
		return _basicIfModel.getData(containerName);
	}

	@Override
	public boolean copyData(IName srcContainerName, IName dstContainerName) {
		return _basicIfModel.copyData(srcContainerName, dstContainerName);
	}

	@Override
	public boolean copyData(IContainer srcContainer, IContainer dstContainer) {
		return _basicIfModel.copyData(srcContainer, dstContainer);
	}

	@Override
	public void addDataTransitively(Collection<IData> data,
			IName dstContainerName) {
		_basicIfModel.addDataTransitively(data, dstContainerName);
	}

	@Override
	public void addDataTransitively(Collection<IData> data,
			IContainer dstContainer) {
		_basicIfModel.addDataTransitively(data, dstContainer);
	}

	@Override
	public Set<IContainer> getContainers(IData data) {
		return _basicIfModel.getContainers(data);
	}

	@Override
	public <T extends IContainer> Set<T> getContainers(IData data, Class<T> type) {
		return _basicIfModel.getContainers(data, type);
	}

	@Override
	public void addData(Collection<IData> data, IContainer container) {
		_basicIfModel.addData(data, container);
	}

	@Override
	public void addName(IName name, IContainer container) {
		_basicIfModel.addName(name, container);
	}

	@Override
	public void addName(IName oldName, IName newName) {
		_basicIfModel.addName(oldName, newName);
	}

	@Override
	public void removeName(IName name) {
		_basicIfModel.removeName(name);
	}

	@Override
	public void removeName(IName name, boolean deleteUnreferencedContainer) {
		_basicIfModel.removeName(name, deleteUnreferencedContainer);
	}

	@Override
	public IContainer getContainer(IName name) {
		return _basicIfModel.getContainer(name);
	}

	@Override
	public Set<IContainer> getAllContainers() {
		return _basicIfModel.getAllContainers();
	}

	@Override
	public Collection<IName> getAllNames() {
		return _basicIfModel.getAllNames();
	}

	@Override
	public <T extends IName> Collection<T> getAllNames(Class<T> type) {
		return _basicIfModel.getAllNames(type);
	}

	@Override
	public Collection<IName> getAllNames(IContainer container) {
		return _basicIfModel.getAllNames(container);
	}

	@Override
	public <T extends IName> List<T> getAllNames(IName containerName,
			Class<T> type) {
		return _basicIfModel.getAllNames(containerName, type);
	}

	@Override
	public <T extends IName> List<T> getAllNames(IContainer cont, Class<T> type) {
		return _basicIfModel.getAllNames(cont, type);
	}

	@Override
	public List<IName> getAllNamingsFrom(IContainer pid) {
		return _basicIfModel.getAllNamingsFrom(pid);
	}

	@Override
	public void push() {
		_basicIfModel.push();
	}

	@Override
	public void pop() {
		_basicIfModel.pop();
	}

	@Override
	public boolean openScope(IScope scope) {
		return ((ScopeInformationFlowModel) _ifModelExtensions
				.get(EInformationFlowModel.SCOPE)).openScope(scope);
	}

	@Override
	public boolean closeScope(IScope scope) {
		return ((ScopeInformationFlowModel) _ifModelExtensions
				.get(EInformationFlowModel.SCOPE)).closeScope(scope);
	}

	@Override
	public boolean isScopeOpened(IScope scope) {
		return ((ScopeInformationFlowModel) _ifModelExtensions
				.get(EInformationFlowModel.SCOPE)).isScopeOpened(scope);
	}

	@Override
	public IScope getOpenedScope(IScope scope) {
		return ((ScopeInformationFlowModel) _ifModelExtensions
				.get(EInformationFlowModel.SCOPE)).getOpenedScope(scope);
	}

	@Override
	public IData newStructuredData(Map<String, Set<IData>> structure) {
		return ((StructuredInformationFlowModel) (_ifModelExtensions
				.get(EInformationFlowModel.STRUCTURE)))
				.newStructuredData(structure);
	}

	@Override
	public Map<String, Set<IData>> getStructureOf(IData data) {
		return ((StructuredInformationFlowModel) (_ifModelExtensions
				.get(EInformationFlowModel.STRUCTURE))).getStructureOf(data);
	}

	@Override
	public Set<IData> flattenStructure(IData data) {
		return ((StructuredInformationFlowModel) (_ifModelExtensions
				.get(EInformationFlowModel.STRUCTURE))).flattenStructure(data);
	}

	@Override
	public Entry<EBehavior, IScope> XBehav(IEventHandler eventHandler) {
		return ((ScopeInformationFlowModel) (_ifModelExtensions
				.get(EInformationFlowModel.SCOPE))).XBehav(eventHandler);
	}

	@Override
	public Set<Entry<IScope, EScopeState>> XDelim(IEventHandler eventHandler) {
		return ((ScopeInformationFlowModel) (_ifModelExtensions
				.get(EInformationFlowModel.SCOPE))).XDelim(eventHandler);
	}

	@Override
	public Map<IContainer, Set<IContainer>> XAlias(IEventHandler eventHandler) {
		return ((ScopeInformationFlowModel) (_ifModelExtensions
				.get(EInformationFlowModel.SCOPE))).XAlias(eventHandler);
	}

	@Override
	public void addName(IName name, IContainer container,
			boolean deleteUnreferencedContainer) {
		_basicIfModel.addName(name, container, deleteUnreferencedContainer);
	}

}
