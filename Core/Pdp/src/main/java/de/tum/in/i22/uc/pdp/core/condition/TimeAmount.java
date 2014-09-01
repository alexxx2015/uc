package de.tum.in.i22.uc.pdp.core.condition;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.pdp.xsd.time.TimeUnitType;

public class TimeAmount {
	private static Logger _logger = LoggerFactory.getLogger(TimeAmount.class);

	private final long _amount;

	private final String _unit;
	private final long _interval;
	private final long _timestepInterval;

	public TimeAmount(long amount, TimeUnitType tu, long mechanismTimestepSize) {
		_amount = amount;
		_unit = tu.value();
		_interval = amount * getTimeUnitMultiplier(tu);
		_timestepInterval = _interval / mechanismTimestepSize;

		_logger.debug("Interval: {}, timestepInterval: {}", _interval, _timestepInterval);
	}

	public long getTimestepInterval() {
		return _timestepInterval;
	}

	public static long getTimeUnitMultiplier(TimeUnitType tu) {
		if (tu == null) {
			_logger.warn("Cannot calculate timeUnit-multiplier for null!");
			return 1;
		}
		switch (tu) {
		case MILLISECONDS:
			return 1;
		case SECONDS:
			return 1000;
		case MINUTES:
			return 60000;
		case HOURS:
			return 3600000L;
		case DAYS:
			return 86400000L;
		case WEEKS:
			return 604800000L;
		case MONTHS:
			return 2592000000L;
		case YEARS:
			return 31104000000L;
		case NANOSECONDS:
		case MICROSECONDS:
		case TIMESTEPS:
		default:
			_logger.warn("Unexpected (unsupported) timeunit found: ", tu.value());
			return 1;
		}
	}

	@Override
	public String toString() {
		return _amount + " " + _unit + "(" + _timestepInterval + ")";
	}
}
