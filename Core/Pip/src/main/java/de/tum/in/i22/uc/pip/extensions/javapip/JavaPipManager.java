package de.tum.in.i22.uc.pip.extensions.javapip;

import java.util.HashMap;
import java.util.concurrent.BlockingQueue;

import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic.EStatus;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IEvent;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.cm.distribution.IPLocation;
import de.tum.in.i22.uc.cm.distribution.Location;


/**
 * Manager for Java Pips.
 * 
 * 
 * @author Lovat
 *
 */


public class JavaPipManager implements Runnable {
	private HashMap<String, Location> _location;
	private HashMap<String, String> _filter;
	private HashMap<String, Integer> _frequency; 
	
	public JavaPipManager() {
		_location = new HashMap<String, Location>();
		_filter = new HashMap<String, String>();
	}

	public IStatus addListener(String ip, int port, String id, String filter) {
		//TODO: sanitize inputs
		
		Location loc = new IPLocation(ip, port);
		_location.put(id, loc);
		_filter.put(id, filter);
		return new StatusBasic(EStatus.OKAY);
	}

	public IStatus setUpdateFrequency(int msec, String id) {
		//TODO: sanitize inputs
		
		_frequency.put(id, msec);
		return new StatusBasic(EStatus.OKAY);
	}
	
	public void run(){
		
	}
		
	
}
