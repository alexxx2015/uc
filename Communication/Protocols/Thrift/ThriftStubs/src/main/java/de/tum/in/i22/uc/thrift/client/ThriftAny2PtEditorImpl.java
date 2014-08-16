package de.tum.in.i22.uc.thrift.client;

import java.util.HashSet;
import java.util.Set;

import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.datatypes.interfaces.IContainer;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.cm.interfaces.IAny2PtEditor;
import de.tum.in.i22.uc.thrift.ThriftConverter;
import de.tum.in.i22.uc.thrift.types.TAny2PtEditor;
import de.tum.in.i22.uc.thrift.types.TContainer;

class ThriftAny2PtEditorImpl implements IAny2PtEditor {
	protected static final Logger _logger = LoggerFactory.getLogger(ThriftAny2PtEditorImpl.class);

	private final TAny2PtEditor.Client _handle;

	public ThriftAny2PtEditorImpl(TAny2PtEditor.Client handle) {
		_handle = handle;
	}

	@Override
	public IStatus specifyPolicyFor(Set<IContainer> representations,
			String dataClass) {
		Set<TContainer> representationsT = new HashSet<TContainer>();
		for (IContainer cont : representations) representationsT.add(ThriftConverter.toThrift(cont));
		try {
			return ThriftConverter.fromThrift(_handle.specifyPolicyFor(representationsT, dataClass));
		} catch (TException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

}
