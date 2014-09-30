package de.tum.in.i22.uc.pip.eventdef.java;

import java.util.Set;

import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IData;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IName;

public class JavaPipStatus extends StatusBasic {
	private final IName _contName;
	private final Set<IData> _dataSet;
	

	public JavaPipStatus(EStatus eStatus, IName contName, Set<IData> dataSet) {
		super(eStatus);
		_contName=contName;
		_dataSet=dataSet;
	}

//	public static JavaPipStatus createRemoteDataFlowStatus(RemoteDataFlowInfo dataflow) {
//		return new JavaPipStatus(EStatus.REMOTE_DATA_FLOW_HAPPENED, dataflow);
//	}

	public Set<IData> getDataSet() {
		return _dataSet;
	}
	
	public IName getContName() {
		return _contName;
	}

}
