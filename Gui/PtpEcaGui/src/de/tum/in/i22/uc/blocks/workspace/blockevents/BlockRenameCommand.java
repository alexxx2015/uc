package de.tum.in.i22.uc.blocks.workspace.blockevents;

import de.tum.in.i22.uc.blocks.controller.WorkspaceController;
import de.tum.in.i22.uc.blocks.renderable.RenderableBlock;
import de.tum.in.i22.uc.blocks.workspace.Page;

/**
 * Handles block renaming event
 * 
 * @author ELIJAH
 *
 */
public class BlockRenameCommand implements Command {
	
	private RenderableBlock rbBlock;	
	private BlockReceiverAction blockReceiverAction;
	private Page pgCurrent;
	
	/**
	 * 
	 * 
	 * @param controller
	 * @param block
	 * @param oldName
	 * @param newName
	 */
	public BlockRenameCommand(WorkspaceController controller, RenderableBlock block, String oldName, String newName){		
		rbBlock=block;		
		blockReceiverAction=new BlockReceiverAction(controller,oldName,newName);
		pgCurrent=controller.getWorkspace().getCurrentPage();
	}

	@Override
	public void execute() {

	}

	//A new name label has been set. Revert to old name label
	@Override
	public void undo() {
		blockReceiverAction.setOldBlockLabel(rbBlock, pgCurrent);
	}

	@Override
	public void redo() {
		blockReceiverAction.setNewBlockLabel(rbBlock, pgCurrent);
	}

}
