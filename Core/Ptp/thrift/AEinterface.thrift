namespace java de.tum.in.i22.uc.remotelistener.thrift  // defines the namespace   

/**
* @Author Cipri L.
*/
      
     
    
    
    
     /**
    	Defines the Service for the Adaptation Engine, part of the PTP.
    */
    service AdaptationEngine {
    
    	string adaptModel(1: string requestId, 2: map<string,string> parameters, 3: string model),
    	
    	string getLastChangeDate(),
    }