package de.tum.in.i22.uc.pip.requests;

import java.util.Map;
import java.util.Set;

import de.tum.in.i22.uc.cm.datatypes.interfaces.IChecksum;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IData;
import de.tum.in.i22.uc.cm.processing.PipProcessor;

public class NewChecksumPipRequest extends PipRequest<Boolean> {
	private final IData _data;
	private final IChecksum _checksum;
	private final boolean _overwrite;
	
	public NewChecksumPipRequest(IData data, IChecksum checksum, boolean overwrite) {
		_data=data;
		_checksum=checksum;
		_overwrite=overwrite;
	}
		
	@Override
	public Boolean process(PipProcessor processor) {
		return processor.newChecksum(_data, _checksum, _overwrite);
	}
}
