package structures;
import java.util.Hashtable;
import java.util.List;



    public class PIPSemantics
    {

        //constants to control whether to add new containers for windows or not
        private final boolean CREATE_NEW_CONTAINER = true;

        /**
         *  Updates the information flow model according to the semantics of the provided event.
         *   
         */
        public int processEvent(PDPEvent incomingEvent, PIPStruct ifModel)
        {
            if (incomingEvent == null | ifModel == null) return -1;

            List<Hashtable> parameters = incomingEvent.parameters;
            String action = incomingEvent.action;

            switch (action)
            {
                case "OpenFile":
                    return 1;

                case "CloseHandle":
                    return 1;

                case "ReadFile":
                    String filename = getValueForKey("InFileName", parameters);

                    String PID = getValueForKey("PID", parameters);
                    String processName = getValueForKey("ProcessName", parameters);
                    int processContainerID = instantiateProcess(PID, processName, ifModel);

                    int fileContainerID = ifModel.getContainerByName(new PIPName(-1, filename));

                    //check if container for filename exists and create new container if not
                    if (fileContainerID == -1)
                    {
                        fileContainerID = ifModel.addContainer(null);
                        int fileDataID = ifModel.addData(null);

                        ifModel.addDataContainer(fileContainerID, fileDataID);

                        ifModel.addName(new PIPName(-1, filename), fileContainerID);
                    }

                    //add data to transitive reflexive closure of process container
                    for (int tempContainerID : ifModel.getAliasClosureByID(processContainerID))
                    {
                        ifModel.addDataContainerList(tempContainerID, ifModel.getDataInContainer(fileContainerID));
                    }

                    return 1;
                                      

                case "WriteFile":
                    filename = getValueForKey("InFileName", parameters);

                    PID = getValueForKey("PID", parameters);
                    processName = getValueForKey("ProcessName", parameters);
                    processContainerID = instantiateProcess(PID, processName, ifModel);

                    fileContainerID = ifModel.getContainerByName(new PIPName(-1, filename));

                    //check if container for filename exists and create new container if not
                    if (fileContainerID == -1)
                    {
                        fileContainerID = ifModel.addContainer(null);
                        int fileDataID = ifModel.addData(null);

                        ifModel.addDataContainer(fileContainerID, fileDataID);

                        ifModel.addName(new PIPName(-1, filename), fileContainerID);
                    };

                    ifModel.addDataContainerList(fileContainerID, ifModel.getDataInContainer(processContainerID));

                    return 1;

                case "CreateFile":
                    return 1;

                case "CopyFile":
                    return 1;

                case "MoveFile":
                    return 1;

                case "ReplaceFile":
                    return 1;

                case "FileCreated":
                    return 1;

                case "FileChanged":
                    return 1;

                case "FileDeleted":
                    return 1;

                case "FileRenamed":
                    return 1;

                case "CreateWindow":
                    PID = getValueForKey("PID", parameters);
                    processName = getValueForKey("ProcessName", parameters);
                    processContainerID = instantiateProcess(PID, processName, ifModel);
                    int windowContainerID = -1;

                    String windowHandle = getValueForKey("WindowHandle", parameters);

                    int windowContainerByHandleID = ifModel.getContainerByName(new PIPName(-1, windowHandle));

                    //check if container for window exists and create new container if not
                    if (windowContainerByHandleID == -1)
                    {
                        windowContainerID = ifModel.addContainer(null);
                        ifModel.addName(new PIPName(-1, windowHandle), windowContainerID);
                    };

                    ifModel.addDataContainerList(windowContainerID, ifModel.getDataInContainer(processContainerID));
                    ifModel.addAlias(processContainerID, windowContainerID);

                    return 1;

                case "CreateProcess":
                    PID = getValueForKey("PID_Child", parameters);
                    String PPID = getValueForKey("PID", parameters);
                    String visibleWindows = getValueForKey("VisibleWindows", parameters);

                    processName = getValueForKey("ChildProcessName", parameters);
                    String parentProcessName = getValueForKey("ParentProcessName", parameters);

                    processContainerID = instantiateProcess(PID, processName, ifModel);
                    int parentProcessContainerID = instantiateProcess(PPID, parentProcessName, ifModel);

                    //add data of parent process container to child process container
                    ifModel.addDataContainerList(processContainerID, ifModel.getDataInContainer(parentProcessContainerID));

                    //add initial windows of process to model
                    //TODO: REGEX??
                    String[] visibleWindowsArray = visibleWindows.split(",",0);

                    for (String handle : visibleWindowsArray)
                    {
                        windowContainerID = ifModel.getContainerByName(new PIPName(-1, handle));
                        
                        if(windowContainerID == -1)
                        {
                            windowContainerID = ifModel.addContainer(null);
                            ifModel.addName(new PIPName(-1, handle), windowContainerID);
                        }

                        ifModel.addDataContainerList(windowContainerID, ifModel.getDataInContainer(processContainerID));

                        ifModel.addAlias(processContainerID, windowContainerID);
                    }    

                    return 1;

                case "KillProcess":
                    PID = getValueForKey("PID_Child", parameters);
                    processName = getValueForKey("ChildProcessName", parameters);

                    processContainerID = ifModel.getContainerByName(new PIPName(-1,PID));

                    //check if container for process exists
                    if (processContainerID != -1)
                    {
                        ifModel.emptyContainer(processContainerID);
                        
                        //also remove all depending containers

                        for (int contID : ifModel.getAliasClosureByID(processContainerID))
                        {
                            ifModel.removeContainer(contID);
                        }

                        ifModel.removeAllAliasesFrom(processContainerID);
                        ifModel.removeAllAliasesTo(processContainerID);
                        ifModel.removeContainer(processContainerID);

                        for (PIPName nm : ifModel.getAllNamingsFrom(processContainerID))
                        {
                            ifModel.removeName(nm);
                        }
                    };

                    return 1;

                case "SetClipboardData":
                    PID = getValueForKey("PID", parameters);
                    processName = getValueForKey("ProcessName", parameters);
                    processContainerID = instantiateProcess(PID, processName, ifModel);

                    int clipboardContainerID = ifModel.getContainerByName(new PIPName(-1, "clipboard"));

                    //check if container for clipboard exists and create new container if not
                    if (clipboardContainerID == -1)
                    {
                        clipboardContainerID = ifModel.addContainer(null);
                        ifModel.addName(new PIPName(-1, "clipboard"), clipboardContainerID);
                    };

                    ifModel.emptyContainer(clipboardContainerID);
                    ifModel.addDataContainerList(clipboardContainerID, ifModel.getDataInContainer(processContainerID));

                    return 1;

                case "GetClipboardData":
                    PID = getValueForKey("PID", parameters);
                    processName = getValueForKey("ProcessName", parameters);
                    processContainerID = instantiateProcess(PID, processName, ifModel);

                    clipboardContainerID = ifModel.getContainerByName(new PIPName(-1, "clipboard"));

                    //check if container for clipboard exists and create new container if not
                    if (clipboardContainerID == -1)
                    {
                        clipboardContainerID = ifModel.addContainer(null);
                        ifModel.addName(new PIPName(-1, "clipboard"), clipboardContainerID);
                    };

                    //add data to transitive reflexive closure of process container
                    for (int tempContainerID : ifModel.getAliasClosureByID(processContainerID))
                    {
                        ifModel.addDataContainerList(tempContainerID, ifModel.getDataInContainer(clipboardContainerID));
                    }
                    
                    return 1;

                case "EmptyClipboard":
                    clipboardContainerID = ifModel.getContainerByName(new PIPName(-1, "clipboard"));

                    //check if container for clipboard exists and create new container if not
                    if (clipboardContainerID == -1)
                    {
                        clipboardContainerID = ifModel.addContainer(null);
                        ifModel.addName(new PIPName(-1, "clipboard"), clipboardContainerID);
                    };

                    ifModel.emptyContainer(clipboardContainerID);

                    return 1;

                case "CreateDC":
                    PID = getValueForKey("PID", parameters);
                    processName = getValueForKey("ProcessName", parameters);

                    processContainerID = instantiateProcess(PID, processName, ifModel);

                    String deviceName = getValueForKey("lpszDevice", parameters);
                 
                    int deviceContainerID = ifModel.getContainerByName(new PIPName(Integer.parseInt(PID), deviceName));

                    //check if container for device exists and create new container if not
                    if (deviceContainerID == -1)
                    {
                        deviceContainerID = ifModel.addContainer(null);
                        ifModel.addName(new PIPName(Integer.parseInt(PID), deviceName), deviceContainerID);
                    };

                    ifModel.addDataContainerList(deviceContainerID, ifModel.getDataInContainer(processContainerID));

                    return 1;

                case "TakeScreenshot":
                    String visibleWindow = getValueForKey("VisibleWindow", parameters);
                                    
                    clipboardContainerID = ifModel.getContainerByName(new PIPName(-1, "clipboard"));

                    //check if container for clipboard exists and create new container if not
                    if (clipboardContainerID == -1)
                    {
                        clipboardContainerID = ifModel.addContainer(null);
                        ifModel.addName(new PIPName(-1, "clipboard"), clipboardContainerID);
                    };

                    //do not empty as take screenshot events are splitted to one screenshot event per visible window
                    //ifModel.emptyContainer(clipboardContainerID);

                    windowContainerID = ifModel.getContainerByName(new PIPName(-1, visibleWindow));
                    ifModel.addDataContainerList(clipboardContainerID, ifModel.getDataInContainer(windowContainerID));

                    return 1;

                default:
                    return 1;
            }

        }

        /**
         *  Returns the value for a specific key inside a parameter set.
         *   
         */
        private String getValueForKey(String key, List<Hashtable> parameters)
        {
            for (Hashtable<?, ?> parameter : parameters)
            {
                if(parameter.containsKey("name") && parameter.get("name").equals(key)) return (String)parameter.get("value");
            }

            return "error";
        }

        /**
         *  Checks if a process with given parameters already exists, if not create container, data and names for it.
         *   
         */
        private int instantiateProcess(String PID, String processName, PIPStruct ifModel)
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
        }
    }
