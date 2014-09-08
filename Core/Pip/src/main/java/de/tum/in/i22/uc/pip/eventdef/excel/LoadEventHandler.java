package de.tum.in.i22.uc.pip.eventdef.excel;

import java.util.Collection;
import java.util.HashSet;
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

public class LoadEventHandler extends ExcelEvents {

	public LoadEventHandler() {
		super();
	}

	@Override
	protected IStatus update() {
		String externalFile = "";
		try {
			externalFile = getParameterValue("externalFile");
		} catch (ParameterNotFoundException e) {
			_logger.error(e.getMessage());
			return _messageFactory.createStatus(
					EStatus.ERROR_EVENT_PARAMETER_MISSING, e.getMessage());
		}
		if ((externalFile == null) || (externalFile.equals(""))){
			String err = "ExternalFile path  is null";
			_logger.debug(err);
			return _messageFactory.createStatus(
					EStatus.ERROR_EVENT_PARAMETER_MISSING, err);
		}

		IContainer externalFileContainer = _informationFlowModel.getContainer(new NameBasic(externalFile));

		if (externalFileContainer==null){
			String err = "ExternalFile path  is null";
			_logger.debug(err);
			return _messageFactory.createStatus(
					EStatus.ERROR_EVENT_PARAMETER_MISSING, err);
		}


		Set<IData> externalFileContent = _informationFlowModel.getData(externalFileContainer);
		if (externalFileContent==null) return _messageFactory.createStatus(EStatus.OKAY);

		Collection<CellName> allCells = _informationFlowModel.getAllNames(CellName.class);

		//set of new cells added by the structure
		Set<CellName> newCells = new HashSet<CellName>();

		//set of data that needs to be added to every container
		Set<IData> unstructuredData = new HashSet<IData>();


		//safety check:  the structure we are loading should concern only one workbook
		String newWbName=null;

		for (IData data : externalFileContent){
			Map<String,Set<IData>> map = _informationFlowModel.getStructureOf(data);
			if (map==null)	{
				unstructuredData.add(data);
			} else {
				for (String label : map.keySet()){
					CellName newCellName=CellName.create(label);
					IContainer newCellContainer = new ContainerBasic();
					// there cannot already exists a container with this name. The workbook cannot be already opened
					_informationFlowModel.addName(newCellName, newCellContainer, true);
					_informationFlowModel.addData(map.get(label), newCellContainer);

					//remember which new cell has been added
					newCells.add(newCellName);


					//safety check: all the new cells must be talking about the same workbook
					if (newWbName==null){
							newWbName=newCellName.getWorkbook();
					}
					else{
						if (!newWbName.equals(newCellName.getWorkbook())){
							throw new RuntimeException("Load event shouldn't be loading more than one workbook at the time ("+newWbName + "," + newCellName.getWorkbook()+"). Aborting");
						}
					}

				}
			}

		}

		return _messageFactory.createStatus(EStatus.OKAY);
	}
}
