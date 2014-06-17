package de.tum.in.i22.uc.pip.eventdef.excel;

import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic.EStatus;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.pip.eventdef.ParameterNotFoundException;

public class LoadEventHandler extends ExcelEvents {

	public LoadEventHandler() {
		super();
	}

	@Override
	protected IStatus update() {
		String externalFile = "";

		try {
			externalFile = getParameterValue("externalFile");

		} catch (ParameterNotFoundException e) {
			_logger.error(e.getMessage());
			return _messageFactory.createStatus(
					EStatus.ERROR_EVENT_PARAMETER_MISSING, e.getMessage());
		}
		if ((externalFile == null) || (externalFile.equals(""))) {
			_logger.debug("nothign to Open. Close the workbook.");
			return _messageFactory.createStatus(EStatus.OKAY);
		}

		

		return _messageFactory.createStatus(EStatus.OKAY);
	}

}
