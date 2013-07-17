package de.tum.in.i22.pmp2pdp;

import java.io.IOException;

import de.tum.in.i22.pdp.cm.in.pmp.EPmp2PdpMethod;
import de.tum.in.i22.pdp.cm.out.FastConnector;
import de.tum.in.i22.pdp.datatypes.IMechanism;
import de.tum.in.i22.pdp.datatypes.basic.MechanismBasic;
import de.tum.in.i22.pdp.gpb.PdpProtos.GpMechanism;
import de.tum.in.i22.pdp.gpb.PdpProtos.GpStatus;
import de.tum.in.i22.pdp.gpb.PdpProtos.GpStatus.EStatus;
import de.tum.in.i22.pdp.gpb.PdpProtos.GpString;
import de.tum.in.i22.pdp.util.GpUtil;

/**
 * Each method writes a message to the output stream. 
 * First an {@link EPmp2PdpMethod} is written that identifies the method.
 * Then the message size is written as int.
 * Finally, the message is written.
 * @author Stoimenov
 *
 */
public class Pmp2PdpFastImp extends FastConnector
		implements IPmp2PdpFast {

 	public Pmp2PdpFastImp(String address, int port) {
 		super(address, port);
	}

	public EStatus deployMechanism(IMechanism mechanism) {
		_logger.debug("Deploy mechanism");
		_logger.trace("Create Google Protocol Buffer Mechanism instance");
		GpMechanism gpMechanism = MechanismBasic.createGpbMechanism(mechanism);
		
		try {
			sendData(EPmp2PdpMethod.DEPLOY_MECHANISM.getValue(), gpMechanism);
			_logger.trace("GpMechanism written to OutputStream");
			
			_logger.trace("Wait for GpStatus message");
			GpStatus gpStatus = GpStatus.parseDelimitedFrom(getInputStream());
			return gpStatus.getValue();
		} catch (IOException ex) {
			_logger.error("Deploy mechanism failed.", ex);
			return null;
		}
	}

	public IMechanism exportMechanism(String par) {
		_logger.debug("Export mechanism");
		_logger.trace("Create Google Protocol Buffer GpString");
		GpString gpString = GpUtil.createGpString(par);
		try {
			sendData(EPmp2PdpMethod.EXPORT_MECHANISM.getValue(), gpString);
			_logger.trace("GpString written to OutputStream");
			
			_logger.trace("Wait for GpMechanism message");
			GpMechanism gpMechanism = GpMechanism.parseDelimitedFrom(getInputStream());
			return new MechanismBasic(gpMechanism);
		} catch (IOException ex) {
			_logger.error("Export mechanism failed.", ex);
			return null;
		}
	}

	public EStatus revokeMechanism(String par) {
		_logger.debug("Revoke mechanism");
		_logger.trace("Create Google Protocol Buffer GpString");
		GpString gpString = GpUtil.createGpString(par);
		try {
			sendData(EPmp2PdpMethod.REVOKE_MECHANISM.getValue(), gpString);
			_logger.trace("GpString written to OutputStream");
			
			_logger.trace("Wait for GpStatus message");
			GpStatus gpStatus = GpStatus.parseDelimitedFrom(getInputStream());
			return gpStatus.getValue();
		} catch (IOException ex) {
			_logger.error("Revoke mechanism failed.", ex);
			return null;
		}
	}
}
