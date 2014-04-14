package de.tum.in.i22.uc.pdp.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.pdp.core.condition.comparisonOperators.ElementInListComparisonOperator;
import de.tum.in.i22.uc.pdp.core.condition.comparisonOperators.EndsWithComparisonOperator;
import de.tum.in.i22.uc.pdp.core.condition.comparisonOperators.EqualsComparisonOperator;
import de.tum.in.i22.uc.pdp.core.condition.comparisonOperators.EqualsIgnoreCaseComparisonOperator;
import de.tum.in.i22.uc.pdp.core.condition.comparisonOperators.GeComparisonOperator;
import de.tum.in.i22.uc.pdp.core.condition.comparisonOperators.GenericComparisonOperator;
import de.tum.in.i22.uc.pdp.core.condition.comparisonOperators.GtComparisonOperator;
import de.tum.in.i22.uc.pdp.core.condition.comparisonOperators.LeComparisonOperator;
import de.tum.in.i22.uc.pdp.core.condition.comparisonOperators.ListInListComparisonOperator;
import de.tum.in.i22.uc.pdp.core.condition.comparisonOperators.LtComparisonOperator;
import de.tum.in.i22.uc.pdp.core.condition.comparisonOperators.NotEqualsComparisonOperator;
import de.tum.in.i22.uc.pdp.core.condition.comparisonOperators.StartsWithComparisonOperator;
import de.tum.in.i22.uc.pdp.core.condition.comparisonOperators.SubstringComparisonOperator;
import de.tum.in.i22.uc.pdp.core.shared.Param;
import de.tum.in.i22.uc.pdp.xsd.ParamMatchType;

public class ParamMatch extends ParamMatchType
{
  private static Logger log = LoggerFactory.getLogger(ParamMatch.class);
  
  public String toString()
  {
    String str = "" + this.getName() + " -> " + this.getValue() + " ("+this.getType()+")";
    return str;
  }
  
  public boolean paramMatches(Param<?> param)
  {
	  boolean matches=false;
	  if(param==null)
    {
      log.trace("Parameter [{}] not present", this.getName());
      return false;
    }
    if(this.getName().equals(param.getName()))
    {
      log.trace("param name matches");
      GenericComparisonOperator compOp;
      if (this.getCmpOp()!=null){
    	  switch (this.getCmpOp()){
		case ELEMENT_IN_LIST:
			compOp=new ElementInListComparisonOperator();
			break;
		case ENDS_WITH:
			compOp=new EndsWithComparisonOperator();
			break;
		case EQUALS:
			compOp=new EqualsComparisonOperator();
			break;
		case EQUALS_IGNORE_CASE:
			compOp=new EqualsIgnoreCaseComparisonOperator();
			break;
		case GE:
			compOp=new GeComparisonOperator();
			break;
		case GT:
			compOp=new GtComparisonOperator();
			break;
		case LE:
			compOp=new LeComparisonOperator();
			break;
		case LIST_IN_LIST:
			compOp=new ListInListComparisonOperator();
			break;
		case LT:
			compOp=new LtComparisonOperator();
			break;
		case NOT_EQUALS:
			compOp=new NotEqualsComparisonOperator();
			break;
		case STARTS_WITH:			
			compOp=new StartsWithComparisonOperator();
			break;
		case SUBSTRING:
			compOp=new SubstringComparisonOperator();
			break;
		default:
			compOp=new EqualsComparisonOperator(); //default comparison is equality
			break;
    	  }
    	  matches=compOp.compare((String)param.getValue(),this.getValue());
      }
      log.trace("param value does "+(matches?"":"NOT ")+"match");
      return matches;
    }
    return false;
  }
  
}
