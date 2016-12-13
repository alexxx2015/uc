package de.tum.in.i22.ucwebmanager.view;

import com.vaadin.shared.ui.JavaScriptComponentState;

public class RuntimeDiagramState extends JavaScriptComponentState {
	private String json;

	public String getJson() {
		return json;
	}

	public void setJson(String json) {
		this.json = json;
	}
	
	private boolean visLabels;

	public boolean isVisLabels() {
		return visLabels;
	}

	public void setVisLabels(boolean visLabels) {
		this.visLabels = visLabels;
	}
	
	public void setParameters(String json, boolean visLabels) {
		this.json=json;
		this.visLabels = visLabels;
	}
	
}
