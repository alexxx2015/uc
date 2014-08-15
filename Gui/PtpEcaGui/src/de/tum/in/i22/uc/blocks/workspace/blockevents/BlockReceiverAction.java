package de.tum.in.i22.uc.blocks.workspace.blockevents;

import java.util.ArrayList;

import de.tum.in.i22.uc.blocks.codeblocks.BlockLink;
import de.tum.in.i22.uc.blocks.controller.WorkspaceController;
import de.tum.in.i22.uc.blocks.renderable.BlockUtilities;
import de.tum.in.i22.uc.blocks.renderable.Comment;
import de.tum.in.i22.uc.blocks.renderable.Param;
import de.tum.in.i22.uc.blocks.renderable.RenderableBlock;
import de.tum.in.i22.uc.blocks.workspace.Page;
import de.tum.in.i22.uc.model.ParamTableModel;

import java.awt.Dimension;
import java.awt.Point;
import javax.swing.undo.UndoManager;

/**
 * This class handles undo and redo for some actions as a delegate.
 * 
 * @author ELIJAH
 *
 */
public class BlockReceiverAction {
	
	private WorkspaceController workspaceController;	
	//4 Block addition and removal event
	private ArrayList<Long> alStubIDs;
	//4 Block renaming events. 03 March 2013	
	private String sOldName;
	private String sNewName;	
		
	/**
	 * For block addition and removal 
	 * @param controller
	 * @param listSiblings
	 */
	public BlockReceiverAction(WorkspaceController controller, ArrayList<Long> listSiblings){
		workspaceController=controller;
		alStubIDs=listSiblings;
	}
	
	/**
	 * For block-renaming and block-genus-renaming events. 03 March 2013.
	 * 
	 * @param controller
	 * @param oldName
	 * @param newName
	 */
	public BlockReceiverAction(WorkspaceController controller, String oldName, String newName){
		workspaceController=controller;
		sOldName=oldName;
		sNewName=newName;
	}
	
	/**
	 * For block deletion/restoration, connection/disconnection . 03 March 2013
	 */
	public BlockReceiverAction(){	
		
	}
	
	
	/**
	 * 24 February 2013. Undo block addition/removal 
	 * 
	 * @param block
	 */
	public void removeBlock(RenderableBlock block){			
		Page pgCurrent=workspaceController.getWorkspace().getCurrentPage();		
		
		if(block!=null){ 		 
			//remove block and update canvas
			if(pgCurrent!=null){
			pgCurrent.removeBlock(block);
			pgCurrent.finishClearing();
			}
		}
	}
	
	/**
	 * 24 February 2013. Redo block addition/removal
	 * 
	 * @param block
	 */
	public void addBlock(RenderableBlock block){
		Page pgCurrent=workspaceController.getWorkspace().getCurrentPage();
		if(block!=null){ 				
			//add our block
			if(pgCurrent!=null){
			pgCurrent.addBlock(block);
			pgCurrent.finishClearing();
			}
		}
	}
	
	/**
	 * Undo block renaming. 03 March 2013 
	 * 
	 * @param block
	 * @param page
	 */
	public void setOldBlockLabel(RenderableBlock block, Page page){
		if(block!=null){												
			block.getBlock().setBlockLabel(sOldName);
			block.repaintBlock();
			if(page!=null)	page.finishClearing();
		}
	}
	
	/**
	 * Redo block renaming. 03 March 2013
	 * 
	 * @param block
	 * @param page
	 */
	public void setNewBlockLabel(RenderableBlock block, Page page){
		if(block!=null){							
			block.getBlock().setBlockLabel(sNewName);
			block.repaintBlock();
			if(page!=null)	page.finishClearing();
		}
	}
	
	/**
	 * Undo block genus change.
	 * 
	 * @param block
	 * @param page
	 */
	public void setOldBlockGenusName(RenderableBlock block, Page page){
		if(block!=null){
			block.getBlock().changeGenusTo(sOldName);
			block.repaintBlock();
			if(page!=null)	page.finishClearing();
		}
	}
		
