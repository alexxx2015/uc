package de.tum.in.i22.uc.blocks.workspace.blockevents;



import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileFilter;

import de.tum.in.i22.uc.blocks.controller.WorkspaceController;
import de.tum.in.i22.uc.blocks.renderable.RenderableBlock;
import de.tum.in.i22.uc.blocks.workspace.Page;

import java.util.List;

import java.io.File;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.IOException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;


/**
 * 21-Feb-2013
 * This class encapsulates logic for all menu actions 
 * associated with the file menu items like open, close
 * and save. This is passed into the concrete command (e.g. open)
 * 
 */
public class FileMenuAction {
	
	private JFileChooser fcDialogBox;
	private WorkspaceController workspaceController;	
	private Document docPolicy;
	private Page pageCurrent;	
	//this must be static to avoid null pointers
	static public File fCurrentFile;
	private Invoker invoker;
	static private int iUndoRedoStackSize;
	
	/**
	 * Creates an instance of this class with a controller object
	 * 
	 * @param controller
	 */
	public FileMenuAction(WorkspaceController controller){
		//workspace controller
		workspaceController=controller;
		pageCurrent=workspaceController.getWorkspace().getCurrentPage();			
		invoker=new Invoker();
		//preparing dialog box for open and save commands
		fcDialogBox=new JFileChooser();		
		FileFilter fnefFilter=new FileNameExtensionFilter("Policy Files (.xml)","xml");		
		fcDialogBox.setFileFilter(fnefFilter);			
	}
	
	/**
	 * 
	 * @param controller
	 * @param invoker
	 */
	public FileMenuAction(WorkspaceController controller,Invoker invoker){
		this(controller);
		this.invoker=invoker;
	}
	
	private boolean isUnsavedFile(){
		return getCurrentPolicyFileName().trim().toLowerCase().equals("usage control policy");
	}
	
	
	/**
	 * 03 March 2013. To create a new workspace, helping 
	 * user to save any work present if any 
	 */
	public void newPolicyFile(){
	
	//no file currently opened...
	if(isUnsavedFile()){
		//...but the user tried doing some things		
		if(pageCurrent.getBlocks().size()>0 || invoker.getStackSizes()>0/*hasPolicyFileChanged()*/)
			requestSaving(false,true,"This action clears unsaved work.\nWould you like to save current content?");				
	}
	else {			
		//This means user has opened a policy file and made some changes to it	
//		System.out.println("Monitor1: "+iUndoRedoStackSize);
//		System.out.println("Monitor1: "+invoker.getStackSizes());
		if(iUndoRedoStackSize!=invoker.getStackSizes())
			requestSaving(false,false,"Changes have been made to currently-loaded file. Save changes?");
		else closePolicyFile();
	}
		
	}
	
	/**
	 * 
	 * @param exiting: user wants to exit the program
	 * @param create: user did some unsaved work and calls File > new
	 * @param prompt: specialized message
	 */
	private void requestSaving(boolean exiting, boolean create, String prompt){
		
		//User has done some work and is left on the page
		int result=JOptionPane.showConfirmDialog(workspaceController.getApplicationFrame(), prompt, "Unsaved Information", JOptionPane.YES_NO_OPTION);
		if(result==JOptionPane.YES_OPTION)
			{
				try{	
						if(!exiting){
									if(create)savePolicyFile();
									else save();
									closePolicyFile();
							}
						else{
							//user want to exit the program
							if(create) savePolicyFile();
							else save();
						}
					}
						catch(Exception e){
							e.printStackTrace();
						}
			}
			else {
				if(!exiting)
				closePolicyFile();
			}		
		}
	
	/**
	 *  
	 * @return The current policy file name from the GUI
	 */
	private String getCurrentPolicyFileName(){
		return workspaceController.getApplicationFrame().getTitle();
	}
	
	/**
	 * Sets the name of the current policy file
	 * 
	 * @param name
	 */
	private void setCurrentPolicyFileName(String name){
		workspaceController.getApplicationFrame().setTitle("Usage Control Policy - "+name);
	}
	
