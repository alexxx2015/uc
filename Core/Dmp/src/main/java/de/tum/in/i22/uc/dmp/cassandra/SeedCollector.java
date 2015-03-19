package de.tum.in.i22.uc.dmp.cassandra;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.settings.Settings;


/**
 *
 * @author Florian Kelbert
 *
 */
class SeedCollector {
	protected static final Logger _logger = LoggerFactory.getLogger(SeedCollector.class);

	private final Set<String> _seeds;
	private final File _file;

	SeedCollector() {
		_seeds = Collections.synchronizedSet(new HashSet<>());
		_file = new File(Settings.getInstance().getDistributionSeedFile());

		if (_file.isDirectory()) {
			throw new RuntimeException("Provided seed (" + _file.getAbsolutePath() + ") file exists, but is a directory.");
		}

		new CassandraUsageControlSeedProvider().getSeeds().forEach(s -> _seeds.add(s.getHostAddress()));
	}

	void add(Set<String> locs) {
		locs.forEach(l -> _seeds.add(l));
		writeOut();
	}

	void add(String loc) {
		add(Collections.singleton(loc));
	}

	void remove(String loc) {
		_seeds.remove(loc);
		writeOut();
	}

	private void writeOut() {
		try {
			_file.delete();
			_file.createNewFile();

			StringBuffer sb = new StringBuffer(_seeds.size() * 20);
			synchronized (_seeds) {
				for (String s : _seeds) {
					sb.append(s + ",");
				}
			}
			sb.deleteCharAt(sb.length() - 1);

			BufferedWriter writer = new BufferedWriter(new FileWriter(_file.getAbsolutePath()));
			writer.write(sb.toString());
			writer.close();
		}
		catch (IOException e) {
			_logger.warn("Unable to write seeds to file {}.", _file);
		}
	}
}
