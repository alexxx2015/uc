package de.tum.in.i22.uc.ptp.policy.translation.ecacreation;

/**
 * @author Cipri
 * This class is a wrapper class for all the templates stored in the ecarules.config file.
 */
public class ECATemplates {

	private ECATemplate[] ecas;
	
	public ECATemplate[] getEcas(){
		return ecas;
	}
	
	public void setEcas(ECATemplate[] ecas){
		this.ecas = ecas;
	}
	
	public ECATemplate getTemplate(String id){
		ECATemplate result = null ;
		for(ECATemplate var : ecas){
			if( var.getId().equals(id)){
				result = var;
				break;
			}
		}
		return result;
	}
	
	public String toString(){
		String s = "";
		for(ECATemplate t : ecas){
			s += "\n" + t.toString();
		}
		return s;
	}
}
