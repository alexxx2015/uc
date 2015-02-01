namespace java de.tum.in.i22.uc.remotelistener.thrift  // defines the namespace   

/**
* @Author Cipri L.
*/
      
    typedef i32 int  //typedefs to get convenient names for your types         
    
    /**
    	Defines the Service for the Translator Engine, part of the PTP.
    */
    
    
	exception TranslationException {
	  1: int what,
	  2: string why
	}
    
    service TranslationEngine { 
		/*
			requestId
			parameters: 
				template_id - the ECA template to be applied for the translation
				object_instance 				
		*/    	
    	string translatePolicy(1: string requestId, 2: map<string,string> parameters, 3: string policy) throws (1:TranslationException translationException),
    }
    
    
