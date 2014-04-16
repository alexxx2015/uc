/**
 *   
 * 
 *  THIS 
 *  
 *  CLASS
 *  
 *  IS
 *   
 *  FOR 
 *   
 *  TESTING 
 *  
 *  PURPOSES 
 *  
 *  ONLY
 * 
 * 
 */


package de.tum.in.i22.uc.cm.processing.dummy;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.datatypes.interfaces.IData;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.cm.distribution.Location;
import de.tum.in.i22.uc.cm.processing.PmpProcessor;

public class DummyPmpProcessor extends PmpProcessor {
	private static Logger _logger = LoggerFactory.getLogger(DummyPmpProcessor.class);

	@Override
	public IStatus receivePolicies(Set<String> policies) {
		// TODO Auto-generated method stub
		_logger.error("PmpProcessor DUMMY Implementation");
		_logger.error("receivePolicies method invoked");
		return null;
	}

	@Override
	public IStatus informRemoteDataFlow(Location srcLocation,
			Location dstLocation, Set<IData> dataflow) {
		// TODO Auto-generated method stub
		_logger.error("PmpProcessor DUMMY Implementation");
		_logger.error("informRemoteDataFlow method invoked");
		return null;
	}
	
}
