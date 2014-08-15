package de.tum.in.i22.uc.blocks.workspace.blockevents;

/** 23 February 2013
 * The major commands in this program come either from menus or
 * outside menus. Menu commands include File > Open and commands
 * from outside menus include commands that change the state of
 * blocks or pages or the workspace. The approach to handle all 
 * these commands is the command pattern. So, the enumeration below
 * helps to differentiate menu commands (those that require to be
 * undone and redone) from non-menu commands.
 * 
 * */
public enum CommandType {
	/**
	 * Describes a menu command that does not require undo or redo
	 */
	MENU_ORDINARY_COMMAND,
	/**
	 * Describes a menu command that requires to be undone and redone
	 */
	MENU_UNDO_REDO_COMMAND,
	/**
	 * Describes a non-menu command not requiring undo or redo
	 */
	ORDINARY_COMMAND,
	/**
	 * Describes a non-menu command that requires undo and redo
	 */
	UNDO_REDO_COMMAND,
	/**
	 * Describes the typical undo command of any application achievable
	 * using Edit > Undo, or CTRL + Z
	 */
	UNDO_COMMAND,
	/**
	 * Describes the typical redo command of any application achievable
	 * using Edit > Redo, or CTRL + Y
	 */
	REDO_COMMAND
}
