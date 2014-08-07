package de.tum.in.i22.uc.blocks.workspace;

import javax.swing.JMenuItem;

import de.tum.in.i22.uc.blocks.workspace.blockevents.CommandType;

/**
 * 23 February 2013
 * 
 * This class is smart because it carries some data about the type of
 * command which this menu item does. It is going to be very useful because
 * menu commands and non-menu commands are handled using the command pattern
 * and there, we have to know what kind of command we are dealing (i.e. an 
 * ordinary command or a command that requires undo and redo) and where 
 * it is coming from (i.e. from menus or some other place within the application).
 * 
 */
public class SmartJMenuItem extends JMenuItem {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2995544176259419573L;	
	
	private CommandType commandType;
	/**
	 * 
	 * @param label
	 * @param menuCommandType
	 */
	public SmartJMenuItem(String label, CommandType menuCommandType){
		super(label);
		commandType=menuCommandType;
	}
	
	public CommandType getCommandType(){
		return commandType;
	}

}
