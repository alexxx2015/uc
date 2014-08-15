package de.tum.in.i22.uc.blocks.workspace.blockevents;

import de.tum.in.i22.uc.blocks.codeblocks.BlockLink;

/**
 * Handles block disconnection
 * 
 * @author ELIJAH
 *
 */
public class BlockDisconnCommand implements Command {

	private BlockLink blockLink;
	private BlockReceiverAction blockReceiverAction;
	
	/**
	 * 
	 * @param blocklink
	 */
	public BlockDisconnCommand(BlockLink blocklink){
		blockLink=blocklink;
		blockReceiverAction=new BlockReceiverAction();
	}
	
	@Override
	public void execute() {

	}

	//Undo means connect blocks that have been disconnected now
	@Override
	public void undo() {
		blockReceiverAction.connect(blockLink);
	}

	@Override
	public void redo() {
		blockReceiverAction.disconnect(blockLink);
	}

}
