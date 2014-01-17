package de.fraunhofer.iese.pef.pdp.internal.condition;

import java.util.ArrayList;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.Descriptors.FieldDescriptor;

import de.fraunhofer.iese.pef.pdp.gproto.ConditionProto.PbBinaryOperator;
import de.fraunhofer.iese.pef.pdp.gproto.ConditionProto.PbTimeBoundedUnaryOperator;
import de.fraunhofer.iese.pef.pdp.gproto.ConditionProto.PbUnaryOperator;
import de.fraunhofer.iese.pef.pdp.gproto.EventProto.PbEvent;
import de.fraunhofer.iese.pef.pdp.internal.condition.operators.Always;
import de.fraunhofer.iese.pef.pdp.internal.condition.operators.Before;
import de.fraunhofer.iese.pef.pdp.internal.condition.operators.During;
import de.fraunhofer.iese.pef.pdp.internal.condition.operators.EventMatchOperator;
import de.fraunhofer.iese.pef.pdp.internal.condition.operators.OSLAnd;
import de.fraunhofer.iese.pef.pdp.internal.condition.operators.OSLFalse;
import de.fraunhofer.iese.pef.pdp.internal.condition.operators.OSLImplies;
import de.fraunhofer.iese.pef.pdp.internal.condition.operators.OSLNot;
import de.fraunhofer.iese.pef.pdp.internal.condition.operators.OSLOr;
import de.fraunhofer.iese.pef.pdp.internal.condition.operators.OSLTrue;
import de.fraunhofer.iese.pef.pdp.internal.condition.operators.Since;
import de.fraunhofer.iese.pef.pdp.internal.condition.operators.Within;
import de.fraunhofer.iese.pef.pdp.internal.exceptions.InvalidOperatorException;

public class PbOperatorHelper
{
  private static Logger              log =LoggerFactory.getLogger(PbOperatorHelper.class);

  private PbUnaryOperator            op1 =null;
  private PbBinaryOperator           op2 =null;
  private PbTimeBoundedUnaryOperator op3 =null;
  
  public PbOperatorHelper()
  {}
  
  public Map<FieldDescriptor, Object> getAllFields()
  {
    if(op1!=null) return op1.getAllFields();
    else if(op2!=null) return op2.getAllFields();
    else return op3.getAllFields();
  }
  
  public PbUnaryOperator getNot(int index)
  {
    if(op1!=null) return op1.getNot();
    else if(op2!=null) return op2.getNot(index);
    else return op3.getNot();
  }
  public PbBinaryOperator getOr(int index)
  {
    if(op1!=null) return op1.getOr();
    else if(op2!=null) return op2.getOr(index);
    else return op3.getOr();
  }
  public PbBinaryOperator getAnd(int index)
  {
    if(op1!=null) return op1.getAnd();
    else if(op2!=null) return op2.getAnd(index);
    else return op3.getAnd();
  } 
  public PbBinaryOperator getImplies(int index)
  {
    if(op1!=null) return op1.getImplies();
    else if(op2!=null) return op2.getImplies(index);
    else return op3.getImplies();
  }
  public PbEvent getEventMatch(int index)
  {
    if(op1!=null) return op1.getEventMatch();
    else if(op2!=null) return op2.getEventMatch(index);
    else return op3.getEventMatch();
  }
  public PbBinaryOperator getSince(int index)
  {
    if(op1!=null) return op1.getSince();
    else if(op2!=null) return op2.getSince(0);
    else return op3.getSince();
  }
  public PbUnaryOperator getAlways(int index)
  {
    if(op1!=null) return op1.getAlways();
    else if(op2!=null) return op2.getAlways(0);
    else return op3.getAlways();
  }
  public PbTimeBoundedUnaryOperator getBefore(int index)
  {
    if(op1!=null) return op1.getBefore();
    else if(op2!=null) return op2.getBefore(0);
    else return op3.getBefore();
  }
  public PbTimeBoundedUnaryOperator getWithin(int index)
  {
    if(op1!=null) return op1.getWithin();
    else if(op2!=null) return op2.getWithin(0);
    else return op3.getWithin();
  }  
  public PbTimeBoundedUnaryOperator getDuring(int index)
  {
    if(op1!=null) return op1.getDuring();
    else if(op2!=null) return op2.getDuring(0);
    else return op3.getDuring();
  }  

