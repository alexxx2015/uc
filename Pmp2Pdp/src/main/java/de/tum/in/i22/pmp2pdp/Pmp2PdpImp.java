package de.tum.in.i22.pmp2pdp;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;

import de.tum.in.i22.cm.pdp.internal.Mechanism;
import de.tum.in.i22.pdp.core.IPmp2Pdp;
import de.tum.in.i22.uc.cm.basic.MechanismBasic;
import de.tum.in.i22.uc.cm.basic.StatusBasic;
import de.tum.in.i22.uc.cm.datatypes.IMechanism;
import de.tum.in.i22.uc.cm.datatypes.IStatus;
import de.tum.in.i22.uc.cm.gpb.PdpProtos.GpMechanism;
import de.tum.in.i22.uc.cm.gpb.PdpProtos.GpStatus;
import de.tum.in.i22.uc.cm.gpb.PdpProtos.GpString;
import de.tum.in.i22.uc.cm.methods.EPmp2PdpMethod;
import de.tum.in.i22.uc.cm.out.Connection;
import de.tum.in.i22.uc.cm.out.Connector;
import de.tum.in.i22.uc.cm.util.GpUtil;

/**
 * Each method writes a message to the output stream.
 * First an {@link EPmp2PdpMethod} is written that identifies the method.
 * Then the message size is written as int.
 * Finally, the message is written.
 * @author Stoimenov
 *
 */
public abstract class Pmp2PdpImp extends Connection implements IPmp2Pdp {

 	public Pmp2PdpImp(Connector connector) {
 		super(connector);
	}

	@Override
	public IStatus deployMechanism(IMechanism mechanism) {
		_logger.debug("Deploy mechanism");
		_logger.trace("Create Google Protocol Buffer Mechanism instance");
		GpMechanism gpMechanism = MechanismBasic.createGpbMechanism(mechanism);

		try {
			OutputStream out = getOutputStream();
			out.write(EPmp2PdpMethod.DEPLOY_MECHANISM.getValue());
			gpMechanism.writeDelimitedTo(out);
			out.flush();
			_logger.trace("GpMechanism written to OutputStream");

			_logger.trace("Wait for GpStatus message");
			GpStatus gpStatus = GpStatus.parseDelimitedFrom(getInputStream());
			return new StatusBasic(gpStatus);
		} catch (IOException ex) {
			_logger.error("Deploy mechanism failed.", ex);
			return null;
		}
	}

	@Override
	public IMechanism exportMechanism(String par) {
		_logger.debug("Export mechanism");
		_logger.trace("Create Google Protocol Buffer GpString");
		GpString gpString = GpUtil.createGpString(par);
		try {
			OutputStream out = getOutputStream();
			out.write(EPmp2PdpMethod.EXPORT_MECHANISM.getValue());
			gpString.writeDelimitedTo(out);
			out.flush();
			_logger.trace("GpString written to OutputStream");

			_logger.trace("Wait for GpMechanism message");
			GpMechanism gpMechanism = GpMechanism.parseDelimitedFrom(getInputStream());
			return new MechanismBasic(gpMechanism);
		} catch (IOException ex) {
			_logger.error("Export mechanism failed.", ex);
			return null;
		}
	}

	@Override
	public IStatus revokeMechanism(String par) {
		_logger.debug("Revoke mechanism");
		_logger.trace("Create Google Protocol Buffer GpString");
		GpString gpString = GpUtil.createGpString(par);
		try {
			OutputStream out = getOutputStream();
			out.write(EPmp2PdpMethod.REVOKE_MECHANISM.getValue());
			gpString.writeDelimitedTo(out);
			out.flush();
			_logger.trace("GpString written to OutputStream");

			_logger.trace("Wait for GpStatus message");
			GpStatus gpStatus = GpStatus.parseDelimitedFrom(getInputStream());
			return new StatusBasic(gpStatus);
		} catch (IOException ex) {
			_logger.error("Revoke mechanism failed.", ex);
			return null;
		}
	}

	@Override
	public IStatus deployPolicy(String policyFilePath) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HashMap<String, ArrayList<Mechanism>> listMechanisms() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IStatus revokeMechanism(String policyName, String mechName) {
		// TODO Auto-generated method stub
		return null;
	}
}
