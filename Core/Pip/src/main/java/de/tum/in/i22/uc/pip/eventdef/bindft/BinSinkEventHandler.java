package de.tum.in.i22.uc.pip.eventdef.bindft;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;

import de.tum.in.i22.uc.cm.datatypes.basic.DataBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.NameBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic.EStatus;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IData;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IEvent;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IScope;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.cm.pip.interfaces.EBehavior;
import de.tum.in.i22.uc.cm.pip.interfaces.EScopeType;
import de.tum.in.i22.uc.pip.eventdef.ParameterNotFoundException;

public class BinSinkEventHandler extends BinDftEventHandler {


	@Override
	protected IScope buildScope(String delimiter) {
		return buildScope(EScopeType.BIN_GENERIC_SAVE);
	}


	@Override
	protected IStatus update() {
		return update(EBehavior.INTRA, null);
	}

	@Override
	protected IStatus update(EBehavior direction, IScope scope) {
		try {
			String delimiter = getParameterValue("delimiter");
			String fileDescriptor = getParameterValue("fileDescriptor");
			int pid = Integer.valueOf(getParameterValue("PID"));
			String nonce = getParameterValue("nonce");
			String listOfData = getParameterValue("listOfData");

			String[] data = listOfData.split(":");

			Set<IData> setOfData = new HashSet<IData>();

			for (String sd: data){
				IData d=_informationFlowModel.getDataFromId(sd);
				if ((d!=null) && (d.equals(new DataBasic("null")))) setOfData.add(d);
			}


			if ((direction.equals(EBehavior.IN))
						|| (direction.equals(EBehavior.INTRAIN))) {
					// ERROR: a sink event is never IN
					return new StatusBasic(EStatus.ERROR,
							"Error: A sink event is never IN");
				}

			if ((direction.equals(EBehavior.OUT))
						|| (direction.equals(EBehavior.INTRAOUT))) {
					IScope os= _informationFlowModel.getOpenedScope(scope);
					if (os!=null) scope=os;
					_informationFlowModel.addDataTransitively(setOfData,
							new NameBasic(scope.getId()));
				}


		} catch (ParameterNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return _messageFactory.createStatus(EStatus.OKAY);
	}




	@Override
	protected Pair<EBehavior, IScope> XBehav() {
		String delimiter = null;
		try {
			delimiter = getParameterValue(_delimiterName);
		} catch (ParameterNotFoundException e) {
			_logger.error(e.getMessage());
			return null;
		}

		delimiter=delimiter.toLowerCase();
		IScope scope = buildScope(delimiter);
		if (scope==null)return Pair.of(EBehavior.UNKNOWN, null);
		if (delimiter.equals(_openDelimiter)) return Pair.of(EBehavior.OUT, scope);
		if (delimiter.equals(_closeDelimiter)) return Pair.of(EBehavior.INTRA, scope);
		//this line should never be reached
		return Pair.of(EBehavior.UNKNOWN, null);
	}

}
