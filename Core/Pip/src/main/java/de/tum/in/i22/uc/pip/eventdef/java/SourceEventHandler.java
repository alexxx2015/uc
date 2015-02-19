package de.tum.in.i22.uc.pip.eventdef.java;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;

import de.tum.in.i22.uc.cm.datatypes.basic.ContainerBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.NameBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic.EStatus;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IContainer;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IData;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IEvent;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IName;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IScope;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.cm.pip.interfaces.EBehavior;
import de.tum.in.i22.uc.cm.pip.interfaces.EScopeType;
import de.tum.in.i22.uc.pip.eventdef.ParameterNotFoundException;

public class SourceEventHandler extends JavaEventHandler {

	@Override
	protected IStatus update() {
		return update(EBehavior.INTRA, null);
	}

	@Override
	protected IScope buildScope(String delimiter) {
		return buildScope(EScopeType.JBC_GENERIC_LOAD);
	}


	@Override
	protected IStatus update(EBehavior direction, IScope scope) {
		IName srcName=null;
		Set<IData> scopeData = null;
		try {
//			String signature = getParameterValue("signature");
//			String location = getParameterValue("location");
			int pid = Integer.valueOf(getParameterValue("PID"));

			String sourceId = ""+pid+_javaIFDelim+getParameterValue("id");

//			String sourceId = pid+ _otherDelim + _srcPrefix+ _otherDelim + location + _javaIFDelim + signature;

			if ((sourceId != null) && (!sourceId.equals(""))) {
				srcName=new NameBasic(sourceId);
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
					scopeData = _informationFlowModel.getData(scopeName);

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

		if (direction.equals(EBehavior.INTRA)) return _messageFactory.createStatus(EStatus.OKAY);

		return new JavaPipStatus(EStatus.OKAY, srcName, scopeData);
	}


	@Override
	protected Pair<EBehavior, IScope> XBehav() {
		String delimiter = null;
		try {
			delimiter = getParameterValue(_delimiterName);
		} catch (ParameterNotFoundException e) {
			_logger.error(e.getMessage());
			return null;
		}
		delimiter=delimiter.toLowerCase();
		IScope scope = buildScope(delimiter);
		if (scope==null)return Pair.of(EBehavior.UNKNOWN, null);
		if (delimiter.equals(_openDelimiter)) Pair.of(EBehavior.INTRA, scope);
		if (delimiter.equals(_closeDelimiter)) Pair.of(EBehavior.IN, scope);
		//this line should never be reached
		return Pair.of(EBehavior.UNKNOWN, null);
	}


}
