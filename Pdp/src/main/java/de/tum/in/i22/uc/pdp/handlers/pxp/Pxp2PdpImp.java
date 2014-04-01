//package de.tum.in.i22.uc.pdp.handlers.pxp;
//
//import de.tum.in.i22.uc.cm.client.Connection;
//import de.tum.in.i22.uc.cm.client.Connector;
//import de.tum.in.i22.uc.cm.datatypes.IPxpSpec;
//import de.tum.in.i22.uc.cm.interfaces.IAny2Pdp;
//
//public class Pxp2PdpImp extends Connection implements IAny2Pdp {
//
//	public Pxp2PdpImp(Connector connector) {
//		super(connector);
//	}
//
//	@Override
//	public boolean registerPxp(IPxpSpec pxp) {
////		// TODO Auto-generated method stub
////		_logger.debug("register PXP {}" + pxp.getId());
////
////		PdpProtos.GpRegPxp.Builder gpEventBuilder = PdpProtos.GpRegPxp.newBuilder();
////		gpEventBuilder.setDescription(pxp.getDescription());
////		gpEventBuilder.setId(pxp.getId());
////		gpEventBuilder.setIp(pxp.getIp());
////		gpEventBuilder.setPort(pxp.getPort());
////		GpRegPxp gpRegPxpEvent = gpEventBuilder.build();
////
////		try {
////			OutputStream out = getOutputStream();
////			out.write(EPep2PdpMethod.REGISTER_PXP.getValue());
////			gpRegPxpEvent.writeDelimitedTo(out);
////			out.flush();
////			_logger.trace("Event written to OutputStream");
////
////			_logger.trace("Wait for GpResponse");
////			GpBoolean response = GpBoolean.parseDelimitedFrom(getInputStream());
////			return response.getValue();
////		} catch (IOException ex) {
////			_logger.error("Failed to notify event.", ex);
////		}
////
//		return false;
//	}
//
//}
