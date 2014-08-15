package de.tum.in.i22.uc.blocks.workspace.blockevents;

import java.awt.Point;

import de.tum.in.i22.uc.blocks.renderable.Comment;
import de.tum.in.i22.uc.blocks.renderable.RenderableBlock;

/**
 * Handles comment movement events
 * 
 * @author ELIJAH
 *
 */
public class CommentMoveCommand implements Command {
	
	private BlockReceiverAction receiverAction;
	private RenderableBlock rbParent;
	private Comment cChildComment;
	private Point pointOld;
	private Point pointNew;
	
	/**
	 * 
	 * @param previousState
	 */
	public CommentMoveCommand(PreviousState previousState){
		receiverAction=new BlockReceiverAction();		
		cChildComment=(Comment)previousState.getState();
		rbParent=(RenderableBlock)previousState.getAlOtherStates().get(0);
		pointOld=(Point)previousState.getAlOtherStates().get(1);
		pointNew=null;
	}

	@Override
	public void execute() {
		

	}

	@Override
	public void undo() {
		
		pointNew=cChildComment.getLocation();
		receiverAction.undoRedoCommentMove(cChildComment, rbParent, pointOld);
	}

	@Override
	public void redo() {
		
		//This is very important becos redoing comment addition
		//creates a new comment object and ours persisted here is stale
		cChildComment=rbParent.getComment();
		receiverAction.undoRedoCommentMove(cChildComment, rbParent, pointNew);
	}

}
