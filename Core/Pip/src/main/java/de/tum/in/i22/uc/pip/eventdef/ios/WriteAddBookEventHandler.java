package de.tum.in.i22.uc.pip.eventdef.ios;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.http.message.BasicNameValuePair;
import org.eclipse.persistence.platform.database.oracle.NString;

import de.tum.in.i22.uc.cm.datatypes.basic.NameBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic.EStatus;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IContainer;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IData;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IName;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.cm.datatypes.linux.FiledescrName;
import de.tum.in.i22.uc.cm.pip.interfaces.EBehavior;
import de.tum.in.i22.uc.pip.PipHandler;
import de.tum.in.i22.uc.pip.eventdef.BaseEventHandler;
import de.tum.in.i22.uc.pip.eventdef.ParameterNotFoundException;

public class WriteAddBookEventHandler extends BaseEventHandler {

	@Override
	protected IStatus update() {

		String fileName = null;// identifier of the file to be written
		String processName = null; // identifier of the running application
									// (app's name)
		String processId = null; // identifier of the phone
		String nameOfTheRunningAppMemoryContainer = null;
		String dataFilter = null;
		// needed if the filter is ON
		String dataId = null; // identifier of the data
		String originalContainerName = null; // name of the original ucitem
												// container = AB*itemID
		try {
			fileName = getParameterValue("FileName");
			processId = getParameterValue("ProcessID");
			processName = getParameterValue("ProcessName");
			dataFilter = getParameterValue("DataFilter");
			if (dataFilter.equals("ON"))
				dataId = getParameterValue("DataID");
			if(dataFilter.equals("OFF"))
				nameOfTheRunningAppMemoryContainer = getParameterValue("nameOfTheRunningAppMemoryContainer");
				
		} catch (ParameterNotFoundException e) {
			_logger.error(e.getMessage());
			return _messageFactory.createStatus(
					EStatus.ERROR_EVENT_PARAMETER_MISSING, e.getMessage());
		}
		// check if a container for "fileName" exists, If not, create a new
		// container : container-name= fileName
		IContainer desContainer = null;
		String newContainerName = processId + "x" + processName + "x"
				+ fileName;
		desContainer = _informationFlowModel.getContainer(new NameBasic(
				newContainerName));
		if (desContainer == null) {
			// 1. create new container
			desContainer = _messageFactory.createContainer();
			// 2. add name to the container
			_informationFlowModel.addName(new NameBasic(newContainerName),
					desContainer, true);
		}
		if (dataFilter.equals("OFF")) {
			// Bind all abUCitems to the RunningAppMemoryContainer
			Set<IData> RunningAppMemoryContainerData = _informationFlowModel
					.getData(_informationFlowModel.getContainer(new NameBasic(
							nameOfTheRunningAppMemoryContainer)));
			_informationFlowModel.addData(RunningAppMemoryContainerData,
					desContainer);
		} else {
			// Data Filter is ON --> bind only the abUCitems that have been
			// written to the RunningAppMemoryContainer
			// 3. add data to the container
			originalContainerName = processId + "x" + dataId;
			IContainer dataOrgiContainer = null;
			dataOrgiContainer = _informationFlowModel
					.getContainer(new NameBasic(originalContainerName));
			Set<IData> ucData = _informationFlowModel
					.getData(dataOrgiContainer);
//			System.out.println("process container is  : " + dataOrgiContainer);
//			System.out.println("data of the original container : " + ucData);
			_informationFlowModel.addData(ucData, desContainer);

		}
		return _messageFactory.createStatus(EStatus.OKAY);

	}

}
