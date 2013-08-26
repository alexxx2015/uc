package de.tum.in.i22.pip.core;

import org.apache.log4j.Logger;

import de.tum.in.i22.pip.core.actions.DefaultActionHandler;
import de.tum.in.i22.pip.core.actions.IActionHandler;
import de.tum.in.i22.uc.cm.datatypes.IEvent;
import de.tum.in.i22.uc.cm.datatypes.IStatus;

public class PipSemantics {
	
	private static final Logger _logger = Logger.getLogger(PipSemantics.class);
	
	public IStatus processEvent(IEvent event) {
		
		String action = event.getName();
		_logger.debug("Action name: " + action);
		
		_logger.trace("Create action handler");
		
		IActionHandler actionHandler = null;
		try {
			actionHandler = getActionHandler(action, event);
		} catch (IllegalAccessException | InstantiationException e) {
			_logger.error("Failed to create Action Handler for action " + action, e);
			
		}
		
		if (actionHandler == null) {
			_logger.trace("Create default action handler");
			
			actionHandler = new DefaultActionHandler();
		}
		
		IStatus status =  actionHandler.execute();
		_logger.trace("Status to return: " + status);
		return status;
		
//		switch (action)
//        {
//            case "OpenFile": break;
//            case "CloseHandle": break;
//            case "ReadFile":
//            	actionHandler = getActionHandler
//            	break;
//            case "WriteFile":
//                filename = getValueForKey("InFileName", parameters);
//
//                PID = getValueForKey("PID", parameters);
//                processName = getValueForKey("ProcessName", parameters);
//                processContainerID = instantiateProcess(PID, processName, ifModel);
//
//                fileContainerID = ifModel.getContainerByName(new PIPName(-1, filename));
//
//                //check if container for filename exists and create new container if not
//                if (fileContainerID == -1)
//                {
//                    fileContainerID = ifModel.addContainer(null);
//                    int fileDataID = ifModel.addData(null);
//
//                    ifModel.addDataContainer(fileContainerID, fileDataID);
//
//                    ifModel.addName(new PIPName(-1, filename), fileContainerID);
//                };
//
//                ifModel.addDataContainerList(fileContainerID, ifModel.getDataInContainer(processContainerID));
//
//                return 1;
//
//            case "CreateFile": break;
//            case "CopyFile": break;
//            case "MoveFile": break;
//            case "ReplaceFile": break;
//            case "FileCreated": break;
//            case "FileChanged": break;
//            case "FileDeleted": break;
//            case "FileRenamed": break;
//
//            case "CreateWindow":
//                PID = getValueForKey("PID", parameters);
//                processName = getValueForKey("ProcessName", parameters);
//                processContainerID = instantiateProcess(PID, processName, ifModel);
//                int windowContainerID = -1;
//
//                String windowHandle = getValueForKey("WindowHandle", parameters);
//
//                int windowContainerByHandleID = ifModel.getContainerByName(new PIPName(-1, windowHandle));
//
//                //check if container for window exists and create new container if not
//                if (windowContainerByHandleID == -1)
//                {
//                    windowContainerID = ifModel.addContainer(null);
//                    ifModel.addName(new PIPName(-1, windowHandle), windowContainerID);
//                };
//
//                ifModel.addDataContainerList(windowContainerID, ifModel.getDataInContainer(processContainerID));
//                ifModel.addAlias(processContainerID, windowContainerID);
//
//                return 1;
//
//            case "CreateProcess":
//                PID = getValueForKey("PID_Child", parameters);
//                String PPID = getValueForKey("PID", parameters);
//                String visibleWindows = getValueForKey("VisibleWindows", parameters);
//
//                processName = getValueForKey("ChildProcessName", parameters);
//                String parentProcessName = getValueForKey("ParentProcessName", parameters);
//
//                processContainerID = instantiateProcess(PID, processName, ifModel);
//                int parentProcessContainerID = instantiateProcess(PPID, parentProcessName, ifModel);
//
//                //add data of parent process container to child process container
//                ifModel.addDataContainerList(processContainerID, ifModel.getDataInContainer(parentProcessContainerID));
//
//                //add initial windows of process to model
//                //TODO: REGEX??
//                String[] visibleWindowsArray = visibleWindows.split(",",0);
//
//                for (String handle : visibleWindowsArray)
//                {
//                    windowContainerID = ifModel.getContainerByName(new PIPName(-1, handle));
//                    
//                    if(windowContainerID == -1)
//                    {
//                        windowContainerID = ifModel.addContainer(null);
//                        ifModel.addName(new PIPName(-1, handle), windowContainerID);
//                    }
//
//                    ifModel.addDataContainerList(windowContainerID, ifModel.getDataInContainer(processContainerID));
//
//                    ifModel.addAlias(processContainerID, windowContainerID);
//                }    
//
//                return 1;
//
//            case "KillProcess":
//                PID = getValueForKey("PID_Child", parameters);
//                processName = getValueForKey("ChildProcessName", parameters);
//
//                processContainerID = ifModel.getContainerByName(new PIPName(-1,PID));
//
//                //check if container for process exists
//                if (processContainerID != -1)
//                {
//                    ifModel.emptyContainer(processContainerID);
//                    
//                    //also remove all depending containers
//
//                    for (int contID : ifModel.getAliasClosureByID(processContainerID))
//                    {
//                        ifModel.removeContainer(contID);
//                    }
//
//                    ifModel.removeAllAliasesFrom(processContainerID);
//                    ifModel.removeAllAliasesTo(processContainerID);
//                    ifModel.removeContainer(processContainerID);
//
//                    for (PIPName nm : ifModel.getAllNamingsFrom(processContainerID))
//                    {
//                        ifModel.removeName(nm);
//                    }
//                };
//
//                return 1;
//
//            case "SetClipboardData":
//                PID = getValueForKey("PID", parameters);
//                processName = getValueForKey("ProcessName", parameters);
//                processContainerID = instantiateProcess(PID, processName, ifModel);
//
//                int clipboardContainerID = ifModel.getContainerByName(new PIPName(-1, "clipboard"));
//
//                //check if container for clipboard exists and create new container if not
//                if (clipboardContainerID == -1)
//                {
//                    clipboardContainerID = ifModel.addContainer(null);
//                    ifModel.addName(new PIPName(-1, "clipboard"), clipboardContainerID);
//                };
//
//                ifModel.emptyContainer(clipboardContainerID);
//                ifModel.addDataContainerList(clipboardContainerID, ifModel.getDataInContainer(processContainerID));
//
//                return 1;
//
//            case "GetClipboardData":
//                PID = getValueForKey("PID", parameters);
//                processName = getValueForKey("ProcessName", parameters);
//                processContainerID = instantiateProcess(PID, processName, ifModel);
//
//                clipboardContainerID = ifModel.getContainerByName(new PIPName(-1, "clipboard"));
//
//                //check if container for clipboard exists and create new container if not
//                if (clipboardContainerID == -1)
//                {
//                    clipboardContainerID = ifModel.addContainer(null);
//                    ifModel.addName(new PIPName(-1, "clipboard"), clipboardContainerID);
//                };
//
//                //add data to transitive reflexive closure of process container
//                for (int tempContainerID : ifModel.getAliasClosureByID(processContainerID))
//                {
//                    ifModel.addDataContainerList(tempContainerID, ifModel.getDataInContainer(clipboardContainerID));
//                }
//                
//                return 1;
//
//            case "EmptyClipboard":
//                clipboardContainerID = ifModel.getContainerByName(new PIPName(-1, "clipboard"));
//
//                //check if container for clipboard exists and create new container if not
//                if (clipboardContainerID == -1)
//                {
//                    clipboardContainerID = ifModel.addContainer(null);
//                    ifModel.addName(new PIPName(-1, "clipboard"), clipboardContainerID);
//                };
//
//                ifModel.emptyContainer(clipboardContainerID);
//
//                return 1;
//
//            case "CreateDC":
//                PID = getValueForKey("PID", parameters);
//                processName = getValueForKey("ProcessName", parameters);
//
//                processContainerID = instantiateProcess(PID, processName, ifModel);
//
//                String deviceName = getValueForKey("lpszDevice", parameters);
//             
//                int deviceContainerID = ifModel.getContainerByName(new PIPName(Integer.parseInt(PID), deviceName));
//
//                //check if container for device exists and create new container if not
//                if (deviceContainerID == -1)
//                {
//                    deviceContainerID = ifModel.addContainer(null);
//                    ifModel.addName(new PIPName(Integer.parseInt(PID), deviceName), deviceContainerID);
//                };
//
//                ifModel.addDataContainerList(deviceContainerID, ifModel.getDataInContainer(processContainerID));
//
//                return 1;
//
//            case "TakeScreenshot":
//                String visibleWindow = getValueForKey("VisibleWindow", parameters);
//                                
//                clipboardContainerID = ifModel.getContainerByName(new PIPName(-1, "clipboard"));
//
//                //check if container for clipboard exists and create new container if not
//                if (clipboardContainerID == -1)
//                {
//                    clipboardContainerID = ifModel.addContainer(null);
//                    ifModel.addName(new PIPName(-1, "clipboard"), clipboardContainerID);
//                };
//
//                //do not empty as take screenshot events are splitted to one screenshot event per visible window
//                //ifModel.emptyContainer(clipboardContainerID);
//
//                windowContainerID = ifModel.getContainerByName(new PIPName(-1, visibleWindow));
//                ifModel.addDataContainerList(clipboardContainerID, ifModel.getDataInContainer(windowContainerID));
//
//                return 1;
//
//            default:
//                break;
//        }
	}

	private IActionHandler getActionHandler(String action, IEvent event) 
			throws InstantiationException, IllegalAccessException {
		
		ClassLoader classLoader = PipSemantics.class.getClassLoader();

		String className = "de.tum.in.i22.pip.core.actions." + action + "ActionHandler";
	    try {
	    	_logger.trace("Load class: " + className);
	        Class<?> actionHandlerClass = classLoader.loadClass(className);
	        _logger.trace("Create class " + className + " instance");
	        IActionHandler actionHandler = (IActionHandler)actionHandlerClass.newInstance();
	        actionHandler.setEvent(event);
	        return actionHandler;
	    } catch (ClassNotFoundException e) {
	        _logger.error("Class " + className + " not found.", e);
	        return null;
	    }
	}

}
