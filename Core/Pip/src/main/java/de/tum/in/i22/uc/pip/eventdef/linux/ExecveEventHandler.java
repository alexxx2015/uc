package de.tum.in.i22.uc.pip.eventdef.linux;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;

import de.tum.in.i22.uc.cm.datatypes.basic.NameBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.ScopeBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic.EStatus;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IContainer;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IData;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IEvent;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IName;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IScope;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.cm.datatypes.linux.FileContainer;
import de.tum.in.i22.uc.cm.datatypes.linux.FilenameName;
import de.tum.in.i22.uc.cm.datatypes.linux.ProcessContainer;
import de.tum.in.i22.uc.cm.datatypes.linux.ProcessName;
import de.tum.in.i22.uc.cm.pip.EInformationFlowModel;
import de.tum.in.i22.uc.cm.pip.interfaces.EBehavior;
import de.tum.in.i22.uc.cm.pip.interfaces.EScopeState;
import de.tum.in.i22.uc.cm.pip.interfaces.EScopeType;
import de.tum.in.i22.uc.cm.settings.Settings;
import de.tum.in.i22.uc.pip.eventdef.ParameterNotFoundException;
import de.tum.in.i22.uc.pip.eventdef.scope.AbstractScopeEventHandler;
import de.tum.in.i22.uc.pip.extensions.structured.Merger;
import de.tum.in.i22.uc.pip.extensions.structured.Splitter;
import de.tum.in.i22.uc.pip.extensions.structured.StructuredEvent;

/**
 * 
 * @author Florian Kelbert
 * 
 */
public class ExecveEventHandler extends AbstractScopeEventHandler {
	private StructuredEvent _structuredEvent = null;
	private String host = null;
	private int pid = -1;
	private String filename = null;
	private String cmdline = null;
	private String[] cmds = null;
	private String newProcessName = null;
	private boolean errorInParsingParameters = false;
	private final boolean structActive = Settings.getInstance().getEnabledInformationFlowModels()
			.contains(EInformationFlowModel.STRUCTURE);

	private IName fileName = null;
	private IName procName = null;

	private IContainer fileCont = null;
	private IContainer procCont = null;

	private IData _structData = null;

	private void parseBasicParameters() throws ParameterNotFoundException {
		_logger.trace("Parsing basic parameters of Execve eventhandler");
		try {
			host = getParameterValue("host");
			pid = Integer.valueOf(getParameterValue("pid"));
			filename = getParameterValue("filename");
			cmdline = getParameterValue("cmdline");
		} catch (ParameterNotFoundException e) {
			_logger.error(e.getMessage());
			throw e;
		}

		fileName = FilenameName.create(host, LinuxEvents.toRealPath(filename));
		procName = ProcessName.create(host, pid);

		fileCont = _informationFlowModel.getContainer(fileName);
		procCont = _informationFlowModel.getContainer(procName);

		// if no command line arguments, return empty delimiter
		if (cmdline != null) {
			cmds = cmdline.split(" ");
			if (cmds.length > 1) {
				newProcessName = cmds[0];
			}
		}

		_logger.trace(" host:" + host + " pid:" + pid + " filename:" + filename + " cmdline:" + cmdline
				+ " processname: " + newProcessName);

	}

	@Override
	protected Set<Pair<EScopeState, IScope>> XDelim() {
		try {
			parseBasicParameters();
		} catch (ParameterNotFoundException e) {
			errorInParsingParameters = true;
		}

		// if structured model is not activated, no reason to continue
		if (!structActive)
			return new HashSet<Pair<EScopeState, IScope>>();

		// if no command line arguments, return empty delimiter
		String[] cmds;
		if ((cmdline != null) && (errorInParsingParameters == false)) {
			cmds = cmdline.split(" ");
		} else
			return new HashSet<Pair<EScopeState, IScope>>();

		// build structured object parsing parameters
		_structuredEvent = StructuredEvent.createFromExecve(host, filename, cmds, _informationFlowModel);

		if (_structuredEvent == null)
			return new HashSet<Pair<EScopeState, IScope>>();

		if (_structuredEvent instanceof Merger)
			_structData = ((Merger) _structuredEvent).buildStructure();

		Set<Pair<EScopeState, IScope>> res = new HashSet<Pair<EScopeState, IScope>>();

		Set<IScope> scopes = buildScopes();
		for (IScope scope : scopes)
			res.add(Pair.of(EScopeState.OPEN, scope));
		return res;

	}

