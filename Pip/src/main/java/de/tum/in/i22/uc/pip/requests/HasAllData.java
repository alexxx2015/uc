package de.tum.in.i22.uc.pip.requests;

import java.util.Set;

import de.tum.in.i22.uc.cm.datatypes.IData;
import de.tum.in.i22.uc.cm.processing.PipProcessor;

public class HasAllData extends PipRequest<Boolean> {
	private final Set<IData> _data;

	public HasAllData(Set<IData> data) {
		_data = data;
	}

	@Override
	public Boolean process(PipProcessor processor) {
		return processor.hasAllData(_data);
	}
}
