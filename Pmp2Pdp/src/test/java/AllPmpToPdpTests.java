
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import pdp.tests.AllPdpTests;


@RunWith(Suite.class)
@SuiteClasses({ AllPdpTests.class, TestPmp2PdpCommunication.class })
public class AllPmpToPdpTests {

}
