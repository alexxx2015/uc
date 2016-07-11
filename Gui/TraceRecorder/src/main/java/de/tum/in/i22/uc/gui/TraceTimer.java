package de.tum.in.i22.uc.gui;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import de.tum.in.i22.uc.cm.datatypes.basic.ResponseBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic.EStatus;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IEvent;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IResponse;
import de.tum.in.i22.uc.cm.handlers.RequestHandler;
import de.tum.in.i22.uc.cm.processing.AbstractRequestHandler;
import de.tum.in.i22.uc.cm.settings.Settings;
import de.tum.in.i22.uc.thrift.server.IThriftServer;
import de.tum.in.i22.uc.thrift.server.ThriftServerFactory;

class MyProcessor extends AbstractRequestHandler {
	private static LinkedList<IEvent> list = new LinkedList<IEvent>();
	public static String path = "";
	public static boolean done=false;

	public static void stopAndSave() {
		FileOutputStream fout;
		System.out.print("\n\nSaving " + list.size() + " events to file " + path + "...");
		try {
			fout = new FileOutputStream(path);
			ObjectOutputStream oos = new ObjectOutputStream(fout);
			oos.writeObject(list);
			oos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.exit(-1);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		System.out.println("done!");
		System.out.println("Exiting...");

		done=true;
	}

	@Override
	public void notifyEventAsync(IEvent pepEvent) {
		System.out.print("," + pepEvent.getName());
		if (pepEvent.getName().equals("StopRecording"))
			stopAndSave();
		else
			list.add(pepEvent);
	}

	@Override
	public IResponse notifyEventSync(IEvent pepEvent) {
		System.out.print("," + pepEvent.getName());
		if (pepEvent.getName().equals("StopRecording"))
			stopAndSave();
		else
			list.add(pepEvent);
		return new ResponseBasic(new StatusBasic(EStatus.ALLOW));
	}

	@Override
	public void processEventAsync(IEvent pepEvent) {
		System.out.print("," + pepEvent.getName());
		if (pepEvent.getName().equals("StopRecording"))
			stopAndSave();
		else
			list.add(pepEvent);
	}

	@Override
	public IResponse processEventSync(IEvent pepEvent) {
		System.out.print("," + pepEvent.getName());
		if (pepEvent.getName().equals("StopRecording"))
			stopAndSave();
		else
			list.add(pepEvent);
		return new ResponseBasic(new StatusBasic(EStatus.ALLOW));
	}

	@Override
	public Map<String, Set<Map<String, String>>> filterModel(
			Map<String, String> params) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deployReleaseMechanism(ByteBuffer mechanism) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ByteBuffer getMechanism(String mechanismName) {
		// TODO Auto-generated method stub
		return null;
	}
}

public class TraceTimer {

	public static void main(String[] args) {
		if ((args.length < 2) || (args.length > 4)
				|| !((args[0].equalsIgnoreCase("play")) || (args[0].equalsIgnoreCase("rec")))) {
			System.out.println("Usage: TraceTimer {rec|play} <trace File> [port] [uc.properties path]");
			System.exit(-1);
		}

		String command = args[0];
		String file = args[1];
		int port = 0;
		if (args.length > 2)
			port = Integer.valueOf(args[2]);

		String pp = "";
		if (args.length > 3)
			pp = args[3];
		
		
		
		if (command.equalsIgnoreCase("play")) {
			LinkedList<IEvent> list = new LinkedList<IEvent>();

			
			RequestHandler handler;
			
			if (!pp.equals("")) 
				Settings.setPropertiesFile(pp);
			
			handler = RequestHandler.newInstance();
			
//			while (!handler.isStarted()) {
//				System.out.println("Waiting for controller to start...");
//				try {
//					Thread.sleep(1000);
//				} catch (InterruptedException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}


			try {
				FileInputStream fin = new FileInputStream(file);
				ObjectInputStream ois = new ObjectInputStream(fin);
				list = (LinkedList<IEvent>) ois.readObject();
				ois.close();
			} catch (IOException e) {
				System.out.println(e);
				System.exit(-1);
			} catch (ClassNotFoundException e) {
				System.out.println(e);
				System.exit(-1);
			}
			
			
			long average=0;
			
			int iterations=30;
			
			for (int count=0; count<iterations; count++){
				System.out.println ("Iteration "+count + ". Current average = " + average);
				long startTime = System.nanoTime();
				for (IEvent e : list) {
//					if (e.isActual())
//						handler.notifyEventAsync(e);
//				else
						handler.notifyEventSync(e);
				}
				long endTime = System.nanoTime();
				handler.reset();
				average+=(endTime-startTime);
			}

			average=average/iterations;
			
			System.out.println("Total time (" + list.size() + " events): " + average / 1000 / 1000);
			System.err.println(average / 1000 / 1000);
			System.exit(0);

		} else {
			IThriftServer t = ThriftServerFactory.createPdpThriftServer(port, new MyProcessor());
			new Thread(t).start();

			MyProcessor.path = args[1];

			while (! MyProcessor.done) {
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			t.stop();
			System.exit(0);
			
		}
	}
}
