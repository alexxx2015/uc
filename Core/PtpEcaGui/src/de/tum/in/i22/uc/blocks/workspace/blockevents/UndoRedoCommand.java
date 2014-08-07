package de.tum.in.i22.uc.blocks.workspace.blockevents;

import de.tum.in.i22.uc.blocks.controller.WorkspaceController;

/**
 * Handles the event of user click Edit > Undo or
 * Edit > Redo from the menu bar
 * 
 * @author ELIJAH
 *
 */
public class UndoRedoCommand implements Command {
	EditMenuAction menuAction;
	
	public UndoRedoCommand(WorkspaceController controller){
		menuAction=new EditMenuAction(controller);
	}
	
	@Override
	public void execute() {
		
		
	}

	@Override
	public void undo() {
		
		menuAction.undoCommand();
	}

	@Override
	public void redo() {
		
		menuAction.redoCommand();
	}

}
