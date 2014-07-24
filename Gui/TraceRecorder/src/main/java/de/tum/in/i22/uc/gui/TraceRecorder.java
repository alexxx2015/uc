package de.tum.in.i22.uc.gui;

import java.util.LinkedList;

import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TTransportException;

import de.tum.in.i22.uc.cm.datatypes.interfaces.IEvent;
import de.tum.in.i22.uc.thrift.types.TPep2Pdp;

public class TraceRecorder {
	private TServer server=null;
	private RecordingServerImpl rec;
	
    public boolean start(int port) {
    	if ((port>0)&&(server==null)){
        try {
            TServerSocket serverTransport = new TServerSocket(port);
            rec=new RecordingServerImpl();
            
            TPep2Pdp.Processor processor = new TPep2Pdp.Processor(rec);
 
            server = new TThreadPoolServer(new TThreadPoolServer.Args(serverTransport).
                    processor(processor));
            System.out.println("Starting server on port "+port+" ...");
            server.serve();
            return true;
        } catch (TTransportException e) {
            e.printStackTrace();
            return false;
        }
    	} else return false;
    }

	public boolean isRunning() {
		if (server==null) return false;
		return server.isServing();
	}
	public void stop(){
		if ((server!=null)&&(server.isServing())) server.stop();
		server=null;
	}
	public LinkedList<IEvent> getLl() {
		if (server==null) return null;
		return rec.getTrace();
	}
	
	
}