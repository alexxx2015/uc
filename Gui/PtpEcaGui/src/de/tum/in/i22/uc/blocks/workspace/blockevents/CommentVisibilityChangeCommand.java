package de.tum.in.i22.uc.blocks.workspace.blockevents;

import de.tum.in.i22.uc.blocks.renderable.RenderableBlock;

/**
 * Handles comment visibility change events
 * 
 * @author ELIJAH
 *
 */
public class CommentVisibilityChangeCommand implements Command {
	private RenderableBlock rbBlock;
	private BlockReceiverAction receiverAction;
	private boolean bOldState;
	private boolean bNewState;
	
	/**
	 * 
	 * @param previousState
	 */
	public CommentVisibilityChangeCommand(PreviousState previousState){
		rbBlock=(RenderableBlock)previousState.getState();
		bNewState=(Boolean)previousState.getAlOtherStates().get(0);
		bOldState=!bNewState;
		receiverAction=new BlockReceiverAction();
	}
	
	@Override
	public void execute() {
		

	}

	@Override
	public void undo() {
		
		receiverAction.changeCommentVisibility(rbBlock,bOldState);
	}

	@Override
	public void redo() {
		
		receiverAction.changeCommentVisibility(rbBlock,bNewState);
	}

}
