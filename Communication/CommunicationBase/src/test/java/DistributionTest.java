
import org.junit.Assert;
import org.junit.Test;

import de.tum.in.i22.uc.cm.distribution.DistributionGranularity;
import de.tum.in.i22.uc.cm.distribution.DistributionGranularity.EDistributionGranularity;

public class DistributionTest {
	@Test
	public void test() {
		DistributionGranularity dg;

		dg = new DistributionGranularity("1000");
		Assert.assertEquals(dg.getGranularity(), EDistributionGranularity.MILLISECONDS);
		Assert.assertEquals(dg.getMilliseconds(), 1000);

		dg = new DistributionGranularity("EXACT");
		Assert.assertEquals(dg.getGranularity(), EDistributionGranularity.EXACT);

		dg = new DistributionGranularity("TIMESTEP");
		Assert.assertEquals(dg.getGranularity(), EDistributionGranularity.TIMESTEP);

		dg = new DistributionGranularity("tIMeSteP");
		Assert.assertEquals(dg.getGranularity(), EDistributionGranularity.TIMESTEP);
	}
}
