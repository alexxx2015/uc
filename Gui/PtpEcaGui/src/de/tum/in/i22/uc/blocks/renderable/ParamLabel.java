package de.tum.in.i22.uc.blocks.renderable;

	import java.awt.Color;
import java.awt.event.MouseEvent;

	import javax.swing.BorderFactory;

import de.tum.in.i22.uc.blocks.workspace.Workspace;
import de.tum.in.i22.uc.blocks.workspace.WorkspaceEvent;
import de.tum.in.i22.uc.blocks.workspace.blockevents.PreviousState;


	/**
	 * The ParamLabel class controls the visibility of a param on a RenderableBlock
	 * @author admin
	 *
	 */
	public class ParamLabel extends BlockControlLabel {
		
		private static final long serialVersionUID = 1L;
		private final Workspace workspace;
		
		public ParamLabel(Workspace workspace,long blockID) {
			 super(workspace,blockID);
			 this.workspace=workspace;
			 this.setBackground(Color.darkGray);
			 this.setOpaque(true);
		}

		/**
		 * setup current visual state of button 
		 */
		public void update() {
			RenderableBlock rb = workspace.getEnv().getRenderableBlock(getBlockID());
			
			if (rb != null) {
		        int x = 5;
		        int y = 7;
		        
		        if (rb.getBlock().isCommandBlock()) {
		        	y-=2;
		        	x-=3;
		        }
		        
		        if (rb.getBlock().isDataBlock() || rb.getBlock().isFunctionBlock()) {
		        	x+=6;
		        	y-=2;
		        }
		        
		        if(rb.getBlock().isInfix() && rb.getBlock().getSocketAt(0) != null){ 
		            if(!rb.getBlock().getSocketAt(0).hasBlock()){
		                x+=30;
		            }else{
		            	if (rb.getSocketSpaceDimension(rb.getBlock().getSocketAt(0)) != null)
		                x+=rb.getSocketSpaceDimension(rb.getBlock().getSocketAt(0)).width + 2;
		            }
		            y += 2;
		            x += 1;
		        }
		        
		        
		        x=rb.rescale(x);
		        y=rb.rescale(y);
		        
				setLocation(x, y);
				setSize(rb.rescale(14), rb.rescale(14));
		
				if (isActive()) {
					setText("P");
					this.setForeground(new Color(255,255,0));
				} else {
					setText("P");
					this.setForeground(Color.lightGray);
				}
				rb.setComponentZOrder(this, 0);
			}
		}

	    /**
	     * Implement MouseListener interface
	     * toggle collapse state of block if button pressed
	     */
		public void mouseClicked(MouseEvent e) {
			toggle();
			RenderableBlock rb = workspace.getEnv().getRenderableBlock(getBlockID());
			rb.getParam().setVisible(isActive());
			//10 March 2013
	    	PreviousState ps=new PreviousState(rb);
	    	ps.addOtherStates(isActive());
			workspace.notifyListeners(new WorkspaceEvent(workspace,rb.getParam().getParamSource().getParentWidget(), WorkspaceEvent.BLOCK_PARAM_VISIBILITY_CHANGE,ps));
			update();
			rb.revalidate();
			rb.repaint();
			workspace.getMiniMap().repaint();
		}
		
	    /**
	     * Implement MouseListener interface
	     * highlight button state
		 */
		public void mouseEntered(MouseEvent e) {			
			super.mouseEntered(e);
			this.setBorder(BorderFactory.createLineBorder(Color.yellow));
			Param param = workspace.getEnv().getRenderableBlock(getBlockID()).getParam();
			param.setVisible(true);
			param.showOnTop();
		}

	    /**
	     * Implement MouseListener interface
	     * de-highlight button state
		 */
		public void mouseExited(MouseEvent e) {
			super.mouseExited(e);
			this.setBorder(BorderFactory.createLineBorder(Color.gray));
			Param param = workspace.getEnv().getRenderableBlock(getBlockID()).getParam();
			if (!isActive()) {
				param.setVisible(false);
			}
		}


	}


