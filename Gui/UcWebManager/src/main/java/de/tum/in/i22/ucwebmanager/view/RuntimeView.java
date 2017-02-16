package de.tum.in.i22.ucwebmanager.view;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Iterator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.vaadin.annotations.Push;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.ui.CheckBox;

import de.tum.in.i22.ucwebmanager.DB.App;
import de.tum.in.i22.ucwebmanager.DB.AppDAO;
import de.tum.in.i22.ucwebmanager.FileUtil.FileUtil;
import de.tum.in.i22.ucwebmanager.JSON.JSONUtil;
import de.tum.in.i22.ucwebmanager.Status.Status;
import de.tum.in.i22.ucwebmanager.dashboard.DashboardViewType;

public class RuntimeView extends VerticalLayout implements View{

	RuntimeDiagram runtimeDiagram = new RuntimeDiagram();
	Window subWindow;
	ComboBox cmbListApp;
	private App app;
	private int appId;
	private Label lblApp;
	private HorizontalLayout checkboxesContainer;
	
	private Button btnRefresh;
	
	private long TIMER_DELAY = 15*1000;
	private Timer timer;
	private long prevTimeModified=0;
	
	public RuntimeView() {
		Label labTitle = new Label("Runtime view");
		labTitle.setSizeUndefined();
		labTitle.addStyleName(ValoTheme.LABEL_H1);
		labTitle.addStyleName(ValoTheme.LABEL_NO_MARGIN);
//		addComponent(labTitle);
		
		VerticalLayout parent = new VerticalLayout();
		parent.addComponent(labTitle);
		
		lblApp = new Label();
		
		FormLayout fl = new FormLayout();
		
		Label labFields = new Label("Fields:");
		checkboxesContainer = new HorizontalLayout();
		parent.addComponent(labFields);
		parent.addComponent(checkboxesContainer);
		
//		String url = VaadinServlet.getCurrent().getServletContext().getInitParameter("WebURL");
//		runtimeDiagram.drawFromJSON(url + "/apps/miserables.json");
		
        btnRefresh = new Button("Refresh");
        btnRefresh.addStyleName("mybutton");
        btnRefresh.setVisible(false);
        btnRefresh.addClickListener(new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				UI.getCurrent().getNavigator().navigateTo(DashboardViewType.RUNTIME.getViewName() +
						"/" + app.getId() + "?update");
			}
		});
		
        parent.addComponent(btnRefresh);
        fl.addComponent(runtimeDiagram);
        parent.addComponent(fl);
        
        parent.setSizeFull();
        parent.setSpacing(true);		
		parent.setMargin(true);
		addComponent(parent);
		
		
		subWindow = new Window("No App selected, please choose an app!");
        VerticalLayout subContent = new VerticalLayout();
        subContent.setMargin(true);
        subWindow.setContent(subContent);
        subWindow.setModal(true);
        cmbListApp = new ComboBox("List of available Apps");
        Button btnSubWindowOK = new Button("OK");
        btnSubWindowOK.addClickListener(new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				String s = (String) cmbListApp.getValue();
				String[] temp = s.split(" ");
				try {
					app = AppDAO.getAppById(Integer.parseInt(temp[0]));
					subWindow.close();
					drawRuntimeGraph(false);
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
			boolean requestOfUpdate=false;
			for (String msg : msgs) {
				appId = 0;
				if (msg != null){
					// Refresh of the page requested from the timer
					if (msg.contains("?update")) {
						msg = msg.replaceAll("\\?update", "");
						requestOfUpdate=true;
					}
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
				drawRuntimeGraph(requestOfUpdate);
			}
		}
		else {
			fillCmbListApp();
			UI.getCurrent().addWindow(subWindow);
		}
	}
	
	private void drawRuntimeGraph(boolean requestOfUpdate) {
		btnRefresh.setVisible(true);
		lblApp.setValue("App selected: "+ app.getName());
		
		String url = FileUtil.getUrlGraphFile(app.getHashCode());
		System.out.println("Drawing: "+url);
		
		//Add checkboxes to show/hide fields of the nodes
		Map<String, Boolean> labelsMap = new HashMap<String, Boolean>();
		for (String field: readFieldsFromJSONFile(FileUtil.getPathGraphFile(app.getHashCode())))
			labelsMap.put(field, true);
		fillCheckBoxList(labelsMap.keySet(), true);
		runtimeDiagram.setLabelsMap(labelsMap);
		
		
		//Add date to avoid cache for json file
		runtimeDiagram.drawFromJSON(url+"?v="+(new Date()).getTime());
		
		if (!requestOfUpdate) {
			startRefresherTimer();
		}
			
	}
	
	private void fillCmbListApp(){
		List<App> apps = AppDAO.getAppByStatus(Status.INSTRUMENTATION.getStage());
		for (App app : apps){
			cmbListApp.addItem(app.getId() + " " + app.getName());
		}
	}
	
	private static List<String> readFieldsFromJSONFile(String url) {
		List<String> lstFields = new ArrayList<String>();
		
		JSONParser parser = new JSONParser();
		JSONObject cnt = new JSONObject();
		
		try {
			cnt = (JSONObject) parser.parse(new FileReader(url));
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if (cnt.containsKey(JSONUtil.NODES)) {
			JSONArray cntNodes = (JSONArray) cnt.get(JSONUtil.NODES);
			if (cntNodes.size()>0) {
				JSONObject json = (JSONObject) cntNodes.get(0);
				for (Iterator iter = json.keySet().iterator(); iter.hasNext();) {
					String key = (String) iter.next();
					lstFields.add(key);
				}
					
			}
		
		}
		
		return lstFields;
			
	}
	
	private void fillCheckBoxList (Set<String> fields, boolean defaultValue) {
		for (String field : fields) {
			CheckBox chkField = new CheckBox(field, defaultValue);
			chkField.addValueChangeListener(event -> 
			   runtimeDiagram.setLabelVisibility(chkField.getCaption(), 
					   Boolean.parseBoolean(event.getProperty().getValue().toString())));
			checkboxesContainer.addComponent(chkField);
		}
	}
	
	
	private void startRefresherTimer() {
		stopTimer();
		String graphPath = FileUtil.getPathGraphFile(app.getHashCode());
		File graph = new File(graphPath);
		prevTimeModified = graph.lastModified();
	
		timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask(){

			@Override
			public void run() {
				String graphPath = FileUtil.getPathGraphFile(app.getHashCode());
				File graph = new File(graphPath);
				if (prevTimeModified < graph.lastModified()) {
					prevTimeModified = graph.lastModified();
					// Refresh page will reload the diagram
					System.out.println("Refresh runtime view");
					
					UI.getCurrent().getNavigator().navigateTo(DashboardViewType.RUNTIME.getViewName() +
							"/" + app.getId() + "?update");
					UI.getCurrent().push();
					
				}
			}
			
		}, TIMER_DELAY, TIMER_DELAY);
	}
	
	public void stopTimer() {
		if (timer!=null) timer.cancel();
	}
	
	public void updatePage() {
		UI.getCurrent().getNavigator().navigateTo(DashboardViewType.RUNTIME.getViewName() +
				"/" + app.getId() + "?update");
		UI.getCurrent().push();
	}
}
