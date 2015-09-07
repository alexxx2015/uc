package de.tum.in.i22.uc.pip.requests;

import java.util.Map;
import java.util.Set;

import de.tum.in.i22.uc.cm.datatypes.interfaces.IName;
import de.tum.in.i22.uc.cm.processing.PipProcessor;


public class FilterModelPipRequest extends PipRequest<Map<String, Set<Map<String, String>>>> {

    	private final Map<String, String> _params;

	public FilterModelPipRequest(Map<String, String> params) {
		_params = params;
	}
    
	@Override
	public Map<String, Set<Map<String, String>>> process(PipProcessor processor) {
		return processor.filterModel(_params);
	}
}
