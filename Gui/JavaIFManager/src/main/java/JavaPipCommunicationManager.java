import java.io.IOException;

import de.tum.in.i22.uc.cm.datatypes.interfaces.IEvent;
import de.tum.in.i22.uc.cm.distribution.IPLocation;
import de.tum.in.i22.uc.cm.distribution.client.Any2PipClient;
import de.tum.in.i22.uc.thrift.client.ThriftClientFactory;
import de.tum.in.i22.uc.thrift.server.IThriftServer;
import de.tum.in.i22.uc.thrift.server.ThriftServerFactory;


public class JavaPipCommunicationManager {
	
	private String pipHost = "localhost";
	private int pipPort = 9090;
	
	public static final String SERVER_HOST = "localhost";
	public static final int SERVER_PORT = 10001;
	private IThriftServer server;
	private Any2PipClient client;
	
	public JavaPipCommunicationManager() {
		// TODO Auto-generated constructor stub
		MyJPipServer mjpipserver = new MyJPipServer();
		server = ThriftServerFactory.createJPipThriftServer(SERVER_PORT , mjpipserver);
		Thread t = new Thread(server);
		t.start();
		
		ThriftClientFactory tcf = new ThriftClientFactory();
		client = tcf.createAny2PipClient(new IPLocation(pipHost, pipPort));
	}
	
	
	public void connect(){
		try {
			client.connect();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public Any2PipClient getClient(){
		return this.client;
	}
}
