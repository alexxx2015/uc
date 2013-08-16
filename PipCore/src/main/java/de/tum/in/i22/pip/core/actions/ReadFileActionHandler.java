package de.tum.in.i22.pip.core.actions;

import de.tum.in.i22.pip.core.PipModel;
import de.tum.in.i22.uc.cm.datatypes.IEvent;
import de.tum.in.i22.uc.cm.datatypes.IStatus;

public class ReadFileActionHandler extends BaseActionHandler {

	public ReadFileActionHandler(IEvent event, PipModel ifModel) {
		super(event, ifModel);
		// TODO Auto-generated constructor stub
	}

	@Override
	public IStatus execute() {
		/*
		PipModel ifModel = getIfModel();
		Map<String, String> parameters = getEvent().getParameters();
		String filename = getParameterValue("InFileName");

		String pid = getParameterValue("PID");
		String processName = getParameterValue("ProcessName");
		int processContainerID = instantiateProcess(pid, processName, ifModel);

		int fileContainerID = ifModel.getContainerByName(new PIPName(-1,
				filename));

		// check if container for filename exists and create new container if
		// not
		if (fileContainerID == -1) {
			fileContainerID = ifModel.addContainer(null);
			int fileDataID = ifModel.addData(null);

			ifModel.addDataContainer(fileContainerID, fileDataID);

			ifModel.addName(new PIPName(-1, filename), fileContainerID);
		}

		// add data to transitive reflexive closure of process container
		for (int tempContainerID : ifModel
				.getAliasClosureByID(processContainerID)) {
			ifModel.addDataContainerList(tempContainerID,
					ifModel.getDataInContainer(fileContainerID));
		}
		*/
		return null;

	}
	
	/*
    /**
     *  Checks if a process with given parameters already exists, if not create container, data and names for it.
     *   
     
    private int instantiateProcess(String PID, String processName, PIPStructOld ifModel)
    {
       int processContainerID = ifModel.getContainerByName(new PIPName(-1, PID));

        //check if container for process exists and create new container if not
        if (processContainerID == -1)
        {
            processContainerID = ifModel.addContainer(null);
            ifModel.addDataContainer(processContainerID, ifModel.addData(null));
            ifModel.addName(new PIPName(-1, PID), processContainerID);
            ifModel.addName(new PIPName(Integer.parseInt(PID), processName), processContainerID);
        };

        return processContainerID;
    } */

}
