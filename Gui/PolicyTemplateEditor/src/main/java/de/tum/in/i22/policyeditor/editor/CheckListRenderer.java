package de.tum.in.i22.policyeditor.editor;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JCheckBox;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;

public class CheckListRenderer extends JCheckBox implements ListCellRenderer<Object> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2918917362297069493L;
	private final Color HOVER_COLOR = new Color(135, 206,250);
	private int hoverIndex = -1;
	private MouseAdapter handler;
	
	 public CheckListRenderer() {
	   setBackground(UIManager.getColor("List.textBackground"));
	   setForeground(UIManager.getColor("List.textForeground"));
	   this.setOpaque(true);
	 }
	
	 public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean hasFocus) {
		if(value == null)
			return this;
		setEnabled(list.isEnabled());
		setSelected(((CheckableItem) value).isSelected());
		setFont(list.getFont());
		String text = ((CheckableItem) value).getClearDescription();
		setText("  " + text);
		setToolTipText("<html>" + "Double click to edit policy:" + "<br>" + text + "</html>");

		Color c = index == hoverIndex ? HOVER_COLOR : list.getBackground();
		setBackground(c);

		return this;
	 }
	 
	 public MouseAdapter getHandler(JList<?> list) {
		    if (handler == null) {
		      handler = new HoverMouseHandler(list);
		    }
		    return handler;
		  }
	 
	 class HoverMouseHandler extends MouseAdapter {

		    private final JList<?> list;

		    public HoverMouseHandler(JList<?> list) {
		      this.list = list;
		    }

		    @Override
		    public void mouseExited(MouseEvent e) {
		      setHoverIndex(-1);
		    }

		    @Override
		    public void mouseMoved(MouseEvent e) {
		      int index = list.locationToIndex(e.getPoint());
		      if(index!=-1)
		    	  setHoverIndex(list.getCellBounds(index, index).contains(e.getPoint())  ? index : -1);
		    }

		    private void setHoverIndex(int index) {
		      if (hoverIndex == index) return;
		      hoverIndex = index;
		      list.repaint();
		    }
		  }
	 
}