package de.tum.in.i22.uc.blocks.workspace.blockevents;

import java.util.ArrayList;

import de.tum.in.i22.uc.blocks.controller.WorkspaceController;
import de.tum.in.i22.uc.blocks.renderable.RenderableBlock;

/**
 * Handles addition and removal of blocks
 * 
 * @author ELIJAH
 *
 */
public class BlockAddCommand implements Command {
	private BlockReceiverAction blockReceiverAction;	
	private RenderableBlock rbBlock;	
	
	/**
	 * 
	 * @param controller
	 * @param block
	 * @param listSiblings
	 */
	public BlockAddCommand(WorkspaceController controller, RenderableBlock block, ArrayList<Long> listSiblings){
		blockReceiverAction=new BlockReceiverAction(controller, listSiblings);
		rbBlock=block;		
	}
	
	@Override
	public void execute() {
		

	}

	/*
	 * Since a block has been added, we remove it
	 */
	@Override
	public void undo() {
				
		blockReceiverAction.removeBlock(rbBlock);		
	}

	/*
	 * Just add the block again
	 */
	@Override
	public void redo() {
				
		blockReceiverAction.addBlock(rbBlock);		
	}

}
