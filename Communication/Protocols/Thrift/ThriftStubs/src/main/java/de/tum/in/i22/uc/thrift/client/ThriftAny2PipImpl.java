package de.tum.in.i22.uc.thrift.client;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.datatypes.basic.ConflictResolutionFlagBasic.EConflictResolution;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IChecksum;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IContainer;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IData;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IEvent;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IName;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IPipDeployer;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.cm.interfaces.IAny2Pip;
import de.tum.in.i22.uc.thrift.ThriftConverter;
import de.tum.in.i22.uc.thrift.types.TAny2Pip;
import de.tum.in.i22.uc.thrift.types.TData;

class ThriftAny2PipImpl implements IAny2Pip {
	protected static final Logger _logger = LoggerFactory.getLogger(ThriftAny2PipImpl.class);

	private final TAny2Pip.Client _handle;

	public ThriftAny2PipImpl(TAny2Pip.Client handle) {
		_handle = handle;
	}

	@Override
	public boolean evaluatePredicateSimulatingNextState(IEvent event, String predicate) {
		try {
			return _handle.evaluatePredicateSimulatingNextState(ThriftConverter.toThrift(event), predicate);
		} catch (TException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}


	@Override
	public boolean evaluatePredicateCurrentState(String predicate) {
		try {
			return _handle.evaluatePredicatCurrentState(predicate);
		} catch (TException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}


	@Override
	public Set<IContainer> getContainersForData(IData data) {
		try {
			return ThriftConverter.fromThriftContainerSet(_handle.getContainerForData(ThriftConverter.toThrift(data)));
		} catch (TException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}


	@Override
	public Set<IData> getDataInContainer(IName containerName) {
		try {
			return ThriftConverter.fromThriftDataSet(_handle.getDataInContainer(ThriftConverter.toThrift(containerName)));
		} catch (TException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}


	@Override
	public IStatus update(IEvent event) {
		try {
			return ThriftConverter.fromThrift(_handle.update(ThriftConverter.toThrift(event)));
		} catch (TException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}


	@Override
	public IStatus startSimulation() {
		try {
			return ThriftConverter.fromThrift(_handle.startSimulation());
		} catch (TException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}


	@Override
	public IStatus stopSimulation() {
		try {
			return ThriftConverter.fromThrift(_handle.stopSimulation());
		} catch (TException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}


	@Override
	public boolean isSimulating() {
		try {
			return _handle.isSimulating();
		} catch (TException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	@Override
	public IStatus initialRepresentation(IName containerName, Set<IData> data) {
		try {
			return ThriftConverter.fromThrift(_handle.initialRepresentation(ThriftConverter.toThrift(containerName), ThriftConverter.toThriftDataSet(data)));
		} catch (TException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	@Override
	public IData newInitialRepresentation(IName containerName) {
		try {
			return ThriftConverter.fromThrift(_handle.newInitialRepresentation(ThriftConverter.toThrift(containerName)));
		} catch (TException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	@Override
	public IStatus updateInformationFlowSemantics(IPipDeployer deployer, File jarFile, EConflictResolution conflictResolutionFlag) {
		// TODO Auto-generated method stub
		// not yet supported by thrift interface
		return null;
	}

	@Override
	public IData newStructuredData(Map<String, Set<IData>> structure) {
		try {
			Map<String, Set<TData>> map = new HashMap<String, Set<TData>>();
			for (String s: structure.keySet()){
				map.put(s, ThriftConverter.toThriftDataSet(structure.get(s)));
			}
			return ThriftConverter.fromThrift(_handle.newStructuredData(map));
		} catch (TException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	@Override
	public Map<String, Set<IData>> getStructureOf(IData data) {
		try {
			Map<String, Set<TData>> map = _handle.getStructureOf(ThriftConverter.toThrift(data));
			Map<String, Set<IData>> res = new HashMap<String,Set<IData>>();
			for (String s: map.keySet()){
				res.put(s, ThriftConverter.fromThriftDataSet(map.get(s)));
			}
			return res;
		} catch (TException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	@Override
	public Set<IData> flattenStructure(IData data){
		try {
			return ThriftConverter.fromThriftDataSet(_handle.flattenStructure(ThriftConverter.toThrift(data)));
		} catch (TException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public IData getDataFromId(String id) {
		try {
			return ThriftConverter.fromThrift(_handle.getDataFromId(id));
		} catch (TException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public IStatus addJPIPListener(String ip, int port, String id, String filter) {
		try {
			return ThriftConverter.fromThrift(_handle.addJPIPListener(ip, port, id, filter));
		} catch (TException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public IStatus setUpdateFrequency(int msec, String id) {
		try {
			return ThriftConverter.fromThrift(_handle.setUpdateFrequency(msec, id));
		} catch (TException e) {
			e.printStackTrace();
		}		
		return null;
	}

	@Override
	public boolean newChecksum(IData data, IChecksum checksum, boolean overwrite) {
		try {
			return _handle.newChecksum(ThriftConverter.toThrift(data),ThriftConverter.toThrift(checksum),overwrite);
		} catch (TException e) {
			throw new RuntimeException(e.getMessage(), e);
		}	
	}

	@Override
	public IChecksum getChecksumOf(IData data) {
		try {
			return ThriftConverter.fromThrift(_handle.getChecksumOf(ThriftConverter.toThrift(data)));
		} catch (TException e) {
			throw new RuntimeException(e.getMessage(), e);
		}		
	}

	@Override
	public boolean deleteChecksum(IData d) {
		try {
			return _handle.deleteChecksum(ThriftConverter.toThrift(d));
		} catch (TException e) {
			throw new RuntimeException(e.getMessage(), e);
		}				
	}

	@Override
	public boolean deleteStructure(IData d) {
		try {
			return _handle.deleteStructure(ThriftConverter.toThrift(d));
		} catch (TException e) {
			throw new RuntimeException(e.getMessage(), e);
		}				
	}
}
