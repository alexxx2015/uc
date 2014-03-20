package pdp.tests;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.cm.pdp.internal.condition.CircularArray;

public class CircArrayTest
{
  private static Logger log = LoggerFactory.getLogger(CircArrayTest.class);

  //@Test
  public void test()
  {
    log.debug("CircularArrayTest");
    
    CircularArray<Integer> a = new CircularArray<Integer>(5);
    log.debug("a: " + a);
    
    a.push(42);
    log.debug("a: " + a);
    
    a.push(36);
    a.push(37);
    log.debug("pop: " + a.pop());
    log.debug("a: " + a);
    a.push(38);
    a.push(39);
    log.debug("a: " + a);
    log.debug("pop: " + a.pop());
    log.debug("a: " + a);
    a.push(99);
    log.debug("a: " + a);
    log.debug("pop: " + a.pop());
    log.debug("pop: " + a.pop());
    log.debug("pop: " + a.pop());
    log.debug("pop: " + a.pop());
    log.debug("a: " + a);
    
    assert(true);
  }

}
