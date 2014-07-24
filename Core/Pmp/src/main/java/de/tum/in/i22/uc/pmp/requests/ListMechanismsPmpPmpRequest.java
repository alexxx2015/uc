package de.tum.in.i22.uc.pmp.requests;

import java.util.Map;
import java.util.Set;

import de.tum.in.i22.uc.cm.processing.PmpProcessor;

public class ListMechanismsPmpPmpRequest extends PmpRequest<Map<String,Set<String>>> {

	@Override
	public Map<String,Set<String>> process(PmpProcessor processor) {
		return processor.listMechanismsPmp();
	}

}
