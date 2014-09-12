package de.tum.in.i22.policyeditor.model;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;



import java.util.Arrays;
import java.util.Comparator;

import com.google.gson.Gson;

import de.tum.in.i22.policyeditor.logger.EditorLogger;
import de.tum.in.i22.policyeditor.util.PublicMethods;

/**
 * @author Cipri
 * Templates markers:
		#SU[subject]SU#
		#CL[class]CL#
		#NU0[number]NU#
		#TI[time]TI#
		#AC[action]AC#
		#OB[object]OB#
 *
 */
public class TemplatesLoader {

	private PolicyTemplate[] templates;
	private static EditorLogger logger = EditorLogger.instance();
	
	class PolicyComparator implements Comparator<PolicyTemplate> {
		@Override
		public int compare(PolicyTemplate arg0, PolicyTemplate arg1) {
			String id0 = arg0.getTemplateId();
			String id1 = arg1.getTemplateId();
			return id0.compareTo(id1);
		}
	}
	
	public TemplatesLoader(){
		templates = new PolicyTemplate[0];
	}

	public PolicyTemplate[] getTemplates() {
		return templates;
	}

	public void setTemplates(PolicyTemplate[] templates) {
		this.templates = templates;
	}
	
	public String toString(){
		String s = "";
		for (int i = 0; i < templates.length; i++) {
			s += "\n| " + templates[i].toString() +" ||"+templates[i].getClasses()[0] + "||";
		}
		return s;
	}
	
	private void orderTemplates(){
		Arrays.sort(templates, new PolicyComparator());
	}
	
	public static TemplatesLoader loadPolicyTemplates() {
		logger.debugLog(System.getProperty("user.dir"), null);
		String templatesFile = System.getProperty("user.dir")+File.separator+"src"
						+File.separator
						+"main"+File.separator
						+"resources"+File.separator
						+"templates.cfg"; 
		
		TemplatesLoader templates = new TemplatesLoader();
		String json = "";
		try {
			json = PublicMethods.readFile(templatesFile, StandardCharsets.UTF_8);
		} catch (IOException e) {
			logger.errorLog("templates.cfg loading error", e);
			return null;
		}

		Gson gson = new Gson();
		templates = gson.fromJson(json, TemplatesLoader.class);
		templates.orderTemplates();
		String logMsg = "Templates loaded: " + templates.toString();
		logger.infoLog(logMsg, null);

		loadActionTemplates();
		loadSubjectTemplates();
		
		return templates;
	}
			
	private static void loadActionTemplates(){
		String actionsFile = System.getProperty("user.dir")+File.separator+"src"
				+File.separator
				+"main"+File.separator
				+"resources"+File.separator
				+"actions.cfg";
		String file = "";
		try {
			file = PublicMethods.readFile(actionsFile, StandardCharsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
			logger.errorLog("actions.cfg loading error", e);
		}
		file = file.replaceAll("\n", "");
		file = file.replaceAll("\r", "");
		String[] actions = file.split(",");
		file = "";
		for (int i = 0; i < actions.length; i++) {
			String string = actions[i];
			if(string.length() == 0)
				continue;
			file += string + ",";
		}
		actions = file.split(",");
		UserAction.setMenuItems(actions);
		
		String logMsg = "Action templates loaded: " + file;
		logger.infoLog(logMsg, null);
	}
	
	private static void loadSubjectTemplates(){
		String subjectsFile = System.getProperty("user.dir")+File.separator+"src"
				+File.separator
				+"main"+File.separator
				+"resources"+File.separator
				+"subjects.cfg";
		String file = "";
		try {
			file = PublicMethods.readFile(subjectsFile, StandardCharsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
			logger.errorLog("subjects.cfg loading error", e);
		}
		file = file.replaceAll("\n", "");
		file = file.replaceAll("\r", "");
		String[] subjects = file.split(",");
		file = "";
		for (int i = 0; i < subjects.length; i++) {
			String string = subjects[i];
			if(string.length() == 0)
				continue;
			file += string + ",";
		}
		subjects = file.split(",");
		UserSubject.setMenuItems(subjects);
		
		String logMsg = "Subject templates loaded: " + file;
		logger.infoLog(logMsg, null);
	}
	
	public static void main(String[] args){
		TemplatesLoader.loadPolicyTemplates();
	}
	
}
