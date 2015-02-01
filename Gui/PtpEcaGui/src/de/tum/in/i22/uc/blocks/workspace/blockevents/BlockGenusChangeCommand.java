package de.tum.in.i22.uc.blocks.workspace.blockevents;

import de.tum.in.i22.uc.blocks.controller.WorkspaceController;
import de.tum.in.i22.uc.blocks.renderable.RenderableBlock;
import de.tum.in.i22.uc.blocks.workspace.Page;

/**
 * Handles change of block genus
 * 
 * @author ELIJAH
 *
 */
public class BlockGenusChangeCommand implements Command {
			
	private RenderableBlock rbBlock;
	private BlockReceiverAction blockReceiverAction;
	private Page pgCurrent;
	
	/**
	 * 
	 * @param controller
	 * @param rblock
	 * @param oldGenusName
	 */
	public BlockGenusChangeCommand(WorkspaceController controller, RenderableBlock rblock, String oldGenusName){
		rbBlock=rblock;
		pgCurrent=controller.getWorkspace().getCurrentPage();
		blockReceiverAction=new BlockReceiverAction(controller,oldGenusName,rbBlock.getGenus());
	}

	@Override
	public void execute() {

	}

	//Set old genus name
	@Override
	public void undo() {
		blockReceiverAction.setOldBlockGenusName(rbBlock, pgCurrent);
	}

	@Override
	public void redo() {
		blockReceiverAction.setNewBlockGenusName(rbBlock, pgCurrent);
	}

}
