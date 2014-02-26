package pip.core.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import pip.core.test.PipCoreTest;

@RunWith(Suite.class)
@SuiteClasses({ InformationFlowModelTest.class, PipCoreTest.class})
public class AllTestsPipCore {

}
