package de.tum.in.i22.uc.pip.eventdef;


import java.util.Set;

import de.tum.in.i22.uc.cm.datatypes.basic.NameBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic.EStatus;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IContainer;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IData;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;

public class SchemaUpdaterEventHandler extends BaseEventHandler {

	public SchemaUpdaterEventHandler() {
		super();
	}

	@Override
	public IStatus update() {

		//This event is used only during tests to update the information flow schema to a specific state
		// It assumes the state contains a container TEST_C containing TEST_D (see SchemaInitializer) and removes it


		String contName="TEST_C";

		IContainer cont = _informationFlowModel.getContainer(new NameBasic(
				contName));
		//this should contain TEST_D

		// check if container for clipboard exists and create new container if not
		if (cont == null) {
			_logger.error("No container named "+ contName);
			return _messageFactory.createStatus(EStatus.ERROR);
		}

		Set<IData> dataIds=_informationFlowModel.getData(cont);

		if ((dataIds == null)||(dataIds.size()!=1)){
			_logger.error("content of "+ contName+" different from expected. is dataIds empty? "+(dataIds == null));
			return _messageFactory.createStatus(EStatus.ERROR);
		}

		_logger.debug("emptying container "+ contName);
		_informationFlowModel.emptyContainer(cont);

		_logger.debug("deleting data d from ifModel... ");
		_informationFlowModel.remove((IData)dataIds.toArray()[0]);


		_logger.debug("number of data elements in container TEST_C = "+ _informationFlowModel.getData(cont).size());


		_logger.debug(_informationFlowModel.toString());

		return _messageFactory.createStatus(EStatus.OKAY);
	}

}