	/**
	 * Redo block genus change
	 * 
	 * @param block
	 * @param page
	 */
	public void setNewBlockGenusName(RenderableBlock block, Page page){
		if(block!=null){
			block.getBlock().changeGenusTo(sNewName);
			block.repaintBlock();
			if(page!=null)	page.finishClearing();
		}
	}
	
	
	/**
	 * Undo block deletion to recycle bin, by restoring it to canvas
	 * 
	 * @param rblock
	 * @param page
	 * @param link
	 */
	public void restoreBlock(RenderableBlock rblock, Page page, BlockLink link){		
		if(rblock!=null){												
			
			rblock.setParentWidget(page);
			page.addBlock(rblock);	
			//set the block's original location
			PreviousState ps=rblock.getPreviousState();
			Point point=null;
			if(ps!=null) {				
				point=(Point)ps.getState();
				rblock.setLocation(point);				
			}
			else{
				//These are dynamically generated stubs. Just link them				
				if(link!=null) link.connectProperly(false);								
			}
					
			//finalizing
			if(page!=null)	page.finishClearing();				
		}
	}
	
	
	/**
	 * Redo block deletion
	 * 
	 * @param rblock
	 * @param page
	 */
	public void deleteBlock(RenderableBlock rblock, Page page){
		if(rblock!=null) BlockUtilities.deleteBlock(rblock, false);
		if(page!=null)	page.finishClearing();		
	}
	
	
	/**
	 * Undo block connection 
	 * 
	 * @param blocklink
	 */
	public void disconnect(BlockLink blocklink){
		if(blocklink!=null) blocklink.disconnect();
	}
	
	
	/**
	 * Redo block connection 
	 * 
	 * @param blocklink
	 */
	public void connect(BlockLink blocklink){
		if(blocklink!=null) blocklink.connect();
	}
	
	
	/**
	 * Undo block comment addition
	 * 
	 * @param block
	 */
	public void removeComment(RenderableBlock block){
		if(block!=null) {
			block.removeCommentProperly(false);
			block.repaintBlock();
		}
	}
	
	
	/**
	 * Redo block comment addition
	 * 
	 * @param block
	 * @param text
	 */
	public void createComment(RenderableBlock block, String text){
		if(block!=null){
				block.addCommentProperly(false);
				block.getComment().setTextProperly(text,false);
				block.repaintBlock();				
		}
	}
	
	
	/**
	 * Undo block-comment-change actions
	 * 
	 * @param block
	 * @param text
	 */
	public void undoCommentChange(RenderableBlock block, String text){
		if(block!=null){
			block.getComment().setTextProperly(text, false);
			block.repaintBlock();			
		}
	}
	
	/**
	 * Redo block-comment-change actions
	 * 
	 * @param block
	 * @param text
	 */
	public void redoCommentChange(RenderableBlock block, String text){
		if(block!=null){
			block.getComment().setTextProperly(text, false);
			block.repaintBlock();			
		}
	}
	
	
	/**
	 * Handles both undo and redo of block comment visibility
	 * 
	 * @param block
	 * @param visible
	 */
	public void changeCommentVisibility(RenderableBlock block, boolean visible){		
		if(block!=null){
			block.getComment().setVisible(visible);			
		}
	}
	
	
	/**
	 * 10 March 2013. Undoes/Redoes comment move command
	 * 
	 * @param comment
	 * @param block
	 * @param point
	 */
	public void undoRedoCommentMove(Comment comment, RenderableBlock block, Point point){
		if(comment!=null && block!=null){
			if(point!=null)	comment.setLocation(point);
			block.repaintBlock();			
		}
	}
	
	
	/**
	 * 10 March 2013.  Undoes/Redoes comment resize command
	 * 
	 * @param comment
	 * @param block
	 * @param dimension
	 */
	public void undoRedoCommentResize(Comment comment, RenderableBlock block, Dimension dimension){
		if(comment!=null && block!=null){	
			if(dimension!=null){
				comment.setMyHeight(dimension.height);
				comment.setMyWidth(dimension.width);
			}
			comment.reformComment();
			block.repaintBlock();			
		}
	}
	
	
	/**
	 * Handles both undo and redo of block param visibility
	 * 
	 * @param block
	 * @param visible
	 */
	public void changeParamVisibility(RenderableBlock block, boolean visible){		
		if(block!=null){
			block.getParam().setVisible(visible);			
		}
	}
		
	
	/**
	 * 10 March 2013. Undoes/Redoes param move command
	 * 
	 * @param param
	 * @param block
	 * @param point
	 */
	public void undoRedoParamMove(Param param, RenderableBlock block, Point point){
		if(param!=null && block!=null){
			if(point!=null)	param.setLocation(point);
			block.repaintBlock();			
		}
	}
		
	
	/**
	 * Undo block comment addition
	 * 
	 * @param block
	 */
	public void removeParam(RenderableBlock block){
		if(block!=null) {
			block.removeParamProperly(false);
			block.repaintBlock();
		}
	}
		
	
	/**
	 * Redo block comment addition
	 * 
	 * @param block
	 * @param model
	 */
	public void createParam(RenderableBlock block, ParamTableModel model){
		if(block!=null){
				block.addParamProperly(false);
				if(model!=null)
				block.getParam().setTableModel(model);
				block.repaintBlock();				
		}
	}
	
	
	/**
	 * 14 March 2013.  Undoes/Redoes parameter resize command
	 * 
	 * @param param
	 * @param block
	 * @param dimension
	 */
	public void undoRedoParamResize(Param param, RenderableBlock block, Dimension dimension){
		if(param!=null && block!=null){	
			if(dimension!=null){
				param.setMyHeight(dimension.height);
				param.setMyWidth(dimension.width);
			}
			param.reformParam();
			block.repaintBlock();			
			}
		}
	
}
