package pmp.tests;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Set;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.datatypes.basic.DataBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic.EStatus;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IData;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IName;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.cm.processing.PdpProcessor;
import de.tum.in.i22.uc.cm.processing.PipProcessor;
import de.tum.in.i22.uc.cm.processing.PmpProcessor;
import de.tum.in.i22.uc.cm.processing.dummy.DummyDmpProcessor;
import de.tum.in.i22.uc.cm.processing.dummy.DummyPdpProcessor;
import de.tum.in.i22.uc.cm.processing.dummy.DummyPipProcessor;
import de.tum.in.i22.uc.pmp.PmpHandler;

public class PmpTest {

	@Test
	public void test() {
		PipProcessor pip = new MyDummyPipProcessor();
		PdpProcessor pdp = new DummyPdpProcessor();
		PmpProcessor pmp = new PmpHandler();
		pmp.init(pip, pdp, new DummyDmpProcessor());

		String path = "src/test/resources/testPmp.xml";

		byte[] encoded = null;
		try {
			encoded = Files.readAllBytes(Paths.get(path));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// To be safe, encoding should be added to the next command
		String policyString = new String(encoded);

		pmp.deployPolicyRawXMLPmp(policyString);

		assert true;
	}

	private static class MyDummyPipProcessor extends DummyPipProcessor {

		private static final Logger log = LoggerFactory
				.getLogger(MyDummyPipProcessor.class);

		@Override
		public IStatus initialRepresentation(IName containerName,
				Set<IData> data) {
			log.debug("DUMMY - initialRepresentation invoked");
			log.debug("DUMMY - simulate PIP adding data " + data
					+ " to container " + containerName);
			log.debug("DUMMY - everything went fine");
			return new StatusBasic(EStatus.OKAY);
		}

		@Override
		public IData newInitialRepresentation(IName containerName) {
			log.debug("DUMMY - initialRepresentation invoked");
			log.debug("DUMMY - simulate creation of new data for container "
					+ containerName);
			IData d = new DataBasic();
			log.debug("DUMMY - new data is " + d);
			return d;
		}

	}
}
