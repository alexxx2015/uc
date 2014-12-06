package de.tum.in.i22.uc.cm.distribution.cassandra;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Collections;
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

import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic.EStatus;
import de.tum.in.i22.uc.cm.datatypes.basic.XmlPolicy;
import de.tum.in.i22.uc.cm.datatypes.interfaces.AtomicOperator;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IData;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IOperator;
import de.tum.in.i22.uc.cm.datatypes.linux.SocketContainer;
import de.tum.in.i22.uc.cm.datatypes.linux.SocketName;
import de.tum.in.i22.uc.cm.distribution.IDistributionManager;
import de.tum.in.i22.uc.cm.distribution.IPLocation;
import de.tum.in.i22.uc.cm.distribution.Threading;
import de.tum.in.i22.uc.cm.distribution.client.Pdp2PepClient;
import de.tum.in.i22.uc.cm.distribution.client.Pip2PipClient;
import de.tum.in.i22.uc.cm.distribution.client.Pmp2PmpClient;
import de.tum.in.i22.uc.cm.pip.RemoteDataFlowInfo;
import de.tum.in.i22.uc.cm.processing.PdpProcessor;
import de.tum.in.i22.uc.cm.processing.PipProcessor;
import de.tum.in.i22.uc.cm.processing.PmpProcessor;
import de.tum.in.i22.uc.cm.settings.Settings;
import de.tum.in.i22.uc.thrift.client.ThriftClientFactory;

public class CassandraDistributionManager implements IDistributionManager {
	protected static final Logger _logger = LoggerFactory.getLogger(CassandraDistributionManager.class);

	static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ");

	private PdpProcessor _pdp;
	private PipProcessor _pip;
	private PmpProcessor _pmp;

	private boolean _initialized = false;

	private final PrivateKeyspace _privateKeyspace;

	private final SharedKeyspaceManager _sharedKeyspaces;

	/**
	 * Maps the IP of a PEP to its responsible PDP.
	 */
	private final Map<String, IPLocation> _responsiblePdps;

	private final SeedCollector _seedcollector;
	
	private final Cluster _cluster;

	public CassandraDistributionManager() {
		/*
		 * Default consistency level.
		 */
		QueryOptions options = new QueryOptions().setConsistencyLevel(ConsistencyLevel.QUORUM);

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
	}


	@Override
	public void init(PdpProcessor pdp, PipProcessor pip, PmpProcessor pmp) {
		if (_initialized) {
			throw new RuntimeException("Was already initialized");
		}

		_pdp = pdp;
		_pip = pip;
		_pmp = pmp;

		/*
		 * Get the policies stored in the private keyspace
		 * and deploy them.
		 * For some reason, this won't work if parallelized
		 */
		_privateKeyspace.getPolicies().forEach(p -> _pmp.deployPolicyRawXMLPmp(p));

		_initialized = true;
	}

