package de.tum.in.i22.uc.pip.eventdef.java;

import java.util.HashSet;
import java.util.Set;

import de.tum.in.i22.uc.cm.datatypes.basic.ContainerBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.NameBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.Pair;
import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic.EStatus;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IContainer;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IData;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IEvent;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IName;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IScope;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.cm.datatypes.java.names.SourceSinkName;
import de.tum.in.i22.uc.cm.pip.interfaces.EBehavior;
import de.tum.in.i22.uc.cm.pip.interfaces.EScopeType;
import de.tum.in.i22.uc.pip.eventdef.ParameterNotFoundException;

public class SourceEventHandler extends JavaEventHandler {

	
	@Override
	protected IStatus update() {
		
		
		
		return _messageFactory.createStatus(EStatus.OKAY);
	}
	
	protected IStatus _update() {
		return update(EBehavior.INTRA, null);
	}

	@Override
	protected IScope buildScope(String delimiter) {
		return buildScope(EScopeType.JBC_GENERIC_LOAD);
	}
	
	
	@Override
	protected IStatus update(EBehavior direction, IScope scope) {
		try {
//			String signature = getParameterValue("signature");
//			String location = getParameterValue("location");
			int pid = Integer.valueOf(getParameterValue("PID"));
			
			String sourceId = ""+pid+_javaIFDelim+getParameterValue("id");
			
//			String sourceId = pid+ _otherDelim + _srcPrefix+ _otherDelim + location + _javaIFDelim + signature;

			if ((sourceId != null) && (!sourceId.equals(""))) {
				IName srcName=new NameBasic(sourceId);
				IContainer srcCnt = _informationFlowModel
						.getContainer(srcName);

				if (srcCnt==null){
					srcCnt=new ContainerBasic();
					_informationFlowModel.addName(srcName, srcCnt, true);
					Set<IContainer> set = containersByPid.get(""+pid);
					if (set==null) set = new HashSet<IContainer>();
					set.add(srcCnt);
					containersByPid.put(""+pid, set);
				}
				
				
				if ((direction.equals(EBehavior.IN))
						|| (direction.equals(EBehavior.INTRAIN))) {

					IScope os= _informationFlowModel.getOpenedScope(scope);
					if (os!=null) scope=os;
					IName scopeName = new NameBasic(scope.getId());
					Set<IData> scopeData = _informationFlowModel.getData(scopeName);
					
					_informationFlowModel.addDataTransitively(scopeData, srcCnt);
				}

				if ((direction.equals(EBehavior.OUT))
						|| (direction.equals(EBehavior.INTRAOUT))) {
					// ERROR: a sink event is never IN
					return new StatusBasic(EStatus.ERROR,
							"Error: A source event is never OUT");
				}
				
				

			}

		} catch (ParameterNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return _messageFactory.createStatus(EStatus.OKAY);
	}

	
	@Override
	protected Pair<EBehavior, IScope> XBehav(IEvent event) {
		String delimiter = null;
		try {
			delimiter = getParameterValue(_delimiterName);
		} catch (ParameterNotFoundException e) {
			_logger.error(e.getMessage());
			return null;
		}
		delimiter=delimiter.toLowerCase();
		IScope scope = buildScope(delimiter);
		if (scope==null)return new Pair<EBehavior, IScope>(EBehavior.UNKNOWN, null);
		if (delimiter.equals(_openDelimiter)) return new Pair<EBehavior, IScope>(EBehavior.INTRA, scope);
		if (delimiter.equals(_closeDelimiter)) return new Pair<EBehavior, IScope>(EBehavior.IN, scope);
		//this line should never be reached
		return new Pair<EBehavior, IScope>(EBehavior.UNKNOWN, null);
	}

	

	
}
