package de.tum.in.i22.uc.gui;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.LinkedList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.datatypes.interfaces.IEvent;
import de.tum.in.i22.uc.cm.factories.IMessageFactory;
import de.tum.in.i22.uc.cm.factories.MessageFactoryCreator;
import de.tum.in.i22.uc.cm.interfaces.IPep2Pdp;

public class PlayingClient {

	private LinkedList<IEvent> ll = new LinkedList<IEvent>();
	private IMessageFactory mf = MessageFactoryCreator.createMessageFactory();
	private static Logger log = LoggerFactory.getLogger(PlayingClient.class);
	private IPep2Pdp pdp = null;

	public PlayingClient() {
		ll.clear();
	}

	public PlayingClient(IPep2Pdp pdp) {
		ll.clear();
		this.pdp = pdp;
	}

	public LinkedList<IEvent> getTrace() {
		return ll;
	}

	public String getTraceString() {
		String res = "";
		for (IEvent e : ll) {
			res += e + "\n";
		}
		return res;
	}

	public void loadTrace(String path) {
		try {
			log.debug("Loading trace from file " + path + " ...");
			FileInputStream fin = new FileInputStream(path);
			ObjectInputStream ois = new ObjectInputStream(fin);
			ll = (LinkedList<IEvent>) ois.readObject();
			ois.close();
			log.debug("Loading trace from file " + path + " completed !");
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	public void playTrace() {
		if (pdp == null) {
			log.error("Impossible to play to a null pdp");
			return;
		}
		if (ll.size() == 0) {
			log.error("Impossible to play empty trace");
			return;
		}

		try {
			log.debug("Playing trace to pdp " + pdp + "...");
			for (IEvent e:ll){
				if (e.isActual()) pdp.notifyEventAsync(e);
				else pdp.notifyEventSync(e);
			}
			log.debug("Playing trace to pdp " + pdp + " completed !");
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	public void setPdp(IPep2Pdp pdp){
		this.pdp=pdp;
	}
}
