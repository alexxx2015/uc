package de.tum.in.i22.uc.blocks.workspace.blockevents;

import java.util.ArrayList;

/**
 * This class is very vital to undo and redo. Usually
 * actions that are undoable and redoable have states: state
 * before the action and state after the action. Undo takes
 * the system to the state before the action and redo does the
 * opposite.
 * 
 * @author ELIJAH
 *
 */
public class PreviousState {
	/**
	 * Store a single state
	 */
	private Object oState;
	
	private boolean bBlockMovedFromFactory;
	/**
	 * Store other states.
	 */
	private ArrayList<Object> alOtherStates;
	/**
	 * 
	 * @param object
	 */
	public PreviousState(Object object){
		oState=object;
		bBlockMovedFromFactory=true;
		alOtherStates=new ArrayList<Object>();
	}
	/**
	 * 
	 * @return the primary state
	 */
	public Object getState(){
		return oState;
	}
	/**
	 * 
	 * @return
	 */
	public boolean isBlockMovedFromFactory(){
		return bBlockMovedFromFactory;
	}
	/**
	 * Set the primary state
	 * 
	 * @param object
	 */
	public void setState(Object object){
		oState=object;
	}
	/**
	 * 
	 */
	public void setBlockMovedFromFactory(){
		bBlockMovedFromFactory=true;
	}
	/**
	 * Get all other states
	 * 
	 * @return all other states different from the primary state
	 */
	public ArrayList<Object> getAlOtherStates() {
		return alOtherStates;
	}
	/**
	 * Set all other states from the primary state
	 * 
	 * @param alOtherStates
	 */
	public void setAlOtherStates(ArrayList<Object> alOtherStates) {
		this.alOtherStates = alOtherStates;
	}
	/**
	 * Add to all other states
	 * 
	 * @param object
	 */
	public void addOtherStates(Object object){
		alOtherStates.add(object);
	}
	
}
