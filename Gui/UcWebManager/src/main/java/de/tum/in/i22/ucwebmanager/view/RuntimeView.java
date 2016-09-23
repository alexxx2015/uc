package de.tum.in.i22.ucwebmanager.view;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
public class RuntimeView extends VerticalLayout implements View{

	final RuntimeDiagram runtimeDiagram = new RuntimeDiagram();
	public RuntimeView() {
		Label lab = new Label("Runtime view");
		addComponent(lab);
		
		VerticalLayout parent = new VerticalLayout();
		parent.addComponent(lab);
		
		FormLayout fl = new FormLayout();
		fl.setSizeFull();
		String url = VaadinServlet.getCurrent().getServletContext().getInitParameter("WebURL");
		runtimeDiagram.drawFromJSON(url + "/apps/miserables.json");
		fl.addComponent(runtimeDiagram);
		
		parent.addComponent(fl);		
		parent.setMargin(true);
		addComponent(parent);
	}

	@Override
	public void enter(ViewChangeEvent event) {
		// TODO Auto-generated method stub
		
	}

}
