package de.tum.in.i22.uc.dmp.cassandra;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datastax.driver.core.AtomicMonotonicTimestampGenerator;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.ConsistencyLevel;
import com.datastax.driver.core.QueryOptions;
import com.google.common.collect.Sets;

import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic.EStatus;
import de.tum.in.i22.uc.cm.datatypes.basic.XmlPolicy;
import de.tum.in.i22.uc.cm.datatypes.interfaces.AtomicOperator;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IData;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IName;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IOperator;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.cm.datatypes.linux.SocketContainer;
import de.tum.in.i22.uc.cm.distribution.IPLocation;
import de.tum.in.i22.uc.cm.distribution.Threading;
import de.tum.in.i22.uc.cm.distribution.client.Dmp2DmpClient;
import de.tum.in.i22.uc.cm.distribution.client.Pdp2PepClient;
import de.tum.in.i22.uc.cm.factories.IClientFactory;
import de.tum.in.i22.uc.cm.interfaces.IDmp2Pip;
import de.tum.in.i22.uc.cm.interfaces.IDmp2Pmp;
import de.tum.in.i22.uc.cm.pip.RemoteDataFlowInfo;
import de.tum.in.i22.uc.cm.processing.DmpProcessor;
import de.tum.in.i22.uc.cm.settings.Settings;
import de.tum.in.i22.uc.thrift.client.ThriftClientFactory;


/**
 *
 * @author Florian Kelbert
 *
 */
public class CassandraDmp extends DmpProcessor {
	protected static final Logger _logger = LoggerFactory.getLogger(CassandraDmp.class);

	static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ");

	private IDmp2Pip _pip;
	private IDmp2Pmp _pmp;

	private boolean _initialized = false;

	private final PrivateKeyspace _privateKeyspace;

	private final SharedKeyspaceManager _sharedKeyspaces;

	/**
	 * Maps the IP of a PEP to its responsible PDP.
	 */
	private final Map<String, IPLocation> _responsiblePdps;

	private final SeedCollector _seedcollector;

	private final Cluster _cluster;

	private final IClientFactory _clientFactory;

	private final Map<IPLocation,Set<IData>> knowsData;

	public CassandraDmp() {
		/*
		 * Default consistency level.
		 */
		QueryOptions options = new QueryOptions().setConsistencyLevel(ConsistencyLevel.QUORUM);

		knowsData = new HashMap<>();

		InetAddress addr = null;
		try {
			addr = InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
			_logger.error("Unable to retrieve hostname: {}.", e.getMessage());
			throw new RuntimeException(e);
		}

		_cluster = Cluster.builder()
							.withQueryOptions(options)
							.addContactPoint(addr.getHostAddress())
							.withTimestampGenerator(new AtomicMonotonicTimestampGenerator())
							.build();

		_responsiblePdps = new ConcurrentHashMap<>();
		_privateKeyspace = new PrivateKeyspace(_cluster);
		_sharedKeyspaces = new SharedKeyspaceManager(_cluster);
		_seedcollector = new SeedCollector();
		_clientFactory = new ThriftClientFactory();
	}


	@Override
	public void init(IDmp2Pip pip, IDmp2Pmp pmp) {
		if (_initialized) {
			throw new RuntimeException("Was already initialized");
		}

		_pip = pip;
		_pmp = pmp;

		/*
		 * Get the policies stored in the private keyspace
		 * and deploy them.
		 * For some reason, this won't work if parallelized.
		 */
		_privateKeyspace.getPolicies().forEach(p -> _pmp.deployPolicyRawXMLPmp(p));

		_initialized = true;
	}

	/**
	 * Perform cross-system data flow tracking on a per-location granularity, i.e.
	 * remember which locations are aware of which data.
	 *
	 * @param data the set of data that has flown
	 * @param dstLocation the location to which the specified data has flown
	 */
	private void doCrossSystemDataTrackingCoarse(Set<IData> data, IPLocation dstLocation) {
		_logger.debug("doCrossSystemDataTrackingCoarse invoked: " + dstLocation + " -> " + data);

		data.forEach(d -> {
			_pmp.getPolicies(d).forEach(p -> {
				_sharedKeyspaces.get(p.getName()).addData(d, dstLocation);
			});
		});
	}



