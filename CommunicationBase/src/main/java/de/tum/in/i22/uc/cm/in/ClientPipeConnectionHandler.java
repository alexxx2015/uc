package de.tum.in.i22.uc.cm.in;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import com.google.common.base.Objects;


/**
 * Template class
 * @author Florian Kelbert
 *
 */
public abstract class ClientPipeConnectionHandler extends ClientConnectionHandler  {

	private final File _inPipe;
	private final File _outPipe;

	protected ClientPipeConnectionHandler(File inPipe, File outPipe) throws FileNotFoundException {
		super(new DataInputStream(new BufferedInputStream(new FileInputStream(inPipe))),
				new BufferedOutputStream(new FileOutputStream(outPipe)));
		_inPipe = inPipe;
		_outPipe = outPipe;
	}


	@Override
	protected void disconnect() {
	}


	@Override
	public String toString() {
		return Objects.toStringHelper(this)
			.add("_inPipe", _inPipe)
			.add("_outPipe", _outPipe)
			.add("_shouldContinue", _shouldContinue)
			.add("_response", _response)
			.toString();
	}

}