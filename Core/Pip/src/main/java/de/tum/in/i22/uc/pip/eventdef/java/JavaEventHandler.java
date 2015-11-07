package de.tum.in.i22.uc.pip.eventdef.java;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;

import de.tum.in.i22.uc.cm.datatypes.basic.ScopeBasic;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IContainer;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IData;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IName;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IScope;
import de.tum.in.i22.uc.cm.datatypes.java.containers.ArrayContainer;
import de.tum.in.i22.uc.cm.datatypes.java.containers.ArrayElementContainer;
import de.tum.in.i22.uc.cm.datatypes.java.containers.ObjectContainer;
import de.tum.in.i22.uc.cm.datatypes.java.containers.ValueContainer;
import de.tum.in.i22.uc.cm.datatypes.java.names.ArrayElementName;
import de.tum.in.i22.uc.cm.datatypes.java.names.ArrayName;
import de.tum.in.i22.uc.cm.datatypes.java.names.InstanceMethodVariableName;
import de.tum.in.i22.uc.cm.datatypes.java.names.ObjectName;
import de.tum.in.i22.uc.cm.pip.ifm.IAnyInformationFlowModel;
import de.tum.in.i22.uc.cm.pip.interfaces.EScopeState;
import de.tum.in.i22.uc.cm.pip.interfaces.EScopeType;
import de.tum.in.i22.uc.cm.settings.Settings;
import de.tum.in.i22.uc.pip.eventdef.ParameterNotFoundException;
import de.tum.in.i22.uc.pip.eventdef.scope.AbstractScopeEventHandler;

public abstract class JavaEventHandler extends AbstractScopeEventHandler {

	protected static Map<String, String[]> iFlow = new HashMap<String, String[]>();

	protected static Map<String, Set<IContainer>> containersByPid = new HashMap<String, Set<IContainer>>();

	protected static Map<String, String> contextToObject = new HashMap<String, String>();

	protected final String _paramId = "id";
	protected final String _paramSignature = "signature";
	protected final String _paramLocation = "location";
	protected final String _paramParamPos = "parampos";
	protected final String _paramType = "type";
	protected final String _paramOffset = "offset";
	protected final String _paramObjectId = "objectId";
	protected final String _paramContextId = "context";
	protected final String _paramContextLocation = "contextLocation";
	protected final String _paramContextOffset = "contextOffset";
	protected final String _paramPID = "PID";
	protected final String _paramThreadId = "ThreadId";

	protected final String _javaIFDelim = ":";
	protected final String _otherDelim = Settings.getInstance().getJoanaPidPoiSeparator();
	protected final String _srcPrefix = "source";
	protected final String _snkPrefix = "sink";

	protected final String DLM = "|";
	protected final String RET = "ret";
	protected final String OBJ = "obj";
	protected final String NOADDRESS = "implicit";

	public String scopeName(EScopeType type, String fileDescriptor, String pid) {
		return "Scope for generic " + (type.equals(EScopeType.JBC_GENERIC_LOAD) ? "source" : "sink")
				+ " event with fileDescriptor + " + fileDescriptor + " (pid " + pid + ")";
	}

	@Override
	public void reset() {
		super.reset();
		// other parameters don't need to be reset cause they are settings
		// values
	}

	/*
	 * For this generic action the scope is only one and the "delimiter"
	 * (start/end) is given as a parameter
	 *
	 * @see de.tum.in.i22.pip.core.eventdef.BaseEventHandler#createScope()
	 */
	@Override
	protected Set<Pair<EScopeState, IScope>> XDelim() {
		String delimiter = null;
		try {
			delimiter = getParameterValue(_delimiterName);
		} catch (ParameterNotFoundException e) {
			_logger.error(e.getMessage());
			return null;
		}

		delimiter = delimiter.toLowerCase();
		IScope scope = buildScope(delimiter);

		if (scope == null) {
			return new HashSet<Pair<EScopeState, IScope>>();
		}

		Set<Pair<EScopeState, IScope>> res = new HashSet<Pair<EScopeState, IScope>>();
		if (delimiter.equals(_openDelimiter)) {
			// opening already handled at step 2 of executeEvent in
			// BaseEventHandler
			// openScope(scope);
			res.add(Pair.of(EScopeState.OPEN, scope));
		} else if (delimiter.equals(_closeDelimiter)) {
			// closing already handled at step 4 of executeEvent in
			// BaseEventHandler
			// closeScope(scope);
			res.add(Pair.of(EScopeState.CLOSE, scope));
		}

		return res;
	}

