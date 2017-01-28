package de.tum.in.i22.ucwebmanager.view;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.http.client.ClientProtocolException;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinSession;
import com.vaadin.shared.Position;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import de.tum.in.i22.ucwebmanager.DB.App;
import de.tum.in.i22.ucwebmanager.DB.AppDAO;
import de.tum.in.i22.ucwebmanager.FileUtil.FileUtil;
import de.tum.in.i22.ucwebmanager.FileUtil.TomcatConfig;
import de.tum.in.i22.ucwebmanager.Status.Status;
import de.tum.in.i22.ucwebmanager.deploy.DeployManager;

public class DeploymentView extends VerticalLayout implements View {

	private App app;
	
	private Window subWindowTomcat;
	private Window subWindowApp;
	private ComboBox cmbListTomcatConf;
	private ComboBox cmbListApp;
	
	
	private ComboBox cmbReportFile;
	private TextField txtConfName, txtUsername, txtPassword, txtHost, txtPort;
	
	private static final String DEFAULT_USERNAME = "tomcat";
	private static final String DEFAULT_HOST = "localhost";
	private static final String DEFAULT_PORT = "8181";
	
	public DeploymentView() {
		//Set the UI with its components
		cmbReportFile = new ComboBox("Select Report File");
		
		txtConfName = new TextField("Name");
		txtConfName.setWidth("100%");
		
		txtUsername = new TextField("Username", DEFAULT_USERNAME);
		txtUsername.setWidth("100%");
		
		txtPassword = new TextField("Password");
		txtPassword.setWidth("100%");
		
		txtHost = new TextField("Host", DEFAULT_HOST);
		txtHost.setWidth("100%");
		
		txtPort = new TextField("Port", DEFAULT_PORT);
		txtPort.setWidth("100%");

		// Build layout
		VerticalLayout parent = new VerticalLayout();
		Label titleLabel = new Label("Deploy Management");
		titleLabel.setSizeUndefined();
		titleLabel.addStyleName(ValoTheme.LABEL_H1);
		titleLabel.addStyleName(ValoTheme.LABEL_NO_MARGIN);
		parent.addComponent(titleLabel);

		FormLayout fl = new FormLayout();
		fl.addComponent(cmbReportFile);
		fl.addComponent(txtConfName);
		fl.addComponent(txtUsername);
		fl.addComponent(txtPassword);
		fl.addComponent(txtHost);
		fl.addComponent(txtPort);
		
		//Create sub window to select Tomcat Configuration
		subWindowTomcat = new Window("Please choose a Tomcat configuration");
        VerticalLayout subContent = new VerticalLayout();
        subContent.setMargin(true);
        subWindowTomcat.setModal(true);
        subWindowTomcat.setContent(subContent);
        cmbListTomcatConf = new ComboBox("List of available configurations");
        Button btnSubWindowOK = new Button("OK");
        btnSubWindowOK.addClickListener(new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				String filename = (String) cmbListTomcatConf.getValue();
				if (filename==null) return;
				fillFields(readConfigurationFromFile(filename), filename);
				subWindowTomcat.close();
			}
		});
    
        // Put some components in it
        subContent.addComponent(cmbListTomcatConf);
        subContent.addComponent(btnSubWindowOK);
   
        // Center it in the browser window
        subWindowTomcat.center();
        
        //Create sub window to select the app
        subWindowApp = new Window("No App selected, please choose an app!");
        VerticalLayout subContent2 = new VerticalLayout();
        subContent2.setMargin(true);
        subWindowApp.setModal(true);
        subWindowApp.setContent(subContent2);
        cmbListApp = new ComboBox("List of available Apps");
        Button btnSubWindowAppOK = new Button("OK");
        btnSubWindowAppOK.addStyleName("mybutton");
        btnSubWindowAppOK.addClickListener(new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				String s = (String) cmbListApp.getValue();
				String[] temp = s.split(" ");
				try {
					app = AppDAO.getAppById(Integer.parseInt(temp[0]));
					fillCmbReportFile(app);
					subWindowApp.close();
				} catch (NumberFormatException | ClassNotFoundException | SQLException e) {
					e.printStackTrace();
				}
			}
		});
        
        subContent2.addComponent(cmbListApp);
        subContent2.addComponent(btnSubWindowAppOK);
        
        subWindowApp.center();
        
		fl.setSizeFull();
		parent.addComponent(fl);
        
		Button btnSaveTomcatConf = new Button("Save Tomcat configuration");
		btnSaveTomcatConf.addStyleName("mybutton");
		btnSaveTomcatConf.addClickListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				saveTomcatConfiguration();
			}
		});
		
		Button btnDeploy = new Button("Deploy");
		btnDeploy.addStyleName("mybutton");
		btnDeploy.addClickListener(new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				deploy();
			}
		});
		
		Button btnUndeploy = new Button("Undeploy");
		btnUndeploy.addStyleName("mybutton");
		btnUndeploy.addClickListener(new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				undeploy();
			}
		});
		
		HorizontalLayout tmpParent = new HorizontalLayout();
		tmpParent.addComponent(btnDeploy);
		tmpParent.addComponent(btnUndeploy);
		tmpParent.addComponent(btnSaveTomcatConf);
		parent.addComponent(tmpParent);
		
		parent.setMargin(true);
		addComponent(parent);
	}
	
	private TomcatConfig readConfigurationFromFile(String filename) {
		TomcatConfig tc = new TomcatConfig();
		String filepath = FileUtil.getPathTomcatConfigurations() + File.separator + filename;
		tc.load(filepath);
		return tc;
	}
	
	private void fillFields(TomcatConfig conf, String confName) {
		txtConfName.setValue(confName);
		txtUsername.setValue(conf.getUsername());
		txtPassword.setValue(conf.getPassword());
		txtHost.setValue(conf.getHost());
		txtPort.setValue(conf.getPort());
	}
	
	private TomcatConfig readTomcatConfigFromFields() {
		
		TomcatConfig data = new TomcatConfig();
		data.setUsername(txtUsername.getValue());
		data.setPassword(txtPassword.getValue());
		data.setHost(txtHost.getValue());
		data.setPort(txtPort.getValue());
		
		StringBuilder strError = new StringBuilder();
		if ("".equals(data.getUsername())){
			strError.append("Username must be specified");
			strError.append("<br/>");
		}
		if ("".equals(data.getPassword())){
			strError.append("Password must be specified");
			strError.append("<br/>");
		}
		if ("".equals(data.getHost())){
			strError.append("Host must be specified");
			strError.append("<br/>");
		}
		if("".equals(data.getPort())){
			strError.append("Port must be specified");
			strError.append("<br/>");
		}
		
		if(!"".equals(strError.toString())){
			//If there is an error return null
			data=null;
			
			Notification notification = new Notification(
	                "Message Box");
	        notification.setDescription(strError.toString());
	        notification.setHtmlContentAllowed(true);
	        notification.setStyleName("tray dark small closable login-help");
	        notification.setPosition(Position.BOTTOM_RIGHT);
	        notification.setDelayMsec(5000);
	        notification.show(Page.getCurrent());
		}
		
		return data;
	}
	
	private void deploy() {
		TomcatConfig configurationData = readTomcatConfigFromFields();
		if (configurationData==null) {
			Notification.show("Error during the reading from fields!");
			return;
		}
		
		
		DeployManager dp = new DeployManager(configurationData);
		String response="";
		
		try {
			response=dp.deploy(app, cmbReportFile.getValue().toString());
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		System.out.println(response);
		Notification.show(response);
	}
	
	private void undeploy() {
		TomcatConfig configurationData = readTomcatConfigFromFields();
		if (configurationData==null) return;
		
		DeployManager dp = new DeployManager(configurationData);
		String reportName = "" + cmbReportFile.getValue();
		String contextName = FilenameUtils.getBaseName(app.getName()) + "_" + reportName;
		String response="";
		try {
			response = dp.undeploy(contextName);
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println(response);
		Notification.show(response);
	}

	private void saveTomcatConfiguration() {

		TomcatConfig data = readTomcatConfigFromFields();
		
		if (data!=null) {
			String name = txtConfName.getValue();
			if ("".equals(name))
				name = FileUtil.TOMCAT_DEFAULT_FILE;
			
			String filepath = FileUtil.getPathTomcatConfigurations() + File.separator + name;
			data.save(filepath);
		}
	        
	}
	

	@Override
	public void enter(ViewChangeEvent event) {
		
		
		if (event.getParameters() != null) {
			// split at "/", add each part as a label
			String[] msgs = event.getParameters().split("/");
			for (String msg : msgs) {
				int appId = 0;
				if (msg != null && msg != "") {
					appId = Integer.parseInt(msg);
					System.out.println("enter Deployment view " + appId);

					try {
						app = AppDAO.getAppById(appId);
					} catch (ClassNotFoundException | SQLException e) {
						e.printStackTrace();
					}
					if (app != null) {
						fillCmbReportFile(app);
					}

				}
				else {
					fillCmbListApp();
					if (!subWindowApp.isAttached())
						UI.getCurrent().addWindow(subWindowApp);
				}
			}
		}
		
		fillCmbListTomcatConf();
		if (!subWindowTomcat.isAttached() && !cmbListTomcatConf.getItemIds().isEmpty())
			UI.getCurrent().addWindow(subWindowTomcat);
	}
	
	private void fillCmbListApp(){
		List<App> apps = AppDAO.getAllApps();
		for (App app : apps){
			cmbListApp.addItem(app.getId() + " " + app.getName());
		}
	}

	private void fillCmbListTomcatConf(){
		cmbListTomcatConf.removeAllItems();
		String filepath = FileUtil.getPathTomcatConfigurations();
		File file = new File(filepath);
		if (!file.exists() || file.list() == null)
			return;
		
		for (String name : file.list())
			cmbListTomcatConf.addItem(name);
		
		 ArrayList<String> names = new ArrayList<String>(Arrays.asList(file.list()));
		 for (String name : names)
				cmbListTomcatConf.addItem(name);
	}
	
	 private void fillCmbReportFile(App app){
		 cmbReportFile.removeAllItems();
		 File instrumentedCodeFolder = new File(FileUtil.getPathInstrumentationOfApp(app.getHashCode()));
		 ArrayList<String> names = new ArrayList<String>(Arrays.asList(instrumentedCodeFolder.list()));
		 for (String name : names) cmbReportFile.addItem(name);
	 }
	
}
