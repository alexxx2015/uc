package de.tum.in.i22.uc.thrift.generator;

import java.util.Objects;

public class ThriftMethod {
	private final String _signature;
	private final String _definingClass;
	private final String _originalSignature;

	public ThriftMethod(String signature, String definingClass, String originalSignature) {
		_signature = signature;
		_definingClass = definingClass;
		_originalSignature = originalSignature;
	}

	public String getSignature() {
		return _signature;
	}

	public String getDefiningClass() {
		return _definingClass;
	}

	public String getOriginalSignature() {
		return _originalSignature;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ThriftMethod) {
			return Objects.equals(_signature, ((ThriftMethod) obj)._signature);
		}
		return super.equals(obj);
	}

	@Override
	public int hashCode() {
		return Objects.hash(_signature);
	}

	@Override
	public String toString() {
		return _signature;
	}
}
