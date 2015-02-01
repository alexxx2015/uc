package de.tum.in.i22.uc.blocks.workspace.blockevents;

import java.util.Stack;

/**
 * 21-Feb-2013.
 * Does actual invoking of menu item commands. It also contains logic
 * for undoing and redoing actions
 * 
 * @author ELIJAH
 *
 */
public class Invoker {
	private Stack<Command> stackUndo;
	private Stack<Command> stackRedo;
	public Command command;
	
	public Invoker(){
		stackUndo=new Stack<Command>();
		stackRedo=new Stack<Command>();
	}
	
	/**
	 * Tells the invoker the command to 
	 * use
	 * 
	 * @param com
	 */
	public void setCommand(Command com){
		command=com;
	}
	
	public void reset(){
		stackUndo.clear();
		stackRedo.clear();
	}
	
	/**
	 * does command.execute()
	 * 
	 * @param isUndoRedoCommand
	 */
	public void execute(boolean isUndoRedoCommand){
		command.execute();
	}
	
	public void redo(){
		Command c=null;
		printStack("redo","B4");
		if(!stackRedo.empty()){
			//remove from redo stack
			c=stackRedo.pop();
			//push into undo stack
			stackUndo.push(c);
			//execute command
			c.redo();
			printStack("redo","A5");
		}
	}
	
	public void undo(){
		Command c=null;
		printStack("undo","B4");
		if(!stackUndo.empty()){
			//remove from undo stack
			c=stackUndo.pop();
			//push into redo stack
			stackRedo.push(c);
			//execute command
			c.undo();	
			printStack("undo","A5");
		}
		
	}
	
	/**
	 * Enlists commands that have been done for
	 * undo and redo
	 * 
	 * @param com
	 */
	public void putInUndoStack(Command com){
		stackUndo.push(com);
	}
	
	public void printStack(String s, String p){
		if(s.equals("undo"))
		{	
			System.out.println("Undo: "+p+"-----");
			System.out.println(stackUndo);			
		}
		else if(s.equals("redo")){	
			System.out.println("Redo: "+p+"-----");
			System.out.println(stackRedo);			
		}		
	}
	
	/**
	 * 
	 * @return The last added undo command
	 */
	public Command getLastAddedUndoCommand(){
		if(!stackUndo.isEmpty())
		return stackUndo.lastElement();
		else return null;
	}
	
	/**
	 * 06 March 2013. This is used to detect if the user plays with
	 * the policy editor with a new and unsaved work
	 * 
	 * @return Boolean status of change in policy file
	 */
	public boolean hasPolicyFileChanged(){
		return !stackUndo.isEmpty() && !stackRedo.isEmpty();
	}
	
		
	/**
	 * 06 March 2013. Called to detect changes to an opened policy file
	 * 
	 * @return The total number of commands in our stack
	 */
	public int getStackSizes(){
		return stackUndo.size() + stackRedo.size();
	}
	
	
	/**
	 * I realized that when 2 blocks are connected, the move command
	 * comes as last after the connect command is added: wrong. What
	 * is logically needed is the other way round 
	 * 
	 */
	public void orderMoveConnectCommands(){
		if(stackUndo!=null){
			//get
			Command cMove=stackUndo.pop();
			Command cConnect=stackUndo.pop();
			//push as desired
			stackUndo.push(cMove);
			stackUndo.push(cConnect);
		}
	}
	
	/**
	 * Ensures that parameter addition is added before
	 * genus change command.
	 * 
	 */
	public void orderGenusChangeParamAddCommands(){
		if(stackUndo!=null){
			//get
			Command cGenusChange=stackUndo.pop();
			Command cAddParam=stackUndo.pop();
			//push as desired
			stackUndo.push(cGenusChange);
			stackUndo.push(cAddParam);
		}
	}
		
}
