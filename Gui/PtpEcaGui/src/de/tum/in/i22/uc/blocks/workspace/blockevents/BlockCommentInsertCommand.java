package de.tum.in.i22.uc.blocks.workspace.blockevents;

import de.tum.in.i22.uc.blocks.renderable.RenderableBlock;

/**
 * Handles block insertions
 * 
 * @author ELIJAH
 *
 */
public class BlockCommentInsertCommand implements Command {
	
	private BlockReceiverAction blockReceiverAction;
	private RenderableBlock rbBlock;
	private String sOldCommentText;
	private String sNewCommentText;
	
	/**
	 * 
	 * @param previousState
	 */
	public BlockCommentInsertCommand(PreviousState previousState){
		blockReceiverAction=new BlockReceiverAction();
		rbBlock=(RenderableBlock)previousState.getState();
		sOldCommentText=(String)previousState.getAlOtherStates().get(0);
		sNewCommentText=rbBlock.getComment().getText();
		
	}

	@Override
	public void execute() {
		
	}

	@Override
	public void undo() {
		blockReceiverAction.undoCommentChange(rbBlock, sOldCommentText);
	}

	@Override
	public void redo() {
		blockReceiverAction.redoCommentChange(rbBlock,sNewCommentText);
	}

}
