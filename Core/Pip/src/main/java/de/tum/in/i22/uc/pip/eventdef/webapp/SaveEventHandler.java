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

public class SaveEventHandler extends AbstractScopeEventHandler {
		private String _delimiter = null;

		@Override
		public void reset(){
			super.reset();
			_delimiter=null;
			//other parameters don't need to be reset cause they are settings values
		}

		public SaveEventHandler() {
			super();
		}

		private IScope buildScope() {
			String filename;
			try {
				filename = getParameterValue("filename");

			} catch (ParameterNotFoundException e) {
				_logger.error(e.getMessage());
				return null;
			}

			Map<String, Object> attributes = new HashMap<String, Object>();
			attributes.put("app", "IFSWebApp");
			attributes.put("filename", filename);

			return new ScopeBasic("IFSWebApp saving file " + filename,
					EScopeType.SAVE_FILE, attributes);
		}

		@Override
		protected Set<Pair<EScopeState, IScope>> XDelim(IEvent event) {
			try {
				_delimiter = getParameterValue(_delimiterName);

			} catch (ParameterNotFoundException e) {
				_logger.error(e.getMessage());
				return new HashSet<Pair<EScopeState, IScope>>();
			}
			_delimiter=_delimiter.toLowerCase();
			IScope scope = buildScope();
			if (scope==null){
				return new HashSet<Pair<EScopeState, IScope>>();
			}
						Set<Pair<EScopeState, IScope>> res = new HashSet<Pair<EScopeState, IScope>>();
			if (_delimiter.equals(_openDelimiter)) {
				res.add(Pair.of(EScopeState.OPEN, scope));
				return res;
			} else if (_delimiter.equals(_closeDelimiter)) {
				res.add(Pair.of(EScopeState.CLOSE, scope));
				return res;
			}
			return res;
		}

		@Override
		protected Pair<EBehavior, IScope> XBehav(IEvent event) {
			IScope scope = buildScope();
			if ((scope == null) || !( _closeDelimiter.equals(_delimiter)))
				return Pair.of(EBehavior.UNKNOWN, null);
			return Pair.of(EBehavior.OUT, scope);
		}

		@Override
		protected IStatus update() {
			return new StatusBasic(EStatus.OKAY);
		}

		@Override
		protected IStatus update(EBehavior direction, IScope scope) {
			if ((direction.equals(EBehavior.OUT))&&(scope!=null)){
				IContainer dest = _informationFlowModel.getContainer(new NameBasic(
						scope.getId()));
				_informationFlowModel.addData(
						_informationFlowModel.getData(_informationFlowModel.getContainer(new NameBasic("myIFSWebAppInternalContainer"))),dest);
				return new StatusBasic(EStatus.OKAY);
			} else return super.update(direction, scope);
		}
	}
