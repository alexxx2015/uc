package de.tum.in.i22.uc.blocks.workspace.blockevents;

import de.tum.in.i22.uc.blocks.controller.WorkspaceController;
import de.tum.in.i22.uc.blocks.workspace.Page;

/**
 * Handles actions from Edit menu of GUI
 * 
 * @author ELIJAH
 *
 */
public class EditMenuAction {
	private WorkspaceController workspaceController;
	private Page pageCurrent;
	
	/**
	 * 
	 * @param controller
	 */
	public EditMenuAction(WorkspaceController controller){
		workspaceController=controller;
		pageCurrent=workspaceController.getWorkspace().getPageNamed("Turtles");
	}
	
	public void undoCommand(){
				
	}
	
	public void redoCommand(){
		
	}
}
