package de.tum.in.i22.ucwebmanager.view;

import java.util.Map;

import com.vaadin.annotations.JavaScript;
import com.vaadin.shared.ui.JavaScriptComponentState;
import com.vaadin.ui.AbstractJavaScriptComponent;
@JavaScript({"d3.min.js", "de_tum_in_i22_ucwebmanager_view_RuntimeDiagram.js"})
public class RuntimeDiagram extends AbstractJavaScriptComponent{

public void drawFromJSON(String url){
	
	getState().setJson(url);

}

public void setLabelsMap(Map<String, Boolean> labelsMap) {
	getState().setLabelsMap(labelsMap);
}

public void setLabelVisibility(String field, boolean visible) {
	
	getState().setLabelVisibility(field, visible);
	
}

@Override
public RuntimeDiagramState getState() {
	return (RuntimeDiagramState) super.getState();
}
}
