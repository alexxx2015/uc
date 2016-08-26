package de.tum.in.i22.ucwebmanager.view;

import java.util.List;

import com.vaadin.shared.ui.JavaScriptComponentState;

public class DiagramState extends JavaScriptComponentState {
	 private List<Integer> coords;
	 
	    public List<Integer> getCoords() {
	        return coords;
	    }
	 
	    public void setCoords(final List<Integer> coords) {
	        this.coords = coords;
	    }
}
