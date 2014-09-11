package de.tum.in.i22.policyeditor.model;

import java.awt.Component;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PolicyTemplateParser {

	private static List<String> inputOrder(String str){    
	    List<String> result = new ArrayList<String>();
	    int numberCount = 0;
	    String SUB0 = UserSubject.START;     
	    String NUM0 = UserNumber.START.replace(UserNumber.COUNTER, ""+numberCount) ;  
	    String ACT0 = UserAction.START;    
	    String TIME0 = UserTime.START;    
	    String mark = "start";
	    
	    while(mark != ""){
	        int subIndex = str.indexOf(SUB0);
	        int numIndex = str.indexOf(NUM0);
	        int actIndex = str.indexOf(ACT0);
	        int timIndex = str.indexOf(TIME0);    
	        int min = str.length()-1;
	        mark = "";
	        if(subIndex!=-1 && min > subIndex){
	            min = subIndex ;
	            mark = SUB0;
	        }    
	        if(numIndex !=-1 && min > numIndex){
	            min = numIndex;
	            mark = NUM0;
	        }
	        if(actIndex !=-1 && min > actIndex){
	            min = actIndex;
	            mark = ACT0;
	        }
	        if(timIndex != -1 && min > timIndex){
	            min = timIndex;
	            mark = TIME0;
	        }
	        if(mark == NUM0){
	            numberCount ++;
	            NUM0 = UserNumber.START.replace(UserNumber.COUNTER, ""+numberCount);
	        }
	        if(mark != "")
	            result.add(mark);
	        str = str.substring(min + mark.length());
	    }
	    
	    return result;
	}


	/**
	 * @param str
	 * @return
	 */
	private static Object[] insertSubjectSelection(String str){
	    Object[] result = new Object[4];
	    String FIRST = UserSubject.START;
	    String LAST = UserSubject.END;
	     
	    int firstIndex = str.indexOf(FIRST);
	    if(firstIndex == -1){
	        result[0] = null;
	        result[1] = null;
	        result[2] = str;
	        return result;
	    }
	    String head = str.substring(0, firstIndex);
	    int lastIndex = str.substring(firstIndex+FIRST.length()).indexOf(LAST);
	    String middle = str.substring(firstIndex+FIRST.length()).substring(0,lastIndex);
	    String res = str.substring(firstIndex+FIRST.length()+lastIndex+LAST.length()); 
	  	
    	Component labelText = new UserLabel(head);            
	    Component selectUsers = new UserSubject(middle);
	    
	    result[0] = labelText;
	    result[1] = selectUsers;
	    result[2] =  res;   
	    result[3] = middle;
	    
	    return result;
	    
	}
	
	/**
	 * @param str
	 * @return
	 */
	private static Object[] insertActionSelection(String str){
		Object[] result = new Object[4];
	    String FIRST = UserAction.START;
	    String LAST = UserAction.END;
	    
	    int firstIndex = str.indexOf(FIRST);
	    if(firstIndex == -1){
	    	result[0] = null;
		    result[1] = null;
		    result[2] = str;
		    return result;
	    }
	    
	    String head = str.substring(0, firstIndex);
	    int lastIndex = str.substring(firstIndex+FIRST.length()).indexOf(LAST);
	    String middle = str.substring(firstIndex+FIRST.length()).substring(0,lastIndex);
	    String res = str.substring(firstIndex+FIRST.length()+lastIndex+LAST.length());
	    
	    Component labelText = new UserLabel(head);            
	    Component selectActions = new UserAction(middle);
	    
	    result[0] = labelText;
	    result[1] = selectActions;
	    result[2] =  res;   
	    result[3] = middle;
	    
	    return result;
	}

	private static Object[] insertNumber(String str, int index){
		Object[] result = new Object[4];
	    String NUM0 = UserNumber.START.replace(UserNumber.COUNTER, ""+index);;
	    String NUM1 = UserNumber.END;
	    String FIRST = NUM0;
	    String LAST = NUM1;
	    
	    int firstIndex = str.indexOf(FIRST);
	    if(firstIndex == -1){
	    	result[0] = null;
		    result[1] = null;
		    result[2] = str;
	        return result;
	    }
	        
	    String head = str.substring(0, firstIndex);
	    int lastIndex = str.substring(firstIndex+FIRST.length()).indexOf(LAST);
	    String middle = str.substring(firstIndex+FIRST.length()).substring(0,lastIndex);
	    String res = str.substring(firstIndex+FIRST.length()+lastIndex+LAST.length());

	    Component labelText = new UserLabel(head);            
	    Component selectNumber = new UserNumber(middle, index);
	    
	    result[0] = labelText;
	    result[1] = selectNumber;
	    result[2] = res;   
	    result[3] = middle;        
	    
	    return result;
	}
	
	private static Object[] insertTime(String str){
		Object[] result = new Object[4];
	    String FIRST = UserTime.START;
	    String LAST = UserTime.END;
	     
	    int firstIndex = str.indexOf(FIRST);
	    if(firstIndex == -1){
	    	result[0] = null;
		    result[1] = null;
		    result[2] = str;
	        return result;
	    }
	    String head = str.substring(0, firstIndex);
	    int lastIndex = str.substring(firstIndex+FIRST.length()).indexOf(LAST);
	    String middle = str.substring(firstIndex+FIRST.length()).substring(0,lastIndex);
	    String res = str.substring(firstIndex+FIRST.length()+lastIndex+LAST.length());     

	    Component labelText = new UserLabel(head);            
	    Component selectTime = new UserTime(middle);
	    
	    result[0] = labelText;
	    result[1] = selectTime;
	    result[2] = res;   
	    result[3] = middle;   
	    
	    return result;
	}

	
	/**
	 * @param element
	 * @param htmlInput
	 * @param str
	 * @param counter
	 * @param numberCounter
	 * @return 	[0] - components list; 
	 * 			[1] - remaining string to be parsed
	 * 			[2] - elements counter
	 */
	private static Object[] insertElement(String element, String str, int counter, int numberCounter){
	    List<Component> components = new ArrayList<Component>() ;
	    Object[] result = new Object[4];
	    Object[] data = new Object[4];
	    String numberTag = UserNumber.START.replace(UserNumber.COUNTER, ""+numberCounter); 
	    if(element.equals(UserSubject.START) ){
	        data = insertSubjectSelection(str);                                                  
	        if(data[0] != null){
	        	components.add((Component) data[0]);
	            counter++;
	            components.add((Component) data[1]); 
	            counter++;
	        }
	    }
	    else if(element.equals(UserAction.START)){
	        data = insertActionSelection(str);                                                  
	        if(data[0] != null){
	        	components.add((Component) data[0]);
	            counter++;
	            components.add((Component) data[1]); 
	            counter++;
	        }        
	    } 
	    else if(element.equals(numberTag)){
	        data = insertNumber(str,numberCounter);
	        if(data[0] != null){
	        	components.add((Component) data[0]);
	            counter++;
	            components.add((Component) data[1]); 
	            counter++;
	        }
	    }
	    else if(element.equals(UserTime.START)){
	        data = insertTime(str);
	        if(data[0] != null){
	        	components.add((Component) data[0]);
	            counter++;
	            components.add((Component) data[1]); 
	            counter++;
	        }
	    }
	    else{
	        //no recognized element
	    }
	                  
	    String remaining = (String) data[2];  //text after element
	    result[0] = components;
	    result[1] = remaining;
	    result[2] = counter;
	    return result;
	}
	
	/**
	 * Remove the parameter tags.
	 * @param input
	 * @return
	 */
	public static String filterData(String input){    
	    String str = input;
	    String result = "";
	    int numberCount = 0;
	    String SUB0 = UserSubject.START; 
	    String SUB1 =  UserSubject.END;
	    String NUM0 = UserNumber.START.replace(UserNumber.COUNTER, ""+numberCount); 
	    String NUM1 = UserNumber.END;    
	    String ACT0 = UserAction.START;
	    String ACT1 =  UserAction.END;
	    String TIME0 = UserTime.START;
	    String TIME1 = UserTime.END;    
	    String mark0 = "start";
	    String mark1 = "end";
	    
	    while(mark0 != ""){
	        int subIndex = str.indexOf(SUB0);
	        int numIndex = str.indexOf(NUM0);
	        int actIndex = str.indexOf(ACT0);
	        int timIndex = str.indexOf(TIME0);           
	        mark0 = "";
	        
	        if(subIndex!=-1){            
	            mark0 = SUB0;
	            mark1 = SUB1;
	            str = str.replace(mark0, "");
	            str = str.replace(mark1, "");
	        }    
	        if(numIndex !=-1){            
	            mark0 = NUM0;
	            mark1 = NUM1;
	            str = str.replace(mark0, "");
	            str = str.replace(mark1, "");
		    numberCount ++;
	            NUM0 = UserNumber.START.replace(UserNumber.COUNTER, ""+numberCount);
	        }
	        if(actIndex !=-1){            
	            mark0 = ACT0;
	            mark1 = ACT1;
	            str = str.replace(mark0, "");
	            str = str.replace(mark1, "");
	        }
	        if(timIndex != -1){            
	            mark0 = TIME0;
	            mark1 = TIME1;
	            str = str.replace(mark0, "");
	            str = str.replace(mark1, "");
	        }
	           
	    }
	    result = str;
	    
	    return result;
	}
	
	public static List<Component> parsePolicyTemplate(String policyTemplate){
		List<Component> components = new ArrayList<Component>();
		List<String> order = inputOrder(policyTemplate);
		Object[] res = new Object[4];
		res[0] = "";
		res[1] = policyTemplate;
		int counter = 0;
		int numberCounter = 0;
		for (String element : order) {
			res = insertElement(element, (String) res[1], counter, numberCounter );
			components.addAll((Collection<? extends Component>) res[0]);
			String numTag = UserNumber.START.replace(UserNumber.COUNTER, ""+numberCounter);
			if(element.equals(numTag))
				numberCounter++;
			counter = (int) res[2];
		}
		
		UserLabel remainingLabel = new UserLabel((String) res[1]);
		components.add(remainingLabel);
		return components;
	}
	
	public static String reconstructPolicy(String policyTemplate, List<Component> components){
		String reconstructedPolicy = "";
		List<String> order = inputOrder(policyTemplate);
		
		return reconstructedPolicy;
	}
	
}
