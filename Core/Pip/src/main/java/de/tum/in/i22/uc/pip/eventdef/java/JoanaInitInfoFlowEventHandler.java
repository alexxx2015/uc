package de.tum.in.i22.uc.pip.eventdef.java;
/**
 * This class initializes all sinks and sources according to the joana output
 */

import java.util.Set;

import de.tum.in.i22.uc.cm.datatypes.basic.DataBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.NameBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic.EStatus;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IContainer;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IData;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.pip.eventdef.BaseEventHandler;
import de.tum.in.i22.uc.pip.eventdef.ParameterNotFoundException;

public class JoanaInitInfoFlowEventHandler extends BaseEventHandler {

	public JoanaInitInfoFlowEventHandler() {
		super();
	}

	static IData data = null;

	@Override
	protected IStatus update() {

		//This event is used only during tests to initialize the information flow schema to a specific state

		String id;
		String signature;
		String location;
		String parampos;
		String type;
		String offset;

		try {
			id = getParameterValue("id");
			signature = getParameterValue("signature");
			location = getParameterValue("location");
			parampos = getParameterValue("parampos");
			type = getParameterValue("type");
			offset = getParameterValue("offset");
		} catch (ParameterNotFoundException e) {
			_logger.error(e.getMessage());
			return _messageFactory.createStatus(EStatus.ERROR_EVENT_PARAMETER_MISSING, e.getMessage());
		}

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
		String[] infoConts = new String[]{signature
										, location+delim+offset+delim+signature
										, location+delim+offset+delim+signature+delim+parampos
										, location+delim+signature+parampos
										};
		for(String infoCont : infoConts){
			infoCont = prefix + infoCont;
			if(data == null){
				data = new DataBasic();//DataBasic(infoCont);
			}
			IContainer infoContId = basicIfModel.getContainer(new NameBasic(infoCont));

			_logger.debug("contID = " + infoContId);

			if (infoContId == null) {
				IContainer signatureCont = _messageFactory.createContainer();

				basicIfModel.addName(new NameBasic(infoCont), signatureCont);

//				IData d= _messageFactory.createData();
//				basicIfModel.addData(data, signatureCont);
				_logger.debug(basicIfModel.toString());
			} else {
				Set<IData> dataIds = basicIfModel.getData(infoContId);
				if((dataIds != null) && (dataIds.size() > 0)){
					data = (IData) dataIds.toArray()[0];
				}
				_logger.error("contID = " + infoContId+" Already exists!!!! IMPOSSIBRU!!!");
			_logger.debug(basicIfModel.toString());
			}
		}

//		Process alias relationship
		IContainer id1 = basicIfModel.getContainer(new NameBasic(prefix+infoConts[0]));
		IContainer id2 = basicIfModel.getContainer(new NameBasic(prefix+infoConts[1]));
		IContainer id3 = basicIfModel.getContainer(new NameBasic(prefix+infoConts[2]));
		IContainer id4 = basicIfModel.getContainer(new NameBasic(prefix+infoConts[3]));

		if(type.toLowerCase().equals("source")){
			basicIfModel.addAlias(id1, id2);
			basicIfModel.addAlias(id2, id3);
			basicIfModel.addAlias(id1, id4);
			basicIfModel.addAlias(id4, id3);
		}
		else if (type.toLowerCase().equals("sink")){
			basicIfModel.addAlias(id3, id2);
			basicIfModel.addAlias(id2, id1);
			basicIfModel.addAlias(id4, id1);			
		}

		return _messageFactory.createStatus(EStatus.OKAY);
	}

}
