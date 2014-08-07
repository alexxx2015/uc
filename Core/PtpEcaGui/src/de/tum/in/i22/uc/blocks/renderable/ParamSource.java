package de.tum.in.i22.uc.blocks.renderable;



	import java.awt.Component;
import java.awt.Container;
import java.awt.Point;

import de.tum.in.i22.uc.blocks.workspace.WorkspaceWidget;


	/**
	 *ParamSource interface that must be implemented by a class if a param is to be linked to it.
	 * 
	 * @author joshua
	 *
	 */
	public interface ParamSource {
		
		/**
		 * returns the parent of the paramSource
		 * @return
		 */
		public Container getParent();

		/**
		 * Returns the parent WorkspaceWidget containing this
		 * @return the parent WorkspaceWidget containing this
		 */
		public WorkspaceWidget getParentWidget();
		
		/**
	     * returns where the paramArrow should draw from
	     * @return
	     */
	    public Point getParamLocation();

	    /**
	     * add paramLabel to the paramSource 
	     * @param paramLabel
	     */
		public Component add(Component paramLabel);

		public Long getBlockID();
	}

