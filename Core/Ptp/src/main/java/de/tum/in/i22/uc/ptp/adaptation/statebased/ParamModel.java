package de.tum.in.i22.uc.ptp.adaptation.statebased;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Cipri
 *	The class is used to wrap a param element from the eventDecl.xml
 *	configuration file
 *
 */
public class ParamModel {

	private String paramName;
	private List<String> paramValues;
	
	public ParamModel(String name){
		this.paramName = name;
		paramValues = new ArrayList<String>();
	}
	
	public void setParamName(String name){
		this.paramName = name;
	}
	public String getParamName(){
		return this.paramName;
	}
	
	public boolean addParamValue(String value){
		if(value == null)
			return false;
		if(value.equals(""))
			return false;
		if(paramValues.contains(value))
			return false;
		return paramValues.add(value);
	}
	
	public List<String> getParamValues(){
		return this.paramValues;
	}
	
	public String toString(){
		String result = "";
		result += paramName;
		result +=" " + this.paramValues;
		return result;
	}
	
	public int hashCode(){
		return this.paramName.hashCode();
	}
	
	public boolean equals(Object o){
		if (o == null)
			return false;
		if (!(o instanceof ParamModel)){
			return false;
		}
		ParamModel obj = (ParamModel) o;
		return this.paramName.equals(obj.paramName);
	}
}
