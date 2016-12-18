package de.tum.in.i22.ucwebmanager.view;

import java.util.HashMap;
import java.util.Map;

import com.vaadin.shared.ui.JavaScriptComponentState;

public class RuntimeDiagramState extends JavaScriptComponentState {
	private String json;
	private Map<String, Boolean> labelsMap = new HashMap<String, Boolean>();

	public String getJson() {
		return json;
	}

	public void setJson(String json) {
		this.json = json;
	}

	public Map<String, Boolean> getLabelsMap() {
		return labelsMap;
	}
	
	public void setLabelsMap(Map<String, Boolean> labelsMap) {
		this.labelsMap = labelsMap;
	}

	public void setLabelVisibility(String label, boolean visible) {
		labelsMap.put(label, visible);
	}
	
	
}
