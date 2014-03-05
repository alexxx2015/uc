package de.tum.in.i22.pep2pdp;

import java.io.File;
import de.tum.in.i22.uc.cm.out.PipeConnector;

public class Pep2PdpPipeImp extends Pep2PdpImp implements IPep2PdpPipe {
	public Pep2PdpPipeImp(File inPipe, File outPipe) {
		super(new PipeConnector(inPipe, outPipe));
	}
}
