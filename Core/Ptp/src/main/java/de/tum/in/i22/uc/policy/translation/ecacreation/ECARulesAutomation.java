package de.tum.in.i22.uc.policy.translation.ecacreation;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.google.gson.Gson;

import de.tum.in.i22.uc.policy.translation.Config;
import de.tum.in.i22.uc.utilities.PublicMethods;

/**
 * @author Cipri
 * This class is used for the automation of the eca rules generation.
 * It loads the templates from the ecarules.config file.
 */
public class ECARulesAutomation {

	private ArrayList<Subformula> allSubformulas;
	private String ecaTemplateId;
	private ECARuleTemplate ecaTemplate;
	private Map<String,String> policyParams;
	
	private static ECATemplates loadedTemplates = null;
	// represents access configuration file of properties
	private static Config config;
	/** The already generated subformulas are used here.
	 * @param policyParams2
	 * @param subformulas
	 */
	public ECARulesAutomation(Map<String, String> policyParams2,	ArrayList<Subformula> subformulas) {
		allSubformulas = subformulas;
		ecaTemplateId = policyParams2.get("template_id");
		ecaTemplate = null;
		loadedTemplates = null;
		this.policyParams = policyParams2;
		try {
			config = new Config();
		} catch (IOException e) {
			e.printStackTrace();
			config = null;
		}
		
	}

	public boolean transform() {
		boolean templatesLoaded = loadECATemplates();
		if (!templatesLoaded)
			return false;

		// for each subformula
		for (Subformula subformula : this.allSubformulas) {
			this.addECA(subformula);
		}

		return true;
	}

	private boolean addECA(Subformula subformula) {		
		subformula.setECATemplate(ecaTemplate);
		ECARule eca = subformula.getECARule();
		if (eca == null) {
			
			boolean complexEvent = false;
			Node source = subformula.getNodeSource();
			String sourceName = source.getNodeName();
			if(sourceName.equals("and")){
				complexEvent = true;
			}
			
			Object[] eventAndCondition = getEventAndCondition(subformula, complexEvent);
			
			SubformulaEvent event = (SubformulaEvent) eventAndCondition[0];
			String condition = (String) eventAndCondition[1];
			Object[] action = getAction(subformula);
			eca = new ECARule(event, condition, action);
			String type = ecaTemplate.getType();
			eca.setType(type);
			subformula.setECARule(eca);
		}

		return true;
	}

	/**
	 * The extraction of the events and condition are done based on ECA rules conventions.
	 * @param subformula
	 * @param complexEvent
	 * @return
	 */
	private Object[] getEventAndCondition(Subformula subformula, boolean complexEvent){
		String tEvent = this.ecaTemplate.getEvent();
		SubformulaEvent event = null;
		String tCondition = this.ecaTemplate.getCondition();
		String condition = tCondition;
		Object[] result = new Object[2];
		if ( ! tEvent.equals("<*/>") ) {
			//extract name of the event from the template;
			String eventName = tEvent.substring(1, tEvent.length()-2);
			event = new SubformulaEvent("*");
			condition = subformula.getNodeString();
			result[0] = event;
			result[1] = condition;
			return result;
		}
		
		if(!complexEvent){
			int events = subformula.getEvents().size();
			if(events == 1){
				event = subformula.getEvents().get(0);	
				condition = "<true/>";
			}
			else {
				event = new SubformulaEvent("*");
				condition = subformula.getNodeString();
			}
			result[0] = event;
			result[1] = condition;
			return result;
		}
		
		//AND condition
		Node source = subformula.getNodeSource();
		String suString = subformula.getNodeString();
		NodeList sourceChildren = source.getChildNodes();
		for(int i=0; i<sourceChildren.getLength();i++){
			Node child = sourceChildren.item(i);
			if(child.getNodeType() != Node.ELEMENT_NODE)
				continue;
			String childName = child.getNodeName();
			
			if(event == null){
				if(!childName.equals("event")){
					//the first child is not an event
					event = new SubformulaEvent("*");
					condition = subformula.getNodeString();
					break;
				}
				else {
					Node evNameNode = child.getAttributes().getNamedItem("name");
					String evName = "*";
					if(evNameNode != null){
						evName = evNameNode.getNodeValue();
					}
					event = new SubformulaEvent(evName);
				}
			}
			else{
				condition = subformula.getNodeString(child);
				break;
			}			
		}
		
		
		result[0] = event;
		result[1] = condition;
		return result;
		
	}
	

	private Object[] getAction(Subformula subformula) {
		Object[] actions;
		String[] tAction = this.ecaTemplate.getAction();
		actions = tAction;
		int length = tAction.length;
		int index = 0;		
		if(index < length && tAction[index].equals("<execute/>")){
			ArrayList<Object> obj = new ArrayList<Object>();
			obj.add(tAction[index]);
			index++;
			if(!(index < length))
				return obj.toArray();
			String eventName = tAction[index];
			index++;
			ArrayList<SubformulaEventParameter> parameters = new ArrayList<SubformulaEventParameter>();
			for(int i=index; i<length; i++){				
				String paramName = tAction[i];
				String paramValue = tAction[++i];
				if(paramValue.equals("<@objectInstance@>")){
					String objectInstance = policyParams.get("object_instance");
					if(objectInstance !=null){
						paramValue = objectInstance;
					}
				}
				SubformulaEventParameter param = new SubformulaEventParameter(paramName, paramValue);								
				parameters.add(param);				 				
			}
			SubformulaEvent action = new SubformulaEvent(eventName, parameters);
			obj.add(action);
			actions = obj.toArray();
		}		
		return actions;
	}

	/**
	 * The templates are stored in a JSON format.
	 * @return
	 */
	private boolean loadECATemplates() {
		if (config == null)
			return false;
		
		if(!loadTemplatesFromFile()){
			System.out.println(">Templates config failed to load.");
			return false;
		}
		
		ECATemplate template = loadedTemplates.getTemplate(ecaTemplateId);
		if (template.getDataset().length <= 0)
			return false;
		//it's not dead code!
		if(template == null){
			System.out.println("TEMPLATE ID INVALID: " + ecaTemplateId);
			return false;
		}
		ecaTemplate = template.getDataset()[0];

		System.out.println(">Template selected: " + ecaTemplate);

		return true;
	}

	private static boolean loadTemplatesFromFile(){
		if(loadedTemplates != null){
			return true;
		}
		String ecaRulesFile = config.getProperty("ecaRulesTemplates");
		ecaRulesFile = config.getUserDir() + File.separator + ecaRulesFile;
		String json = "";
		try {
			json = PublicMethods.readFile(ecaRulesFile);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}

		Gson gson = new Gson();
		ECATemplates templates = gson.fromJson(json, ECATemplates.class);
		loadedTemplates = templates;
		return true;
	}
	
}
