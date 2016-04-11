package de.tum.in.i22.ucwebmanager.view;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Upload.SucceededListener;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.ui.VerticalLayout;

public class InstrumentationView extends VerticalLayout implements View{
	String appName;

	private Upload upload;
	private TextArea textArea;
	File file;
	public InstrumentationView() {
		Label lab = new Label("Instrumentation");
		lab.setSizeUndefined();
		lab.addStyleName(ValoTheme.LABEL_H1);
		lab.addStyleName(ValoTheme.LABEL_NO_MARGIN);

		textArea = new TextArea();
		textArea.setWidth("100%");
		textArea.setVisible(false);
		
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
		//fl.addComponent(textArea);
		fl.setSizeFull();
		
		parent.addComponent(fl);
		parent.addComponent(textArea);
		
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
}
