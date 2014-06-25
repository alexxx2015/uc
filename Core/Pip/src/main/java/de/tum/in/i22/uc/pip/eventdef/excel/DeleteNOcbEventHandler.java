package de.tum.in.i22.uc.pip.eventdef.excel;

import java.util.Set;

import org.apache.derby.diag.ContainedRoles;

import de.tum.in.i22.uc.cm.datatypes.basic.ContainerBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.NameBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic.EStatus;
import de.tum.in.i22.uc.cm.datatypes.excel.CellName;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IContainer;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.pip.eventdef.ParameterNotFoundException;

public class DeleteNOcbEventHandler extends ExcelEvents {

	public DeleteNOcbEventHandler() {
		super();
	}

	@Override
	protected IStatus update() {
		int pos = -1;
		try {
			pos = Integer.valueOf(getParameterValue("position"));
		} catch (ParameterNotFoundException e) {
			_logger.error(e.getMessage());
			return _messageFactory.createStatus(
					EStatus.ERROR_EVENT_PARAMETER_MISSING, e.getMessage());
		}

		IContainer posNOfOcb = getOCB(pos);

		if ((pos < 0) || (posNOfOcb == null)) {
			_logger.debug("impossible to delete position " +pos+ " from office clipboard");
			return _messageFactory
					.createStatus(EStatus.ERROR_EVENT_PARAMETER_MISSING);
		}

		deleteOCB(pos);
		return _messageFactory.createStatus(EStatus.OKAY);
	}

}
