package de.tum.in.i22.pip.core.eventdef;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import de.tum.in.i22.pip.core.InformationFlowModel;
import de.tum.in.i22.pip.core.Scope;
import de.tum.in.i22.uc.cm.datatypes.EStatus;
import de.tum.in.i22.uc.cm.datatypes.IStatus;

public class LoadEventHandler extends BaseEventHandler {
	private static final Logger _logger = Logger
			.getLogger(LoadEventHandler.class);
	
	public LoadEventHandler() {
		super();
	}
	
	@Override
	public IStatus execute() {
		_logger.info("Load event handler execute");

//		String direction = null;
		String delimiter = null;
		String filename = null;
		

		try {
//			direction = getParameterValue("direction");
			delimiter = getParameterValue("delimiter");
			filename = getParameterValue("filename");
			
			
		} catch (ParameterNotFoundException e) {
			_logger.error(e.getMessage());
			return _messageFactory.createStatus(EStatus.ERROR_EVENT_PARAMETER_MISSING, e.getMessage());
		}
		
		InformationFlowModel ifModel = getInformationFlowModel();
		if (delimiter.equals("start")){
			//check whether a scope with this loading have been already started
			Map<String,Object> attributes = new HashMap<String,Object>();
			attributes.put("app", "Thunderbird");
			attributes.put("filename", filename);
			Scope scope= new Scope("TB loading file "+filename+ " START",Scope.scopeType.LOAD_FILE,attributes);

			boolean isOpen=ifModel.isScopeOpened(scope);
			_logger.info("isScopeOpened="+isOpen);
			
			if (isOpen|!(ifModel.openScope(scope))) {
				_logger.info("Scope "+scope+" is already opened");
				return _messageFactory.createStatus(EStatus.ERROR, "Scope" + scope+" is already opened");
			}
			_logger.info("Scope "+scope+" is now open!");
		}
		
		if (delimiter.equals("end")){
			//check whether a scope with this loading have been already started
			Map<String,Object> attributes = new HashMap<String,Object>();
			attributes.put("app", "Thunderbird");
			attributes.put("filename", filename);
			Scope scope= new Scope("TB loading file "+filename+ " END",Scope.scopeType.LOAD_FILE,attributes);
			boolean isOpen=ifModel.isScopeOpened(scope);
			_logger.info("isScopeOpened="+isOpen);

			Map<String,Object> attributes2 = new HashMap<String,Object>();
			attributes2.put("app", "Thunderbird");
			attributes2.put("filename", filename);
			Scope scope2= new Scope("TB loading file "+filename+ " END",Scope.scopeType.LOAD_FILE,attributes);
			Scope scope3= new Scope("TB loading file "+filename+ " END",Scope.scopeType.LOAD_FILE,attributes2);
			_logger.info("Scope2 is "+ (scope2.equals(scope3)?"":"not")+" equals to scope3");
			_logger.info("Scope3 is "+ (scope3.equals(scope2)?"":"not")+" equals to scope2");
			attributes2.put("next field","test");
			Scope scope4= new Scope("TB loading file "+filename+ " END",Scope.scopeType.LOAD_FILE,attributes2);
			_logger.info("Scope3 is "+ (scope3.equals(scope4)?"":"not")+" equals to scope4");
			_logger.info("Scope4 is "+ (scope4.equals(scope3)?"":"not")+" equals to scope3");
			_logger.info("Scope2 is "+ (scope2.equals(scope4)?"":"not")+" equals to scope4");
			_logger.info("Scope4 is "+ (scope4.equals(scope2)?"":"not")+" equals to scope2");
			
					
			
			
			
			if (!(isOpen)|!(ifModel.closeScope(scope))) {
				_logger.info("Scope "+scope+" is already closed");
				return _messageFactory.createStatus(EStatus.ERROR, "Scope" + scope+" is already closed");
			}
			_logger.info("Scope "+scope+" is now closed!");
			
			

			
			
			
		}
		
		return _messageFactory.createStatus(EStatus.OKAY);
	}

}
