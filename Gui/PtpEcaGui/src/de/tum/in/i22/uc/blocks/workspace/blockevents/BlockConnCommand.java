package de.tum.in.i22.uc.blocks.workspace.blockevents;

import de.tum.in.i22.uc.blocks.codeblocks.BlockLink;

/**
 * Handles block connections
 * 
 * @author ELIJAH
 *
 */
public class BlockConnCommand implements Command {
	
	private BlockLink blockLink;
	private BlockReceiverAction blockReceiverAction;
	
	/**
	 * 
	 * @param blocklink
	 */
	public BlockConnCommand(BlockLink blocklink){
		this.blockLink=blocklink;
		blockReceiverAction=new BlockReceiverAction();
	}
	
	@Override
	public void execute() {

	}

	//Undo means disconnect blocks that have been connected now
	@Override
	public void undo() {
		blockReceiverAction.disconnect(blockLink);
	}

	@Override
	public void redo() {
		blockReceiverAction.connect(blockLink);
	}

}