	/**
	 * Resets the current policy file name.
	 */
	private void resetCurrentPolicyFileName(){
		workspaceController.getApplicationFrame().setTitle("Usage Control Policy");
	}
	
	
	/**
	 * Called to open policy files. File > Open 
	 * I realized the edu.mit.blocks.workspace.Page class has methods
	 * that make this task easy
	 */
	public void openPolicyFile(){
		fcDialogBox.setDialogType(JFileChooser.OPEN_DIALOG);
		fcDialogBox.setDialogTitle("Select a policy file to open");
		fcDialogBox.setCurrentDirectory(new File(System.getProperty("user.dir")));
		int iReturn=fcDialogBox.showOpenDialog(workspaceController.getApplicationFrame());
		
		try{
		
		//code to open policy file
		if(iReturn==JFileChooser.APPROVE_OPTION){
						
			if(!fcDialogBox.getSelectedFile().exists()) return;			
			docPolicy=DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(fcDialogBox.getSelectedFile());
			Node nodePolicy=docPolicy.getDocumentElement().cloneNode(true);
			List<RenderableBlock> lRenderableBlocks=pageCurrent.loadPageFrom(nodePolicy, true);
			pageCurrent.addLoadedBlocks(lRenderableBlocks, true);			
			//
			fCurrentFile=fcDialogBox.getSelectedFile();						
			setCurrentPolicyFileName(fcDialogBox.getSelectedFile().getName());						
			//
			invoker.reset();
			iUndoRedoStackSize=invoker.getStackSizes();					
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
	}
	

	/**
	 * File > Save As and File > Save actions share this method
	 * 
	 * @throws ParserConfigurationException
	 * @throws TransformerFactoryConfigurationError
	 * @throws TransformerException
	 * @throws IOException
	 */
	private void save() throws ParserConfigurationException, TransformerFactoryConfigurationError, TransformerException, IOException{
		docPolicy=DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
		Node nodePolicy=pageCurrent.getSaveNode(docPolicy);
		Transformer t= TransformerFactory.newInstance().newTransformer();
		
		//Saving
		if(fCurrentFile!=null){
			FileWriter fWriter=new FileWriter(fCurrentFile,false);
			BufferedWriter bWriter=new BufferedWriter(fWriter);
			t.transform(new DOMSource(nodePolicy), new StreamResult(bWriter));
			iUndoRedoStackSize=invoker.getStackSizes();
			System.out.println("Saving. stacksize from invoker: "+invoker.getStackSizes());
			System.out.println("Saving. stacksize from URM: "+iUndoRedoStackSize);
		}
	}
	
	/**
	 * I realized the edu.mit.blocks.workspace.Page class has methods
	 * that make this task easy
	 * called to save policy files. Same as File > Save As
	 * 
	 */
	public void savePolicyFile(){
		
		fcDialogBox.setDialogType(JFileChooser.SAVE_DIALOG);
		fcDialogBox.setDialogTitle("Specify file name of new policy");
		fcDialogBox.setApproveButtonText("SAVE");
		fcDialogBox.setCurrentDirectory(new File(System.getProperty("user.dir")));
		int iReturn=fcDialogBox.showSaveDialog(workspaceController.getApplicationFrame());
		//File file=null;		
		try{			 
				//code to save policy file
				if(iReturn==JFileChooser.APPROVE_OPTION){	
					//automatically append .xml or .policy to selected file if not present									
					String sFilename=getCorrectFileName(fcDialogBox.getSelectedFile().getAbsolutePath());					
					fCurrentFile=new File(sFilename);
					save();
					setCurrentPolicyFileName(getCorrectFileName(fcDialogBox.getSelectedFile().getName()));
				}
		}
		catch(Exception e){
			e.printStackTrace(System.out);
		}
	}
	
	/**
	 * Contains logic for avoiding extension repetitions
	 * in file name, e.g. .xml.xml
	 * 
	 * @param name
	 * @return A proper file name with one extension
	 */
	private String getCorrectFileName(String name){
		String sTrueFileName="";
		int iFirstOccur=name.indexOf(".xml", 0);		
		if(iFirstOccur==-1){
			sTrueFileName=name+".xml";												
		}					
		else{
			//avoid multiple .xml endings
			//get only file name without endings						
			//System.out.println("First Occurence: "+iFirstOccur);
			if(iFirstOccur>0){
				String sFile=name.substring(0, iFirstOccur);	
				sTrueFileName=sFile+".xml";										
			}
		}
		return sTrueFileName;
	}
	
	/**
	 * Called to update changes in policy files. Same as File > Save
	 * 
	 */
	public void updatePolicyFile(){		
		try{
			//save into temporary file
			if(isUnsavedFile()) savePolicyFile();
			else save();		
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Called to close a file probably before opening a new one
	 * 
	 */
	public void closePolicyFile(){				
		pageCurrent.clearPage();
		resetCurrentPolicyFileName();
		invoker.reset();
		iUndoRedoStackSize=invoker.getStackSizes();
	}
	
	
	/**
	 * Called to kill our program
	 * 
	 */
	public void exitProgram(){
		//no file currently opened...
		if(isUnsavedFile()){
			//...but the user tried doing some things		
			if(pageCurrent.getBlocks().size()>0 || invoker.hasPolicyFileChanged())
				requestSaving(true,true,"Program is closing.\nWould you like to save current content?");				
		}
		else {			
			//This means user has opened a policy file and made some changes to it	
//			System.out.println("Monitor1: "+iUndoRedoStackSize);
//			System.out.println("Monitor1: "+invoker.getStackSizes());
			if(iUndoRedoStackSize!=invoker.getStackSizes())
				requestSaving(true,false,"Program is exiting but changes have been made \nto currently-loaded file. Save changes?");			
		}
		System.exit(0);
	}
	
}
