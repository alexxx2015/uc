package de.tum.in.i22.uc.blocks.workspace.blockevents;

import java.util.ArrayList;

import de.tum.in.i22.uc.blocks.codeblocks.Block;
import de.tum.in.i22.uc.blocks.codeblocks.BlockConnector;
import de.tum.in.i22.uc.blocks.codeblocks.BlockLink;
import de.tum.in.i22.uc.blocks.codeblocks.BlockLinkChecker;
import de.tum.in.i22.uc.blocks.codeblocks.BlockStub;
import de.tum.in.i22.uc.blocks.controller.WorkspaceController;
import de.tum.in.i22.uc.blocks.renderable.RenderableBlock;
import de.tum.in.i22.uc.blocks.workspace.WorkspaceEvent;
import de.tum.in.i22.uc.blocks.workspace.WorkspaceListener;


/**
 *  24 February 2013
 * This is another approach to undo and redo. Try to define these actions
 * for singleton commands rather than the generalized approach of state management
 * for pages and workspace. With this approach, we register to listen for commands
 * on blocks and even pages and for each command, we define a redo and undo action.
 * In addition, these commands have been executed before we are notified and since
 * they did not pass through the subcontroller, we by-pass invoker.execute() and
 * call invoker.pushIntoUndoStack()
 */
public class WorkspaceInvokerClient implements WorkspaceListener {
	
	private WorkspaceController workspaceController;
	private Invoker invoker;
	private static ArrayList<Long> alStubIDs=new ArrayList<Long>();
	private static boolean bDontAddMoveEvent=true;
	
	/**
	 * 
	 * @param controller
	 * @param invoker
	 */
	public WorkspaceInvokerClient(WorkspaceController controller, Invoker invoker){
		workspaceController=controller;
		this.invoker=invoker;		
	}
	
