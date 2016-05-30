package de.tum.in.i22.ucwebmanager.view;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.google.gwt.thirdparty.guava.common.io.Files;
import com.vaadin.annotations.Push;
import com.vaadin.data.Item;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FileResource;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinService;
import com.vaadin.shared.communication.PushMode;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.Grid.SingleSelectionModel;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Upload.SucceededListener;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.renderers.ButtonRenderer;
import com.vaadin.ui.themes.ValoTheme;

import de.tum.in.i22.ucwebmanager.Configuration;
import de.tum.in.i22.ucwebmanager.DB.App;
import de.tum.in.i22.ucwebmanager.DB.AppDAO;
import de.tum.in.i22.ucwebmanager.FileUtil.FileUtil;
import de.tum.in.i22.ucwebmanager.FileUtil.MD5Checksum;
import de.tum.in.i22.ucwebmanager.Status.Status;
import de.tum.in.i22.ucwebmanager.analysis.Analyser;
import de.tum.in.i22.ucwebmanager.dashboard.DashboardViewType;
@Push()
public class MainView extends VerticalLayout implements View {
	Grid grid = new Grid();
	Upload upload;
	File fileTmp;
	App app;
	String appName;
	String hashCodeOfApp;
	Map<Integer, String> map = new HashMap<Integer, String>();
	public MainView() throws SQLException {
		Label lab = new Label("Main view");
		lab.setSizeUndefined();
		lab.addStyleName(ValoTheme.LABEL_H1);
		lab.addStyleName(ValoTheme.LABEL_NO_MARGIN);
		String basepath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();	// .../src/main/webapp	
		//System.out.println(basepath);
		FileResource res = new FileResource(new File(basepath
				+ "/img/tum_logo.png"));
		Image image = new Image(null, res);
		image.setHeight("50%");
		image.setWidth("50%");
		
		HorizontalLayout horLayout = new HorizontalLayout();
		horLayout.addComponent(lab);
		horLayout.addComponent(image);
		// Upload
		Upload.Receiver receiver = createReceiver();
		upload = new Upload("Upload your App", receiver);
		upload.addSucceededListener(new SucceededListener() {

			@Override
			public void uploadSucceeded(SucceededEvent event) {
				try {
					createFolderAndSaveApp(appName);
				} catch (IOException | SQLException e) {
					e.printStackTrace();
				}
			}
		});
		// Table
		grid.setSizeFull();
		fillGrid();
		gridClickListener();
		addComponent(horLayout);
		addComponent(upload);
		addComponent(grid);
	}

