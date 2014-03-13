package de.tum.in.i22.pip.core.eventdef.Java;
/**
 * This class initializes all sinks and sources according to the joana output
 */

import java.util.Map;

import de.tum.in.i22.pip.core.eventdef.BaseEventHandler;
import de.tum.in.i22.uc.cm.basic.DataBasic;
import de.tum.in.i22.uc.cm.basic.NameBasic;
import de.tum.in.i22.uc.cm.datatypes.EStatus;
import de.tum.in.i22.uc.cm.datatypes.IContainer;
import de.tum.in.i22.uc.cm.datatypes.IData;
import de.tum.in.i22.uc.cm.datatypes.IEvent;
import de.tum.in.i22.uc.cm.datatypes.IStatus;

public class JoanaInitInfoFlowEventHandler extends BaseEventHandler {

	public JoanaInitInfoFlowEventHandler() {
		super();
	}

	static IData data = null;

	@Override
	public IStatus execute() {

		//This event is used only during tests to initialize the information flow schema to a specific state

		IEvent e = getEvent();
		Map<String,String> param = e.getParameters();
		String id = param.get("id");
		String signature = param.get("signature");
		String location = param.get("location");
		String parampos = param.get("parampos");
		String type = param.get("type");
		String offset = param.get("offset");

		String delim = ":";

		String prefix = "";
		if(type.toLowerCase().equals("source")){
			prefix = "src_";
		} else if(type.toLowerCase().equals("sink")){
			prefix = "snk_";
		}

		//Version 1: signature is used as naming identifier for a container
		//Version 2: signature+location is used as naming identifier for a container
		//Version 3: ignature+location+parampos is used as naming identifier for a container
		String[] infoConts = new String[]{signature, location+delim+offset+delim+signature, location+delim+offset+delim+signature+delim+parampos};
		for(String infoCont : infoConts){
			infoCont = prefix + infoCont;
			if(data == null){
				data = new DataBasic();//DataBasic(infoCont);
			}
			IContainer infoContId = ifModel.getContainer(new NameBasic(infoCont));

			_logger.debug("contID = " + infoContId);

			if (infoContId == null) {
				IContainer signatureCont = _messageFactory.createContainer();

				ifModel.addName(new NameBasic(infoCont), signatureCont);

//				IData d= _messageFactory.createData();
				ifModel.addDataToContainerMapping(data, signatureCont);
				_logger.debug(ifModel.toString());
			} else {
				_logger.error("contID = " + infoContId+" Already exists!!!! IMPOSSIBRU!!!");
			_logger.debug(ifModel.toString());
			}
		}

//		Process alias relationship
		IContainer id1 = ifModel.getContainer(new NameBasic(prefix+infoConts[0]));
		IContainer id2 = ifModel.getContainer(new NameBasic(prefix+infoConts[1]));
		IContainer id3 = ifModel.getContainer(new NameBasic(prefix+infoConts[2]));

		if(type.toLowerCase().equals("source")){
			ifModel.addAlias(id1, id2);
			ifModel.addAlias(id2, id3);
		}
		else if (type.toLowerCase().equals("sink")){
			ifModel.addAlias(id3, id2);
			ifModel.addAlias(id2, id1);
		}

		return _messageFactory.createStatus(EStatus.OKAY);
	}

}
