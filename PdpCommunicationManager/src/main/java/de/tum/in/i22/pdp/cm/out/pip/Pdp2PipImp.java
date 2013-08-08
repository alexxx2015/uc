package de.tum.in.i22.pdp.cm.out.pip;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import org.apache.log4j.Logger;

import de.tum.in.i22.pip.cm.in.pdp.EPdp2PipMethod;
import de.tum.in.i22.uc.cm.basic.ContainerBasic;
import de.tum.in.i22.uc.cm.basic.DataBasic;
import de.tum.in.i22.uc.cm.basic.EventBasic;
import de.tum.in.i22.uc.cm.basic.ResponseBasic;
import de.tum.in.i22.uc.cm.datatypes.IContainer;
import de.tum.in.i22.uc.cm.datatypes.IData;
import de.tum.in.i22.uc.cm.datatypes.IEvent;
import de.tum.in.i22.uc.cm.datatypes.IResponse;
import de.tum.in.i22.uc.cm.gpb.PdpProtos.GpBoolean;
import de.tum.in.i22.uc.cm.gpb.PdpProtos.GpContainer;
import de.tum.in.i22.uc.cm.gpb.PdpProtos.GpContainerList;
import de.tum.in.i22.uc.cm.gpb.PdpProtos.GpData;
import de.tum.in.i22.uc.cm.gpb.PdpProtos.GpDataList;
import de.tum.in.i22.uc.cm.gpb.PdpProtos.GpEvent;
import de.tum.in.i22.uc.cm.gpb.PdpProtos.GpResponse;
import de.tum.in.i22.uc.cm.gpb.PdpProtos.GpString;
import de.tum.in.i22.uc.cm.out.FastConnector;
import de.tum.in.i22.uc.cm.util.GpUtil;

public class Pdp2PipImp extends FastConnector implements IPdp2PipFast {

	
	private static final Logger _logger = Logger.getLogger(Pdp2PipImp.class);
	
	public Pdp2PipImp(String address, int port) {
		super(address, port);
	}

	@Override
	public Boolean evaluatePredicate(IEvent event, String predicate) {
		_logger.debug("Evaluate predicate invoked");
		_logger.trace("Create Google Protocol Buffer Event instance");
		GpEvent gpEvent = EventBasic.createGpbEvent(event);
		_logger.trace("Create Google Protocol Buffer String instance");
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
		_logger.debug("Get container for data invoked");
		_logger.trace("Create Gpb data instance");
		GpData gpData = DataBasic.createGpbData(data);
		try {
			OutputStream out = getOutputStream();
			out.write(EPdp2PipMethod.GET_CONTAINER_FOR_DATA.getValue());
			gpData.writeDelimitedTo(out);
			out.flush();
			_logger.trace("GpData written to OutputStream");
			
			_logger.trace("Wait for containers");
			GpContainerList gpContainerList = GpContainerList.parseDelimitedFrom(getInputStream());
			return GpUtil.convertToList(gpContainerList);
		} catch (IOException e) {
			_logger.error("Get container for data failed.", e);
			return null;
		}
	}

	@Override
	public List<IData> getDataInContainer(IContainer container) {
		_logger.debug("Get data in container invoked");
		_logger.trace("Create Gpb container instance");
		GpContainer gpContainer = ContainerBasic.createGpbContainer(container);
		try {
			OutputStream out = getOutputStream();
			out.write(EPdp2PipMethod.GET_DATA_IN_CONTAINER.getValue());
			gpContainer.writeDelimitedTo(out);
			out.flush();
			_logger.trace("GpContainer written to OutputStream");
			
			_logger.trace("Wait for data list");
			GpDataList gpDataList = GpDataList.parseDelimitedFrom(getInputStream());
			return GpUtil.convertToList(gpDataList);
		} catch (IOException e) {
			_logger.error("Get data in container failed.", e);
			return null;
		}
	}
	
	@Override
	public IResponse notifyActualEvent(IEvent event) {
		_logger.debug("Notify actual event invoked");
		_logger.trace("Create Gpb event instance");
		GpEvent gpEvent = EventBasic.createGpbEvent(event);
		try {
			OutputStream out = getOutputStream();
			out.write(EPdp2PipMethod.NOTIFY_ACTUAL_EVENT.getValue());
			gpEvent.writeDelimitedTo(out);
			out.flush();
			_logger.trace("GpEvent written to OutputStream");
			
			_logger.trace("Wait for response");
			GpResponse gpResponse = GpResponse.parseDelimitedFrom(getInputStream());
			return new ResponseBasic(gpResponse);
		} catch (IOException e) {
			_logger.error("Notify actual event failed.", e);
			return null;
		}
	}

}
