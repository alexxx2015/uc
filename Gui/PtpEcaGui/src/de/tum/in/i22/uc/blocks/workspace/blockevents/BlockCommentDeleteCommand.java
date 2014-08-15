package de.tum.in.i22.uc.blocks.workspace.blockevents;

import de.tum.in.i22.uc.blocks.renderable.RenderableBlock;

/**
 * Handles comment deletion
 * 
 * @author ELIJAH
 *
 */
public class BlockCommentDeleteCommand implements Command {

	private BlockReceiverAction blockReceiverAction;
	private RenderableBlock rbBlock;
	private String sOldCommentText;
	private String sNewCommentText;
	
	/**
	 * Prepares an instance of this class using information
	 * of previous state
	 * 
	 * @param previousState
	 */
	public BlockCommentDeleteCommand(PreviousState previousState){
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
		blockReceiverAction.redoCommentChange(rbBlock, sOldCommentText);
	}

	@Override
	public void redo() {
		blockReceiverAction.undoCommentChange(rbBlock,sNewCommentText);
	}
}
