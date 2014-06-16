package de.tum.in.i22.uc.pip.eventdef.excel;

import java.util.Set;

import de.tum.in.i22.uc.cm.datatypes.basic.ContainerBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.NameBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic.EStatus;
import de.tum.in.i22.uc.cm.datatypes.excel.CellName;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IContainer;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.pip.eventdef.ParameterNotFoundException;

public class SaveEventHandler extends ExcelEvents {

	public SaveEventHandler() {
		super();
	}

	@Override
	protected IStatus update() {
		String externalFile = "";
		String structuredData = "";
		try {
			externalFile = getParameterValue("externalFile");
			structuredData = getParameterValue("structuredData");

		} catch (ParameterNotFoundException e) {
			_logger.error(e.getMessage());
			return _messageFactory.createStatus(
					EStatus.ERROR_EVENT_PARAMETER_MISSING, e.getMessage());
		}
		if ((externalFile == null) || (externalFile.equals(""))|| (structuredData.equals(""))|| (structuredData.equals(""))) {
			_logger.debug("Either externalFile path is missing OR structuredData is null");
			return _messageFactory.createStatus(EStatus.OKAY);
		}

		return _messageFactory.createStatus(EStatus.OKAY);
	}

}
