package de.tum.in.i22.uc.blocks.workspace.blockevents;

import java.awt.Dimension;
import java.awt.Point;

import de.tum.in.i22.uc.blocks.renderable.Comment;
import de.tum.in.i22.uc.blocks.renderable.RenderableBlock;

/**
 * Handles comment resize events
 * 
 * @author ELIJAH
 *
 */
public class CommentResizeCommand implements Command {

	private BlockReceiverAction receiverAction;
	private RenderableBlock rbParent;
	private Comment cChildComment;
	private Dimension dimensionOld;
	private Dimension dimensionNew;
	
	/**
	 * 
	 * @param previousState
	 */
	public CommentResizeCommand(PreviousState previousState){
		receiverAction=new BlockReceiverAction();		
		cChildComment=(Comment)previousState.getState();
		rbParent=(RenderableBlock)previousState.getAlOtherStates().get(0);
		dimensionOld=(Dimension)previousState.getAlOtherStates().get(1);
		dimensionNew=cChildComment.getSize();
	}

	@Override
	public void execute() {
		

	}

	@Override
	public void undo() {
				
		receiverAction.undoRedoCommentResize(cChildComment, rbParent, dimensionOld);
	}

	@Override
	public void redo() {
		
		//This is very important becos redoing comment addition
		//creates a new comment object and ours persisted here is stale
		cChildComment=rbParent.getComment();
		receiverAction.undoRedoCommentResize(cChildComment, rbParent, dimensionNew);
	}
}
