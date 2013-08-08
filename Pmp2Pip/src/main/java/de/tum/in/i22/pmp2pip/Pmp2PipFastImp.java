package de.tum.in.i22.pmp2pip;

import java.io.IOException;
import java.io.OutputStream;

import org.apache.log4j.Logger;

import de.tum.in.i22.uc.cm.basic.ContainerBasic;
import de.tum.in.i22.uc.cm.basic.DataBasic;
import de.tum.in.i22.uc.cm.datatypes.IContainer;
import de.tum.in.i22.uc.cm.datatypes.IData;
import de.tum.in.i22.uc.cm.gpb.PdpProtos.GpContainer;
import de.tum.in.i22.uc.cm.gpb.PdpProtos.GpData;
import de.tum.in.i22.uc.cm.gpb.PdpProtos.GpStatus;
import de.tum.in.i22.uc.cm.gpb.PdpProtos.GpStatus.EStatus;
import de.tum.in.i22.uc.cm.out.FastConnector;

public class Pmp2PipFastImp extends FastConnector implements IPmp2PipFast {

	
	private static final Logger _logger = Logger
			.getLogger(Pmp2PipFastImp.class);
	
	public Pmp2PipFastImp(String address, int port) {
		super(address, port);
	}

	@Override
	public EStatus initialRepresentation(IContainer container, IData data) {
		_logger.debug("Method Initial representation");
		_logger.trace("Create Google Protocol Buffer Container and Data instance");
		GpContainer gpContainer = ContainerBasic.createGpbContainer(container);
		GpData gpData = DataBasic.createGpbData(data);
		try {
			OutputStream out = getOutputStream();
			// write one dummy byte, this byte can be later used to encode the operation
			// currently we have only one operation
			out.write(0);
			gpContainer.writeDelimitedTo(out);
			gpData.writeDelimitedTo(out);
			out.flush();
			_logger.trace("GpContainer and GpData written to OutputStream");
			
			_logger.trace("Wait for GpStatus message");
			GpStatus gpStatus = GpStatus.parseDelimitedFrom(getInputStream());
			return gpStatus.getValue();
		} catch (IOException ex) {
			_logger.error("Method Initial Representation failed", ex);
			return null;
		}
	}

}
