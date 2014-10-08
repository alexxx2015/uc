import java.util.List;

import org.apache.thrift.TException;

import de.tum.in.i22.uc.thrift.ThriftConverter;
import de.tum.in.i22.uc.thrift.types.TEvent;
import de.tum.in.i22.uc.thrift.types.TPip2JPip;


public class MyJPipServer implements TPip2JPip.Iface{

	public void notifyAsync(List<TEvent> eventList) throws TException {
		// TODO Auto-generated method stub
		System.out.println("Test "+ThriftConverter.fromThriftEventList(eventList));
	}
}
