package de.tum.in.i22.uc.thrift.server;

import com.linecorp.armeria.common.SerializationFormat;
import com.linecorp.armeria.common.SessionProtocol;
import com.linecorp.armeria.server.Server;
import com.linecorp.armeria.server.ServerBuilder;
import com.linecorp.armeria.server.http.cors.CorsServiceBuilder;
import com.linecorp.armeria.server.thrift.THttpService;

public class ThriftWebServer implements IThriftServer {
	private Server server = null;
	private boolean isStarted = false;

	public ThriftWebServer(int port, ThriftServerHandler handler) {
		String url = "/pmp";// Settings.getInstance().get

		ServerBuilder sb = new ServerBuilder();
		sb.port(port, SessionProtocol.HTTP);

		CorsServiceBuilder csb = CorsServiceBuilder.forOrigin("*");
		csb.allowRequestMethods(com.linecorp.armeria.common.http.HttpMethod.POST)
//				.allowRequestHeaders("Content-Type, Access-Control-Allow-Headers, Authorization, X-Requested-With")
		;

		THttpService service = THttpService.of(handler, SerializationFormat.THRIFT_JSON);
//		service.decorate(csb.newDecorator());

		this.server = sb.serviceAt(url, service).build();
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		this.server.start();
		this.isStarted = true;
	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub
		this.server.stop();
		this.isStarted = false;
	}

	@Override
	public boolean started() {
		// TODO Auto-generated method stub
		return this.isStarted;
	}

}
