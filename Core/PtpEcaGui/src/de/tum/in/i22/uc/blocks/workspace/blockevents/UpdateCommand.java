package de.tum.in.i22.uc.blocks.workspace.blockevents;

import de.tum.in.i22.uc.blocks.controller.WorkspaceController;

/**
 * Handles File > Save action from the GUI 
 * 
 * @author ELIJAH
 *
 */
public class UpdateCommand implements Command {
	FileMenuAction menuAction;
	
	/**
	 * 
	 * @param controller
	 * @param invoker
	 */
	public UpdateCommand(WorkspaceController controller, Invoker invoker){
		menuAction=new FileMenuAction(controller,invoker);
	}
	
	@Override
	public void execute() {
		
		menuAction.updatePolicyFile();
	}

	@Override
	public void undo() {
		

	}

	@Override
	public void redo() {
		

	}

}