	private Set<IScope> buildScopes() {
		if (_structuredEvent == null)
			return null;

		Set<IScope> result = new HashSet<IScope>();

		if (_structuredEvent instanceof Merger) {
			Map<String, Object> attributes = new HashMap<String, Object>();
			attributes.put("app", newProcessName);
			attributes.put("host", host);
			attributes.put("pid", pid);
			attributes.put("intermediateContainerName", ((Merger) _structuredEvent).getIntermediateContainerName().getName());
			result.add(new ScopeBasic(newProcessName + " merging into intermediate container " + ((Merger) _structuredEvent).getIntermediateContainerName().getName(),
					EScopeType.TAR_MERGE, attributes));

		} else if (_structuredEvent instanceof Splitter) {
			for (Entry<String, IName> structEntry : ((Splitter) _structuredEvent).getDestination().entrySet()) {
				Map<String, Object> attributes = new HashMap<String, Object>();
				attributes.put("app", newProcessName);
				attributes.put("host", host);
				attributes.put("pid", pid);
				attributes.put("destContainerName", structEntry.getValue());
				attributes.put("destContainerLabel", structEntry.getKey());
				result.add(new ScopeBasic(newProcessName + " splitting into destination container "
						+ structEntry.getValue(), EScopeType.TAR_SPLIT, attributes));
			}

		} else
			return null; // this condition should never be reached, either a
							// structured event is a merger or a splitter

		return result;
	}

	@Override
	protected IStatus update(EBehavior direction, IScope scope) {

		if ((_structuredEvent == null) || (direction.equals(EBehavior.INTRAOUT)) || (direction.equals(EBehavior.INTRAIN)) || (direction.equals(EBehavior.INTRA)) || (direction.equals(EBehavior.UNKNOWN))) {
			if (fileCont == null) {
				fileCont = new FileContainer();
				_informationFlowModel.addName(fileName, fileCont);
			}

			if (procCont == null) {
				procCont = new ProcessContainer(host, pid);
				_informationFlowModel.addName(procName, procCont);
			}

			_informationFlowModel.copyData(fileName, procName);
		}

		if ((direction.equals(EBehavior.OUT)) || (direction.equals(EBehavior.INTRAOUT))) {

			if (scope.getScopeType().equals(EScopeType.SPECIAL)) { // SPLITTER

				// retrieve the structured data from the file using the
				// parameters of the structured event
				Set<IData> sd = _informationFlowModel.getData(((Splitter) _structuredEvent).getIntermediateContainer());

				for (IScope s : (Set<IScope>) scope.getAttribute("scopes")) { // Retrieve
																				// all
																				// the
																				// scopes
					IContainer splitDest = _informationFlowModel.getContainer(new NameBasic(s.getId()));

					for (IData d : sd) {
						Map<String, Set<IData>> struct = _informationFlowModel.getStructureOf(d);
						if (struct != null) {
							_informationFlowModel.addData(
									struct.get(((Splitter) _structuredEvent).getIntermediateContainer().toString()),
									splitDest);
						} else {
							_informationFlowModel.addData(d, splitDest);
						}
					}

				}
			} else { // MERGER

				_informationFlowModel.addData(_structData,
						_informationFlowModel.getContainer(new NameBasic(scope.getId())));
			}
		}
		return STATUS_OKAY;
	}

	@Override
	protected Pair<EBehavior, IScope> XBehav() {
		// if structured model is not activated, no reason to continue
		if (!structActive)
			return Pair.of(EBehavior.INTRA, null);
		if (_structuredEvent == null)
			return Pair.of(EBehavior.INTRA, null);

		if (_structuredEvent instanceof Merger) {
			Set<IScope> scopes = buildScopes();
			if (scopes == null) {
				_logger.error("Error in function buildscopes. For a merger we should get exactly one scope instead we got a null");
				return Pair.of(EBehavior.UNKNOWN, null);
			}
			if (scopes.size() != 1) {
				_logger.error("Error in function buildscopes. For a merger we should get exactly one scope instead of "
						+ scopes.size());
				return Pair.of(EBehavior.UNKNOWN, null);
			}
			return Pair.of(EBehavior.OUT, scopes.iterator().next());
		} else if (_structuredEvent instanceof Splitter) {
			Set<IScope> scopes = buildScopes();
			if (scopes == null) {
				_logger.error("Error in function buildscopes. For a splitter we should get exactly at least one scope instead we got a null");
				return Pair.of(EBehavior.UNKNOWN, null);
			}
			_logger.debug("Special case of an event behaving OUT w.r.t. multiple scopes");

			Map<String, Object> attributes = new HashMap<String, Object>();
			attributes.put("scopes", scopes);
			return Pair.of(EBehavior.OUT, new ScopeBasic("SPECIAL OUT", EScopeType.SPECIAL, attributes));
		} else
			return Pair.of(EBehavior.INTRA, null);
	}

	@Override
	protected IStatus update() {
		return update(EBehavior.INTRA, null);
	}

}
