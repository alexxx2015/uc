package pdp.tests;
import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Test;

import de.tum.in.i22.pdp.datatypes.IData;
import de.tum.in.i22.pdp.datatypes.IEvent;
import de.tum.in.i22.pdp.datatypes.basic.ConditionBasic;
import de.tum.in.i22.pdp.datatypes.basic.DataBasic;
import de.tum.in.i22.pdp.datatypes.basic.DataEventMapBasic;
import de.tum.in.i22.pdp.datatypes.basic.EventBasic;
import de.tum.in.i22.pdp.datatypes.basic.MechanismBasic;
import de.tum.in.i22.pdp.datatypes.basic.OslFormulaBasic;
import de.tum.in.i22.pdp.datatypes.basic.SimplifiedTemporalLogicBasic;
import de.tum.in.i22.pdp.gpb.PdpProtos.GpMechanism;


public class GpConversionTest {

	@Test
	public void testEmptyMechanism() {
		MechanismBasic m = new MechanismBasic();
		GpMechanism gpM = MechanismBasic.createGpbMechanism(m);
		Assert.assertEquals(m, new MechanismBasic(gpM));
	}
	
	@Test
	public void testMechanism() {
		MechanismBasic m = new MechanismBasic();
		
		
		//* set condition
		ConditionBasic condition = new ConditionBasic();
		//** set condition condition
		OslFormulaBasic formula = new OslFormulaBasic("Formula xxxx");
		condition.setCondition(formula);
		//** set condition conditionSimp
		SimplifiedTemporalLogicBasic conditionSimp = new SimplifiedTemporalLogicBasic();
		conditionSimp.setFormula(new OslFormulaBasic("Formula yyyy"));
		
		//*** set condition conditionSimp dataEventMap
		Map<IData, IEvent> map1 = new HashMap<IData, IEvent>();
		map1.put(new DataBasic("id1"), new EventBasic("event1", null));
		DataEventMapBasic dataEventMap = new DataEventMapBasic(map1);
		conditionSimp.setDataEventMap(dataEventMap);
		condition.setConditionSimp(conditionSimp);
		m.setCondition(condition);
		
		GpMechanism gpM = MechanismBasic.createGpbMechanism(m);
		MechanismBasic newM = new MechanismBasic(gpM);
		Assert.assertEquals(m, newM);
		
		newM.setCondition(null);
		Assert.assertNotSame(m, newM);
	}

}
