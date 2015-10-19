package de.tum.in.i22.uc.pdp;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.datatypes.basic.EventBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.PxpSpec;
import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic.EStatus;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IEvent;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.cm.distribution.IPLocation;
import de.tum.in.i22.uc.cm.distribution.client.Any2PxpClient;
import de.tum.in.i22.uc.pdp.core.ExecuteAction;
import de.tum.in.i22.uc.thrift.client.ThriftClientFactory;

import com.datastax.driver.core.ConsistencyLevel;
/**
 * This class manages the connection with the Pxp. It handles the registrations
 * of Pxp components and it dispatches the to-be-executed action to the proper
 * Pxp.
 *
 * @author Enrico Lovat
 *
 */

public class PxpManager {
	private static Logger _logger = LoggerFactory.getLogger(PxpManager.class);

	private static HashMap<String, PxpSpec> pxpSpec = new HashMap<>();

	public boolean execute(ExecuteAction execAction, boolean synchronous) {
		if (!synchronous && execAction.getProcessor().toLowerCase().equals("pep")) {
			_logger.warn(
					"Execution of asynchronous executeAction [{}] not possible with processor PEP",
					execAction.getName());
			return false;
		}

		_logger.info("Executing {}synchronous action {} with parameters: {}",
				(synchronous == true ? "" : "a"), execAction.getName(), execAction.getParameters());

		String pxpId = execAction.getPxpId();
		IStatus res = null;
		if (pxpId != null) {
			PxpSpec pxp = pxpSpec.get(pxpId);
			if (pxp != null) {

				try {
					Any2PxpClient client = new ThriftClientFactory().createAny2PxpClient(new IPLocation(pxp.getIp(), pxp.getPort()));

					try {
						client.connect();
					} catch (Exception e) {
						throw new RuntimeException(e.getMessage(), e);
					}

					List<IEvent> listOfEventsToBeExecuted = new LinkedList<>();
					listOfEventsToBeExecuted.add(new EventBasic(execAction.getName(), execAction.getParameters()));

					if (synchronous) {
						res = client.executeSync(listOfEventsToBeExecuted);
					}
					else {
						client.executeAsync(listOfEventsToBeExecuted);
					}

				} catch (NumberFormatException e) {
					e.printStackTrace();
				}
			}
		}

		return res != null ? res.isStatus(EStatus.OKAY) : false;
	}

	public boolean registerPxp(PxpSpec pxp) {
		boolean b = false;
		if (!pxpSpec.containsKey(pxp.getId())) {
			b = pxpSpec.put(pxp.getId(), pxp) == null;
			_logger.info("PXP " + pxp.getId() + " registered.");
		}
		return b;
	}
}
