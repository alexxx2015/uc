package de.tum.in.i22.uc.pip.eventdef.excel;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import de.tum.in.i22.uc.cm.datatypes.basic.ContainerBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.NameBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic.EStatus;
import de.tum.in.i22.uc.cm.datatypes.excel.CellName;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IContainer;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IData;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.pip.eventdef.ParameterNotFoundException;

public class SaveEventHandler extends ExcelEvents {

	public SaveEventHandler() {
		super();
	}

	@Override
	protected IStatus update() {
		String externalFile = "";
		String targetWb = "";
		try {
			externalFile = getParameterValue("externalFile");
			targetWb = getParameterValue("targetWb");

		} catch (ParameterNotFoundException e) {
			_logger.error(e.getMessage());
			return _messageFactory.createStatus(
					EStatus.ERROR_EVENT_PARAMETER_MISSING, e.getMessage());
		}
		if ((externalFile == null) || (externalFile.equals(""))|| (targetWb.equals(""))|| (targetWb.equals(""))) {
			_logger.debug("Either externalFile path is missing OR structuredData is null");
			return _messageFactory.createStatus(EStatus.OKAY);
		}
		
		IContainer externalFileContainer = _informationFlowModel.getContainer(new NameBasic(externalFile));
		if (externalFileContainer==null) {
			externalFileContainer=new ContainerBasic();
			_informationFlowModel.addName(new NameBasic(externalFile), externalFileContainer, true);
		}

		Collection<CellName> allCells = _informationFlowModel.getAllNames(CellName.class);
		Map<String,Set<IData>> map = new HashMap<String,Set<IData>>();
		
		
		for (CellName cn : allCells){
			if (targetWb.equals(cn.getWorkbook())){
				map.put(cn.getName(),_informationFlowModel.getData(cn));
			}
		}
		
		IData SD=_informationFlowModel.newStructuredData(map);
		_informationFlowModel.addData(SD, externalFileContainer);
		
		
		return _messageFactory.createStatus(EStatus.OKAY);
	}

}
