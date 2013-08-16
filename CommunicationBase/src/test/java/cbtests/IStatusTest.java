package cbtests;

import static org.junit.Assert.*;
import junit.framework.Assert;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.tum.in.i22.pdp.gpb.PdpProtos.GpStatus.GpEStatus;
import de.tum.in.i22.uc.cm.datatypes.EStatus;

public class IStatusTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testConversionFromGpEStatusToEStatus() {
		EStatus status = EStatus.convertFromGpEStatus(GpEStatus.INHIBIT);
		Assert.assertEquals(EStatus.INHIBIT, status);
	}
	
	@Test
	public void testConversionFromEStatusToGpEStatus() {
		EStatus status = EStatus.MODIFY;
		
		Assert.assertEquals(GpEStatus.MODIFY, status.asGpEStatus());
	}

}
