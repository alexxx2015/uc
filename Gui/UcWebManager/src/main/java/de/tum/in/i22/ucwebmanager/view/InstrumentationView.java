package de.tum.in.i22.ucwebmanager.view;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.instrument.IllegalClassFormatException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

import com.vaadin.event.Action;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.MouseEventDetails;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Upload.SucceededListener;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.themes.ValoTheme;

import de.tum.in.i22.ucwebmanager.DB.App;
import de.tum.in.i22.ucwebmanager.DB.AppDAO;
import de.tum.in.i22.ucwebmanager.FileUtil.FileUtil;
import edu.tum.uc.jvm.Instrumentor;
public class InstrumentationView extends VerticalLayout implements View{
	App app;
	File file;
	String appName, arg0, arg1, arg2;
	private int appId;
	private TextArea textArea;
	private final Table blackList, whiteList;
	private Button btnrun;
	private TextField txtSrcFolder, txtDestFolder;
	private ComboBox cmbReportFile;
	public InstrumentationView() {
		Label lab = new Label("Instrumentation");
		lab.setSizeUndefined();
		lab.addStyleName(ValoTheme.LABEL_H1);
		lab.addStyleName(ValoTheme.LABEL_NO_MARGIN);
		//Combobox
		cmbReportFile = new ComboBox("Select Report File");
//		blackbox = new TableGrid("Black Box", "blackbox");
//		whitebox = new TableGrid("White box", "whitebox");
		blackList = new Table("Black List");
		blackList.addContainerProperty("blackbox", TextField.class, null);
		blackList.setPageLength(5);
		blackList.setWidth("100%");
		blackList.addItemClickListener(new ItemClickListener() {
			public void itemClick(ItemClickEvent event) {
				if (event.getButton() == MouseEventDetails.MouseButton.RIGHT) {
					blackList.select(event.getItemId());
				}
			}

		});

		blackList.addActionHandler(new Action.Handler() {
			public Action[] getActions(Object target, Object sender) {
				return new Action[] { new Action("New"), new Action("Delete") };
			}

			@Override
			public void handleAction(Action action, Object sender, Object target) {
				if (action.getCaption() == "New") {
					Object newItemId = blackList.addItem();
					TextField t = new TextField();
					t.setWidth("100%");
					blackList.getItem(newItemId)
							.getItemProperty("blackbox").setValue(t);
				} else if (action.getCaption() == "Delete") {
					blackList.removeItem(target);
				}
			}
		});
		whiteList = new Table("White List");
		whiteList.addContainerProperty("whitebox", TextField.class, null);
		whiteList.setPageLength(5);
		whiteList.setWidth("100%");
		whiteList.addItemClickListener(new ItemClickListener() {
			public void itemClick(ItemClickEvent event) {
				if (event.getButton() == MouseEventDetails.MouseButton.RIGHT) {
					whiteList.select(event.getItemId());
				}
			}

		});

		whiteList.addActionHandler(new Action.Handler() {
			public Action[] getActions(Object target, Object sender) {
				return new Action[] { new Action("New"), new Action("Delete") };
			}

			@Override
			public void handleAction(Action action, Object sender, Object target) {
				if (action.getCaption() == "New") {
					Object newItemId = whiteList.addItem();
					TextField t = new TextField();
					t.setWidth("100%");
					whiteList.getItem(newItemId)
							.getItemProperty("whitebox").setValue(t);
				} else if (action.getCaption() == "Delete") {
					whiteList.removeItem(target);
				}
			}
		});
		textArea = new TextArea();
		textArea.setWidth("100%");
		textArea.setVisible(false);
		txtSrcFolder = new TextField("Source Folder");
		txtSrcFolder.setWidth("100%");
//		txtSrcFolder.setReadOnly(true);
		
		txtDestFolder = new TextField("Destination Folder");
		txtDestFolder.setWidth("100%");
//		txtDestFolder.setReadOnly(true);
		
		btnrun = new Button("Run");
		btnrun.addClickListener(new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
//				System.out.println(getReportFileFromComboBox());
				try {
					
					arg0 = FileUtil.getPathCode(app.getHashCode());
					String reportFolderName = (String) cmbReportFile.getValue();
					arg1 = FileUtil.getPathInstrumentationOfApp(app.getHashCode()) + "/" + reportFolderName;
					File f = new File(arg1);
					f.mkdirs();
					arg2 = getReportFileFromComboBox();
					System.out.println(arg0);
					System.out.println(arg1);
					System.out.println(arg2);
					Instrumentor.main(new String[]{arg0, arg1, arg2});
				} catch (IOException | IllegalClassFormatException e) {
					e.printStackTrace();
				}
				
			}
		});
		VerticalLayout parent = new VerticalLayout();
		parent.addComponent(lab);
		
		FormLayout fl = new FormLayout();
		
		fl.addComponent(cmbReportFile);
		fl.addComponent(textArea);
		fl.setSizeFull();
		fl.addComponent(textArea);
		fl.addComponent(blackList);
		fl.addComponent(whiteList);
		fl.addComponent(txtSrcFolder);
		fl.addComponent(txtDestFolder);
		fl.addComponent(btnrun);
		
		parent.addComponent(fl);
		
		parent.setMargin(true);
		addComponent(parent);
	}
	@Override
	public void enter(ViewChangeEvent event) {
		// TODO Auto-generated method stub
		if (event.getParameters() != null) {
			// split at "/", add each part as a label
			String[] msgs = event.getParameters().split("/");
			for (String msg : msgs) {
				appId = 0;
				if (msg != null && msg != "") {
					appId = Integer.parseInt(msg);
					System.out.println("enter Instrumentation view " + appId);

					try {
						app = AppDAO.getAppById(appId);
					} catch (ClassNotFoundException | SQLException e) {
						e.printStackTrace();
					}
					if (app != null) {
						appName = app.getName();
						fillComboBox(app);
						fillSrcAndDest(app);
					}

				}
			}
		}
	}
	 private String readXmlFile(File file){
			String xml = "";
			try (BufferedReader br = new BufferedReader(new FileReader(file))){
				String sCurrentLine;

				while ((sCurrentLine = br.readLine()) != null) {
					System.out.println(sCurrentLine);
					xml = xml + "\n" + sCurrentLine;
				} 
			}catch (IOException e) {
				e.printStackTrace();
			}
			return xml;
		}
	 private void fillComboBox(App app){
		 File staticAnalysisOutput = new File(FileUtil.getPathOutput(app.getHashCode()));
		 ArrayList<String> names = new ArrayList<String>(Arrays.asList(staticAnalysisOutput.list()));
		 for (String name : names) cmbReportFile.addItem(name);
	 }
	 private void fillSrcAndDest(App app){
		 String txtSrcFolder = FileUtil.getPathCode(app.getHashCode());
		 String txtDestFolder = FileUtil.getPathInstrumentationOfApp(app.getHashCode());
		 this.txtSrcFolder.setReadOnly(false);
		 this.txtSrcFolder.setValue(txtDestFolder);
		 this.txtSrcFolder.setReadOnly(true);
		 this.txtDestFolder.setReadOnly(false);
		 this.txtDestFolder.setValue(txtSrcFolder);
		 this.txtDestFolder.setReadOnly(true);
		 
	 }
	 private String getReportFileFromComboBox(){
		 String reportFile = "/report.xml";
		 return FileUtil.getPathOutput(app.getHashCode()) + "/" + cmbReportFile.getValue() + reportFile;
	 }
//	 private void initTable(Table table, String name, String property){
//		 table = new Table("ClassPath");
//			table.addContainerProperty("Classpath", TextField.class, null);
//			table.setPageLength(5);
//			table.setWidth("100%");
//			table.addItemClickListener(new ItemClickListener() {
//				public void itemClick(ItemClickEvent event) {
//					if (event.getButton() == MouseEventDetails.MouseButton.RIGHT) {
//						table.select(event.getItemId());
//					}
//				}
//
//			});
//
//			table.addActionHandler(new Action.Handler() {
//				public Action[] getActions(Object target, Object sender) {
//					return new Action[] { new Action("New"), new Action("Delete") };
//				}
//
//				@Override
//				public void handleAction(Action action, Object sender, Object target) {
//					if (action.getCaption() == "New") {
//						Object newItemId = table.addItem();
//						TextField t = new TextField();
//						t.setWidth("100%");
//						table.getItem(newItemId)
//								.getItemProperty("Classpath").setValue(t);
//					} else if (action.getCaption() == "Delete") {
//						table.removeItem(target);
//					}
//				}
//			});
//	 }
}
