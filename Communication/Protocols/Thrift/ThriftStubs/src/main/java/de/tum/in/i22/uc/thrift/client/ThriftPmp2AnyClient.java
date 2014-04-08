package de.tum.in.i22.uc.thrift.client;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.client.Any2PdpClient;
import de.tum.in.i22.uc.cm.client.Any2PipClient;
import de.tum.in.i22.uc.cm.client.Any2PmpClient;
import de.tum.in.i22.uc.cm.client.IConnectable;
import de.tum.in.i22.uc.cm.client.Pmp2AnyClient;
import de.tum.in.i22.uc.cm.distribution.IPLocation;
import de.tum.in.i22.uc.cm.settings.Settings;

public class ThriftPmp2AnyClient extends Pmp2AnyClient implements IConnectable {

	private static Logger _logger = LoggerFactory.getLogger(ThriftPmp2AnyClient.class);

	private final Any2PdpClient _pdpClient;
	private final Any2PipClient _pipClient;
	private final Any2PmpClient _pmpClient;

	public ThriftPmp2AnyClient(IPLocation pdpLoc, IPLocation pipLoc, IPLocation pmpLoc) {
		_pdpClient = new ThriftClientFactory().createPdpClientHandler(pdpLoc);
		_pipClient = new ThriftClientFactory().createPipClientHandler(pipLoc);
		_pmpClient = new ThriftClientFactory().createPmpClientHandler(pmpLoc);
	}

	@Override
	public void connect() throws IOException {
		_pdpClient.connect();
		_pipClient.connect();
		_pmpClient.connect();
		_pdp = _pdpClient;
		_pip = _pipClient;
		_pmp = _pmpClient;
	}

	@Override
	public void disconnect() {
		_pdp = null;
		_pip = null;
		_pmp = null;
	}


	public boolean connect(int maxTries) {
		int sleep = Settings.getInstance().getConnectionAttemptInterval();

		boolean pdpConnected = false;
		boolean pipConnected = false;
		boolean pmpConnected = false;

		while (maxTries-- > 0 && (!pdpConnected || !pipConnected || !pmpConnected)) {
			try {
				if (!pdpConnected) {
					_pdpClient.connect();
					_pdp = _pdpClient;
					pdpConnected = true;
					_logger.info("Connection to PDP succeeded");
				}
			} catch (IOException e) {
				_logger.info("Connection to PDP failed. Trying again in " + sleep + "ms.");
			}

			try {
				if (!pipConnected) {
					_pipClient.connect();
					_pip = _pipClient;
					pipConnected = true;
					_logger.info("Connection to PIP succeeded");
				}
			} catch (IOException e) {
				_logger.info("Connection to PIP failed. Trying again in " + sleep + "ms.");
			}

			try {
				if (!pmpConnected) {
					_pmpClient.connect();
					_pmp = _pmpClient;
					pmpConnected = true;
					_logger.info("Connection to PMP succeeded");
				}
			} catch (IOException e) {
				_logger.info("Connection to PMP failed. Trying again in " + sleep + "ms.");
			}

			if (!pdpConnected || !pipConnected || !pmpConnected) {
				try {
					Thread.sleep(sleep);
				} catch (InterruptedException e) {}
			}
		}

		return pdpConnected && pipConnected && pmpConnected;
	}
}