	protected IScope buildScope(String delimiter) {
		// TO BE OVERRIDDEN BY SUBCLASSES SOURCE AND SINK
		return null;
	}

	/*
	 * Auxiliary function to support the creation of a scope for sources and
	 * sinks
	 */
	protected IScope buildScope(EScopeType type) {
		String fileDescriptor;
		// String tid;
		String pid;
		try {
			fileDescriptor = getParameterValue("fileDescriptor");
			// tid = getParameterValue("ThreadId");
			pid = getParameterValue("PID");
		} catch (ParameterNotFoundException e) {
			_logger.error(e.getMessage());
			return null;
		}

		String HRscope = scopeName(type, fileDescriptor, pid);

		// create the new scope
		Map<String, Object> attributes = new HashMap<String, Object>();
		attributes.put("fileDescriptor", fileDescriptor);
		// attributes.put("tid", tid);
		attributes.put("pid", pid);
		return new ScopeBasic(HRscope, type, attributes);
	}

	/**
	 * This method is invoked by the KillProcess Event Handler from Windows. The
	 * purpose of this method is to clean up the PIP removing all the
	 * Java-specific containers when the process dies.
	 */

	public static void killProcess(String pid, IAnyInformationFlowModel _informationFlowModel) {
		if ((pid == null) || (pid.equals(""))) {
			_logger.error("Impossible to kill process with null PID");
			return;
		}

		Set<IContainer> set = containersByPid.get(pid);
		if (set != null)
			for (IContainer c : set)
				_informationFlowModel.remove(c);

		// String _otherDelim =
		// Settings.getInstance().getJoanaPidPoiSeparator();
		// for (String entry : iFlow.keySet()) {
		// String[] fields = entry.split(_otherDelim);
		// if (fields[0].equals(pid)) {
		// _informationFlowModel.removeName(new NameBasic(entry), true);
		// }
		// }
	}

	private String getPID() {
		try {
			return getParameterValue("processId");
		} catch (ParameterNotFoundException e) {
			// TODO Auto-generated catch block
			_logger.error(e.getMessage());
		}
		return null;
	}

	protected boolean isValidAddress(String address) {
		return address != null && !address.equals("null");
	}

	protected boolean isArrayType(String className) {
		return className.contains("[");
	}

