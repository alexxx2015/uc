package de.tum.in.i22.uc.pip.eventdef.java;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import de.tum.in.i22.uc.cm.datatypes.basic.NameBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.Pair;
import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic.EStatus;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IContainer;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IData;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IEvent;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IScope;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.cm.datatypes.java.SourceSinkName;
import de.tum.in.i22.uc.cm.pip.interfaces.EBehavior;
import de.tum.in.i22.uc.cm.pip.interfaces.EScopeType;
import de.tum.in.i22.uc.pip.eventdef.ParameterNotFoundException;

public class SinkEventHandler extends JavaEventHandler {


	@Override
	protected IScope buildScope(String delimiter) {
		return buildScope(EScopeType.JBC_GENERIC_SAVE);
	}

	
	@Override
	protected IStatus update() {
		return update(EBehavior.INTRA, null);
	}
	
	@Override
	protected IStatus update(EBehavior direction, IScope scope) {
		try {
//			String signature = getParameterValue("signature");
//			String location = getParameterValue("location");
			int pid = Integer.valueOf(getParameterValue("PID"));
			
			String sinkId = ""+pid+_javaIFDelim+getParameterValue("id");
			
//			String sinkId = pid+_otherDelim + _snkPrefix+ _otherDelim + location + _javaIFDelim + signature;
			String[] sourceIds = iFlow.get(sinkId);

			Set<IData> srcData = new HashSet<IData>();
			
			if (sourceIds!=null){
			for (String sourceId : sourceIds){
//				String[] arrStr=sourceId.split(_otherDelim);
//					if ((sourceId != null) && (!sourceId.equals("")) && (arrStr!=null) && (arrStr.length==3)) {
//						IContainer srcCnt = _informationFlowModel
//								.getContainer(new SourceSinkName(sourceId)); 
//						Set<IData> s = _informationFlowModel.getData(srcCnt);
//						if (s!=null) srcData.addAll(s);
//					}
				IContainer srcCnt = _informationFlowModel
							.getContainer(new NameBasic(sourceId)); 
					Set<IData> s = _informationFlowModel.getData(srcCnt);
					if (s!=null) srcData.addAll(s);
			}
			
			IContainer sinkCnt = _informationFlowModel
					.getContainer(new NameBasic(sinkId));

			
				if ((direction.equals(EBehavior.INTRA))
						|| (direction.equals(EBehavior.INTRAOUT))) {
					_informationFlowModel.addData(srcData, sinkCnt);
					Collection<IContainer> aliasSinkCnt = _informationFlowModel
							.getAliasesFrom(sinkCnt);
					Iterator<IContainer> sinkCntIt = aliasSinkCnt.iterator();
					while (sinkCntIt.hasNext()) {
						_informationFlowModel
								.addDataTransitively(srcData, sinkCntIt.next());
					}
				}

				if ((direction.equals(EBehavior.IN))
						|| (direction.equals(EBehavior.INTRAIN))) {
					// ERROR: a sink event is never IN
					return new StatusBasic(EStatus.ERROR,
							"Error: A sink event is never IN");
				}

				if ((direction.equals(EBehavior.OUT))
						|| (direction.equals(EBehavior.INTRAOUT))) {
					IScope os= _informationFlowModel.getOpenedScope(scope);
					if (os!=null) scope=os;
					_informationFlowModel.addDataTransitively(srcData,
							new NameBasic(scope.getId()));
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
		if (delimiter.equals(_openDelimiter)) return new Pair<EBehavior, IScope>(EBehavior.OUT, scope);
		if (delimiter.equals(_closeDelimiter)) return new Pair<EBehavior, IScope>(EBehavior.INTRA, scope);
		//this line should never be reached
		return new Pair<EBehavior, IScope>(EBehavior.UNKNOWN, null);
	}

}
