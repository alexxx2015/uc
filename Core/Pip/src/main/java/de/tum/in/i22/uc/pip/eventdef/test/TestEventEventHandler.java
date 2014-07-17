package de.tum.in.i22.uc.pip.eventdef.test;

/**
 * This class initializes all sinks and sources according to the joana output
 */

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.datatypes.basic.NameBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic.EStatus;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IData;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.pip.eventdef.BaseEventHandler;

public class TestEventEventHandler extends BaseEventHandler {
	private static final Logger _logger = LoggerFactory.getLogger(TestEventEventHandler.class);
	static IData data = null;

	public TestEventEventHandler() {
		super();
	}


	@Override
	public void reset(){
		super.reset();
		data=null;
	}
	
	@Override
	protected IStatus update() {

		// The uncommented version of this code is stored in the file
		// updateIF.jar in the test folder.
		// This event is used to delete an initial representation in the pdp
		// test.s

//		_logger.debug("TestEventEventHandler. Trying to empty initial container");
//		_logger.debug("As of now, initial container contains the following ["+basicIfModel.getData(new NameBasic("initialContainer")));
//
//		_informationFlowModel.emptyContainer(new NameBasic("initialContainer"));
//
//		_logger.debug("After emptying it, initial container contains the following ["+basicIfModel.getData(new NameBasic("initialContainer")));

		//
		return _messageFactory.createStatus(EStatus.OKAY);
	}

}
