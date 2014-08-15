package de.tum.in.i22.uc.blocks.workspace.blockevents;

import java.awt.Dimension;

import de.tum.in.i22.uc.blocks.renderable.Param;
import de.tum.in.i22.uc.blocks.renderable.RenderableBlock;

/**
 * Handles parameter resize events
 * 
 * @author ELIJAH
 *
 */
public class ParamResizeCommand implements Command {

	private BlockReceiverAction receiverAction;
	private RenderableBlock rbParent;
	private Param cChildParam;
	private Dimension dimensionOld;
	private Dimension dimensionNew;
	
	/**
	 * 
	 * @param previousState
	 */
	public ParamResizeCommand(PreviousState previousState){
		receiverAction=new BlockReceiverAction();		
		cChildParam=(Param)previousState.getState();
		rbParent=(RenderableBlock)previousState.getAlOtherStates().get(0);
		dimensionOld=(Dimension)previousState.getAlOtherStates().get(1);
		dimensionNew=cChildParam.getSize();
	}

	@Override
	public void execute() {
		

	}

	@Override
	public void undo() {
				
		receiverAction.undoRedoParamResize(cChildParam, rbParent, dimensionOld);
	}

	@Override
	public void redo() {
		
		//This is very important becos redoing comment addition
		//creates a new comment object and ours persisted here is stale
		cChildParam=rbParent.getParam();
		receiverAction.undoRedoParamResize(cChildParam, rbParent, dimensionNew);
	}

}
