package de.tum.in.i22.uc.pip.eventdef.java;

/**
 * This class initializes all sinks and sources according to the joana output
 */

import de.tum.in.i22.uc.cm.datatypes.basic.NameBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic.EStatus;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IContainer;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IData;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.pip.eventdef.BaseEventHandler;
import de.tum.in.i22.uc.pip.eventdef.ParameterNotFoundException;

public class JoanaInitInfoFlowEventHandler extends JavaEventHandler {

	private final String _paramId = "id";
	private final String _paramSignature = "signature";
	private final String _paramLocation = "location";
	private final String _paramParamPos = "parampos";
	private final String _paramType = "type";
	private final String _paramOffset = "offset";

	private final String _javaIFDelim = ":";
	private final String _srcPrefix = "src_";
	private final String _snkPrefix = "snk_";

	public JoanaInitInfoFlowEventHandler() {
		super();
	}

	static IData data = null;

	@Override
	protected IStatus update() {

		// This event is used only during tests to initialize the information
		// flow schema to a specific state

		String id;
		String signature;
		String location;
		String parampos;
		String type;
		String offset;

		try {
			type = getParameterValue(_paramType);
		} catch (ParameterNotFoundException e) {
			_logger.error(e.getMessage());
			return _messageFactory.createStatus(
					EStatus.ERROR_EVENT_PARAMETER_MISSING, e.getMessage());
		}

		// If this is an information flow mapping event then fill internal
		// static mapping
		if (type.equals("iflow")) {
			try {
				String sink = getParameterValue("sink");
				String source = getParameterValue("source");
				iFlow.put(sink, source);
			} catch (ParameterNotFoundException e) {
				_logger.error(e.getMessage());
				return _messageFactory.createStatus(
						EStatus.ERROR_EVENT_PARAMETER_MISSING, e.getMessage());
			}
		} else {

			try {
				id = getParameterValue(_paramId);
				signature = getParameterValue(_paramSignature);
				location = getParameterValue(_paramLocation);
				parampos = getParameterValue(_paramParamPos);
				offset = getParameterValue(_paramOffset);
			} catch (ParameterNotFoundException e) {
				_logger.error(e.getMessage());
				return _messageFactory.createStatus(
						EStatus.ERROR_EVENT_PARAMETER_MISSING, e.getMessage());
			}

			String prefix = "";
			if (type.toLowerCase().equals("source")) {
				prefix = _srcPrefix;
			} else if (type.toLowerCase().equals("sink")) {
				prefix = _snkPrefix;
			}

			// Version 1: signature is used as naming identifier for a container
			// Version 2: signature+location is used as naming identifier for a
			// container
			// Version 3: signature+location+parampos is used as naming
			// identifier for a container
			// Version 4: signature+parampos is used as naming identifier for a
			// container
			String[] infoConts = new String[] {
					signature,
					location + _javaIFDelim + offset + _javaIFDelim + signature,
					location + _javaIFDelim + offset + _javaIFDelim + signature
							+ _javaIFDelim + parampos,
					signature + _javaIFDelim + parampos };

			for (String infoCont : infoConts) {
				infoCont = prefix + infoCont;
				IContainer infoContId = _informationFlowModel
						.getContainer(new NameBasic(infoCont));

				_logger.debug("contID = " + infoContId);

				if (infoContId == null) {
					IContainer signatureCont = _messageFactory
							.createContainer();

					_informationFlowModel.addName(new NameBasic(infoCont),
							signatureCont);
				}
				_logger.debug(_informationFlowModel.toString());
			}

			// Process alias relationship
			IContainer sig = _informationFlowModel.getContainer(new NameBasic(
					prefix + infoConts[0]));
			IContainer locSig = _informationFlowModel
					.getContainer(new NameBasic(prefix + infoConts[1]));
			IContainer locSigPar = _informationFlowModel
					.getContainer(new NameBasic(prefix + infoConts[2]));
			IContainer sigPar = _informationFlowModel
					.getContainer(new NameBasic(prefix + infoConts[3]));

			if (type.toLowerCase().equals("source")) {
				_informationFlowModel.addAlias(sig, locSig);
				_informationFlowModel.addAlias(locSig, locSigPar);
				_informationFlowModel.addAlias(sig, sigPar);
				_informationFlowModel.addAlias(sigPar, locSigPar);
			} else if (type.toLowerCase().equals("sink")) {
				_informationFlowModel.addAlias(locSigPar, locSig);
				_informationFlowModel.addAlias(locSigPar, sigPar);
				_informationFlowModel.addAlias(locSig, sig);
				_informationFlowModel.addAlias(sigPar, sig);
			}
		}

		return _messageFactory.createStatus(EStatus.OKAY);
	}

}
