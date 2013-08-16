package main;
import structures.*;
import helper.*;

public class PIPHandler implements IPIPCommunication {

    private PIPStruct pipModel;
    private PIPSemantics4JBC pipSemantics;
    @Override
	public boolean initializePIP() {
        pipModel = new PIPStruct();
        pipSemantics = new PIPSemantics4JBC();

        System.out.println("Initialize PIP" + "[PIPLib]");

        return true;
	}

	@Override
	public int initialRepresentation(int PID, String rep) {
        if (pipSemantics == null | pipModel == null)
        {
        	System.out.println("PIP not yet initialized => call initializePIP() first!" + "[PIPLib]");
            return -1;
        }

        int initialDataID;
        int containerID = pipModel.getContainerByName(new PIPName(PID, rep));

        if (containerID == -1)
        {
            int initialContainerID = pipModel.addContainer(null);

            pipModel.addName(new PIPName(PID, rep), initialContainerID);

            initialDataID = pipModel.addData(null);

            pipModel.addDataContainer(initialDataID, initialContainerID);
        }
        else
        {
            initialDataID = pipModel.getDataInContainer(containerID).get(0);
        }

        return initialDataID;
	}

	@Override
	public int updatePIP(PDPEvent newEvent) {
        if (pipSemantics == null | pipModel == null)
        {
        	System.out.println("PIP not yet initialized => call initializePIP() first!" + "[PIPLib]");
            return -1;
        }

        return pipSemantics.processEvent(newEvent, pipModel);
	}

	@Override
	public int representationRefinesData(int PID, String rep, int dataID, boolean strict) {
        //overload setting for strict to make is always false => needed by implementation for smart meter
        strict = false;

        if (pipSemantics == null | pipModel == null)
        {
        	System.out.println("PIP not yet initialized => call initializePIP() first!" + "[PIPLib]");
            return -1;
        }

        int foundContainerID;

        if (strict)
        {
            foundContainerID = pipModel.getContainerByName(new PIPName(PID, rep));
        }
        else
        {
            foundContainerID = pipModel.getContainerByNameRelaxed(new PIPName(PID, rep));
        }


        if (pipModel.hasDataByID(dataID) & pipModel.getContainerOfData(dataID).contains(foundContainerID)) 
        {
            return 1;
        }
        else
        {
            return 0;
        }
	}

	@Override
	public UniqueIntList getDataIDbyRepresentation(int PID, String rep,	boolean strict) {
        if (pipSemantics == null | pipModel == null)
        {
        	System.out.println("PIP not yet initialized => call initializePIP() first!" + "[PIPLib]");
            return null;
        }

        int foundContainerID;
        UniqueIntList dataItems = null;

        if (strict)
        {
            foundContainerID = pipModel.getContainerByName(new PIPName(PID, rep));
            dataItems = pipModel.getDataInContainer(foundContainerID);
        }
        else
        {
            foundContainerID = pipModel.getContainerByNameRelaxed(new PIPName(PID, rep));
            dataItems = pipModel.getDataInContainer(foundContainerID);
        }

        return dataItems;
	}

	@Override
	public void resetPIP() {
		// TODO Auto-generated method stub

	}

	@Override
	public String printModel() {
        if (pipModel == null)
        {
        	System.out.println("PIP not yet initialized => call initializePIP() first!" + "[PIPLib]");
            return "error";
        }

        String printedModel = pipModel.printModel();

        return printedModel;
	}

}
