package de.tum.in.i22.uc.blocks.workspace.blockevents;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import de.tum.in.i22.uc.blocks.controller.WorkspaceController;
import de.tum.in.i22.uc.blocks.workspace.SmartJMenuItem;

/**
 * Subcontroller to the actions within the editor:
 * GUI menu-related commands, commands that involve policy
 * specification
 * 
 * @author ELIJAH
 *
 */
public class SubController implements ActionListener{
	private Invoker invoker;
	private WorkspaceController workspaceController;
	public SubController(WorkspaceController wc, Invoker i){
		invoker=i;
		workspaceController=wc;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
				
		SmartJMenuItem sMenuItem=(SmartJMenuItem)(e.getSource());
		CommandType commandType=sMenuItem.getCommandType();		
		Command command=null;
		
		switch(commandType){
		
		case MENU_ORDINARY_COMMAND: 
				
			String sCommand=sMenuItem.getText().toLowerCase();
							
				if(sCommand.equals("new")) 	//File > New
					command=new NewCommand(workspaceController,invoker);
				else if(sCommand.equals("open..."))
					command=new OpenCommand(workspaceController,invoker);
				else if(sCommand.equals("save as..."))	//File > Save As
					command=new SaveCommand(workspaceController,invoker);
				else if(sCommand.equals("save"))	//File > Save
					command=new UpdateCommand(workspaceController,invoker);				
				else if(sCommand.equals("exit"))
					command=new ExitCommand(workspaceController, invoker);
		
				//set our invoker
				invoker.setCommand(command);
				invoker.execute(false);
				
				break;
		
		//For the future when some doable operation will come up
		case MENU_UNDO_REDO_COMMAND:	
			
				break;
		
		//These will not go through the invoker because of the approach taken by
		//the guys who are refactoring Openblocks
		case UNDO_COMMAND: 
				invoker.undo();
			break;
		
		case REDO_COMMAND:
				invoker.redo();
			break;
			
	}

	}
		
}
