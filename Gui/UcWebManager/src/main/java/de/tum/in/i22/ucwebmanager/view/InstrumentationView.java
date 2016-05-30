package de.tum.in.i22.ucwebmanager.view;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;

import com.vaadin.event.Action;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.MouseEventDetails;
import com.vaadin.ui.Button;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Upload.SucceededListener;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.tum.in.i22.ucwebmanager.DB.App;

public class InstrumentationView extends VerticalLayout implements View{
	App app;
	File file;
	String appName;
	private Upload upload;
	private TextArea textArea;
	private final Table blackbox, whitebox;
	private Button btnrun;
	private TextField txtSrcFolder, txtDestFolder;
	
	public InstrumentationView() {
		Label lab = new Label("Instrumentation");
		lab.setSizeUndefined();
		lab.addStyleName(ValoTheme.LABEL_H1);
		lab.addStyleName(ValoTheme.LABEL_NO_MARGIN);
//		blackbox = new TableGrid("Black Box", "blackbox");
//		whitebox = new TableGrid("White box", "whitebox");
		blackbox = new Table("Black Box");
		blackbox.addContainerProperty("blackbox", TextField.class, null);
		blackbox.setPageLength(5);
		blackbox.setWidth("100%");
		blackbox.addItemClickListener(new ItemClickListener() {
			public void itemClick(ItemClickEvent event) {
				if (event.getButton() == MouseEventDetails.MouseButton.RIGHT) {
					blackbox.select(event.getItemId());
				}
			}

		});

		blackbox.addActionHandler(new Action.Handler() {
			public Action[] getActions(Object target, Object sender) {
				return new Action[] { new Action("New"), new Action("Delete") };
			}

			@Override
			public void handleAction(Action action, Object sender, Object target) {
				if (action.getCaption() == "New") {
					Object newItemId = blackbox.addItem();
					TextField t = new TextField();
					t.setWidth("100%");
					blackbox.getItem(newItemId)
							.getItemProperty("blackbox").setValue(t);
				} else if (action.getCaption() == "Delete") {
					blackbox.removeItem(target);
				}
			}
		});
		whitebox = new Table("White box");
		whitebox.addContainerProperty("whitebox", TextField.class, null);
		whitebox.setPageLength(5);
		whitebox.setWidth("100%");
		whitebox.addItemClickListener(new ItemClickListener() {
			public void itemClick(ItemClickEvent event) {
				if (event.getButton() == MouseEventDetails.MouseButton.RIGHT) {
					whitebox.select(event.getItemId());
				}
			}

		});

		whitebox.addActionHandler(new Action.Handler() {
			public Action[] getActions(Object target, Object sender) {
				return new Action[] { new Action("New"), new Action("Delete") };
			}

			@Override
			public void handleAction(Action action, Object sender, Object target) {
				if (action.getCaption() == "New") {
					Object newItemId = whitebox.addItem();
					TextField t = new TextField();
					t.setWidth("100%");
					whitebox.getItem(newItemId)
							.getItemProperty("whitebox").setValue(t);
				} else if (action.getCaption() == "Delete") {
					whitebox.removeItem(target);
				}
			}
		});
		textArea = new TextArea();
		textArea.setWidth("100%");
		textArea.setVisible(false);
		txtSrcFolder = new TextField("Source Folder");
		txtSrcFolder.setWidth("100%");
		txtDestFolder = new TextField("Destination Folder");
		txtDestFolder.setWidth("100%");
		btnrun = new Button("Run");
		Upload.Receiver receiver = new Upload.Receiver() {
			FileOutputStream fos = null;
			@Override
			public OutputStream receiveUpload(String filename, String mimeType) {
				// TODO Auto-generated method stub
				
				try {
					file = new File("/home/dat/tmp/" + filename);
					fos = new FileOutputStream(file);
				} catch (FileNotFoundException e) {
					
					e.printStackTrace();
					return null;
				}
				//textArea.setValue(readXmlFile(file));
				return fos;
			}
			
		};
		upload = new Upload("Upload your xml file",receiver);
		upload.setWidth("100%");
		upload.addSucceededListener(new SucceededListener() {
			
			@Override
			public void uploadSucceeded(SucceededEvent event) {
				// TODO Auto-generated method stub
				textArea.setVisible(true);
				textArea.setValue(readXmlFile(file));
			}

			
		});
		VerticalLayout parent = new VerticalLayout();
		parent.addComponent(lab);
		
		FormLayout fl = new FormLayout();
		
		fl.addComponent(upload);
		fl.addComponent(textArea);
		fl.setSizeFull();
		fl.addComponent(textArea);
		fl.addComponent(blackbox);
		fl.addComponent(whitebox);
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
