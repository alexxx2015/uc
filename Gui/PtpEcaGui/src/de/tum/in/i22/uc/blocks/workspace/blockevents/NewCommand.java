package de.tum.in.i22.uc.blocks.workspace.blockevents;

import de.tum.in.i22.uc.blocks.controller.WorkspaceController;

/**
 * Handles File > New GUI action
 * 
 * @author ELIJAH
 *
 */
public class NewCommand implements Command {
	
	private FileMenuAction menuAction;
	public NewCommand(WorkspaceController wc, Invoker i){
		menuAction=new FileMenuAction(wc,i);
	}

	@Override
	public void execute() {
		
		menuAction.newPolicyFile();
	}

	@Override
	public void undo() {
		

	}

	@Override
	public void redo() {
		

	}

}
