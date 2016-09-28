package de.tum.in.i22.ucwebmanager.view;

import java.sql.SQLException;
import java.util.List;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;

import de.tum.in.i22.ucwebmanager.DB.App;
import de.tum.in.i22.ucwebmanager.DB.AppDAO;
import de.tum.in.i22.ucwebmanager.FileUtil.FileUtil;
import de.tum.in.i22.ucwebmanager.Status.Status;
public class RuntimeView extends VerticalLayout implements View{

	final RuntimeDiagram runtimeDiagram = new RuntimeDiagram();
	Window subWindow;
	ComboBox cmbListApp;
	private App app;
	private int appId;
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
		subWindow = new Window("No App selected, please choose an app!");
        VerticalLayout subContent = new VerticalLayout();
        subContent.setMargin(true);
        subWindow.setContent(subContent);
        cmbListApp = new ComboBox("List of avaialbe Apps");
        Button btnSubWindowOK = new Button("OK");
        btnSubWindowOK.addClickListener(new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				String s = (String) cmbListApp.getValue();
				String[] temp = s.split(" ");
				try {
					app = AppDAO.getAppById(Integer.parseInt(temp[0]));
					//TODO: fill all boxes
					subWindow.close();
				} catch (NumberFormatException | ClassNotFoundException | SQLException e) {
					e.printStackTrace();
				}
			}
		});
        // Put some components in it
        subContent.addComponent(cmbListApp);
        subContent.addComponent(btnSubWindowOK);
        // Center it in the browser window
        subWindow.center();
	}

	@Override
	public void enter(ViewChangeEvent event) {
		if (event.getParameters() != null && event.getParameters() != "") {
			// split at "/", add each part as a label
			String[] msgs = event.getParameters().split("/");
			for (String msg : msgs) {
				appId = 0;
				if (msg != null){
					appId = Integer.parseInt(msg);
					System.out.println("enter view changeevent " + appId);
				
				try {
					app = AppDAO.getAppById(appId);
				} catch (ClassNotFoundException | SQLException e) {
					e.printStackTrace();
				}
				}
			}
			
			if (app != null){
				//TODO show graph from app
			}
		}
//		else {
//			fillCmbListApp();
//			UI.getCurrent().addWindow(subWindow);
//		}
	}
	private void fillCmbListApp(){
		List<App> apps = AppDAO.getAppByStatus(Status.INSTRUMENTATION.getStage());
		for (App app : apps){
			cmbListApp.addItem(app.getId() + " " + app.getName());
		}
	}
}
