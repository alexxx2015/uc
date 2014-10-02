package de.tum.in.i22.uc.pip.extensions.javapip;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic.EStatus;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IEvent;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.cm.distribution.IPLocation;
import de.tum.in.i22.uc.cm.distribution.Location;
import de.tum.in.i22.uc.cm.distribution.client.Pip2JPipClient;
import de.tum.in.i22.uc.thrift.client.ThriftClientFactory;

/**
 * Manager for Java Pips.
 * 
 * 
 * @author Lovat
 * 
 */

public class JavaPipManager implements Runnable {
	private HashMap<String, String> _filter;
	private HashMap<String, updaterThread> _pool;

	private BlockingQueue<IEvent> _masterQueue;

	public JavaPipManager() {
		_filter = new HashMap<String, String>();
		_pool = new HashMap<String, updaterThread>();

		_masterQueue = new LinkedBlockingQueue<>();
	}


	public BlockingQueue<IEvent> getMasterQueue() {
		return _masterQueue;
	}

	private  void addListener(IEvent ev) {
		//TODO: sanitize inputs
		Map<String, String> pars=ev.getParameters();
		addListener(pars.get("ip"), Integer.valueOf(pars.get("port")), pars.get("id"), pars.get("filter"));
	}
	
	private IStatus addListener(String ip, int port, String id, String filter) {
		// TODO: sanitize inputs

		Location loc = new IPLocation(ip, port);
		updaterThread newUpdater = new updaterThread(loc, filter);

		_pool.put(id, newUpdater);
		_filter.put(id, filter);

		Thread t = new Thread(newUpdater);
		t.start();

		return new StatusBasic(EStatus.OKAY);
	}

	
	
	private void setUpdateFrequency(IEvent ev) {
		Map<String, String> pars=ev.getParameters();
		setUpdateFrequency(Integer.valueOf(pars.get("msec")), pars.get("id"));
	}	
	
	private IStatus setUpdateFrequency(int msec, String id) {
		// TODO: sanitize inputs
		_pool.get(id).setFrequency(msec);
		return new StatusBasic(EStatus.OKAY);
	}


	private void updateQueues(IEvent ev) {
		//check which queues this event would be added to
		// add it
		for (String id : _pool.keySet()){
			if (eventMatchesFilter(ev, _filter.get(id))){
				try {
					_pool.get(id).getQueue().put(ev);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	
	
	}
	
	private boolean eventMatchesFilter(IEvent ev, String filter){
		//TODO: Sanitize inputs
		if (filter.startsWith("PID")){
			return ev.getParameters().get("PID").equals(filter.split(":")[1]);
		}
		return false;
	}

	
	
	public void run() {
		try {
			while (true) {
				IEvent ev = (IEvent)_masterQueue.take();
				switch (ev.getName()){
				case "AddListener": 
					addListener(ev);
					break;
				case "SetUpdateFrequency":
					setUpdateFrequency(ev);
					break;
				case "Sink":
				case "Source":
					updateQueues(ev);
					break;
				default:
					throw new RuntimeException("Wrogn event sent to JAVA PIP MANAGER");
				}
			}
		} catch (InterruptedException ex) {
			ex.printStackTrace();
		}
	}

	protected class updaterThread implements Runnable {
		private final BlockingQueue<IEvent> _queue;
		private final Location _loc;
		private final String _filter;
		private final Pip2JPipClient _handle;

		private int _frequency = 100;

		public updaterThread(Location loc, String filter) {
			_queue = new LinkedBlockingQueue<IEvent>();
			_loc = loc;
			_filter = filter;

			ThriftClientFactory tf = new ThriftClientFactory();
			_handle = tf.createPip2JPipClient(_loc);
		}

		@Override
		public void run() {
			while (true) {
				Iterator<IEvent> i = _queue.iterator();
				List<IEvent> eventList = new LinkedList<IEvent>();

				synchronized (_queue) {
					while (i.hasNext()) {
						eventList.add((IEvent) i.next());
					}
					_queue.clear();
				}

				send(eventList);

				try {
					Thread.sleep(_frequency);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		public void send(List<IEvent> eventList) {
			_handle.notifyAsync(eventList);
		}

		public BlockingQueue<IEvent> getQueue() {
			return _queue;
		}

		public Location getLoc() {
			return _loc;
		}

		public String getFilter() {
			return _filter;
		}

		public int getFrequency() {
			return _frequency;
		}

		public void setFrequency(int frequency) {
			this._frequency = frequency;
		}

	}

}
