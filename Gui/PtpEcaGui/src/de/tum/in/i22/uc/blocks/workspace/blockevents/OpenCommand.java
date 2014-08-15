package de.tum.in.i22.uc.blocks.workspace.blockevents;

import de.tum.in.i22.uc.blocks.controller.WorkspaceController;

/**
 * Handles File > Open command
 * for opening policy files. Does not require undo and redo actions
 * 
 * @author ELIJAH
 *
 */
public class OpenCommand implements Command {
	public FileMenuAction menuAction;
	public OpenCommand(WorkspaceController wc, Invoker i){
		menuAction=new FileMenuAction(wc,i);
	}
	
	@Override
	public void execute() {
		
		menuAction.openPolicyFile();
	}

	@Override
	public void undo() {
		

	}

	@Override
	public void redo() {
		

	}

}