	@Override
	public void enter(ViewChangeEvent event) {

		if (!event.getParameters().equals("")) {
			// split at "/", add each part as a label
			String[] msgs = event.getParameters().split("/");
			
			if (msgs[0].equals(DashboardViewType.STATANALYSIS.getViewName())) {
				int appId = 0;
				String configFile = "";
				for (int i = 1; i < msgs.length; i++){
					if (i == 1) {
						appId = Integer.parseInt(msgs[i]);
					}
					else configFile = msgs[i];
				}
				// start static analysis, update start time
				try {
					App app = AppDAO.getAppById(appId);
					Analyser analyser = new Analyser(app, configFile);
					analyser.start();
				} catch (ClassNotFoundException | SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			// map.put(appId, inputStream);
			
		}

	}

	private void createFolderAndSaveApp(String fileName) throws IOException, SQLException {
//		String path = Configuration.WebAppRoot + "/apps/"+ String.valueOf(hashCodeOfApp)+"/";
		
		String path = FileUtil.Dir.APPS.getDir()+ "/" + String.valueOf(hashCodeOfApp);
		System.out.println(path);
		File dirCode = new File(path + FileUtil.Dir.CODE.getDir());
		File dirOutput = new File(path +  FileUtil.Dir.JOANAOUTPUT.getDir());
		File dirConfig = new File(path + FileUtil.Dir.JOANACONFIG.getDir());
		File dirInstrusment = new File(path + FileUtil.Dir.INSTRUMENTATION.getDir());
		File dirRuntime = new File(path + FileUtil.Dir.RUNTIME.getDir());
		boolean success = dirCode.mkdirs()&& dirOutput.mkdirs() && dirConfig.mkdirs()
							&& dirInstrusment.mkdirs() && dirRuntime.mkdirs();
		if (success) {
			
			File fileSave = new File(dirCode, fileName);
			Files.move(fileTmp, fileSave);
			fileTmp.delete();
			FileUtil.unzipFile(dirCode, fileName);
			try {			
				// create App
				app = new App( fileName, hashCodeOfApp, Status.NONE.getStage());
				//saveToDB(fileName, hashCode, path, Status.NONE.getStage());
				AppDAO.saveToDB(app);
				// because the first time when app saved into db we dont have the id which is important for later use
				app = AppDAO.getAppByHashCode(hashCodeOfApp);
				updateTable(app);
				new Notification("Success!",
						"<br/>File uploaded, Folder created, saved to DB!",
						Notification.Type.WARNING_MESSAGE, true).show(Page.getCurrent());
			} 
			catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		} else {
			System.out.println("Directory is not created");
		}
	}

	private Upload.Receiver createReceiver() {
		Upload.Receiver receiver = new Upload.Receiver() {
			FileOutputStream fos = null;

			@Override
			public OutputStream receiveUpload(String filename, String mimeType) {
				try {
					appName = filename;
					fileTmp = new File(Configuration.WebAppRoot + "/apps/tmp/"
							+ filename);
					fos = new FileOutputStream(fileTmp);
				} catch (FileNotFoundException e) {

					e.printStackTrace();
					return null;
				}
				try {
					hashCodeOfApp = MD5Checksum.getMD5Checksum(fileTmp.toString());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return fos;
			}

		};
		return receiver;
	}

	private void fillGrid() throws SQLException {
		grid.setEditorEnabled(false);
		grid.setSelectionMode(SelectionMode.SINGLE);
		SingleSelectionModel selection = (SingleSelectionModel) grid.getSelectionModel();
		grid.addColumn("ID", Integer.class);
		grid.getColumn("ID").setMaximumWidth(50).setEditable(false);
		grid.addColumn("Name", String.class);
		grid.addColumn("Hash Code",String.class);
		grid.addColumn("Status",String.class);
		grid.getColumn("Status").setMaximumWidth(60);
		grid.addColumn("Static Analysis", String.class).setRenderer(new ButtonRenderer(e->{
			Object selected =  e.getItemId(); // get the selected rows id
			Item item = grid.getContainerDataSource().getItem(selected);
			String status = item.getItemProperty("Status").getValue().toString();
			// Send a message to static analyser view with the id of app
				UI.getCurrent().getNavigator().navigateTo(DashboardViewType.STATANALYSIS.getViewName()+
						"/" + item.getItemProperty("ID").getValue().toString());
			
		}));
		grid.addColumn("SA execute time", String.class);
		grid.addColumn("Instrumentation", String.class).setRenderer(new ButtonRenderer(e->{
			Object selected =  e.getItemId();
			Item item = grid.getContainerDataSource().getItem(selected);
			String stat = item.getItemProperty("Status").getValue().toString();
			if (!stat.equals(Status.NONE.getStage())){
				UI.getCurrent().getNavigator().navigateTo(DashboardViewType.INSTRUMENT.getViewName());
			}
			else {
				new Notification("Error!",
						"<br/>Action not allowed",
						Notification.Type.WARNING_MESSAGE, true).show(Page.getCurrent());
			}
		}));
		grid.addColumn("Execute time", String.class);
		grid.addColumn("Run time", String.class).setRenderer(new ButtonRenderer(e->{
			Object selected =  e.getItemId(); // get the selected rows id
			Item item = grid.getContainerDataSource().getItem(selected);
			String stat = item.getItemProperty("Status").getValue().toString();
			if (stat.equals(Status.INSTRUMENTATION.getStage())){
				UI.getCurrent().getNavigator().navigateTo(DashboardViewType.RUNTIME.getViewName());
			}
			else {
				new Notification("Error!",
						"<br/>Action not allowed",
						Notification.Type.WARNING_MESSAGE, true).show(Page.getCurrent());
			}
		}));
		
		List<App> allApp = null;
		try {
			allApp = AppDAO.getAllApps();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}


		for (App a : allApp) {
			grid.addRow(a.getId(),a.getName(), a.getHashCode(), a.getStatus(),"Go", "", "Go", "", "Go");		
		}
//		grid.getContainerDataSource()
	}

	private void updateTable(App app) {
		grid.addRow(app.getId(),app.getName(), app.getHashCode(), app.getStatus(),"Go", "", "Go", "", "Go");
	}
	private void updateStartTimeTable(int appId){
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd/HH:mm:ss");
		Date date = new Date();
		String stringDate = dateFormat.format(date);
		System.out.println(stringDate);
		SingleSelectionModel m  = (SingleSelectionModel) grid.getSelectionModel();
	}
	private void gridClickListener(){
//		int size = grid.getContainerDataSource().size();
//		for (int i = 0; i < size; )
		grid.addSelectionListener(selectionEvent -> { // Java 8
		    // Get selection from the selection model
		    Object selected = ((SingleSelectionModel) grid.getSelectionModel()).getSelectedRow();
		    if (selected != null){
		    	int appId =  (Integer) grid.getContainerDataSource().getItem(selected).getItemProperty("ID").getValue();
//		    	System.out.println(map.get(appId));
		        Notification.show(map.get(appId));
		    }
		    else
		        Notification.show("Nothing selected");
		});
	}
}
