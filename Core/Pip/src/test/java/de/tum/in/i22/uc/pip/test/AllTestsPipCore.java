package de.tum.in.i22.uc.pip.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
	InformationFlowModelTest.class,
	PipCoreTest.class,
	PipCoreClassReloadingTest.class,
	LinuxEventTest.class})

public class AllTestsPipCore {

}
