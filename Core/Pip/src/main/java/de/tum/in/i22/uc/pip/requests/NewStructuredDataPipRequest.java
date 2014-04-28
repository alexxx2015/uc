package de.tum.in.i22.uc.pip.requests;

import java.util.Map;
import java.util.Set;

import de.tum.in.i22.uc.cm.datatypes.interfaces.IData;
import de.tum.in.i22.uc.cm.processing.PipProcessor;

public class NewStructuredDataPipRequest extends PipRequest <IData> {
	private final Map<String, Set<IData>> _structure;
	
	public NewStructuredDataPipRequest(Map<String, Set<IData>> structure) {
		_structure=structure;
	}

	@Override
	public IData process(PipProcessor processor) {
		return processor.newStructuredData(_structure);
	}
}
