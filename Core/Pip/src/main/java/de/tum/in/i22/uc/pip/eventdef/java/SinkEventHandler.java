package de.tum.in.i22.uc.pip.eventdef.java;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import de.tum.in.i22.uc.cm.datatypes.basic.NameBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic.EStatus;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IContainer;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IData;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.pip.eventdef.ParameterNotFoundException;

public class SinkEventHandler extends JavaEventHandler {

	@Override
	protected IStatus update() {
		IStatus _return = _messageFactory.createStatus(EStatus.OKAY);
		try {
			String signature = getParameterValue("signature");			
			String delimiter = getParameterValue("delimiter");
			String location = getParameterValue("location");
			
			String sinkId = location+_javaIFDelim+signature;
			String sourceId = iFlow.get(sinkId);
			
			if((sourceId != null) && (!sourceId.equals(""))){
				IContainer srcCnt = _informationFlowModel.getContainer(new NameBasic("src_"+sourceId));
				IContainer sinkCnt = _informationFlowModel.getContainer(new NameBasic("snk_"+sinkId));				

				Set<IData> srcData = _informationFlowModel.getData(srcCnt);
				_informationFlowModel.addData(srcData, sinkCnt);
				
				Collection<IContainer> aliasSinkCnt = _informationFlowModel.getAliasesFrom(sinkCnt);
				Iterator<IContainer> sinkCntIt = aliasSinkCnt.iterator();
				while(sinkCntIt.hasNext()){
//					srcData = _informationFlowModel.getData(sinkCntIt.next());
					_informationFlowModel.addData(srcData, sinkCntIt.next());
				}
				
				
			}
			
		} catch (ParameterNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	

		return _messageFactory.createStatus(EStatus.OKAY);
	}

}
