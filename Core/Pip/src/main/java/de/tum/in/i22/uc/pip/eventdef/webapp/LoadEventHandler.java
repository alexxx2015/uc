package de.tum.in.i22.uc.pip.eventdef.webapp;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;

import de.tum.in.i22.uc.cm.datatypes.basic.NameBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.ScopeBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic.EStatus;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IContainer;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IEvent;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IScope;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.cm.pip.interfaces.EBehavior;
import de.tum.in.i22.uc.cm.pip.interfaces.EScopeState;
import de.tum.in.i22.uc.cm.pip.interfaces.EScopeType;
import de.tum.in.i22.uc.pip.eventdef.ParameterNotFoundException;
import de.tum.in.i22.uc.pip.eventdef.scope.AbstractScopeEventHandler;

public class LoadEventHandler extends AbstractScopeEventHandler {

	public LoadEventHandler() {
		super();
	}

	private IScope buildScope() {
		String filename;
		String processId;
		try {
			filename = getParameterValue("filename");
			processId = getParameterValue("PID");

		} catch (ParameterNotFoundException e) {
			_logger.error(e.getMessage());
			return null;
		}

		Map<String, Object> attributes = new HashMap<String, Object>();
		attributes.put("app", "InternalFileSharing");
		attributes.put("filename", filename);
		attributes.put("PID", processId);
		
		return new ScopeBasic("InternalFileSharing loading file " + filename,
				EScopeType.LOAD_FILE, attributes);
	}

	@Override
	protected Set<Pair<EScopeState, IScope>> XDelim(IEvent event) {
		String delimiter;
		try {
			delimiter = getParameterValue(_delimiterName);

		} catch (ParameterNotFoundException e) {
			_logger.error(e.getMessage());
			return new HashSet<Pair<EScopeState, IScope>>();
		}
		delimiter=delimiter.toLowerCase();
		IScope scope = buildScope();
		if (scope == null) {
			return new HashSet<Pair<EScopeState, IScope>>();
		}
		Set<Pair<EScopeState, IScope>> res = new HashSet<Pair<EScopeState, IScope>>();
		if (delimiter.equals(_openDelimiter)) {
			res.add(Pair.of(EScopeState.OPEN, scope));
			return res;
		} else if (delimiter.equals(_closeDelimiter)) {
			res.add(Pair.of(EScopeState.CLOSE, scope));
			return res;
		}
		return res;
	}

	@Override
	protected Pair<EBehavior, IScope> XBehav(IEvent event) {
		IScope scope = buildScope();
		String _delimiter;
		try {
			_delimiter = getParameterValue(_delimiterName);

		} catch (ParameterNotFoundException e) {
			_logger.error(e.getMessage());
			return Pair.of(EBehavior.IN, scope);
		}
		_delimiter=_delimiter.toLowerCase();
		if ((scope == null) || !( _closeDelimiter.equals(_delimiter)))
			return Pair.of(EBehavior.UNKNOWN, null);
		return Pair.of(EBehavior.IN, scope);
	}

	@Override
	protected IStatus update() {
		return new StatusBasic(EStatus.OKAY);
	}

	@Override
	protected IStatus update(EBehavior direction, IScope scope) {
		if ((direction.equals(EBehavior.IN))&&(scope!=null)){
			IContainer src = _informationFlowModel.getContainer(new NameBasic(
					scope.getId()));
			_informationFlowModel.addData(
					_informationFlowModel.getData(src), _informationFlowModel.getContainer(new NameBasic("myIFSWebAppInternalContainer")));
			return new StatusBasic(EStatus.OKAY);
		} else return super.update(direction, scope);
	}

}
