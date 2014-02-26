package de.tum.in.i22.pip.core.eventdef;

import de.tum.in.i22.pip.core.InformationFlowModel;
import de.tum.in.i22.uc.cm.basic.NameBasic;
import de.tum.in.i22.uc.cm.datatypes.EStatus;
import de.tum.in.i22.uc.cm.datatypes.IContainer;
import de.tum.in.i22.uc.cm.datatypes.IData;
import de.tum.in.i22.uc.cm.datatypes.IEvent;
import de.tum.in.i22.uc.cm.datatypes.IStatus;

public class SchemaInitializerEventHandler extends BaseEventHandler {

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


		IContainer cont = ifModel.getContainer(new NameBasic(
				contName));

		_logger.debug("cont = " + cont);

		if (cont == null) {
			cont = _messageFactory.createContainer("TestContainer",contName);
			ifModel.add(cont);
			ifModel.addName(new NameBasic(contName), cont);

			IData d= _messageFactory.createData(dataName);
			ifModel.add(d);
			ifModel.addDataToContainerMapping(d, cont);
			_logger.debug(ifModel.toString());
		} else {
			_logger.error("cont = " + cont+" Already exists!!!! IMPOSSIBRU!!!");
		_logger.debug(ifModel.toString());
		}

		return _messageFactory.createStatus(EStatus.OKAY);
	}

}
