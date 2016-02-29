package de.tum.in.i22.ucwebmanager.view;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

public class InstrumentationView extends VerticalLayout implements View{

	public InstrumentationView() {
		Label lab = new Label("Instrumentation");
		addComponent(lab);
	}
	@Override
	public void enter(ViewChangeEvent event) {
		// TODO Auto-generated method stub
		
	}

}