	@Override
	public void doDataTransfer(RemoteDataFlowInfo dataflow) {
		_logger.info("dataTransfer: " + dataflow);

		for (Entry<SocketContainer, Set<IData>> dst : dataflow.getFlows().entrySet()) {

			SocketContainer dstSocket = dst.getKey();
			Set<IData> data = dst.getValue();

			IPLocation dmpLocation = new IPLocation(dstSocket.getResponsibleLocation().getHost(), Settings.getInstance().getDmpListenerPort());

			Set<IData> known = knowsData.get(dmpLocation);

			if (known == null || Sets.intersection(data, known).isEmpty()) {
				Set<XmlPolicy> policies = getAllPolicies(data);

				Dmp2DmpClient remoteDmp = _clientFactory.createDmp2DmpClient(dmpLocation);

				boolean success = true;

				doCrossSystemDataTrackingCoarse(data, dmpLocation);
				try {
					remoteDmp.connect();
					remoteDmp.remoteTransfer(policies, IPLocation.localIpLocation.getHost(), dstSocket.getSocketName(), data);
				} catch (IOException e) {
					success = false;
					_logger.error("Unable to perform remote data and policy transfer to [" + dmpLocation + "]: " + e.getMessage());
				} finally {
					remoteDmp.disconnect();
				}

				if (success) {
					// On success, the destination is a new seed node
					_seedcollector.add(dmpLocation.getHost());

					if (known == null) {
						known = new HashSet<>();
						knowsData.put(dmpLocation, known);
					}
					known.addAll(data);
				}
			}
		}
	}


	private Set<XmlPolicy> getAllPolicies(Set<IData> data) {
		if (data == null || data.size() == 0) {
			return Collections.emptySet();
		}

		Set<XmlPolicy> policies = new HashSet<>();

		data.forEach(d -> policies.addAll(_pmp.getPolicies(d)));

		return policies;
	}

	@Override
	public void register(XmlPolicy policy) {
		register(policy, null);
	}


	private void register(XmlPolicy policy, String from) {
		ISharedKeyspace ks = _sharedKeyspaces.create(policy);
		ks.enlargeBy(IPLocation.localIpLocation);

		_privateKeyspace.add(policy);
		_seedcollector.add(_sharedKeyspaces.get(policy.getName()).getLocations());
		if (from != null) {
			_seedcollector.add(from);
		}
	}

	@Override
	public void deregister(String policyName, IPLocation location) {
		_sharedKeyspaces.get(policyName).diminishBy(location);
		_privateKeyspace.delete(policyName);
	}

	@Override
	public void setFirstTick(String policyName, String mechanismName, long firstTick) {
		_sharedKeyspaces.get(policyName).setFirstTick(mechanismName, firstTick);
	}

	@Override
	public void notify(IOperator operator, boolean endOfTimestep) {
		_sharedKeyspaces.get(operator).notify(operator, endOfTimestep);
	}

	@Override
	public boolean wasNotifiedAtTimestep(AtomicOperator operator, long timestep) {
		return _sharedKeyspaces.get(operator).wasNotifiedAtTimestep(operator, timestep);
	}

	@Override
	public int howOftenNotifiedAtTimestep(AtomicOperator operator, long timestep) {
		return _sharedKeyspaces.get(operator).howOftenNotifiedAtTimestep(operator, timestep);
	}

	@Override
	public int howOftenNotifiedSinceTimestep(AtomicOperator operator, long timestep) {
		return _sharedKeyspaces.get(operator).howOftenNotifiedSinceTimestep(operator, timestep);
	}

	@Override
	public long getFirstTick(String policyName, String mechanismName) {
		return _sharedKeyspaces.get(policyName).getFirstTick(mechanismName);
	}

	@Override
	public IPLocation getResponsibleLocation(String ip) {
		IPLocation result = _responsiblePdps.get(ip);

		if (result == null) {
			IPLocation loc = new IPLocation(ip, Settings.getInstance().getPepListenerPort());
			Pdp2PepClient pdp2pep = _clientFactory.createPdp2PepClient(loc);
			try {
				_logger.debug("Asking remotely for PdpLocation at {}.", loc);

				Future<?> futureCon = Threading.instance().submit(() -> {
					try {
						pdp2pep.connect();
					} catch (Exception e) {}
				}, null);

				// Timeout, if the requested PEP does not answer
				futureCon.get(200, TimeUnit.MILLISECONDS);

				result = pdp2pep.getResponsiblePdpLocation();
			} catch (Exception e) {
				result = new IPLocation(ip);
				_logger.warn("Unable to connect to {}.", loc);
				_logger.warn("Assuming a responsible location of {}.", ip);
			}
			finally {
				pdp2pep.disconnect();
			}
		}

		_responsiblePdps.put(ip, result);

		return result;
	}


	@Override
	public IStatus remoteTransfer(Set<XmlPolicy> policies, String fromHost, IName containerName, Set<IData> data) {
		_logger.debug("remoteTransfer({}, {}, {}, {})", policies, fromHost, containerName, data);
		_pip.initialRepresentation(containerName, data);
		policies.forEach(p -> {
			_pmp.deployPolicyXMLPmp(p);
			register(p, fromHost);
		});
		return new StatusBasic(EStatus.OKAY);
	}

//	public void awaitPolicyTransfer(String policyName) {
//		while (!SharedKeyspace.existsPhysically(_cluster, policyName)) {
//			_logger.info("Waiting for keyspace {} to be physically available.", policyName);
//			try {
//				Thread.sleep(100);
//			} catch (InterruptedException e) {
//			}
//		};
//	}
}
