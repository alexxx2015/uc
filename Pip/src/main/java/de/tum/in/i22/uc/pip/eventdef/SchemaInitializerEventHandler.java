package de.tum.in.i22.uc.pip.eventdef;

import de.tum.in.i22.uc.cm.basic.NameBasic;
import de.tum.in.i22.uc.cm.datatypes.EStatus;
import de.tum.in.i22.uc.cm.datatypes.IContainer;
import de.tum.in.i22.uc.cm.datatypes.IData;
import de.tum.in.i22.uc.cm.datatypes.IStatus;
import de.tum.in.i22.uc.cm.datatypes.linux.FileContainer;
import de.tum.in.i22.uc.cm.datatypes.linux.FilenameName;

public class SchemaInitializerEventHandler extends BaseEventHandler {

	public SchemaInitializerEventHandler() {
		super();
	}

	@Override
	public IStatus execute() {

		//This event is used only during tests to initialize the information flow schema to a specific state

		String contName="TEST_C";
		String dataName="TEST_D";


		IContainer cont = basicIfModel.getContainer(new NameBasic(
				contName));

		_logger.debug("cont = " + cont);

		if (cont == null) {
			cont = _messageFactory.createContainer("TestContainer",contName);
			basicIfModel.addName(new NameBasic(contName), cont);

			IData d= _messageFactory.createData(dataName);
			basicIfModel.addDataToContainer(d, cont);
			_logger.debug(basicIfModel.toString());
		} else {
			_logger.error("cont = " + cont+" Already exists!!!! IMPOSSIBRU!!!");
		_logger.debug(basicIfModel.toString());
		}


		/*
		 * for FK.
		 */
		IContainer file1 = new FileContainer();
		IContainer file2 = new FileContainer();
		IData data = _messageFactory.createData("MY_DATA");
		basicIfModel.addName(FilenameName.create("machineA", "/tmp/datasrc"), file1);
		basicIfModel.addName(FilenameName.create("machineA", "/home/anonymous/tainted"), file2);
		basicIfModel.addDataToContainer(data, file1);
		basicIfModel.addDataToContainer(data, file2);

		return _messageFactory.createStatus(EStatus.OKAY);
	}

}
