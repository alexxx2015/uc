package de.tum.in.i22.uc.prp;

import de.tum.in.i22.uc.cm.interfaces.IAny2Pdp;
import de.tum.in.i22.uc.cm.processing.PrpProcessor;

public class PrpHandler extends PrpProcessor {
	public static PrpHandler getInstance() {
		return new PrpHandler();
	}
	
	private IAny2Pdp _pdp;

	@Override
	public void init(IAny2Pdp pdp) {
		// TODO Auto-generated method stub
		this._pdp = pdp;
	}

}
