package de.tum.in.i22.ucwebmanager.view;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import com.vaadin.data.Item;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FileResource;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinService;
import com.vaadin.ui.Button;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Upload.SucceededListener;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.tum.in.i22.ucwebmanager.Configuration;
import de.tum.in.i22.ucwebmanager.Status.Status;

public class MainView extends VerticalLayout implements View{
	String appName;						Table table;
	Upload upload;						
	File fileTmp;
	int hashCodeOfApp;
	public MainView() {
		Label lab = new Label("Main view");
		lab.setSizeUndefined();
		lab.addStyleName(ValoTheme.LABEL_H1);
		lab.addStyleName(ValoTheme.LABEL_NO_MARGIN);
		String basepath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();
		FileResource res = new FileResource(new File(basepath + "/img/tum_logo.png"));
		Image image = new Image(null, res);
		image.setHeight("50%");
		image.setWidth("50%");
		HorizontalLayout grid = new HorizontalLayout();
		grid.addComponent(lab);
		grid.addComponent(image);
		//Upload
		Upload.Receiver receiver = createReceiver();
		upload = new Upload("Upload your App",receiver);
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
		//Table
		table = new Table("Overview");
		table.setSizeFull();
		fillTable(table);
		addComponent(grid);
		addComponent(upload);
		addComponent(table);
//		addComponent(lab);
//		addComponent(image);
	}

	@Override
	public void enter(ViewChangeEvent event) {
		// TODO Auto-generated method stub
		
	}
	private void createFolderAndSaveApp(String fileName) throws IOException{
		int hashCode = fileName.hashCode();
		String path = Configuration.WebAppRoot+"/apps/"+String.valueOf(hashCode);
		File dir = new File(path+"/code");
		boolean success = dir.mkdirs();
		if (success){
			File fileSave = new File ( dir,fileName );
			FileOutputStream stream;
			try {
				stream = new FileOutputStream (fileSave);
				// ... write data to the stream ...
				stream.flush();
				stream.close();
				fileTmp.delete();
				saveToDB(fileName, hashCode, path,Status.NONE.getStage());
				updateTable(table, fileName, hashCode, Status.NONE.getStage());
				new Notification("Success!","<br/>File uploaded, Folder created, saved to DB!",Notification.Type.WARNING_MESSAGE,true).show(Page.getCurrent());
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} 
		}
		else{
			System.out.println("Directory is not created");
		}
	}
	private Upload.Receiver createReceiver(){
		Upload.Receiver receiver = new Upload.Receiver() {
			FileOutputStream fos = null;
			@Override
			public OutputStream receiveUpload(String filename, String mimeType) {
				try {
					appName = filename;
					hashCodeOfApp = filename.hashCode();
					fileTmp = new File(Configuration.WebAppRoot+"/apps/tmp/" + filename);
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
	
	private void fillTable(Table t){
		t.addContainerProperty("Name", String.class, null);
		t.addContainerProperty("Hash Code", Integer.class, null);
		t.addContainerProperty("Status", String.class, null);
		ArrayList<String[]> allApp = new ArrayList<String[]>();
		try {
			allApp = getAllApp();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		int i = 1;
		for (String[] s : allApp){
			t.addItem(new Object[]{s[0],Integer.parseInt(s[1]),s[2]},i++);
			
		}
		
	}
	private void updateTable(Table t,String name,int hashCode, String status){
		Object newItemId = table.addItem();
		Item row1 = table.getItem(newItemId);
		row1.getItemProperty("Name").setValue(name);
		row1.getItemProperty("Hash Code").setValue(hashCode);
		row1.getItemProperty("Status").setValue(status);
	}
	private void saveToDB(String fileName, int hashCode, String path, String status) throws ClassNotFoundException{
		Class.forName("org.sqlite.JDBC");
		
		Connection conn = null;
		try {
			conn = DriverManager.getConnection("jdbc:sqlite:UcWebManager.db");
			Statement statement = conn.createStatement();
			String s = "insert into t_app (s_name,i_hashcode,s_path,s_status) values('"
										+fileName+"',"+String.valueOf(hashCode)+",'"+path+ "','"+status+"')";
			statement.executeUpdate(s);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		finally
	    {
	      try
	      {
	        if(conn != null)
	          conn.close();
	      }
	      catch(SQLException e)
	      {
	        // connection close failed.
	        System.err.println(e);
	      }
	    }
	}
	private ArrayList<String[]> getAllApp() throws ClassNotFoundException{
		ArrayList<String[]> result = new ArrayList<String[]>();
		Class.forName("org.sqlite.JDBC");
		Connection conn = null;
		try {
			conn = DriverManager.getConnection("jdbc:sqlite:UcWebManager.db");
			Statement statement = conn.createStatement();
			String s = "SELECT * FROM t_app";
			ResultSet rs = statement.executeQuery(s);
			while (rs.next()) {
				String[] tmp = new String[3];
				tmp[0] = rs.getString("s_name");
				tmp[1] = String.valueOf(rs.getInt("i_hashcode"));
				tmp[2] = rs.getString("s_status");
				result.add(tmp);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				// connection close failed.
				System.err.println(e);
			}

		}
		return result;
	}
}
