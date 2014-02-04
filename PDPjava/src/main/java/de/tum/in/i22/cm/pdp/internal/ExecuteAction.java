package de.tum.in.i22.cm.pdp.internal;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.cm.pdp.gproto.ExecuteActionProto.PbExecuteAction;
import de.tum.in.i22.cm.pdp.gproto.ParameterProto.PbParameter;
import de.tum.in.i22.cm.pdp.xsd.action.ExecuteActionType;
import de.tum.in.i22.cm.pdp.xsd.action.ExecuteAsyncActionType;
import de.tum.in.i22.cm.pdp.xsd.action.ParameterType;

public class ExecuteAction implements Serializable
{
  private static final long serialVersionUID =8451999937686098519L;
  private static Logger     log              =LoggerFactory.getLogger(ExecuteAction.class);

  private String          name      = null;
  private List<Param<?>>  parameter = new ArrayList<Param<?>>();
  private String          processor = null;

  public ExecuteAction(String name, List<Param<?>> params)
  {
    this.name=name;
    this.parameter=params;
  }

  public ExecuteAction()
  {}
  
  public ExecuteAction(ExecuteActionType execAction)
  {
    log.debug("Preparing executeAction from ExecuteActionType");
    this.name = execAction.getName();
    for(ParameterType param : execAction.getParameter())
    {
      this.parameter.add(new Param<String>(param.getName(), param.getValue()));
    }
  }
  
  public ExecuteAction(ExecuteAsyncActionType execAction)
  {
    log.debug("Preparing executeAction from ExecuteAsyncActionType");
    this.name = execAction.getName();
    this.processor = execAction.getProcessor().value();
    for(ParameterType param : execAction.getParameter())
    {
      this.parameter.add(new Param<String>(param.getName(), param.getValue()));
    }
  }  
  
  public ExecuteAction(PbExecuteAction pbExecuteAction)
  {
    if(pbExecuteAction == null) return;

    if(pbExecuteAction.hasName()) name=pbExecuteAction.getName();
    if(pbExecuteAction.getParameterCount()>0)
    {
      for(PbParameter param : pbExecuteAction.getParameterList())
      {
        parameter.add(new Param<String>(param.getName(), param.getValue()));
      }
    }
  }

  public String getName()
  {
    return name;
  }

  public void setName(String name)
  {
    this.name=name;
  }

  public List<Param<?>> getParams()
  {
    return parameter;
  }

  public void setParams(List<Param<?>> params)
  {
    this.parameter=params;
  }

  public void addParameter(Param<?> param)
  {
    this.parameter.add(param);
  }

  public void removeParameter(String name)
  {
    this.parameter.remove(name);
  }

  public Param<?> getParameterForName(String name)
  {
    for(int i=0; i < parameter.size(); i++)
      if(parameter.get(i).getName().equalsIgnoreCase(name)) return parameter.get(i);
    return null;
  }

  public String getProcessor()
  {
    return processor;
  }

  public void setProcessor(String processor)
  {
    this.processor=processor;
  }

  public String toString()
  {
    String str="ExecuteAction name=[" + this.name + "]; "+(this.processor!=null ? "[processed by: " + this.processor + "]" : "")+" Parameter:{";

    for(Param<?> param : this.parameter)
      str+=param.toString() + ";";
    str+="}";
    return str;
  }
}