	/**
	 * Transfers all specified {@link XmlPolicy}s to the specified {@link IPLocation}
	 * via Thrift interfaces.
	 *
	 * @param policies the policies to send
	 * @param pmpLocation the location to which the policies are sent
	 */
	private void doStickyPolicyTransfer(Set<XmlPolicy> policies, IPLocation pmpLocation) {
		_logger.debug("doStickyPolicyTransfer invoked: " + pmpLocation + ": " + policies);

		for (XmlPolicy policy : policies) {
			/*
			 * For each policy, the protocol is as follows:
			 * (1) extend the keyspace with the given location
			 * (2) if the keyspace was in fact extended
			 *     (meaning that the provided location was not yet aware of the policy),
			 *     then the policy is sent to the remote PMP.
			 * (3) if remote deployment of the policy fails, then the given location
			 *     is removed from the policy's keyspace
			 */

			ISharedKeyspace ks = _sharedKeyspaces.get(policy.getName());

			if (ks.enlargeBy(pmpLocation)) {
				boolean success = true;

				// if the location was not yet part of the keyspace, then we need to
				// deploy the policy at the remote location
				try {
					Pmp2PmpClient remotePmp = new ThriftClientFactory().createPmp2PmpClient(pmpLocation);
					remotePmp.connect();

					if (!remotePmp.remotePolicyTransfer(policy.getXml(), IPLocation.localIpLocation.getHost()).isStatus(EStatus.OKAY)) {
						success = false;
					}

					remotePmp.disconnect();
				} catch (IOException e) {
					success = false;
					_logger.error("Unable to deploy XML policy remotely at [" + pmpLocation + "]: " + e.getMessage());
				}


				if (success) {
					// If remote deployment succeeded, then
					// this is a new seed.
					_seedcollector.add(pmpLocation.getHost());
				}
				else {
					// If remote deployment of the policy fails,
					// then we remove the location from the keyspace
					ks.diminishBy(pmpLocation);
				}

				/*
				 * TODO: We need to deal with the fact that success == false at this place.
				 * Possible options: Retry, throw exception, cancel data transfer
				 */
			}
			else {
				_logger.debug("Not performing remote policy transfer. Policy should already be present remotely.");
			}
		}
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


	/**
	 * Perform cross-system data flow tracking on a per-container basis.
	 * The mapping between containers (more precisely: their names) and the
	 * set of data being transferred to them is specified by parameter flows.
	 *
	 * @param pipLocation the location to which the data flow occurred.
	 * @param flows maps the destination container name to set of data it is receiving
	 */
	private void doCrossSystemDataTrackingFine(IPLocation pipLocation, SocketName socketName, Set<IData> data) {
		_logger.debug("doCrossSystemDataTrackingFine invoked: {}, {}, {}.", pipLocation, socketName, data);

		Pip2PipClient remotePip = null;

		try {
			remotePip = new ThriftClientFactory().createPip2PipClient(pipLocation);
			remotePip.connect();
		} catch (IOException e) {
			_logger.error("Unable to perform remote data transfer with [" + pipLocation + "]");
			return;
		}

		remotePip.initialRepresentation(socketName, data);

		remotePip.disconnect();
	}



	@Override
	public void dataTransfer(RemoteDataFlowInfo dataflow) {
		_logger.info("dataTransfer: " + dataflow);

		for (Entry<SocketContainer, Map<SocketContainer, Set<IData>>> flow : dataflow.getFlows().entrySet()) {
			Map<SocketContainer, Set<IData>> dsts = flow.getValue();

			for (Entry<SocketContainer, Set<IData>> dst : dsts.entrySet()) {

				SocketContainer dstSocket = dst.getKey();
				Set<IData> data = dst.getValue();

				IPLocation pipLocation = new IPLocation(dstSocket.getResponsibleLocation().getHost(), Settings.getInstance().getPipListenerPort());
				IPLocation pmpLocation = new IPLocation(dstSocket.getResponsibleLocation().getHost(), Settings.getInstance().getPmpListenerPort());

				doStickyPolicyTransfer(getAllPolicies(data), pmpLocation);
				doCrossSystemDataTrackingCoarse(data, pipLocation);
				doCrossSystemDataTrackingFine(pipLocation, dstSocket.getSocketName(), data);


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
	public void register(XmlPolicy policy, String from) {
		_sharedKeyspaces.create(policy);
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
			Pdp2PepClient pdp2pep = new ThriftClientFactory().createPdp2PepClient(loc);
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
				pdp2pep.disconnect();
			} catch (Exception e) {
				result = new IPLocation(ip);
				_logger.warn("Unable to connect to {}.", loc);
				_logger.warn("Assuming a responsible location of {}.", ip);
			}
		}

		_responsiblePdps.put(ip, result);

		return result;
	}
	
	public void awaitPolicyTransfer(String policyName) {
		while (!SharedKeyspace.existsPhysically(_cluster, policyName)) {
			_logger.info("Waiting for keyspace {} to be physically available.", policyName);
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
			}
		};
	}
}
