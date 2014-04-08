package de.tum.in.i22.uc.pep;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.client.PdpClientHandler;
import de.tum.in.i22.uc.cm.client.PipClientHandler;
import de.tum.in.i22.uc.cm.datatypes.EConflictResolution;
import de.tum.in.i22.uc.cm.datatypes.IEvent;
import de.tum.in.i22.uc.cm.datatypes.IPipDeployer;
import de.tum.in.i22.uc.cm.datatypes.IResponse;
import de.tum.in.i22.uc.cm.datatypes.IStatus;
import de.tum.in.i22.uc.cm.distribution.Location;
import de.tum.in.i22.uc.cm.interfaces.IPep2Any;
import de.tum.in.i22.uc.cm.settings.Settings;
import de.tum.in.i22.uc.thrift.client.ThriftClientHandlerFactory;

public class ThriftPep implements IPep2Any {

	private static Logger _logger = LoggerFactory.getLogger(ThriftPep.class);

	private final PdpClientHandler _pdp;
	private final PipClientHandler _pip;

	private final Settings _settings;

	public ThriftPep(Location pdpLocation, Location pipLocation) {
		_pdp = new ThriftClientHandlerFactory().createPdpClientHandler(pdpLocation);
		_pip = new ThriftClientHandlerFactory().createPipClientHandler(pipLocation);

		_settings = Settings.getInstance();

		int sleep = _settings.getConnectionAttemptInterval();

		boolean pdpConnected = false;
		boolean pipConnected = false;

		while (!pdpConnected || !pipConnected) {
			try {
				if (!pdpConnected) {
					_pdp.connect();
					pdpConnected = true;
					_logger.info("Connection to PDP succeeded");
				}
			} catch (IOException e) {
				_logger.info("Connection to PDP failed. Trying again in " + sleep + "ms.");
			}

			try {
				if (!pipConnected) {
					_pip.connect();
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
	}

	@Override
	public void notifyEventAsync(IEvent event) {
		_pdp.notifyEventAsync(event);
	}

	@Override
	public IResponse notifyEventSync(IEvent event) {
		return _pdp.notifyEventSync(event);
	}

	@Override
	public IStatus updateInformationFlowSemantics(IPipDeployer deployer, File jarFile, EConflictResolution flagForTheConflictResolution) {
		return _pip.updateInformationFlowSemantics(deployer, jarFile, flagForTheConflictResolution);
	}

}
