package de.tum.in.i22.uc.pmp.requests;

import java.util.List;
import java.util.Map;

import de.tum.in.i22.uc.cm.processing.PmpProcessor;

public class ListMechanismsPmpPmpRequest extends PmpRequest<Map<String,List<String>>> {

	@Override
	public Map<String,List<String>> process(PmpProcessor processor) {
		return processor.listMechanismsPmp();
	}

}
