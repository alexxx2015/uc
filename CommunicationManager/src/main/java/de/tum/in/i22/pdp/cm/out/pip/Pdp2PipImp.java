package de.tum.in.i22.pdp.cm.out.pip;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import de.tum.in.i22.pdp.cm.out.EPdp2PipMethod;
import de.tum.in.i22.pdp.cm.out.FastConnector;
import de.tum.in.i22.pdp.cm.out.IPdp2Pip;
import de.tum.in.i22.pdp.datatypes.IContainer;
import de.tum.in.i22.pdp.datatypes.IData;
import de.tum.in.i22.pdp.datatypes.IEvent;
import de.tum.in.i22.pdp.datatypes.basic.EventBasic;
import de.tum.in.i22.pdp.gpb.PdpProtos.GpBoolean;
import de.tum.in.i22.pdp.gpb.PdpProtos.GpEvent;
import de.tum.in.i22.pdp.gpb.PdpProtos.GpString;
import de.tum.in.i22.pdp.util.GpUtil;

public class Pdp2PipImp extends FastConnector implements IPdp2Pip {

	public Pdp2PipImp(String address, int port) {
		super(address, port);
	}

	@Override
	public Boolean evaluatePredicate(IEvent event, String predicate) {
		_logger.debug("Evaluate predicate invoked");
		_logger.trace("Create Google Protocol Buffer Event instance");
		GpEvent gpEvent = EventBasic.createGpbEvent(event);
		GpString gpPredicate = GpUtil.createGpString(predicate);
		try {
			OutputStream out = getOutputStream();
			out.write(EPdp2PipMethod.EVALUATE_PREDICATE.getValue());
			gpEvent.writeDelimitedTo(out);
			gpPredicate.writeDelimitedTo(out);
			out.flush();
			_logger.trace("GpEvent and GpPredicate written to OutputStream");
			
			_logger.trace("Wait for boolean result");
			GpBoolean gpBoolean = GpBoolean.parseDelimitedFrom(getInputStream());
			return gpBoolean.getValue();
		} catch (IOException e) {
			_logger.error("Evaluate predicate failed.", e);
			return null;
		}
	}

	@Override
	public List<IContainer> getContainerForData(IData data) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<IData> getDataInContainer(IContainer container) {
		// TODO Auto-generated method stub
		return null;
	}

}
