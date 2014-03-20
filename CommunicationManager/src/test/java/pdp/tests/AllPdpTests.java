package pdp.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


@RunWith(Suite.class)
@SuiteClasses({
	GpConversionTest.class,
	EventHandlerTest.class
	})
public class AllPdpTests {

}
