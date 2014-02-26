package de.tum.in.i22.pip.core.eventdef;


import java.util.Set;

import de.tum.in.i22.pip.core.InformationFlowModel;
import de.tum.in.i22.uc.cm.basic.NameBasic;
import de.tum.in.i22.uc.cm.datatypes.EStatus;
import de.tum.in.i22.uc.cm.datatypes.IStatus;

public class SchemaUpdaterEventHandler extends BaseEventHandler {

	public SchemaUpdaterEventHandler() {
		super();
	}

	@Override
	public IStatus execute() {

		//This event is used only during tests to update the information flow schema to a specific state
		// It assumes the state contains a container TEST_C containing TEST_D (see SchemaInitializer) and removes it


		InformationFlowModel ifModel = getInformationFlowModel();

		String contName="TEST_C";

		String contId = ifModel.getContainerIdByName(new NameBasic(
				contName));
		//this should contain TEST_D

		// check if container for clipboard exists and create new container if not
		if (contId == null) {
			_logger.error("No container named "+ contName);
			return _messageFactory.createStatus(EStatus.ERROR);
		}

		Set<String> dataIds=ifModel.getDataInContainer(contId);

		if ((dataIds == null)||(dataIds.size()!=1)){
			_logger.error("content of "+ contName+" different from expected. is dataIds empty? "+(dataIds == null));
			return _messageFactory.createStatus(EStatus.ERROR);
		}

		_logger.debug("emptying container "+ contName);
		ifModel.emptyContainer(contId);

		_logger.debug("deleting data d from ifModel... ");
		ifModel.removeData(ifModel.getDataById((String)(dataIds.toArray()[0])));


		_logger.debug("number of data elements in container TEST_C = "+ ifModel.getDataInContainer(contId).size());


		_logger.debug(ifModel.toString());

		return _messageFactory.createStatus(EStatus.OKAY);
	}

}
