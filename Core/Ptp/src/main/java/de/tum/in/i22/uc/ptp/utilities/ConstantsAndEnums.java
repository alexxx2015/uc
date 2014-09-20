/**
 * 
 */
package de.tum.in.i22.uc.ptp.utilities;
/**
 * @author ladmin
 *
 */
public class ConstantsAndEnums {
	
	
	/** enum used for osl operators
	 */
	
	public enum Operators {
		ACTION, TIMESTEPS, NUM, EVAL, TRUE, FALSE, NOT, ALWAYS, AND, OR, IMPLIES, UNTIL, AFTER, WITHIN, DURING, REPMAX, REPLIM , REPUNTIL, EXISTS, FORALL	;

		/** 
		 * Finds the value of the given enumeration by name, *case-insensitive*. 
		 * Throws a RuntimeException if no match is found.  
		 **/
	      public static Operators valueOfIgnoreCase(String str) {
	          boolean found = false;
	          Operators op=null;
	          for(Operators en: values()){
	             if(en.toString().equalsIgnoreCase(str)) {
	            	 op = en;
	            	 found = true;
	             break;
	          }  }
	          if(!found) throw new RuntimeException("Invalid value for custom enum: " +str);
	         
	      return op;
	  }
	}
	
	// constants
	public static final String ACTION = "action";
	public static final String OBJECT = "object";
	public static final String ACTION_TYPE = "desiredEv";
	
	public static final String TRUE = "true";
	public static final String FALSE = "false";

	public static final String NOT = "not";
	public static final String AND = "and";
	public static final String OR = "or";
	public static final String IMPLIES = "implies";
	public static final String UNTIL = "until";
	public static final String AFTER = "after";
	public static final String WITHIN = "within";
	public static final String DURING = "during";
	public static final String ALWAYS = "always";
	public static final String REPMAX = "repmax";
	public static final String REPLIM = "replim";
	public static final String REPUNTIL ="repuntil";

}
