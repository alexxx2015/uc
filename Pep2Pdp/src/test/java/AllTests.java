
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import pdp.tests.CompareUtilTest;
import pdp.tests.GpConversionTest;


@RunWith(Suite.class)
@SuiteClasses({ CompareUtilTest.class, GpConversionTest.class, TestPep2PdpCommunication.class })
public class AllTests {

}
