package de.tum.in.i22.uc.blocks.workspace.blockevents;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import de.tum.in.i22.uc.blocks.renderable.Param;
import de.tum.in.i22.uc.blocks.workspace.Workspace;
import de.tum.in.i22.uc.blocks.workspace.WorkspaceEvent;

/**
 * 27 April 2013
 * This class is used by Param.java and helpful for undo-redo of
 * policy type values
 * 
 * @author ELIJAH
 * 
 */
public class ParamItemListener implements ItemListener{
		
		public ParamItemListener(Param param, Workspace workspace){
			bFireEvent=true;
			parentParam=param;
			paramWorkspace=workspace;
			sOldPolicyType="";
		}
		
		private Param parentParam;		
		/**
		 * Policy file loading, Undo() and redo() require events not to
		 * be fired to avoid cyclic behaviour so b4 they attempt changing
		 * policy value, they set this variable to false...
		 *  
		 */
		public static boolean bFireEvent;
		public static String sOldPolicyType;
		private final Workspace paramWorkspace;

		@Override
		public void itemStateChanged(ItemEvent ie) {
			
			//if there is a selection of new policy type...
			if(ie.getStateChange()==ItemEvent.SELECTED){
				if(bFireEvent){
					PreviousState ps=new PreviousState(parentParam.getParamSource());
					//Add the param object
					ps.addOtherStates(parentParam);
					//Add the new value
					ps.addOtherStates(parentParam.getPolicyType());										
					//
					if(sOldPolicyType.equals(""))
					{
						//Add the old value
						ps.addOtherStates(parentParam.getOldPolicyType());
						paramWorkspace.notifyListeners(new WorkspaceEvent(paramWorkspace,parentParam.getParamSource().getParentWidget(), WorkspaceEvent.BLOCK_PARAM_POLICYTYPE_CHANGED,ps));					
					}
					else{
						//Add the old value
						ps.addOtherStates(sOldPolicyType);
						paramWorkspace.notifyListeners(new WorkspaceEvent(paramWorkspace,parentParam.getParamSource().getParentWidget(), WorkspaceEvent.BLOCK_PARAM_POLICYTYPE_CHANGED,ps));						
					}					
						//set the old value to current value
						sOldPolicyType=parentParam.getPolicyType();						
				}
				//... and we reset to firing events as from the start					
				bFireEvent=true;
			}
		}
		
	}
