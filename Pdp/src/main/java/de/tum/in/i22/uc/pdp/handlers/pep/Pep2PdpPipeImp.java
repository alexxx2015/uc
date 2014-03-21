package de.tum.in.i22.uc.pdp.handlers.pep;

import java.io.File;

import de.tum.in.i22.uc.cm.out.PipeConnector;

public class Pep2PdpPipeImp extends Pep2PdpImp {
	public Pep2PdpPipeImp(File inPipe, File outPipe) {
		super(new PipeConnector(inPipe, outPipe));
	}
}