	/**
	 * Replaces all occurences of an uninitialized object identifier in names
	 * (and containers) for an object whose constructor was called but has not
	 * returned yet. This method is called in every event handler and ensures
	 * that the model objects and names containing the object reference added by
	 * a constructor call get the correct address. Long scan of the names is
	 * only done once (ensured by checking if the name of the object itself has
	 * been fixed).
	 * 
	 * @param threadId
	 * @param pid
	 * @param className
	 * @param objectAddress
	 * @param methodName
	 */
	protected void addAddressToNamesAndContainerIfNeeded(String threadId, String pid, String className,
			String objectAddress, String methodName) {
		if (objectAddress.equals("null"))
			return;
		if (methodName.startsWith("<init>")) {
			String addressReplacement = threadId + NOADDRESS;

			// object
			IName oldObjectName = new ObjectName(pid, className, addressReplacement);
			IContainer oldObjectContainer = _informationFlowModel.getContainer(oldObjectName);
			if (oldObjectContainer != null) {
				IName newObjectName = new ObjectName(pid, className, objectAddress);
				IContainer newObjectContainer = addContainerIfNotExists(newObjectName, className, objectAddress);
				_informationFlowModel.addData(_informationFlowModel.getData(oldObjectContainer), newObjectContainer);
				for (IContainer aliasFrom : _informationFlowModel.getAliasesFrom(oldObjectContainer)) {
					_informationFlowModel.addAlias(aliasFrom, newObjectContainer);
				}
				for (IContainer aliasTo : _informationFlowModel.getAliasesTo(oldObjectContainer)) {
					_informationFlowModel.addAlias(newObjectContainer, aliasTo);
				}
				for (IName oldName : _informationFlowModel.getAllNames(oldObjectContainer)) {
					_informationFlowModel.addName(oldName, newObjectContainer, false);
				}
				_informationFlowModel.removeName(oldObjectName, true);
			} else
				return; // if there is no uninitialized object, then the
			// replacement was already done

			// method locals
			// Add address to constructor parameters and local variables of constructur
			Set<InstanceMethodVariableName> namesToReplace = new HashSet<>();
			for (IName name : _informationFlowModel.getAllNames()) {
				if (name instanceof InstanceMethodVariableName) {
					InstanceMethodVariableName iVarName = (InstanceMethodVariableName) name;
					if (iVarName.getPid().equals(pid) && iVarName.getThreadId().equals(threadId)
							&& iVarName.getClassName().equals(className)
							&& iVarName.getObjectAddress().equals(addressReplacement)) {
						namesToReplace.add(iVarName);
					}
				}
			}
			for (InstanceMethodVariableName oldName : namesToReplace) {
				IName fixedName = new InstanceMethodVariableName(oldName.getPid(), oldName.getThreadId(),
						oldName.getClassName(), objectAddress, oldName.getMethodName(), oldName.getVarName());
				_informationFlowModel.addName(oldName, fixedName);
				_informationFlowModel.removeName(oldName);
			}
		}
	}

	/**
	 * Looks up in the InformationFlowModel if there is already a container with
	 * the specified name. true => return container. false => Create a new
	 * container based on provided className, address and name. If a value
	 * container is desired, className and address should be null.
	 * 
	 * @param name
	 * @param className
	 * @param address
	 * @return
	 */

	protected IContainer addContainerIfNotExists(IName name, String className, String address) {
		IContainer container = _informationFlowModel.getContainer(name);
		if (container == null) {
			if (isValidAddress(address) && className != null) {
				if (isArrayType(className)) {
					container = new ArrayContainer(getPID(), className, address);
					if (!(name instanceof ArrayName)) {
						ArrayName arrayName = new ArrayName(getPID(), className, address);
						_informationFlowModel.addName(arrayName, container, false);
					}
				} else {
					container = new ObjectContainer(getPID(), className, address);
					if (!(name instanceof ObjectName)) {
						ObjectName objectName = new ObjectName(getPID(), className, address);
						_informationFlowModel.addName(objectName, container, false);
					}
				}
			} else {
				if (name instanceof ArrayElementName) {
					ArrayElementName aeName = (ArrayElementName) name;
					container = new ArrayElementContainer(getPID(), aeName.getType(), aeName.getAddress(),
							aeName.getIndex());
				} else {
					container = new ValueContainer(getPID());
				}
			}
			_informationFlowModel.addName(name, container, false);
		}
		return container;
	}

	/**
	 * Traverses the alias graph and returns all data being "visible" in
	 * {@code container} in respect to the alias function.
	 * 
	 * @param container
	 * @return
	 */
	protected Set<IData> getDataTransitively(IContainer container) {
		return getDataTransitively(container, new HashSet<>());
	}

	private Set<IData> getDataTransitively(IContainer container, Set<IContainer> visitedContainers) {
		Set<IData> result = new HashSet<>();
		if (container == null) {
			return result;
		}

		result.addAll(_informationFlowModel.getData(container));

		for (IContainer c : _informationFlowModel.getAliasesTo(container)) {
			if (!visitedContainers.contains(c)) {
				visitedContainers.add(c);
				result.addAll(getDataTransitively(c, visitedContainers));
			}
		}

		return result;
	}
}
