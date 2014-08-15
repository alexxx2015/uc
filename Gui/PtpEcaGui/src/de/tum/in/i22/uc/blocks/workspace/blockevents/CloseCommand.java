package de.tum.in.i22.uc.blocks.workspace.blockevents;


import de.tum.in.i22.uc.blocks.controller.WorkspaceController;


/**
 * Handles GUI action File > Close 
 * 
 * @author ELIJAH
 *
 */
public class CloseCommand implements Command {
	FileMenuAction menuAction;
	
	/**
	 * 
	 * @param controller
	 */
	public CloseCommand(WorkspaceController controller){
		menuAction=new FileMenuAction(controller);
	}
	
	@Override
	public void execute() {
		menuAction.closePolicyFile();
	}

	@Override
	public void undo() {

	}

	@Override
	public void redo() {

	}

}
