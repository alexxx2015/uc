package de.tum.in.i22.uc.pip.eventdef.excel;

import de.tum.in.i22.uc.cm.datatypes.basic.ContainerBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.NameBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic.EStatus;
import de.tum.in.i22.uc.cm.datatypes.excel.CellName;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IContainer;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.pip.eventdef.ParameterNotFoundException;

public class PrintEventHandler extends ExcelEvents {

	public PrintEventHandler() {
		super();
	}

	@Override
	protected IStatus update() {
		String target = "";
		try {
			target = getParameterValue("target");
		} catch (ParameterNotFoundException e) {
			_logger.error(e.getMessage());
			return _messageFactory.createStatus(
					EStatus.ERROR_EVENT_PARAMETER_MISSING, e.getMessage());
		}
		if ((target == null) || (target.equals("")))
			throw new RuntimeException("impossible to print empty target");

		IContainer src = _informationFlowModel
				.getContainer(CellName.create(target));
		IContainer dst = _informationFlowModel.getContainer(new NameBasic(
				"printer"));

		if (dst == null) {
			dst = new ContainerBasic();
			_informationFlowModel.addName(new NameBasic("printer"), dst);
		}
		_informationFlowModel.copyData(src, dst);

		return _messageFactory.createStatus(EStatus.OKAY);
	}

}
