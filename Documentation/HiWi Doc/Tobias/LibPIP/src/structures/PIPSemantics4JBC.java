package structures;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;



    public class PIPSemantics4JBC
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
            String lftVar, rgtVar, var, retVar;
            int varNameId, varFieldNameId, rgtDataId, lftDataId, rgtCont, lftCont, tmpCont, varCont, retVarCont;
            UniqueIntList rgtData, lftAliasClosure, rgtAliasClosure;
            switch (action)
            {	
            	case "assignPrim":
            		lftVar = getValueForKey("lftVar", parameters);
            		rgtVar = getValueForKey("rgtVar", parameters);
            		
            		rgtCont = ifModel.getContainerByName(new PIPName(-1, rgtVar));
            		if(rgtCont == -1){
            			rgtCont = ifModel.addContainer(null);
            			rgtDataId = ifModel.addData(null);
            			ifModel.addDataContainer(rgtCont, rgtDataId);
            			ifModel.addName(new PIPName(-1, rgtVar), rgtCont);
            		}
            		
            		lftCont = ifModel.getContainerByName(new PIPName(-1, lftVar));
            		if(lftCont == -1){
            			lftCont = ifModel.addContainer(null);
            			lftDataId = ifModel.addData(null);
            			ifModel.addDataContainer(lftCont, lftDataId);
            			ifModel.addName(new PIPName(-1, lftVar), lftCont);
            		}
            		
            		ifModel.emptyContainer(lftCont);
            		rgtData = ifModel.getDataInContainer(rgtCont);
            		ifModel.addDataContainerList(lftCont, rgtData);
            		
            		return 1;
            		
            	case "assignRef":
            		lftVar = getValueForKey("lftVar", parameters);
            		rgtVar = getValueForKey("rgtVar", parameters);
            		
            		rgtCont = ifModel.getContainerByName(new PIPName(-1, rgtVar));
            		if(rgtCont == -1){
            			rgtCont = ifModel.addContainer(null);
            			rgtDataId = ifModel.addData(null);
            			ifModel.addDataContainer(rgtCont, rgtDataId);
            			ifModel.addName(new PIPName(-1, rgtVar), rgtCont);
            		}
            		
            		lftCont = ifModel.getContainerByName(new PIPName(-1, lftVar));
            		if(lftCont == -1){
            			lftCont = ifModel.addContainer(null);
            			lftDataId = ifModel.addData(null);
            			ifModel.addDataContainer(lftCont, lftDataId);
            			ifModel.addName(new PIPName(-1, lftVar), lftCont);
            		}
            		
//            		Update storage function
            		ifModel.emptyContainer(lftCont);
            		rgtData = ifModel.getDataInContainer(rgtCont);
            		ifModel.addDataContainerList(lftCont, rgtData);
            		
//            		Update alias function for lftCont
            		rgtAliasClosure = ifModel.getAliasClosureByID(rgtCont);
            		lftAliasClosure = ifModel.getAliasByID(lftCont);
            		Iterator<Integer> rgtAliasIt = rgtAliasClosure.iterator();
            		int rgtAliasId;
            		while(rgtAliasIt.hasNext()){
            			rgtAliasId = rgtAliasIt.next();
            			if(!lftAliasClosure.contains(rgtAliasId)){
            				ifModel.addAlias(lftCont, rgtAliasId);
            			}
            		}
            		
//            		Update alias funcion for rgtCont
            		ifModel.addAlias(rgtCont, lftCont);            		
            		return 1;	
            		
            	case "arithComp":
            		lftVar = getValueForKey("lftVar", parameters);
            		rgtVar = getValueForKey("rgtVar", parameters);
            		
            		rgtCont = ifModel.getContainerByName(new PIPName(-1, rgtVar));
            		if(rgtCont == -1){
            			rgtCont = ifModel.addContainer(null);
            			rgtDataId = ifModel.addData(null);
            			ifModel.addDataContainer(rgtCont, rgtDataId);
            			ifModel.addName(new PIPName(-1, rgtVar), rgtCont);
            		}
            		
            		lftCont = ifModel.getContainerByName(new PIPName(-1, lftVar));
            		if(lftCont == -1){
            			lftCont = ifModel.addContainer(null);
            			lftDataId = ifModel.addData(null);
            			ifModel.addDataContainer(lftCont, lftDataId);
            			ifModel.addName(new PIPName(-1, lftVar), lftCont);
            		}
            		
            		tmpCont = ifModel.addContainer(null);
            		ifModel.addDataContainerList(tmpCont, ifModel.getDataInContainer(rgtCont));
            		ifModel.addDataContainerList(tmpCont, ifModel.getDataInContainer(lftCont));
            		
            		return 1;
            	case "methodExit":
            		var = getValueForKey("var", parameters);
            		retVar = getValueForKey("retVar", parameters);
            		
            		varCont = ifModel.getContainerByName(new PIPName(-1, var));
            		if(varCont == -1){
            			rgtCont = ifModel.addContainer(null);
            			rgtDataId = ifModel.addData(null);
            			ifModel.addDataContainer(rgtCont, rgtDataId);
            			ifModel.addName(new PIPName(-1, var), rgtCont);
            		}
            		
            		retVarCont = ifModel.getContainerByName(new PIPName(-1, retVar));
            		if(retVarCont == -1){
            			lftCont = ifModel.addContainer(null);
            			lftDataId = ifModel.addData(null);
            			ifModel.addDataContainer(lftCont, lftDataId);
            			ifModel.addName(new PIPName(-1, retVar), lftCont);
            		}
            		ifModel.emptyContainer(varCont);
            		ifModel.addDataContainerList(varCont, ifModel.getDataInContainer(retVarCont));
            		
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
    }

//	case "Write2Field":            		
//    case "Write2StaticField":                
//    case "Write2Var":
//    	varName = getValueForKey("varName", parameters);
//    	varFieldName = getValueForKey("varFieldName", parameters);
//    	
//    	//Add container for varName
//    	varNameId = ifModel.getContainerByName(new PIPName(-1, varName));
//    	if(varNameId == -1){
//    		varNameId = ifModel.addContainer(null);
//    		ifModel.addDataContainer(varNameId, ifModel.addData(null));
//    		ifModel.addName(new PIPName(-1, varName), varNameId);
//    	}
//    	
//    	//Add container for varFieldName
//    	varFieldNameId = ifModel.getContainerByName(new PIPName(-1, varFieldName));
//    	if(varFieldNameId == -1){
//    		varFieldNameId = ifModel.getContainerByName(new PIPName(-1, varFieldName));
//    		ifModel.addDataContainer(varFieldNameId, ifModel.addData(null));
//    		ifModel.addName(new PIPName(-1, varFieldName), varFieldNameId);
//    	}
//    	
//        ifModel.addDataContainerList(varNameId, ifModel.getDataInContainer(varFieldNameId));
//        
//    	return 1;

	
	
	/*
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
    */