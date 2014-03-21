package de.tum.in.i22.uc.pdp.internal;

import java.io.Serializable;

public class Param<T> implements Serializable
{
  //private static Logger     log              =LoggerFactory.getLogger(EventMatch.class);
  private static final long serialVersionUID =-7061921148298856812L;

  private String name;
  private T      value;
  private int    type;
  
  public Param()
  {}

  public Param(String name, T value, int type)
  {
    if(name == null) throw new IllegalArgumentException("Name required");
    this.name=name;
    this.value=value;
    this.type=type;
  }

  public Param(String name, T value)
  {
    if(name == null) throw new IllegalArgumentException("Name required");
    this.name=name;
    this.value=value;
    this.type=Constants.PARAMETER_TYPE_STRING;
  }


  public String getName()
  {
    return name;
  }

  public void setName(String name)
  {
    this.name=name;
  }

  public T getValue()
  {
    return value;
  }

  public void setValue(T value)
  {
    this.value=value;
  }

  public int getType()
  {
    return type;
  }

  public void setType(int type)
  {
    this.type=type;
  }

  public static int getIdForName(String type)
  {
    if(type != null)
    {
      if(type.equalsIgnoreCase("datausage"))
        return Constants.PARAMETER_TYPE_DATAUSAGE;
      else if(type.equalsIgnoreCase("xpath"))
        return Constants.PARAMETER_TYPE_XPATH;
      else if(type.equalsIgnoreCase("regex"))
        return Constants.PARAMETER_TYPE_REGEX;
      else if(type.equalsIgnoreCase("context"))
        return Constants.PARAMETER_TYPE_CONTEXT;
      else if(type.equalsIgnoreCase("binary"))
        return Constants.PARAMETER_TYPE_BINARY;
      else if(type.equalsIgnoreCase("int"))
        return Constants.PARAMETER_TYPE_INT;
      else if(type.equalsIgnoreCase("long"))
        return Constants.PARAMETER_TYPE_LONG;
      else if(type.equalsIgnoreCase("bool")) 
        return Constants.PARAMETER_TYPE_BOOL;
    }
    return Constants.PARAMETER_TYPE_STRING;
  }
  
  public String toString()
  {
    return name + ": " + value + " (" + Constants.PARAMETER_TYPE_NAMES[type] + ")";
  }
  
}


