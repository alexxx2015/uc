package de.tum.in.i22.uc.blocks.workspace.blockevents;

import de.tum.in.i22.uc.blocks.controller.WorkspaceController;

/**
 * Handles exit action of GUI
 * 
 * @author ELIJAH
 *
 */
public class ExitCommand implements Command {
	FileMenuAction menuAction;
	
	/**
	 * 
	 * @param controller
	 * @param invoker
	 */
	public ExitCommand(WorkspaceController controller, Invoker invoker){
		menuAction=new FileMenuAction(controller,invoker);
	}
	
	@Override
	public void execute() {
		
		menuAction.exitProgram();
	}

	@Override
	public void undo() {
		

	}

	@Override
	public void redo() {
		

	}

}
