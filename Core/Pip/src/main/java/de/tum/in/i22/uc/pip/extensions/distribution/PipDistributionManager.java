//package de.tum.in.i22.uc.pip.extensions.distribution;
//
//import java.util.Map;
//import java.util.Set;
//
//import de.tum.in.i22.uc.cm.datatypes.interfaces.IData;
//import de.tum.in.i22.uc.cm.datatypes.interfaces.IEvent;
//import de.tum.in.i22.uc.cm.datatypes.interfaces.IName;
//import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
//import de.tum.in.i22.uc.cm.distribution.EDistributionStrategy;
//import de.tum.in.i22.uc.cm.distribution.Location;
//import de.tum.in.i22.uc.cm.settings.Settings;
//
///**
// * This class manages the distributed parts of the PIP.
// *
// * The strategy used by this DistributedPipManager instance is determined by
// * {@link Settings#getDistributionStrategy()}.
// *
// * @author Florian Kelbert
// *
// */
//public class PipDistributionManager implements IPipDistributionStrategy {
//	private static PipDistributionStrategy _strategy;
//
//	public PipDistributionManager() {
//		_strategy = PipDistributionStrategy.create(Settings.getInstance().getDistributionStrategy());
//	}
//
//	public static EDistributionStrategy getStrategy() {
//		return _strategy.getStrategy();
//	}
//
//	@Override
//	public IStatus remoteDataFlow(Location srcLocation, Location dstLocation, Map<IName,Set<IData>> dataflow) {
//		return _strategy.remoteDataFlow(srcLocation, dstLocation, dataflow);
//	}
//
//	@Override
//	public IStatus doRemoteEventUpdate(Location location, IEvent event) {
//		return _strategy.doRemoteEventUpdate(location, event);
//	}
//
//	@Override
//	public boolean hasAllData(Location location, Set<IData> data) {
//		return _strategy.hasAllData(location, data);
//	}
//
//	@Override
//	public boolean hasAnyData(Location location, Set<IData> data) {
//		return _strategy.hasAnyData(location, data);
//	}
//
//	@Override
//	public boolean hasAllContainers(Location location, Set<IName> containers) {
//		return _strategy.hasAllContainers(location, containers);
//	}
//
//	@Override
//	public boolean hasAnyContainer(Location location, Set<IName> containers) {
//		return _strategy.hasAnyContainer(location, containers);
//	}
//
//	@Override
//	public Set<Location> whoHasData(Set<IData> data, int recursionDepth) {
//		return _strategy.whoHasData(data, recursionDepth);
//	}
//}
