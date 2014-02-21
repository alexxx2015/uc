package de.tum.in.i22.pip.core.eventdef;


import org.apache.log4j.Logger;

import de.tum.in.i22.pip.core.InformationFlowModel;
import de.tum.in.i22.pip.core.Name;
import de.tum.in.i22.uc.cm.datatypes.EStatus;
import de.tum.in.i22.uc.cm.datatypes.IContainer;
import de.tum.in.i22.uc.cm.datatypes.IData;
import de.tum.in.i22.uc.cm.datatypes.IEvent;
import de.tum.in.i22.uc.cm.datatypes.IStatus;

public class SchemaInitializerEventHandler extends BaseEventHandler {

	private static final Logger _logger = Logger
			.getLogger(SchemaInitializerEventHandler.class);

	public SchemaInitializerEventHandler() {
		super();
	}

	@Override
	public IStatus execute() {

		//This event is used only during tests to initialize the information flow schema to a specific state

		InformationFlowModel ifModel = getInformationFlowModel();

		String contName="TEST_C";
		String dataName="TEST_D";
		

		IEvent e = getEvent();


		String contId = ifModel.getContainerIdByName(new Name(
				contName));

		_logger.debug("contID = " + contId);

		if (contId == null) {
			IContainer container = _messageFactory.createContainer("TestContainer",contName);

			contId = ifModel.addContainer(container);
			ifModel.addName(new Name(contName), contId);

			IData d= _messageFactory.createData(dataName);
			ifModel.addData(d);
			ifModel.addDataToContainerMapping(dataName, contId);
			_logger.debug(ifModel.toString());
		} else {
			_logger.error("contID = " + contId+" Already exists!!!! IMPOSSIBRU!!!");
		_logger.debug(ifModel.toString());
		}

		return _messageFactory.createStatus(EStatus.OKAY);
	}

}
