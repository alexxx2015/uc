package de.tum.in.i22.uc.blocks.workspace.blockevents;

import de.tum.in.i22.uc.blocks.controller.WorkspaceController;

/**
 * This command is not redoable or undoable and corresponds to
 * File > Save As
 * 
 * @author ELIJAH
 *
 */
public class SaveCommand implements Command {
	FileMenuAction menuAction;
	
	public SaveCommand(WorkspaceController controller, Invoker invoker){
		menuAction=new FileMenuAction(controller,invoker);
	}
	
	@Override
	public void execute() {
		
		menuAction.savePolicyFile();
	}

	@Override
	public void undo() {
		

	}

	@Override
	public void redo() {
		

	}

}
