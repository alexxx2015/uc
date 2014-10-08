package de.tum.in.i22.uc.thrift.client;

import java.util.List;

import org.apache.thrift.TException;

import de.tum.in.i22.uc.cm.datatypes.interfaces.IEvent;
import de.tum.in.i22.uc.cm.interfaces.IPip2JPip;
import de.tum.in.i22.uc.thrift.ThriftConverter;
import de.tum.in.i22.uc.thrift.types.TPip2JPip;

class ThriftAny2JPipImpl implements IPip2JPip {

	private final TPip2JPip.Client _handle;

	public ThriftAny2JPipImpl(TPip2JPip.Client handle) {
		_handle = handle;
	}

	@Override
	public void notifyAsync(List<IEvent> eventList) {
		try {
			_handle.notifyAsync(ThriftConverter.toThriftEventList(eventList));
		} catch (TException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
