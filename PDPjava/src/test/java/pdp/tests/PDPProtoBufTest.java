package pdp.tests;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.TextFormat;
import com.google.protobuf.Message.Builder;

import de.fraunhofer.iese.pef.pdp.PolicyDecisionPoint;
import de.fraunhofer.iese.pef.pdp.gproto.MechanismProto.PbMechanism;
import de.fraunhofer.iese.pef.pdp.gproto.MechanismProto.PbMechanismOrBuilder;
import de.fraunhofer.iese.pef.pdp.internal.IPolicyDecisionPoint;
import de.fraunhofer.iese.pef.pdp.internal.Mechanism;
import de.fraunhofer.iese.pef.pdp.internal.exceptions.InvalidOperatorException;

public class PDPProtoBufTest
{
  private static Logger log = LoggerFactory.getLogger(PDPProtoBufTest.class);

  private static IPolicyDecisionPoint lpdp = null;
  
  public String readFile(String file) throws IOException
  {
    BufferedReader reader=new BufferedReader(new FileReader(file));
    String line=null;
    StringBuilder stringBuilder=new StringBuilder();
    String ls=System.getProperty("line.separator");

    while((line=reader.readLine()) != null)
    {
      stringBuilder.append(line);
      stringBuilder.append(ls);
    }
    reader.close();

    return stringBuilder.toString();
  }  

  @Test
  public void test()
  {
    log.info("PDP w/ gpb test");
    lpdp = PolicyDecisionPoint.getInstance();
    log.debug("lpdp: " + lpdp);
    
    PbMechanismOrBuilder mech = PbMechanism.newBuilder();
    try 
    {
      CharSequence cs = readFile("src/test/resources/mechTest.gpb");
      TextFormat.merge(cs, (Builder)mech);
    }
    catch(Exception e)
    {
      log.error("Exception occured: " + e.getMessage());
      e.printStackTrace();
    }

    log.debug(mech.toString());
    Mechanism a = null;
    try
    {
      a = new Mechanism(mech);
    }
    catch(InvalidOperatorException e)
    {
      log.error("Invalid Operator specified: {}", e.getMessage());
      e.printStackTrace();
    }
    log.info("Mechanism: " + a);
//    
//    boolean ret=lpdp.deployPolicy("src/main/resources/testTUM.xml");
//    log.debug("Deploying policy returned: " + ret);
//    assert(ret==true);
//    
//    log.debug("Deployed Mechanisms: [{}]", lpdp.listDeployedMechanisms());
  }

}
