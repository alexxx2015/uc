package de.tum.in.i22.uc.policy.translation.ecacreation;

/**
 * @author Cipri
 * For an id, one can add multiple templates for the sake of generality.
 * So far, we have used only one rule/template.
 *
 */
public class ECATemplate {

	private String id;
    private ECARuleTemplate[] templates;
 
    public void setId(String id) {
        this.id = id;
    }
 
    public void setDataset(ECARuleTemplate[] templates) {
        this.templates = templates;
    }
 
    public String getId() {
        return id;
    }
 
    public ECARuleTemplate[] getDataset() {
        return templates;
    }
	
    public String toString(){
    	String s = "";
    	for(ECARuleTemplate t : templates){
    		s += t.toString();
    	}
    	return "["+this.id+"]" + s ;
    }
}
