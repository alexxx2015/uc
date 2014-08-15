package de.tum.in.i22.uc.blocks.workspace.blockevents;

import java.awt.Point;

import de.tum.in.i22.uc.blocks.renderable.Param;
import de.tum.in.i22.uc.blocks.renderable.RenderableBlock;

/**
 * Handles parameter movement command
 * 
 * @author ELIJAH
 *
 */
public class ParamMoveCommand implements Command {

	private BlockReceiverAction receiverAction;
	private RenderableBlock rbParent;
	private Param cChildParam;
	private Point pointOld;
	private Point pointNew;
	
	/**
	 * 
	 * @param previousState
	 */
	public ParamMoveCommand(PreviousState previousState){
		receiverAction=new BlockReceiverAction();		
		cChildParam=(Param)previousState.getState();
		rbParent=(RenderableBlock)previousState.getAlOtherStates().get(0);
		pointOld=(Point)previousState.getAlOtherStates().get(1);
		pointNew=null;
	}

	@Override
	public void execute() {
		

	}

	@Override
	public void undo() {
		
		pointNew=cChildParam.getLocation();
		receiverAction.undoRedoParamMove(cChildParam, rbParent, pointOld);
	}

	@Override
	public void redo() {
		
		//This is very important becos redoing comment addition
		//creates a new comment object and ours persisted here is stale
		cChildParam=rbParent.getParam();
		receiverAction.undoRedoParamMove(cChildParam, rbParent, pointNew);
	}

}
