package de.tum.in.i22.policyeditor.server;

import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TServer.Args;
import org.apache.thrift.server.TSimpleServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;

import de.tum.in.i22.policyeditor.util.Config;

public class EditorThriftServer {

	private static EditorHandler handler;
	private static TAny2Editor.Processor processor;

	/**
	 * The default port on which the editor is listening.
	 */
	public static final int Default_PORT = 50001;
	private static int EditorServer_PORT = 0;
	
	public static int getPort(){
		if(EditorServer_PORT == 0){
			try {
				Config config = new Config();
				String portString=config.getProperty("translationEnginePort");
				int port = Integer.parseInt(portString);
				EditorServer_PORT = port;
			} catch (Exception e) {
				return Default_PORT;
			}	
			
		}
		return EditorServer_PORT;
	}
	public static void startEditorServer() {
		try {
			handler = new EditorHandler();
			processor = new TAny2Editor.Processor(handler);

			Runnable simple = new Runnable() {
				public void run() {
					startServer(processor);
				}
			};

			new Thread(simple).start();
		} catch (Exception x) {
			x.printStackTrace();
		}
	}

	private static void startServer(TAny2Editor.Processor processor) {
		try {
			int port = Default_PORT;
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

	/******************************************************************/
	/**
	 * @param args
	 */
	public static void main(String[] args) {

		EditorThriftServer.startEditorServer();
	}

}
