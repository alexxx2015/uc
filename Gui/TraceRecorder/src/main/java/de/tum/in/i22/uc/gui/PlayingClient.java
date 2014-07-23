package de.tum.in.i22.uc.gui;

import java.util.LinkedList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.datatypes.interfaces.IEvent;
import de.tum.in.i22.uc.cm.factories.IMessageFactory;
import de.tum.in.i22.uc.cm.factories.MessageFactoryCreator;
import de.tum.in.i22.uc.cm.interfaces.IPep2Pdp;
import de.tum.in.i22.uc.thrift.types.TobiasEvent;

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

	public void setTrace(LinkedList<IEvent> ll) {
		this.ll=ll;
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
			log.debug("Playing trace ("+ll.size()+") to pdp " + pdp + "...");
			for (IEvent e:ll){
				log.trace("Playing "+e+" to " + pdp);
				
//				if (e instanceof TobiasEvent){
//					log.trace("TobiasEvent");
//					if (e.isActual()) pdp.processEventAsync(e);
//					else pdp.processEventSync(e);	
//				} else {
//					log.trace("IEvent");
				if (e.isActual()) pdp.notifyEventAsync(e);
				else pdp.notifyEventSync(e);
//				}
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
