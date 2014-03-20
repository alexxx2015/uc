package de.tum.in.i22.uc.cm.datatypes.Linux;

import java.util.Objects;

import de.tum.in.i22.uc.cm.basic.NameBasic;

/**
 * Class representing a filename.
 * Notably, a filename is independent of processes,
 * which is why no process id is involved here.
 * This class corresponds to set F_{fn} in NSS'09 paper.
 *
 * @author Florian Kelbert
 *
 */
public class FilenameName extends NameBasic {

	private static final String PREFIX_FILE = "FILE_";

	private final String _host;
	private final String _filename;

	private FilenameName(String host, String filename, String name) {
		super(name);

		_host = host;
		_filename = filename;
	}

	public static FilenameName create(String host, String filename) {
		return new FilenameName(host, filename, PREFIX_FILE + host + "." + filename);
	}

	public String getHost() {
		return _host;
	}

	public String getFilename() {
		return _filename;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof FilenameName) {
			FilenameName o = (FilenameName) obj;
			return Objects.equals(_host, o._host)
					&& Objects.equals(_filename, o._filename);
		}
		return super.equals(obj);
	}



	@Override
	public int hashCode() {
		return Objects.hash(_host, _filename);
	}

	@Override
	public String toString() {
		return com.google.common.base.Objects.toStringHelper(this)
				.add("_host", _host)
				.add("_filename", _filename)
				.toString();
	}
}