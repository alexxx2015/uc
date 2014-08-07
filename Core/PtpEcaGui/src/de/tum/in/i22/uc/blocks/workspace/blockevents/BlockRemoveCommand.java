package de.tum.in.i22.uc.blocks.workspace.blockevents;

import de.tum.in.i22.uc.blocks.codeblocks.BlockLink;
import de.tum.in.i22.uc.blocks.controller.WorkspaceController;
import de.tum.in.i22.uc.blocks.renderable.RenderableBlock;
import de.tum.in.i22.uc.blocks.workspace.Page;

/**
 * Handles block removal events
 * 
 * @author ELIJAH
 *
 */
public class BlockRemoveCommand implements Command {
	
	private BlockReceiverAction blockReceiverAction;	
	private RenderableBlock rbBlock;
	private Page pgCurrent;
	private BlockLink link;
	
	/**
	 * 
	 * 
	 * @param controller
	 * @param rblock
	 * @param link
	 */
	public BlockRemoveCommand(WorkspaceController controller, RenderableBlock rblock, BlockLink link){
		blockReceiverAction=new BlockReceiverAction();		
		rbBlock=rblock;
		pgCurrent=controller.getWorkspace().getCurrentPage();
		this.link=link;
	}

	@Override
	public void execute() {

	}

	//restore block from trash can
	@Override
	public void undo() {
		blockReceiverAction.restoreBlock(rbBlock, pgCurrent,link);
	}

	@Override
	public void redo() {
		blockReceiverAction.deleteBlock(rbBlock, pgCurrent);
	}

}
