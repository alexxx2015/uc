package uctests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ 
	PipTest.class,
	PdpTest.class,
	TestPep2PdpCommunication.class,
	TestPmp2PipCommunication.class})
public class AllTests {

}
