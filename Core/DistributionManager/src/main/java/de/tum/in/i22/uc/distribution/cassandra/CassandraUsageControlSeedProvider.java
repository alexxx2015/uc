package de.tum.in.i22.uc.distribution.cassandra;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.cassandra.config.Config;
import org.apache.cassandra.config.DatabaseDescriptor;
import org.apache.cassandra.locator.SeedProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.settings.Settings;


/**
 *
 * @author Florian Kelbert
 *
 */
public class CassandraUsageControlSeedProvider implements SeedProvider {
	private static final Logger _logger = LoggerFactory.getLogger(CassandraUsageControlSeedProvider.class);

	private String _seedsValue;

	public CassandraUsageControlSeedProvider(Map<String, String> args) {
		Config conf;
        try {
            conf = DatabaseDescriptor.loadConfig();
        }
        catch (Exception e)
        {
            throw new AssertionError(e);
        }

        _seedsValue = conf.seed_provider.parameters.get("seeds");
	}

	public CassandraUsageControlSeedProvider() {
		_seedsValue = Settings.getInstance().getDistributionSeedFile();
	}

	@Override
	public List<InetAddress> getSeeds() {
        File seedFile = new File(_seedsValue);

        boolean fileIntact = false;

        if (!seedFile.exists()) {
        	_logger.info("Provided seed file (" + seedFile.getAbsolutePath() + ") does not exists.");
        }
        else if (seedFile.isDirectory()) {
			_logger.info("Provided seed file (" + seedFile.getAbsolutePath() + ") is a directory.");
		}
        else if (!seedFile.canRead()) {
        	_logger.info("Provided seed file (" + seedFile.getAbsolutePath() + ") can not be read.");
        }
        else if (seedFile.length() == 0) {
        	_logger.info("Provided seed file (" + seedFile.getAbsolutePath() + ") is empty.");
        }
        else {
        	fileIntact = true;
        }

        String line;

        if (fileIntact) {
        	StringBuffer sb = new StringBuffer();
	        try {
				for (String l : Files.readAllLines(seedFile.toPath())) {
					sb.append(l + ",");
				}
			} catch (IOException e) {
				_logger.warn("Error reading file {}.", seedFile.getAbsolutePath());
			}
	        line = sb.toString();
		}
		else {
			// Maybe we just read a default comma-separated list
			// of seeds from the configuration file. Try.
			line = _seedsValue;
		}

		List<InetAddress> seeds = new LinkedList<>();
        for (String host : line.split(",")) {
			try {
				seeds.add(InetAddress.getByName(host.trim()));
				_logger.info("Added seed {}.", host.trim());
			} catch (UnknownHostException e) {
				_logger.warn("Unable to lookup seed {}.", host);
            }
    	}

        if (seeds.isEmpty()) {
        	InetAddress localhost;
			try {
				localhost = InetAddress.getLocalHost();
	        	_logger.warn("No seeds found. Adding localhost ({}) as the only seed.", localhost.toString());
				seeds.add(localhost);
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
        }

        return seeds;
	}
}
