package de.tum.in.i22.uc.blocks.workspace.blockevents;

/**
 * 21-Feb-2013 
 * General interface that all commands will implement. This
 * interface is very central to the internal workings of
 * this editor
 * 
 * @author ELIJAH
 *
 */
public interface Command {
	public void execute();
	public void undo();
	public void redo();
}
