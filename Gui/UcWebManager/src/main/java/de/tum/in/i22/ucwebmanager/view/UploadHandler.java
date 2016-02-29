package de.tum.in.i22.ucwebmanager.view;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import com.vaadin.navigator.View;
import com.vaadin.ui.Upload.Receiver;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Upload.SucceededListener;

import de.tum.in.i22.ucwebmanager.Configuration;

public class UploadHandler implements Receiver, SucceededListener{
	
	private View view;
	
	UploadHandler(View v){
		this.view = v;
	}

	@Override
	public void uploadSucceeded(SucceededEvent event) {
		StaticAnalysisView myview = (StaticAnalysisView)this.view;
		
		// TODO Auto-generated method stub
		if (event.getComponent().getCaption() == "CGFile")
			myview.txtCGFile.setValue(Configuration.WebAppRoot + "/" + myview.txtAnalysisName.getValue() + "/"
					+ event.getFilename());
		else if (event.getComponent().getCaption() == "SDGFile")
			myview.txtSDGFile.setValue(Configuration.WebAppRoot + "/" + myview.txtAnalysisName.getValue()
					+ "/" + event.getFilename());
		else if (event.getComponent().getCaption() == "reportFile")
			myview.txtReportFile.setValue(Configuration.WebAppRoot + "/" + myview.txtAnalysisName.getValue()
					+ "/" + event.getFilename());

		else if (event.getComponent().getCaption() == "Source and sink File")
			myview.txtFldSnSFile.setValue(Configuration.WebAppRoot + "/apps/ws-flowAnaluser/"
					+ myview.appName + "/" + myview.txtAnalysisName.getValue() + "/"
					+ event.getFilename());
	}

	@Override
	public OutputStream receiveUpload(String filename, String mimeType) {
		StaticAnalysisView myview = (StaticAnalysisView)this.view;

		File directory = new File(myview.staticAnalysisPath + "/apps/ws-flowAnaluser"
				+ "/" + myview.appName + "/" + myview.txtAnalysisName.getValue());

		if (!directory.exists()) {
			if (directory.mkdirs()) {
				System.out.println("Directory is created!");

			}
		}
		OutputStream fos = null; // Output stream to write to
		File file = new File(myview.staticAnalysisPath + "/apps/ws-flowAnaluser" + "/"
				+ myview.appName + "/" + myview.txtAnalysisName.getValue() + "/" + filename);// strUploadFilePathCG+
		// file.renameTo(new File(strUploadFilePathCG + filename));

		try {
			// Open the file for writing.
			fos = new FileOutputStream(file);
		} catch (final java.io.FileNotFoundException e) {
			// Error while opening the file. Not reported here.
			e.printStackTrace();
			return null;
		}

		return fos; // Return the output stream to write to
	}

}