	@SuppressWarnings({ "unused", "unchecked" })
	@Override
	public void workspaceEventOccurred(WorkspaceEvent event) {
					
		String sParentName=null;
		Command command=null;
		RenderableBlock rbBlock=null;
			
		switch(event.getEventType()){
		
		case WorkspaceEvent.BLOCK_ADDED: 						
					
			if(workspaceController.getWorkspaceLoadStatus()){				
				rbBlock=workspaceController.getWorkspace().getEnv().getRenderableBlock(event.getSourceBlockID());							
				/* 24 Feb 2013
				 * We need to handle stub dynamic addition.
				 * When our renderable block is null, we have a block added whose
				 * genus family specifies stubs to be automatically generated.
				 * This stub is usually a data block.
				 */
				//Renderable block without data stubs
				if(rbBlock!=null) {									
					command=new BlockAddCommand(workspaceController,rbBlock,(ArrayList<Long>)alStubIDs.clone());	
					invoker.putInUndoStack(command);
					//reset our list of stub ids for any other renderable block coming
					alStubIDs.clear();
//					System.out.println("Block-added event");					
					bDontAddMoveEvent=true;
				}
				else /*data blocks or stubs*/ {						
					Block bStub=workspaceController.getWorkspace().getEnv().getBlock(event.getSourceBlockID());
					alStubIDs.add(bStub.getBlockID());					
				}
			}
			break;			
		
		case WorkspaceEvent.BLOCK_REMOVED:
			//2 be implemented l8r because block removal is not present in application			
			rbBlock=workspaceController.getWorkspace().getEnv().getRenderableBlock(event.getSourceBlockID());				
			
			//we need to get a block link especially for block stubs because they are generated
			//dynamically and have no previous state so that we cant get its position
			//This block link will be helpful so that during undo, we will only link them
			BlockLink link=null;
			Block block=rbBlock.getBlock();
			BlockConnector plug = BlockLinkChecker.getPlugEquivalent(block);
            if (plug != null && plug.hasBlock()) {            	
                Block parent = workspaceController.getWorkspace().getEnv().getBlock(plug.getBlockID());
                BlockConnector socket = parent.getConnectorTo(rbBlock.getBlockID());
                System.out.println("Socket: "+socket);                
                link = BlockLink.getBlockLink(workspaceController.getWorkspace(), block, parent, plug, socket);
            }
			
			command=new BlockRemoveCommand(workspaceController,rbBlock,link);
			
			invoker.putInUndoStack(command);
			System.out.println("Block-deleted event");
			break;
		
		case WorkspaceEvent.BLOCK_MOVED:
			/*
			 * The first move event is a block coming from the factory. I had to add another
			 * constructor at WorkspaceEvent to make this possible just like for block addition 
			 * above
			 */	
						
			if(bDontAddMoveEvent){
				//Block stubs have just been added. Discarding
				//addition of move events.
				bDontAddMoveEvent=false;
			}
			else{					
					Command aCommand=invoker.getLastAddedUndoCommand();					
					//'Real' blocks might have been added. This discards addition
					//of move events. By 'real' blocks, I mean blocks that are not 
					//stubs and with block id
					if(bDontAddMoveEvent){
						bDontAddMoveEvent=false;
					}
					else{
						//Prevents addition of block-move events just after block-delete events
						if( !(aCommand instanceof BlockRemoveCommand))
						{
							PreviousState psObject=event.getPreviousState();
							//psObject==null means block is dragged from factory
							//for which case we won't also add block-move events.
							if(psObject!=null)
							{
								java.awt.Point point=(java.awt.Point)psObject.getState();				
								command=new BlockMoveCommand(point,workspaceController,event.getSourceBlockID());
								invoker.putInUndoStack(command);
								System.out.println("Block-moved event");
								
								//if a connect command comes before this move command
								//we need reorder to maintain mental understanding of user
								if(aCommand instanceof BlockConnCommand)
								invoker.orderMoveConnectCommands();
							}
						}
					}
				
				
			}
			
			break;
		
		/* Concerned classes: blocklabel.java, labelwidget.java, RenderableBlock.java
		 * This event is generated
		 * 1. When a data block e.g. text or numerical block is dragged from the factory to
		 *    our page
		 * 2. When data blocks in 1. above get their contents modified
		 * 3. During application start-up.
		 * Of the 3 provided above, the correct one we are to watch for is number Case 2.
		 * Case 3 is avoided by stopping block-renaming events occurring during application start-up.
		 * Case 1 is avoided below.
		 */
		case WorkspaceEvent.BLOCK_RENAMED:						
				String sOldName=(String)event.getPreviousState().getState();
				rbBlock=workspaceController.getWorkspace().getEnv().getRenderableBlock(event.getSourceBlockID());
				String sNewName=rbBlock.getBlock().getBlockLabel();
				if(!sOldName.toLowerCase().equals(sNewName.toLowerCase())){					
					command=new BlockRenameCommand(workspaceController,rbBlock,sOldName,sNewName);
					invoker.putInUndoStack(command);
				}
			break;
		
		case WorkspaceEvent.BLOCK_GENUS_CHANGED:
				String sOldGenusName=(String)event.getPreviousState().getState();
				rbBlock=workspaceController.getWorkspace().getEnv().getRenderableBlock(event.getSourceBlockID());
				command=new BlockGenusChangeCommand(workspaceController,rbBlock,sOldGenusName);
				//Get last command in undo stack...
				//Command aCommand=invoker.getLastAddedUndoCommand();				
				invoker.putInUndoStack(command);
				//Block-Genus-change commands should come before
				//if(aCommand instanceof AddParamCommand)
					//invoker.orderGenusChangeParamAddCommands();
				
			break;
			
		case WorkspaceEvent.BLOCKS_CONNECTED:
				command=new BlockConnCommand(event.getSourceLink());
				invoker.putInUndoStack(command);	
				System.out.println("Block Connected");
			break;
			
		case WorkspaceEvent.BLOCKS_DISCONNECTED:
				command=new BlockDisconnCommand(event.getSourceLink());
				invoker.putInUndoStack(command);
				System.out.println("Block Disconnected");
			break;
			
		case WorkspaceEvent.BLOCK_COMMENT_ADDED:
			PreviousState psObject=(PreviousState)event.getPreviousState();			
				command=new BlockAddCommentCommand((RenderableBlock)psObject.getState());
				invoker.putInUndoStack(command);
			break;
			
		case WorkspaceEvent.BLOCK_COMMENT_REMOVED:			
			command=new BlockRemoveCommentCommand((PreviousState)event.getPreviousState());
			invoker.putInUndoStack(command);
			break;
			
		case WorkspaceEvent.BLOCK_COMMENT_CHANGED:
			PreviousState ps=(PreviousState)event.getPreviousState();
			String sEvent=(String)ps.getAlOtherStates().get(1);
			if(sEvent.equals("insert"))
				command=new BlockCommentInsertCommand(ps);
			else if(sEvent.equals("delete"))
				command=new BlockCommentDeleteCommand(ps);
			invoker.putInUndoStack(command);
			break;
			
		case WorkspaceEvent.BLOCK_COMMENT_MOVED:
			command=new CommentMoveCommand((PreviousState)event.getPreviousState());
			invoker.putInUndoStack(command);
			break;
		
		case WorkspaceEvent.BLOCK_COMMENT_RESIZED:
			command=new CommentResizeCommand((PreviousState)event.getPreviousState());
			invoker.putInUndoStack(command);
			break;
			
		case WorkspaceEvent.BLOCK_COMMENT_VISIBILITY_CHANGE:
			PreviousState pState=(PreviousState)event.getPreviousState();
			command=new CommentVisibilityChangeCommand(pState);
			invoker.putInUndoStack(command);
			break;
			
		case WorkspaceEvent.BLOCK_PARAM_ADDED:
			PreviousState pst=(PreviousState)event.getPreviousState();			
			command=new ParamAddCommand((RenderableBlock)pst.getState());
			invoker.putInUndoStack(command);
			break;
			
		case WorkspaceEvent.BLOCK_PARAM_MOVED:
			command=new ParamMoveCommand((PreviousState)event.getPreviousState());
			invoker.putInUndoStack(command);
			break;
			
		case WorkspaceEvent.BLOCK_PARAM_REMOVED:
			command=new RemoveParamCommand((PreviousState)event.getPreviousState());
			invoker.putInUndoStack(command);			
			break;
			
		case WorkspaceEvent.BLOCK_PARAM_RESIZED:
			command=new ParamResizeCommand((PreviousState)event.getPreviousState());
			invoker.putInUndoStack(command);
			break;
			
		case WorkspaceEvent.BLOCK_PARAM_VISIBILITY_CHANGE:			
			command=new ParamVisibilityChangeCommand((PreviousState)event.getPreviousState());
			invoker.putInUndoStack(command);
			break;
			
		case WorkspaceEvent.BLOCK_PARAM_POLICYTYPE_CHANGED:			
			command=new PolicyTypeCommand(event.getPreviousState());			
			invoker.putInUndoStack(command);			
			break;
		
		case WorkspaceEvent.BLOCK_PARAM_STATUS_CHANGED:						
			command=new ParamStatusChangeCommand((PreviousState)event.getPreviousState());
			invoker.putInUndoStack(command);
			break;
		
		case WorkspaceEvent.BLOCK_PARAM_CLASS_CHANGED:						
			command=new ParamClassChangeCommand((PreviousState)event.getPreviousState());
			invoker.putInUndoStack(command);
			break;
		
		case WorkspaceEvent.BLOCK_PARAM_VALUE_CHANGED:						
			command=new ParamValueChangeCommand((PreviousState)event.getPreviousState());
			invoker.putInUndoStack(command);
			break;		
			
		}
		
		
		
	}

}
