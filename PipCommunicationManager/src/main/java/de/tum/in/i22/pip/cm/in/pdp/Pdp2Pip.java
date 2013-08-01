package de.tum.in.i22.pip.cm.in.pdp;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.log4j.Logger;

import testutil.DummyMessageGen;

import de.tum.in.i22.pdp.cm.out.IPdp2Pip;
import de.tum.in.i22.pdp.datatypes.IContainer;
import de.tum.in.i22.pdp.datatypes.IData;
import de.tum.in.i22.pdp.datatypes.IEvent;
import de.tum.in.i22.pdp.datatypes.IResponse;
import de.tum.in.i22.pdp.datatypes.basic.ContainerBasic;
import de.tum.in.i22.pdp.datatypes.basic.DataBasic;

/**
 * This class should contain the real implementation of the methods
 * specified in the IPdp2Pip interface.
 * 
 * @author Stoimenov
 *
 */
public class Pdp2Pip implements IPdp2Pip {
	
	private static final Logger _logger = Logger.getLogger(Pdp2Pip.class);
	
	private static Pdp2Pip _instance = new Pdp2Pip();
	
	public static IPdp2Pip getInstance() {
		return _instance;
	}
	
	private Pdp2Pip() {
		super();
	}

	public Boolean evaluatePredicate(IEvent event, String predicate) {
		// returns always true, for testing purposes only
		//FIXME provide real implementation of the method
		return true;
	}

	public List<IContainer> getContainerForData(IData arg0) {
		// returns dummy list, for testing purposes only
		//FIXME provide real implementation of the method
		List<IContainer> list = new ArrayList<IContainer>();
		for (int i = 0; i < 6; i++) {
			IContainer container = new ContainerBasic("dummy class", null);
			list.add(container);
		}
		return list;
	}

	public List<IData> getDataInContainer(IContainer container) {
		// returns dummy list, for testing purposes only
		//FIXME provide real implementation of the method
		List<IData> list = new ArrayList<IData>();
		for (int i = 0; i < 10; i++) {
			IData data = new DataBasic(UUID.randomUUID().toString());
			list.add(data);
		}
		return list;
	}
	
	public IResponse notifyActualEvent(IEvent event) {
		_logger.debug("Notify actual event called");
		return DummyMessageGen.createResponse();
	}

}
