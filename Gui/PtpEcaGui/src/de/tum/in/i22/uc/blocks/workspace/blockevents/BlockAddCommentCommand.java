package de.tum.in.i22.uc.blocks.workspace.blockevents;

import de.tum.in.i22.uc.blocks.renderable.RenderableBlock;

/**
 * Handles addition of block comment
 * 
 * @author ELIJAH
 *
 */
public class BlockAddCommentCommand implements Command {
	
	private RenderableBlock rbBlock;
	private BlockReceiverAction blockReceiverAction;
	private String sOldComment;	
	
	/**
	 * 
	 * @param block
	 */
	public BlockAddCommentCommand(RenderableBlock block){
		rbBlock=block;
		blockReceiverAction=new BlockReceiverAction();		
		sOldComment="";		
	}

	@Override
	public void execute() {

	}

	@Override
	public void undo() {
		blockReceiverAction.removeComment(rbBlock);
	}

	@Override
	public void redo() {
		blockReceiverAction.createComment(rbBlock,sOldComment);
	}

}
