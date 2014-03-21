package de.tum.in.i22.pdp.internal.condition;

import java.util.ArrayList;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.Descriptors.FieldDescriptor;

import de.tum.in.i22.pdp.internal.condition.operators.Always;
import de.tum.in.i22.pdp.internal.condition.operators.Before;
import de.tum.in.i22.pdp.internal.condition.operators.During;
import de.tum.in.i22.pdp.internal.condition.operators.EventMatchOperator;
import de.tum.in.i22.pdp.internal.condition.operators.OSLAnd;
import de.tum.in.i22.pdp.internal.condition.operators.OSLFalse;
import de.tum.in.i22.pdp.internal.condition.operators.OSLImplies;
import de.tum.in.i22.pdp.internal.condition.operators.OSLNot;
import de.tum.in.i22.pdp.internal.condition.operators.OSLOr;
import de.tum.in.i22.pdp.internal.condition.operators.OSLTrue;
import de.tum.in.i22.pdp.internal.condition.operators.Since;
import de.tum.in.i22.pdp.internal.condition.operators.Within;
import de.tum.in.i22.pdp.internal.exceptions.InvalidOperatorException;
import de.tum.in.i22.pdp.internal.gproto.ConditionProto.PbBinaryOperator;
import de.tum.in.i22.pdp.internal.gproto.ConditionProto.PbTimeBoundedUnaryOperator;
import de.tum.in.i22.pdp.internal.gproto.ConditionProto.PbUnaryOperator;
import de.tum.in.i22.pdp.internal.gproto.EventProto.PbEvent;

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
    log.trace("processing descriptorName: {}", descriptorName);
    switch(descriptorName)
    {
      case "true":  op1=new OSLTrue(); break;
      case "false": op1=new OSLFalse(); break;
      case "not": 
      {
        log.trace("processing not");
        PbOperatorHelper helper = new PbOperatorHelper();
        op1 = new OSLNot(helper.processOperator(this.getNot(index)));
        break;
      }
      case "or":
      {
        log.trace("processing or");
        PbOperatorHelper helper = new PbOperatorHelper();
        ArrayList<Operator> children = helper.processOperator(this.getOr(index));
        op1=new OSLOr(children.get(0), children.get(1));
        log.trace("or finished");
        break;
      }
      case "and":
      {
        log.trace("processing and");
        PbOperatorHelper helper = new PbOperatorHelper();
        ArrayList<Operator> children = helper.processOperator(this.getAnd(index));
        op1=new OSLAnd(children.get(0), children.get(1));
        log.trace("and finished");
        break;
      }
      case "implies":
      {
        log.trace("processing implies");
        PbOperatorHelper helper = new PbOperatorHelper();
        ArrayList<Operator> children = helper.processOperator(this.getImplies(index));
        op1=new OSLImplies(children.get(0), children.get(1));
        log.trace("implies finished");
        break;
      }
      case "eventMatch":
      {
        log.trace("processing eventMatch");
        op1 = new EventMatchOperator(this.getEventMatch(index));
        log.trace("eventMatch finished");        
        break;
      }
      case "always":
      {
        log.trace("processing always");
        PbOperatorHelper helper = new PbOperatorHelper();
        op1 = new Always(helper.processOperator(this.getAlways(index)));
        log.trace("always finished");        
        break;
      }
      case "since":
      {
        log.trace("processing since");
        PbOperatorHelper helper = new PbOperatorHelper();
        ArrayList<Operator> children = helper.processOperator(this.getSince(index));
        op1=new Since(children.get(0), children.get(1));
        log.trace("since finished");        
        break;
      }
      case "before":
      {
        log.trace("processing before");
        PbOperatorHelper helper = new PbOperatorHelper();
        PbTimeBoundedUnaryOperator before = this.getBefore(index);
        op1 = new Before(helper.processOperator(before), before.getTimeAmount());
        log.trace("before finished");
        break;
      }
      case "during":
      {
        log.trace("processing during");
        PbOperatorHelper helper = new PbOperatorHelper();
        PbTimeBoundedUnaryOperator during = this.getDuring(index);
        op1 = new During(helper.processOperator(during), during.getTimeAmount());
        log.trace("during finished");
        break;
      }     
      case "within":
      {
        log.trace("processing within");
        PbOperatorHelper helper = new PbOperatorHelper();
        PbTimeBoundedUnaryOperator within = this.getWithin(index);
        op1 = new Within(helper.processOperator(within), within.getTimeAmount());
        log.trace("within finished");
        break;
      }      
    }
    return op1;
  }  
  
  
  public Operator processOperator(PbUnaryOperator op) throws InvalidOperatorException
  {
    log.trace("processOperator unaryOperator");
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
    log.trace("processOperator binaryOperator");
    this.op2=op;
    Map<FieldDescriptor, Object> values=this.getAllFields();
    ArrayList<Operator> ops = new ArrayList<Operator>(2);
    
    if(values.size()==1)
    {
      log.trace("twice the same operator");
      for(FieldDescriptor fd : values.keySet())
      {
        log.trace("processing fdsame: {}", fd.getName());
        ops.add(this.processOperator(fd.getName(), 0));
        ops.add(this.processOperator(fd.getName(), 1));
      }
      log.trace("finished iterating...");
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
    log.trace("processOperator timeBoundedOperator");
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