package de.tum.in.i22.uc.thrift.client;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.client.Any2PdpClient;
import de.tum.in.i22.uc.cm.client.Any2PipClient;
import de.tum.in.i22.uc.cm.client.IConnectable;
import de.tum.in.i22.uc.cm.client.Pep2AnyClient;
import de.tum.in.i22.uc.cm.distribution.IPLocation;
import de.tum.in.i22.uc.cm.settings.Settings;

public class ThriftPep2AnyClient extends Pep2AnyClient implements IConnectable {

	private static Logger _logger = LoggerFactory.getLogger(ThriftPep2AnyClient.class);

	private final Any2PdpClient _pdpClient;
	private final Any2PipClient _pipClient;

	public ThriftPep2AnyClient(IPLocation pdpLocation, IPLocation pipLocation) {
		_pdpClient = new ThriftClientFactory().createPdpClientHandler(pdpLocation);
		_pipClient = new ThriftClientFactory().createPipClientHandler(pipLocation);
	}

	@Override
	public void connect() throws IOException {
		_pdpClient.connect();
		_pipClient.connect();
		_pdp = _pdpClient;
		_pip = _pipClient;
	}

	@Override
	public void disconnect() {
		_pdp = null;
		_pip = null;
	}


	public boolean connect(int maxTries) {
		int sleep = Settings.getInstance().getConnectionAttemptInterval();

		boolean pdpConnected = false;
		boolean pipConnected = false;

		while (maxTries-- > 0 && (!pdpConnected || !pipConnected)) {
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

			if (!pdpConnected || !pipConnected) {
				try {
					Thread.sleep(sleep);
				} catch (InterruptedException e) {}
			}
		}

		return pdpConnected && pipConnected;
	}
}
