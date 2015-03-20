package de.tum.in.i22.uc.gui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import de.tum.in.i22.uc.Controller;
import de.tum.in.i22.uc.cm.datatypes.basic.ConflictResolutionFlagBasic.EConflictResolution;
import de.tum.in.i22.uc.cm.datatypes.basic.PxpSpec;
import de.tum.in.i22.uc.cm.datatypes.basic.ResponseBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic.EStatus;
import de.tum.in.i22.uc.cm.datatypes.basic.XmlPolicy;
import de.tum.in.i22.uc.cm.datatypes.interfaces.AtomicOperator;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IChecksum;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IContainer;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IData;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IEvent;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IMechanism;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IName;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IOperator;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IPipDeployer;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IPtpResponse;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IResponse;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.cm.distribution.IPLocation;
import de.tum.in.i22.uc.cm.handlers.RequestHandler;
import de.tum.in.i22.uc.cm.pip.RemoteDataFlowInfo;
import de.tum.in.i22.uc.cm.processing.IRequestHandler;
import de.tum.in.i22.uc.cm.settings.Settings;
import de.tum.in.i22.uc.thrift.server.IThriftServer;
import de.tum.in.i22.uc.thrift.server.ThriftServerFactory;

class MyProcessor implements IRequestHandler {
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
	public boolean registerPxp(PxpSpec pxp) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public IMechanism exportMechanism(String par) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IStatus revokePolicy(String policyName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IStatus revokeMechanism(String policyName, String mechName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IStatus deployPolicyXML(XmlPolicy XMLPolicy) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, Set<String>> listMechanisms() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean reset() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean evaluatePredicateSimulatingNextState(IEvent eventToSimulate, String predicate) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean evaluatePredicateCurrentState(String predicate) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Set<IContainer> getContainersForData(IData data) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<IData> getDataInContainer(IName containerName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IStatus startSimulation() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IStatus stopSimulation() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isSimulating() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public IStatus update(IEvent updateEvent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IStatus updateInformationFlowSemantics(IPipDeployer deployer, File jarFile,
			EConflictResolution conflictResolutionFlag) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IData getDataFromId(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IStatus initialRepresentation(IName containerName, Set<IData> data) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IData newStructuredData(Map<String, Set<IData>> structure) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, Set<IData>> getStructureOf(IData data) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<IData> flattenStructure(IData data) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean newChecksum(IData data, IChecksum checksum, boolean overwrite) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public IChecksum getChecksumOf(IData data) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean deleteChecksum(IData d) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean deleteStructure(IData d) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public IData newInitialRepresentation(IName containerName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IStatus addJPIPListener(String ip, int port, String id, String filter) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IStatus setUpdateFrequency(int msec, String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IMechanism exportMechanismPmp(String par) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IStatus revokePolicyPmp(String policyName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IStatus revokeMechanismPmp(String policyName, String mechName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IStatus deployPolicyURIPmp(String policyFilePath) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IStatus deployPolicyXMLPmp(XmlPolicy XMLPolicy) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, Set<String>> listMechanismsPmp() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<XmlPolicy> listPoliciesPmp() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IStatus deployPolicyRawXMLPmp(String xml) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<XmlPolicy> getPolicies(IData data) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IPtpResponse translatePolicy(String requestId, Map<String, String> parameters, XmlPolicy xmlPolicy) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IPtpResponse updateDomainModel(String requestId, Map<String, String> parameters, XmlPolicy xmlDomainModel) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getIfModel() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub

	}

	@Override
	public IStatus remoteTransfer(Set<XmlPolicy> policies, String fromHost,
			IName containerName, Set<IData> data) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IStatus incomingPolicyTransfer(XmlPolicy xml, String from) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void notify(IOperator operator, boolean endOfTimestep) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setFirstTick(String policyName, String mechanismName,
			long firstTick) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public long getFirstTick(String policyName, String mechanismName) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean wasNotifiedAtTimestep(AtomicOperator operator, long timestep) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int howOftenNotifiedAtTimestep(AtomicOperator operator, long timestep) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int howOftenNotifiedSinceTimestep(AtomicOperator operator,
			long timestep) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void doDataTransfer(RemoteDataFlowInfo dataflow) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public IPLocation getResponsibleLocation(String ip) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void register(XmlPolicy policy, String from) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deregister(String policyName, IPLocation location) {
		// TODO Auto-generated method stub
		
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
			
			handler= new RequestHandler();
			
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
