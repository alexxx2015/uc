package de.tum.in.i22.uc.thrift.generator;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class ThriftService {
	private final String _name;
	private final List<ThriftMethod> _methods;
	private final String _originalIface;

	public ThriftService(String name, String originalIface) {
		_name = name;
		_originalIface = originalIface;
		_methods = new LinkedList<>();
	}

	public void add(ThriftMethod method) {
		if (!_methods.contains(method)) {
			_methods.add(method);
		}
	}

	public String getOriginalIface() {
		return _originalIface;
	}

	public List<ThriftMethod> getMethods() {
		return Collections.unmodifiableList(_methods);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ThriftService) {
			return Objects.equals(_name, ((ThriftService) obj)._name);
		}
		return super.equals(obj);
	}

	@Override
	public int hashCode() {
		return _name.hashCode();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("/*" + System.lineSeparator());
		sb.append(" * Thrift interfaces defined by" + System.lineSeparator());
		sb.append(" * " + _originalIface + System.lineSeparator());
		sb.append(" */" + System.lineSeparator());
		sb.append("service " + _name + " {" + System.lineSeparator());
		for (int i = 0; i < _methods.size(); i++) {
			ThriftMethod m = _methods.get(i);
			sb.append("\t// " + m.getOriginalSignature() + System.lineSeparator());
			sb.append("\t" + m.getSignature() + (i == _methods.size() - 1 ? "" : ("," + System.lineSeparator())));
			sb.append(System.lineSeparator());
		}
		sb.append("}" + System.lineSeparator());
		return sb.toString();
	}
}
