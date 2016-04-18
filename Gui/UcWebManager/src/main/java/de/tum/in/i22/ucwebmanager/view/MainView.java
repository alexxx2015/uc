package de.tum.in.i22.ucwebmanager.view;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;


import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FileResource;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinService;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Upload.SucceededListener;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.tum.in.i22.ucwebmanager.Configuration;
import de.tum.in.i22.ucwebmanager.DB.App;
import de.tum.in.i22.ucwebmanager.DB.AppDAO;
import de.tum.in.i22.ucwebmanager.Status.Status;

public class MainView extends VerticalLayout implements View {
	
	Grid grid = new Grid();
	Upload upload;
	File fileTmp;
	App app;
	String appName;
	int hashCodeOfApp;
	public MainView() {
		Label lab = new Label("Main view");
		lab.setSizeUndefined();
		lab.addStyleName(ValoTheme.LABEL_H1);
		lab.addStyleName(ValoTheme.LABEL_NO_MARGIN);
		String basepath = VaadinService.getCurrent().getBaseDirectory()
				.getAbsolutePath();
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
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		// Table
		grid.setSizeFull();
		fillGrid(grid);
		addComponent(horLayout);
		addComponent(upload);
		addComponent(grid);
		// addComponent(lab);
		// addComponent(image);
	}

	@Override
	public void enter(ViewChangeEvent event) {
		// TODO Auto-generated method stub

	}

	private void createFolderAndSaveApp(String fileName) throws IOException {
		String path = Configuration.WebAppRoot + "/apps/"
				+ String.valueOf(hashCodeOfApp);
		File dir = new File(path + "/code");
		boolean success = dir.mkdirs();
		if (success) {
			File fileSave = new File(dir, fileName);
			FileOutputStream stream;
			try {
				stream = new FileOutputStream(fileSave);
				// ... write data to the stream ...
				stream.flush();
				stream.close();
				fileTmp.delete();
				// create App
				app = new App( fileName, hashCodeOfApp, dir.toString(), Status.NONE.getStage());
				//saveToDB(fileName, hashCode, path, Status.NONE.getStage());
				AppDAO.saveToDB(app);
				updateTable(grid, app);
				new Notification("Success!",
						"<br/>File uploaded, Folder created, saved to DB!",
						Notification.Type.WARNING_MESSAGE, true).show(Page.getCurrent());
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
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
					hashCodeOfApp = filename.hashCode();
					fileTmp = new File(Configuration.WebAppRoot + "/apps/tmp/"
							+ filename);
					fos = new FileOutputStream(fileTmp);
				} catch (FileNotFoundException e) {

					e.printStackTrace();
					return null;
				}
				return fos;
			}

		};
		return receiver;
	}

	private void fillGrid(Grid g) {
		g.setEditorEnabled(true);
		g.setSelectionMode(SelectionMode.SINGLE);
		g.addColumn("ID", Integer.class);
		g.getColumn("ID").setMaximumWidth(50).setEditable(false);
		g.addColumn("Name", String.class);
		g.addColumn("Hash Code",Integer.class);
		g.addColumn("Status",String.class);
		g.getColumn("Status").setMaximumWidth(60);
		List<App> allApp = null;
		try {
			allApp = AppDAO.getAllApps();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		for (App a : allApp) {
			g.addRow(a.getId(),a.getName(), a.getHashCode(), a.getStatus());		
		}
		
	}

	private void updateTable(Grid g,App app) {
		g.addRow(app.getId(),app.getName(), app.getHashCode(), app.getStatus());
	}

}