  public Operator processOperator(String descriptorName, int index) throws InvalidOperatorException
  {
    Operator op1 = null;
    log.debug("processing descriptorName: {}", descriptorName);
    switch(descriptorName)
    {
      case "true":  op1=new OSLTrue(); break;
      case "false": op1=new OSLFalse(); break;
      case "not": 
      {
        log.debug("processing not");
        PbOperatorHelper helper = new PbOperatorHelper();
        op1 = new OSLNot(helper.processOperator(this.getNot(index)));
        break;
      }
      case "or":
      {
        log.debug("processing or");
        PbOperatorHelper helper = new PbOperatorHelper();
        ArrayList<Operator> children = helper.processOperator(this.getOr(index));
        op1=new OSLOr(children.get(0), children.get(1));
        log.debug("or finished");
        break;
      }
      case "and":
      {
        log.debug("processing and");
        PbOperatorHelper helper = new PbOperatorHelper();
        ArrayList<Operator> children = helper.processOperator(this.getAnd(index));
        op1=new OSLAnd(children.get(0), children.get(1));
        log.debug("and finished");
        break;
      }
      case "implies":
      {
        log.debug("processing implies");
        PbOperatorHelper helper = new PbOperatorHelper();
        ArrayList<Operator> children = helper.processOperator(this.getImplies(index));
        op1=new OSLImplies(children.get(0), children.get(1));
        log.debug("implies finished");
        break;
      }
      case "eventMatch":
      {
        log.debug("processing eventMatch");
        op1 = new EventMatchOperator(this.getEventMatch(index));
        log.debug("eventMatch finished");        
        break;
      }
      case "always":
      {
        log.debug("processing always");
        PbOperatorHelper helper = new PbOperatorHelper();
        op1 = new Always(helper.processOperator(this.getAlways(index)));
        log.debug("always finished");        
        break;
      }
      case "since":
      {
        log.debug("processing since");
        PbOperatorHelper helper = new PbOperatorHelper();
        ArrayList<Operator> children = helper.processOperator(this.getSince(index));
        op1=new Since(children.get(0), children.get(1));
        log.debug("since finished");        
        break;
      }
      case "before":
      {
        log.debug("processing before");
        PbOperatorHelper helper = new PbOperatorHelper();
        PbTimeBoundedUnaryOperator before = this.getBefore(index);
        op1 = new Before(helper.processOperator(before), before.getTimeAmount());
        log.debug("before finished");
        break;
      }
      case "during":
      {
        log.debug("processing during");
        PbOperatorHelper helper = new PbOperatorHelper();
        PbTimeBoundedUnaryOperator during = this.getDuring(index);
        op1 = new During(helper.processOperator(during), during.getTimeAmount());
        log.debug("during finished");
        break;
      }     
      case "within":
      {
        log.debug("processing within");
        PbOperatorHelper helper = new PbOperatorHelper();
        PbTimeBoundedUnaryOperator within = this.getWithin(index);
        op1 = new Within(helper.processOperator(within), within.getTimeAmount());
        log.debug("within finished");
        break;
      }      
    }
    return op1;
  }  
  
  
  public Operator processOperator(PbUnaryOperator op) throws InvalidOperatorException
  {
    log.debug("processOperator unaryOperator");
    this.op1=op;
    Map<FieldDescriptor, Object> values=this.getAllFields();
    Operator op1=null;
    for(FieldDescriptor fd : values.keySet())
    {
      op1=this.processOperator(fd.getName(), 0);
    }
    return op1;
  }
  
  public ArrayList<Operator> processOperator(PbBinaryOperator op) throws InvalidOperatorException
  {
    log.debug("processOperator binaryOperator");
    this.op2=op;
    Map<FieldDescriptor, Object> values=this.getAllFields();
    ArrayList<Operator> ops = new ArrayList<Operator>(2);
    
    if(values.size()==1)
    {
      log.debug("twice the same operator");
      for(FieldDescriptor fd : values.keySet())
      {
        log.debug("processing fdsame: {}", fd.getName());
        ops.add(this.processOperator(fd.getName(), 0));
        ops.add(this.processOperator(fd.getName(), 1));
      }
      log.debug("finished iterating...");
    }
    else
    {
      for(FieldDescriptor fd : values.keySet())
      {
        ops.add(this.processOperator(fd.getName(), 0));
      }
    }
    return ops;    
  }
  
  public Operator processOperator(PbTimeBoundedUnaryOperator op) throws InvalidOperatorException
  {
    log.debug("processOperator timeBoundedOperator");
    this.op3=op;
    Map<FieldDescriptor, Object> values=this.getAllFields();
    Operator op1=null;
    for(FieldDescriptor fd : values.keySet())
    {
      op1=this.processOperator(fd.getName(), 0);
    }
    return op1;
  }
  
  
}