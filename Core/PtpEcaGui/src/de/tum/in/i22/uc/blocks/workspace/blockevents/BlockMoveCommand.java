package de.tum.in.i22.uc.blocks.workspace.blockevents;

import java.awt.Point;

import de.tum.in.i22.uc.blocks.controller.WorkspaceController;
import de.tum.in.i22.uc.blocks.renderable.RenderableBlock;

/**
 * This has to handle movement of
 * 1. single blocks	[done]
 * 2. blocks with stubs automatically created when they are dragged to the page
 * 3. blocks with other blocks added (not automatically) to them.
 */
public class BlockMoveCommand implements Command {
	private Point pOld;
	private Point pNew;	
	//private long lBlockID;
	private WorkspaceController workspaceController;
	private RenderableBlock block;
	
	/**
	 * 
	 * @param point
	 * @param controller
	 * @param blockId
	 */
	public BlockMoveCommand(Point point, WorkspaceController controller, long blockId){
		pOld=point;		
		workspaceController=controller;
		pNew=null;
		block=workspaceController.getWorkspace().getEnv().getRenderableBlock(blockId);
	}
	
	@Override
	public void execute() {

	}
	
	//This blocked has moved from point. undo means take back to original point
	@Override
	public void undo() {
		pNew=block.getLocation();
		block.setLocation(pOld);
		block.moveConnectedBlocks();		
	}

	@Override
	public void redo() {
		block.setLocation(pNew);
		block.moveConnectedBlocks();
	}

}
