package de.tum.in.i22.uc.thrift.server;

import java.util.Set;

import org.apache.thrift.TException;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TServer.Args;
import org.apache.thrift.server.TSimpleServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.datatypes.interfaces.IContainer;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.cm.interfaces.IAny2PtEditor;
import de.tum.in.i22.uc.thrift.ThriftConverter;
import de.tum.in.i22.uc.thrift.types.TAny2PtEditor;
import de.tum.in.i22.uc.thrift.types.TContainer;
import de.tum.in.i22.uc.thrift.types.TStatus;
/**
 * @author Cipri
 *
 */
public class TAny2PtEditorThriftServer implements TAny2PtEditor.Iface  {
	
	private static Logger _logger = LoggerFactory
			.getLogger(TAny2PtEditorThriftServer.class);

	private final IAny2PtEditor _handler;
	private static TAny2PtEditor.Processor<TAny2PtEditorThriftServer> processor;

	private final int port;
	
	public TAny2PtEditorThriftServer(int PORT, IAny2PtEditor handler) {
		_handler = handler;
		this.port = PORT;
	}

	@Override
	public TStatus specifyPolicyFor(Set<TContainer> representations,
			String dataClass) throws TException {
		Set<IContainer> containers = ThriftConverter.fromThriftContainerSet(representations);
		IStatus status = _handler.specifyPolicyFor(containers, dataClass);
		return ThriftConverter.toThrift(status);
	}
	
	public void startEditorServer() {
		try {
			processor = new TAny2PtEditor.Processor<TAny2PtEditorThriftServer>(this);
			Runnable simple = new Runnable() {
				public void run() {
					try {
						TServerTransport serverTransport = new TServerSocket(port);
						TServer server = new TSimpleServer(
								new Args(serverTransport).processor(processor));
						System.out.println("Policy Template Editor Server started on port "
								+ port + " ...");
						server.serve();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			};

			new Thread(simple).start();
		} catch (Exception x) {
			x.printStackTrace();
		}
	}
	
}
