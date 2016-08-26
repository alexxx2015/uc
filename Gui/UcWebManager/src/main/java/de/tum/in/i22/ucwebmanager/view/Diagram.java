package de.tum.in.i22.ucwebmanager.view;

import java.util.List;

import com.vaadin.annotations.JavaScript;
import com.vaadin.ui.AbstractJavaScriptComponent;
@JavaScript({"d3.min.js", "window.de_tum_in_i22_ucwebmanager_view_Diagram.js"})
public class Diagram extends AbstractJavaScriptComponent{
	public void setCoords(final List<Integer> coords) {
        getState().setCoords(coords);
    }
@Override
protected DiagramState getState() {
	// TODO Auto-generated method stub
	return (DiagramState) super.getState();
}
}
