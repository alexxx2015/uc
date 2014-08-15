package de.tum.in.i22.uc.blocks.renderable;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.GeneralPath;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.TableModelEvent;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellEditor;
import javax.swing.undo.UndoManager;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import de.tum.in.i22.uc.blocks.codeblocks.JComponentDragHandler;
import de.tum.in.i22.uc.blocks.codeblockutil.CTracklessScrollPane;
import de.tum.in.i22.uc.blocks.codeblockutil.CScrollPane.ScrollPolicy;
import de.tum.in.i22.uc.blocks.workspace.Workspace;
import de.tum.in.i22.uc.blocks.workspace.WorkspaceEvent;
import de.tum.in.i22.uc.blocks.workspace.blockevents.PreviousState;
import de.tum.in.i22.uc.model.ParamTableModel;
import de.tum.in.i22.uc.utilities.PublicMethods;
import javax.swing.JLabel;
import de.tum.in.i22.uc.blocks.workspace.blockevents.ParamItemListener;


/**
 * Comment stores and displays user-generated text that
 * can be edited by the user. Comments begin in �editable� state.
 *
 * Comments are associated with a parent source of type JComponent.
 * It should "tag" along with that component.  Note, however, that
 * this feature should be ensured by the parent source.  The
 * parent source can guarantee this by invoking the methods
 * setPosition, translatePosition, and setParent when
 * appropriate.
 *
 * text : String //the text stored in this Comment and edited by the user
 */
public class Param extends JPanel {
	private static final long serialVersionUID = 328149080425L;
	/**Background color of all comments*/
    private static final Color background = new Color(255,255,150);
    /**border color*/
    private final Color borderColor;
    /**Table for param names and values UI*/
    private  final JTableX paramTable;
	/**ScrollPane UI*/
    private  CTracklessScrollPane scrollPane;
    /**Dragging handler of this Comment*/
    private JComponentDragHandler jCompDH;
    /**Manager for arrow drawn from this to parent while in editing mode**/
  	private ParamArrow arrow;
  	/**Manages Undo-able Events in this param's text editor*/
  	private UndoManager undoManager;
  	
  	/** The JComponent this param and param label is connected to */
  	private ParamSource paramSource;
  	
  	/** The paramLabel linked to this param and placed on the paramSource 	 */
  	private ParamLabel paramLabel;
  	
  	/** A two to store param name-value pair*/
  	private HashMap<String, String> hashmap = new HashMap<String,String>();
  	
  	/** true if this param should not be able to have a location outside of its parent's bounds, false if it may be located outside of its parent's bounds */
  	private boolean constrainParam = true;

  	static int FONT_SIZE = 14;
  	static int MINIMUM_WIDTH = FONT_SIZE*4;
  	static int MINIMUM_HEIGHT = FONT_SIZE*2;
  	static int DEFAULT_WIDTH = 300;		//adjusted from 200
  	static int DEFAULT_HEIGHT = 130;		//adjusted from 100

  	
  	private boolean resizing = false;
  	private int margin = 6;
  	private int width;// = DEFAULT_WIDTH;
  	private int height;// = DEFAULT_HEIGHT;
  	private double zoom = 1.0;
  	private String fontname = "Monospaced";
  	private Shape  body, resize, pTable;
  	private boolean pressed = false;
  	private boolean active = false;
  	private final Workspace workspace;
  	
  	//10 March 2013
  	private Point pointOld;
  	private Dimension dimensionOld;
  	//14 March 2013
  	private static int iParamHeight[]={68,85,102};
  	//16 March 2013  	  	
  	//helpful for undo and redo of change of parameter-value commands.
  	private ArrayList<String> alOldParameterValues;
  	private ArrayList<String> alOldParameterClasses;  	
  	//18 March 2013 
  	//These are for loading policy files
  	static ArrayList<String> alParamNames;
    static ArrayList<Boolean> alParamSelected;
    static ArrayList<String> alParamClass;
    static ArrayList<String> alParamValues;
    private CellEditorModel cellEditorModel;
    //26-27 April 2013
    private JComboBox jcbPolicyType;
    private JLabel jlbPolicyType;
    private JLabel jlbOtherParams;
  	private String sOldPolicyType;
  	private Object[] oPolicyType={"dataUsage","containerUsage","dataContainerUsage"};
    
    /**
     * Constructs a param
     * with belonging to source, with text of initText, and initial zoom
  	 * The comment's borders will have the color borderColor.  
  	 * 
  	 * Note that initializing a comment only constructs
     * all of the necessary structures.  To graphically display a comment,
     * the implementor must then add the comment using the proper
     * Swing methods OR through the convenience method Comment.setParent()
     *
     * @param initText,  initial text of comment
     * @param source, where the comment is linked to.
     * @param borderColor the color that the border of the comment should be
     * @param zoom  initial zoom
     */
    public Param(Workspace workspace, String action, ParamSource source, Color borderColor, double zoom){
    	this(workspace,action,source,borderColor,zoom,true);
    }
    
    public Param(Workspace workspace, String action, ParamSource source, Color borderColor, double zoom, boolean fireEvent){    	
    	    	
    	//set up important fields 
    	this.workspace=workspace;
    	this.zoom = zoom;
    	this.setLayout(null);
    	this.setOpaque(false);
    	this.borderColor=borderColor;
    	this.paramSource = source;
    	
    	//Create our table and format it
    	paramTable = new JTableX();
    	//prepare editors for specific cells
    	cellEditorModel=new CellEditorModel();        
        paramTable.setCellEditorModel(cellEditorModel);
        //set other table properties
    	paramTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
 	    JTableHeader theader= paramTable.getTableHeader();
 	    Font headerfont= new Font("Calibri", Font.BOLD, 12);
 	    theader.setBackground(this.getBorderColor());
 	    theader.setOpaque(false);
 	    theader.setFont(headerfont);    
        paramTable.setBackground(this.getBackgroundColor());
 	    paramTable.setOpaque(true);	 	     	   
 	    
 	    //For undo-redo of actions to parameter classes and values
	    //in our table
        alOldParameterValues=new ArrayList<String>();
        alOldParameterClasses=new ArrayList<String>();
    	
    	//set up editingPanel, labelPanel and their listeners
    	// set table model, add a column for selection, add param dropdowns
		List<String> paramNames = new PublicMethods().returnParamsByAction(action);	
		
		//prepare the 1st and 3rd columns of our parameter table
        this.initializePopulation(action,paramNames);        
        
	    if(alParamNames!=null && alParamSelected!=null &&
	    		alParamClass!=null && alParamValues!=null)
	    {
	    	//prepare our table from users specified policy file
	    	this.prepareTableModel(populateTableFromFile());
	    }
	    else {
	    	this.prepareTableModel(populateTableAfresh(paramNames));
	    	/* if an external file has launched our editor*/
	    	if(workspace.getEntityInstance()!=null){
	    		cellEditorModel.setIsForConcreteInstance(true);
	    		this.populateInContext(workspace.getEntityInstance());
	    	}
	    }	    	    	                	       	
		
		//prepare 3rd and 4th columns
		finalizePopulation();					
	    
	    //listener for table
	    ((ParamTableModel)paramTable.getModel()).addTableModelListener(new ParameterTableListener(workspace));
		
	    //laying out...
	    this.setLayout(new BoxLayout(this,BoxLayout.PAGE_AXIS));	  
	    
        //add some space above
	    this.add(Box.createRigidArea(new Dimension(0,7)));
	    
	    //This JPanel houses the policy type label and combo-box
	    JPanel jpPolicyType=new JPanel();
	    jpPolicyType.setLayout(new BorderLayout());
	    
		    //add policy type label
		    jlbPolicyType=new JLabel("Policy Type             ");  	    
	        jpPolicyType.add(jlbPolicyType,BorderLayout.LINE_START);
	        
	    //jpPolicyType.add(jlbPolicyType,BorderLayout.CENTER);
	        
		    //we prepare a policy type combo-box and add it before the paramTable
		    
		    jcbPolicyType=new JComboBox(oPolicyType);
		    jcbPolicyType.setSelectedIndex(0);
		    //keep old state for  undo-redo monitoring
		    sOldPolicyType="dataUsage";
		    jcbPolicyType.setBackground(this.getBackgroundColor());
		    jcbPolicyType.addItemListener(new ParamItemListener(this,workspace));
		    //
		    jpPolicyType.add(jcbPolicyType,BorderLayout.LINE_END);
		    //jpPolicyType.add(new JLabel(""),BorderLayout.EAST);
		Dimension dim=jpPolicyType.getPreferredSize();
		jpPolicyType.setMaximumSize(new Dimension(DEFAULT_WIDTH-16,dim.height));		
	    jpPolicyType.setBackground(this.getBackgroundColor());
	    this.add(jpPolicyType);
	    
	    //add some space
	    this.add(Box.createRigidArea(new Dimension(0,7)));	    	    
	    
	    //add some space
	    this.add(Box.createRigidArea(new Dimension(0,3)));
	    
    	//initialize scrollPane, putting our table inside
		scrollPane = new CTracklessScrollPane(paramTable,
				ScrollPolicy.VERTICAL_BAR_AS_NEEDED,				
				ScrollPolicy.HORIZONTAL_BAR_AS_NEEDED,				
    			10, this.borderColor, Param.background);
    	//paramTable.setFillsViewportHeight(true);
		//
		dim=scrollPane.getPreferredSize();
		scrollPane.setMaximumSize(new Dimension(DEFAULT_WIDTH-15,dim.height));
		this.add(scrollPane);
		
		//automatically set bounds. The trick is if table has less than 3 rows
		//the parameter resizes itself accordingly but beyond 3, scrollbars help
		//to see the remaining rows.
		int iRowCount=paramTable.getModel().getRowCount();
		this.width=DEFAULT_WIDTH;
		this.height=DEFAULT_HEIGHT;
		if(iRowCount<3) {
		   	if(iRowCount>0)
		   	{
		   		int iTotalHeight=28+jcbPolicyType.getHeight()+jlbPolicyType.getHeight()+paramTable.getTableHeader().getHeight();		   				   		
		   		this.height=iParamHeight[iRowCount-1]+iTotalHeight;		   		
		   	}
		 }
		else this.height=iParamHeight[2];
		
		
    	//set up listeners
    	ParamEventListener eventListener = new ParamEventListener();
    	this.jCompDH = new JComponentDragHandler(workspace,this);
    	this.addMouseListener(eventListener);
    	this.addMouseMotionListener(eventListener);
    	paramTable.addMouseListener(new MouseAdapter() {
    	    /**
    	     * Implement MouseListener interface
    	     */
    		public void mouseEntered(MouseEvent e) {			
    			Param param = Param.this;
    			param.setPressed(true);
    			param.showOnTop();
    		}
    	});
    	paramTable.addFocusListener(eventListener);    	    	

    	this.reformParam();
    	    	
    	this.arrow = new ParamArrow(this);
    	
    	paramLabel = new ParamLabel(workspace,source.getBlockID());
    	source.add(paramLabel);
    	paramLabel.setActive(true);

    	this.reformParam();     	    	    	    
    	
    	revalidate();
    	repaint();   	
    	
    	//add this param source
        PreviousState psObject=new PreviousState(source);       
    	if(fireEvent)
    	workspace.notifyListeners(new WorkspaceEvent(workspace,getParamSource().getParentWidget(), WorkspaceEvent.BLOCK_PARAM_ADDED,psObject));
    }
    
    /**
     * Sets the state of the 3rd and 4th columns according to the
     * state of the parameter.
     */
    private void finalizePopulation(){   
    		    	
    	for(int i=0; i<paramTable.getRowCount(); ++i){   		
    		
    		//Class Component: JComboBox
    		DefaultCellEditor dce2=(DefaultCellEditor) paramTable.getCellEditor(i, 2);
    		dce2.getComponent().setEnabled(((Boolean)paramTable.getValueAt(i, 0)));
    		
    		//Value component: JTextField
    		JTextField jtf=new JTextField();    		
    		jtf.setEnabled(((Boolean)paramTable.getValueAt(i, 0)));    		
    		TableCellEditor editor = new DefaultCellEditor(jtf);    					
    		cellEditorModel.addCellEditor(i, 3, editor);    		
    	}    	
    }
    
    private void prepareTableModel(Vector<Object> vector){
    	Vector<String> columnName = new Vector<String>();
	    columnName.addElement("Param");
	    columnName.addElement("Name");
	    columnName.addElement("Class");	    
	    columnName.addElement("Value");	    

	    ParamTableModel tableModel = new ParamTableModel();
	    tableModel.setDataVector(vector, columnName);
	    paramTable.setModel(tableModel);
    }
    
    /**
     * Populates the parameter table with instance information supplied
     * as a container object from the command-line arguments to our policy editor
     * @param container
     */
    private void populateInContext(de.tum.in.i22.uc.policy.classes.Resource container){
    	//We are dealing with file system objects for now so we
    	//search for the parameter with name as 'object' and work from 
    	//there
    	for(int i=0; i<paramTable.getRowCount(); i++){
    		if(paramTable.getValueAt(i, 1).toString().equalsIgnoreCase("object")){
    			//set the values of the class and parameter in context
    			paramTable.setValueAt(true, i, 0);
    			paramTable.setValueAt(container.getDataClass(), i, 2);
    			paramTable.setValueAt(container.getDataSource(), i, 3);    	
    			  			
    			break;
    		}
    	}
    }
    
    /**
     * When a parameter table is created freshly in an unsaved session
     * this is called. It just populates the first two columns based upon the
     * users selection of action.
     * @return
     */
    private Vector<Object> populateTableAfresh(List<String> paramNames){    	
    	//populate table
	    Vector<Object> paramNameVector = new Vector<Object>(paramNames.size());
	    Vector<Boolean> selectVector = new Vector<Boolean>();
	    Vector<Object> paramVector = new Vector<Object>(paramNames.size());
	   
	   for (int i = 0; i < paramNames.size(); i++){
			Vector<Object> tempv = new Vector<Object>();
			//add parameter names e.g. object, device
			paramNameVector.removeAllElements();
		    paramNameVector.addElement(paramNames.get(i));
		    //add parameter select status
		    selectVector.removeAllElements();
		    selectVector.addElement(false);
		    //finalizing
		    tempv.removeAllElements();
		    tempv.addAll(selectVector);
		    tempv.addAll(paramNameVector);
		    paramVector.add(tempv);
	   }     	
    	return paramVector;
    }
    
    /**
     * This routine prepares the 3rd column of a parameter table
     * @param action: The action the user selected e.g. copy, play or delete
     */
    private void initializePopulation(String action, List<String> paramNames){    	        
                
    	// for each param in each row, set class
    	for(int x=0; x<paramNames.size(); x++){
    		
    		List<String> paramValues = new PublicMethods().returnValuesByParam(action, paramNames.get(x));
    		        								
    		//status component: JCheckBox
    		JCheckBox jck=new JCheckBox();
    		TableCellEditor dce1=new DefaultCellEditor(jck);
    		cellEditorModel.addCellEditor(x,0,dce1);
    		
    		JComboBox pval = new JComboBox();
    			    
    		//This initialization is for unsaved editor sessions.
    		alOldParameterValues.add(" ");
    		alOldParameterClasses.add("");
    			    
    		//15 March 2013. This combo-box should be disabled until the user checks
    		//the checkbox beside it.    		
    		pval.setEnabled(false);		    
    			    
    		for(int y=0; y<paramValues.size(); y++)		    
    		pval.addItem(paramValues.get(y));			 			
    			    
    		TableCellEditor editor = new DefaultCellEditor(pval);
    		//tell the RowEditorModel to use editor for row x				
    		cellEditorModel.addCellEditor(x, 2, editor);
    		paramTable.revalidate();
    	 
    	 }	
    }
    
    private Vector<Object> populateTableFromFile(){    	    	
    	
    	//populate table
	    Vector<Object> paramNameVector = new Vector<Object>();
	    Vector<Boolean> paramSelectVector = new Vector<Boolean>();
	    Vector<Object> paramValueVector=new Vector<Object>();
	    Vector<Object> paramClassVector=new Vector<Object>();
	    Vector<Object> paramVector = new Vector<Object>();	   
	   
	   for (int i = 0; i < alParamNames.size(); i++){
		Vector<Object> tempv = new Vector<Object>();
		//add parameter name
		paramNameVector.removeAllElements();		
	    paramNameVector.addElement(alParamNames.get(i));
	    //add parameter status: selected or not
	    paramSelectVector.removeAllElements();
	    paramSelectVector.addElement(alParamSelected.get(i));	    		
    	//add parameter value
    	paramValueVector.removeAllElements();	    
	    paramValueVector.addElement(alParamValues.get(i));
	    	//setting this for saved editor sessions that have been loaded.
	    	//helpful for undo and redo.
	    	alOldParameterValues.set(i, alParamValues.get(i));
	    //add class value
	    paramClassVector.removeAllElements();
	    paramClassVector.addElement(alParamClass.get(i));
	    	alOldParameterClasses.set(i,alParamClass.get(i));
	    //Finalizing...
	    tempv.removeAllElements();
	    tempv.addAll(paramSelectVector);
	    tempv.addAll(paramNameVector);
	    tempv.addAll(paramClassVector);
	    tempv.addAll(paramValueVector);
	    paramVector.add(tempv);
	   } 	   	      	   
    	
    	return paramVector;
    }
    
    /**
     * Handle the removal of this param from its param source
     */
    public void delete() {
    	deleteProperly(true);
    }
    
    public void deleteProperly(boolean fireEvent){
    	//preserve old state of block before removal
    	PreviousState psObject=new PreviousState(paramSource);
    	psObject.addOtherStates(paramTable.getModel());
    	RenderableBlock rblock=(RenderableBlock) getParamSource();
    	//2 work on l8r
    	psObject.addOtherStates(rblock.getBlock().getGenusName());
    	
    	//workspace.notifyListeners(new WorkspaceEvent(workspace,getParamSource().getParentWidget(), WorkspaceEvent.BLOCK_PARAM_REMOVED));
    	if(fireEvent)
    	workspace.notifyListeners(new WorkspaceEvent(workspace,getParamSource().getParentWidget(), WorkspaceEvent.BLOCK_PARAM_REMOVED,psObject));
		
    	getParent().remove(arrow.arrow);
		setParent(null);
		
		if (paramSource instanceof RenderableBlock) {
			RenderableBlock rb = (RenderableBlock) paramSource;

			rb.remove(paramLabel);
	    	paramLabel = null;
		}
    }
    
    /**
     * returns the paramSource for this param
     * @return
     */
    public ParamSource getParamSource() {
    	return paramSource;
    }
    
    /**
     * returns the paramLabel for this param
     * @return
     */
    ParamLabel getParamLabel() {
    	return paramLabel;
    }
    
    /**
     * Returns the width of the param label for this param
     * @return
     */
    public int getParamLabelWidth() {
    	if (paramLabel == null) return 0;
    	return paramLabel.getWidth();
    }

    /**
     * Updates the param and paramLabel 
     */
    public void update() {
    	if (paramLabel != null) {
    		setVisible(paramLabel.isActive());
    		paramLabel.update();    		
    		if (arrow.arrow != null) 
    			arrow.setVisible(paramLabel.isActive());    		
    	}
    }
    
    /**
     * Sets the active state of the commentLabel and updates the comment and commentLabel
     * @param visibleState
     */
    public void update(boolean visibleState) {
    	if (paramLabel != null) {    		
    		paramLabel.setActive(visibleState);
    	}
    	update();
    }
    
    /**
     * Set a new zoom level, changes font size, label size, location, shape of comment, and arrow for this comment
     * @param newZoom
     */
    public void setZoomLevel(double newZoom) {
		// calculates the new position based on the initial position when zoom is at 1.0
    	this.zoom = newZoom;
    	this.paramTable.setFont(new Font(fontname, Font.PLAIN, (int)(12*zoom)));
    	if (paramLabel != null) paramLabel.setZoomLevel(newZoom);

    	this.reformParam();
    	this.getArrow().updateArrow();
    }
    
    /**
     * Recalculate the shape of this param 
     */
    public void reformParam(){
    	
    	int w = paramTable.getPreferredSize()!=null ? (int)(this.width*zoom) : (int)(Param.MINIMUM_WIDTH*zoom);
    	int h = paramTable.getPreferredSize()!=null ? (int)(this.height*zoom) : (int)(Param.MINIMUM_HEIGHT*zoom);
    	int m = (int)(this.margin*zoom);

    	GeneralPath path2 = new GeneralPath();
    	path2.moveTo(m-1,m-1);
    	path2.lineTo(w-m, m-1);
    	path2.lineTo(w-m, h-m);
    	path2.lineTo(m-1, h-m);
    	path2.closePath();
    	pTable = path2;
  
    	body = new RoundRectangle2D.Double(0,0,w-1,h-1,3*m,3*m);
    	
    	GeneralPath path3 = new GeneralPath();
    	path3.moveTo(w-3*m,h);
    	path3.lineTo(w,h-3*m);
    	path3.curveTo(w,h,w,h,w-3*m,h);
    	resize = path3;
    	
    	scrollPane.setBounds(m,m, w-2*m, h-2*m);    	
    	scrollPane.setThumbWidth(paramTable.getPreferredSize()!=null ? 2*m : 0);
    	
    	this.setBounds(this.getX(), this.getY(), w, h);
    	this.revalidate();
    	this.repaint();
    	
    	if (arrow != null) arrow.updateArrow();    	
    }
    
	/**
	 * returns the descaled x based on the current zoom
	 * that is given a scaled x it returns what that position would be when zoom == 1
	 * @param x
	 * @return
	 */
	private int descale(double x){
		return (int)(x/zoom);
	}
  
    
    //////////////////////// 
    // SAVING AND LOADING //
    ////////////////////////
    private final String EQ_OPEN_QUOTE = "=\"";
    private final String CLOSE_QUOTE ="\" ";
    /**
     * Returns an escaped (safe) version of string.
     */
    public static String escape(String s) {
        return s.replaceAll("&", "&amp;")
                .replaceAll("<", "&lt;")
                .replaceAll(">", "&gt;");
    }
    
    //TODO may put this in a separate XML writing utility class
    private void appendAttribute(String att, String value, StringBuffer buf){
        buf.append(att);
        buf.append(EQ_OPEN_QUOTE);
        buf.append(escape(value));
        buf.append(CLOSE_QUOTE);
    }
    /**
     * Returns the save String for this comment.
     * @return
     */
    public Node getSaveNode(Document document){
    	Element paramsElement = document.createElement("Params");
    	    	    	      
    	ArrayList<CParameter> alParams=this.getParameters();
    	if(alParams.size()>0)
        for(int i=0; i<alParams.size(); i++){  
          	Element paramElement = document.createElement("Param");
	       	paramElement.setAttribute("status", alParams.get(i).getStatus());
	       	paramElement.setAttribute("name", alParams.get(i).getName());
	       	paramElement.setAttribute("class", alParams.get(i).getClassInfo());
	       	paramElement.setAttribute("value", alParams.get(i).getValue());
	       	//policy type attribute is always associated with an 
	       	//instance row
	       	if(alParams.get(i).getName().equalsIgnoreCase("object")){
	       		paramElement.setAttribute("policyType", jcbPolicyType.getSelectedItem().toString());
	       	}
	       	paramsElement.appendChild(paramElement);         	        	       
        }        
    	
    	// Location
    	Element locationElement = document.createElement("Location");
    	Element xElement = document.createElement("X");
    	xElement.appendChild(document.createTextNode(String.valueOf(descale(getLocation().getX()))));
    	locationElement.appendChild(xElement);
    	
    	Element yElement = document.createElement("Y");
    	yElement.appendChild(document.createTextNode(String.valueOf(descale(getLocation().getY()))));
    	locationElement.appendChild(yElement);
    	
    	paramsElement.appendChild(locationElement);
    	
    	// Box size
    	Element boxSizeElement = document.createElement("BoxSize");
    	Element widthElement = document.createElement("Width");
    	widthElement.appendChild(document.createTextNode(String.valueOf(descale(getWidth()))));
    	boxSizeElement.appendChild(widthElement);
    	
    	Element heightElement = document.createElement("Height");
    	heightElement.appendChild(document.createTextNode(String.valueOf(descale(getHeight()))));
    	boxSizeElement.appendChild(heightElement);
    	
    	paramsElement.appendChild(boxSizeElement);
    	
    	// Collapse
    	if (!paramLabel.isActive()) {
    		Element collapsedElement = document.createElement("Collapsed");
    		paramsElement.appendChild(collapsedElement);
    	}
    	
    	return paramsElement;
    }
    
    //set the policy type. Useful for loading policy diagrams
    public void setPolicyType(String type){    	 
    	//keep the old state for undo-redo monitoring
    	sOldPolicyType=type;
    	ParamItemListener.bFireEvent=false;
    	jcbPolicyType.setSelectedItem(type);    	    	   	
    }
    
    //Get current policy type value
    public String getPolicyType(){
    	return jcbPolicyType.getSelectedItem().toString();
    }
    
    //27 April 2013
    //Gets the previous policy type value. Useful
    //for undo-redo
	public String getOldPolicyType(){
		return sOldPolicyType;
	}
     
    
    /**
     * Loads the param from a NodeList of param parts
     * @param commentChildren
     * @param rb
     * @return
     */
    public static Param loadParam(Workspace workspace, NodeList paramChildren, RenderableBlock rb) {
        Param param = null;
        boolean paramCollapsed = false;
        alParamNames=new ArrayList<String>();
        alParamSelected=new ArrayList<Boolean>();
        alParamClass=new ArrayList<String>();
        alParamValues=new ArrayList<String>();
        String sPolicyType="";
        
        Node paramChild;
        String text = null;
        Point paramLoc = new Point(0,0);
        Dimension boxSize = new Dimension(Param.DEFAULT_WIDTH, Param.DEFAULT_HEIGHT);
        
        //we first get the action name
        Node nParent=paramChildren.item(0).getParentNode().getParentNode();
    	if(nParent!=null){
    		Node nGenus=nParent.getAttributes().getNamedItem("genus-name");    		
    		if(nGenus!=null) {
    			text=nGenus.getNodeValue();    			
    		}
    	}
        
    	//prepare other information necessary to organize this block
        for(int j=0; j<paramChildren.getLength(); j++){
        	paramChild = paramChildren.item(j);
        	
            if(paramChild.getNodeName().equals("Location")){            	
                RenderableBlock.extractLocationInfo(paramChild, paramLoc);
            }else if(paramChild.getNodeName().equals("BoxSize")){            	
                RenderableBlock.extractBoxSizeInfo(paramChild, boxSize);
            }else if(paramChild.getNodeName().equals("Collapsed")){
            	paramCollapsed = true;
            }else if(paramChild.getNodeName().equals("Param")){
            	String sName=paramChild.getAttributes().getNamedItem("name").getNodeValue();
            	String sValue=paramChild.getAttributes().getNamedItem("value").getNodeValue();
            	String sStatus=paramChild.getAttributes().getNamedItem("status").getNodeValue();
            	String sClass=paramChild.getAttributes().getNamedItem("class").getNodeValue();
            	alParamNames.add(sName);
            	alParamValues.add(sValue);
            	alParamSelected.add(Boolean.valueOf(sStatus));
            	alParamClass.add(sClass);
            	//set the policy type
            	//policy type is always associated with
            	//the instance row
            	if(sName.equalsIgnoreCase("object"))
            	sPolicyType=paramChild.getAttributes().getNamedItem("policyType").getNodeValue();
            }        	            
        }

        if(text != null){
        	param=new Param(workspace,text, rb, rb.getBlock().getColor(),rb.getZoom());
            param.setLocation(paramLoc.x, paramLoc.y);
            param.update(!paramCollapsed);
            param.setMyWidth((int)boxSize.getWidth());
            param.setMyHeight((int)boxSize.getHeight());
            param.reformParam();
            param.finalizePopulation();
            param.setPolicyType(sPolicyType);
            //cleaning up
            alParamNames=null;
            alParamValues=null;
            alParamClass=null;
            alParamSelected=null;            
        }
        return param;
    }
    
    /**
     * overrides javax.Swing.JPanel.paint()
     */
 	public void paint(Graphics g){
 		Graphics2D g2 = (Graphics2D)g;
 		g2.addRenderingHints(new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON));
 		
 		
 		if (active) {
 	 		g2.setColor(getBorderColor().brighter());
 		} else {
 	 		g2.setColor(getBorderColor());
 		} 		
 		g2.fill(body);
 		if (active) {
 			g2.setColor(Param.background.brighter());
 		} else {
 			g2.setColor(Param.background);
 		}
 		g2.fill(pTable);
 		if (active) {
 	 		g2.setColor(Color.white);
 		} else {
 	 		g2.setColor(Color.lightGray);
 		}
 		g2.draw(pTable);
 		if (active) {
 	 		g2.setColor(Color.lightGray.brighter());
 		} else {
 	 		g2.setColor(Color.lightGray);
 		}
 		g2.fill(resize);
 		
 		super.paint(g);
 	}
    
    public ArrayList<CParameter> getParameters() {
    	ArrayList<CParameter> alParams=new ArrayList<CParameter>();
  
    	for(int i=0; i<=paramTable.getModel().getRowCount()-1; i++){
    		CParameter param=new CParameter();
    		param.setStatus(paramTable.getModel().getValueAt(i, 0).toString());
    		param.setName(paramTable.getModel().getValueAt(i, 1).toString());
    		//class information
    		if(paramTable.getModel().getValueAt(i, 2)==null) param.setClassInfo(" ");
    		else param.setClassInfo(paramTable.getModel().getValueAt(i, 2).toString());
    		//value
    		if(paramTable.getModel().getValueAt(i, 3)==null) param.setValue("");
    		else param.setValue(paramTable.getModel().getValueAt(i, 3).toString());
    		alParams.add(param);
       }
    	return alParams;
    }
    
    
    class CParameter{
    	private String sStatus;
    	private String sName;
    	private String sValue;
    	private String sClassInfo;
    	
		public CParameter(){   	}
		public String getStatus() {
			return sStatus;
		}
		public void setStatus(String sStatus) {
			this.sStatus = sStatus;
		}
		public String getName() {
			return sName;
		}
		public void setName(String sName) {
			this.sName = sName;
		}
		public String getValue() {
			return sValue;
		}
		public void setValue(String sValue) {
			this.sValue = sValue;
		}
		public String getClassInfo() {
			return sClassInfo;
		}
		public void setClassInfo(String sClass) {
			this.sClassInfo = sClass;
		}
    	
    }

    
    /**
     * @modifies editingPane, labelPane
     * @effects modify eiditngPane such that the next call to
     * 			editingPane.getText().trim() equals text.trim() &&
     * 			modify labelPane such that the next call to
     * 			labelPane.getText().trim() equals text.trim()
     * @param text
     */
    public void setText(String text) {
    	
//    	textArea.setText(text); // as of now commented; modify to get selected params and values
    }

    
     /**
      *  moves this to a new position at (x,y) but not outside of its parent Container
      * @modifies this.location
      * @effect  Set this.location.x to x, if x is within bounds of this.parent.
      * 			if not, then set this.location.x to closest boundary value. 
      *      	Set this.location.y to y, if y is within bounds of this.parent.
      * 			if not, then set this.location.y to closest boundary value. 
      * Override javax.Swing.JComponent.setLocation()
      */
    public void setLocation(int x, int y) {
    	if (isConstrainParam() && this.getParent() != null) {
	        //If x<0, set this.location.x to 0.
	        //If 0<x<this.parent.width, then set this.location.x to x.
	        //If x>this.parent.width, then set this.location.x to this.parent.width.
	        //repeat for y
	        if (y < 0) {
	            y = 0;
	        }else if (y + getHeight()  > this.getParent().getHeight()) {
	            y = Math.max(this.getParent().getHeight() - getHeight(), 0);
	        }
	        
	        if (x < 0) {
	            x = 0;           
	        } else if (x  + getWidth() + 1> this.getParent().getWidth()) {
	            x = Math.max(this.getParent().getWidth() - getWidth() - 1, 0);
	        }
    	}
        super.setLocation(x, y);
    	arrow.updateArrow();
    	workspace.getMiniMap().repaint();
    }
    
    /**
      * moves this to a new position at (x,y) but not outside of its parent Container
      * @modifies this.location
      * @effect  Set this.location.x to x, if x is within bounds of this.parent.
      * 			if not, then set this.location.x to closest boundary value. 
      *      	Set this.location.y to y, if y is within bounds of this.parent.
      * 			if not, then set this.location.y to closest boundary value. 
	  *
      * Override javax.Swing.JComponent.setLocation()
     */
    public void setLocation(Point p){
    	setLocation(p.x, p.y);
    }
    
    
     /**
      * @modifies this
      * @effect translate this.location
      * 		by dx in the x-direction and dy in the y-direction
      * @param dx
      * @param dy
      */
     public void translatePosition(int dx, int dy){
         this.setLocation(this.getX()+dx, this.getY()+dy);
     }
  	
     /**
      * Moves this comment from it's old parent Container to
      * a new Container.  Removal and addition applies only
      * if the Containers are non-null
      * @modifies the current this.parent and newparent
      * @effect First, remove this from current this.parent ONLY if
      * 		current this.parent is non-null.  Second, add this to
      * 		newparent container ONLY if newparent is non-null.
      * 		Third, repaint both modified parent containers.
      * @param newparent
      */
     public void setParent(Container newparent){
         this.setParent(newparent, 0);
     }
     
     /**
      * Over rides the standard setVisible to make sure the arrow's visibility is also set.
      */
     public void setVisible(boolean b) {
    	 super.setVisible(b);
    	 if (arrow.arrow != null) arrow.setVisible(b);
     }
     
     /**
      * Moves this comment from it's old parent Container to
      * a new Container with given constrain.
      * @modifies the current this.parent and newparent
      * @effect First, remove this from current this.parent ONLY if
      * 		current this.parent is non-null.  Second, add this to
      * 		newparent container ONLY if newparent is non-null.
      * 		Third, repaint both modified parent containers.
      * @param newparent
      * @parem constraints
      */
     public void setParent(Container newparent, Object constraints){
    	 //though it's tempting to just write "this.setParent(newparent)"
    	 //we can't do that because we must remove the comment as well
    	 
    	 //remove from the current this.parent Container if non-null
    	 Container oldParent = this.getParent();
         if (oldParent !=null){
        	 oldParent.remove(this);
        	 oldParent.remove(arrow.arrow);
        	 oldParent.validate();
        	 oldParent.repaint();
         }
         //add this to newparent Container if non-null
         if(newparent!=null){
        	 if(constraints == null){
        		 newparent.add(this, 0);
        	 }else{
        		 newparent.add(this, constraints);
        	 }
        	 arrow.updateArrow();
        	 newparent.validate();
             newparent.repaint();
         }
     }
   
    /**
     * String representation of this
     */
     public String toString() {
        return "Param ID: " + " at " + this.getLocation() + " with text: \"" + getParameters() + "\"";
    }
     
     /**
      * Bumps the comment to top of ZOrder of parent if parent exists
      */
     public void showOnTop() {
    	 if (getParent() != null) {
    		 getParent().setComponentZOrder(this, 0);
    	 }
     }
      
     /**
      * ParamEventListener is an inner class that
      * responds to the various external events,
      * and provides the requires semantic operations
      * for Params to be moved/focused correctly.
      * It owns, and sends semantic actions to the
      * outer Param class.
      */
     private class ParamEventListener implements FocusListener, MouseListener, MouseMotionListener{
       	    
    	 /**When focus lost, force a repaint**/	
    	 public void focusGained(FocusEvent e) {
    		 active = true;
    		 repaint();
    	 }
    	 /**When focuses gained, force a repaint**/
    	 public void focusLost(FocusEvent e) {
    		 active = false;
    		 repaint();
    	 }
    	 /**when clicked upon, switch to editing mode*/
    	 public void mouseClicked(MouseEvent e) {
    		 //prevent users from clicking multiple times and crashing the system
    		 if(e.getClickCount()>1) return;
    	 }

    	 /**highlight this param when a mouse begins to hover over this*/
    	 public void mouseEntered(MouseEvent e) {
    		 showOnTop();
    		 jCompDH.mouseEntered(e);
    	 }
    	 /**highlight this param when a mouse hovers over this*/
    	 public void mouseMoved(MouseEvent e) {
    		 if(paramTable.getRowSelectionAllowed()){
	    		 if(e.getX()>(width-2*margin) && e.getY()>(height-2*margin)){
	    			 Param.this.setCursor(new Cursor(Cursor.SE_RESIZE_CURSOR));
	    		 }else{
	        		 jCompDH.mouseMoved(e);
	    		 }
    		 }else{
        		 jCompDH.mouseMoved(e);
    		 }
    	 }
    	 /**stop highlighting this comment when a mouse leaves this*/
    	 public void mouseExited(MouseEvent e) {
    		 jCompDH.mouseExited(e);
    	 }
    	 /**prepare for a drag when mouse is pressed down*/
    	 public void mousePressed(MouseEvent e) {
    		 Param.this.grabFocus();  //atimer.stop();
    		 showOnTop();
    		 
    		 //Get the previous states: the initial position and size
             Param.this.pointOld=Param.this.getLocation();
             Param.this.dimensionOld=Param.this.getSize();
    		 
    		 jCompDH.mousePressed(e);
    		 if(paramTable.getRowSelectionAllowed()){
	    		 if(e.getX()>(width-2*margin) && e.getY()>(height-2*margin)){
	    			 setResizing(true);
	    		 }else{
	    			 if(e.getY()<margin){
	        			 setPressed(true);
	        		 }
	    		 }
    		 }else 
    		 if(e.getY()<margin){
    			 setPressed(true);
    		 }
    		 repaint();
    	 }
    	 
    	 /**when mouse is released*/
    	 public void mouseReleased(MouseEvent e) {
    		 jCompDH.mouseReleased(e);
    		//10 March 2013
             PreviousState ps;
             if(Param.this.isResizing()){
             	//means this comment might have been resized. We can fire event
             	ps=new PreviousState(Param.this);
             	ps.addOtherStates(Param.this.getParamSource());
             	ps.addOtherStates(Param.this.dimensionOld);
             	workspace.notifyListeners(new WorkspaceEvent(workspace, getParamSource().getParentWidget(), WorkspaceEvent.BLOCK_PARAM_RESIZED,ps));
             }
             else{
             	//means this comment might have been moved. We can fire event here
             	ps=new PreviousState(Param.this);
             	ps.addOtherStates(Param.this.getParamSource());
             	ps.addOtherStates(Param.this.pointOld);
             	workspace.notifyListeners(new WorkspaceEvent(workspace, getParamSource().getParentWidget(), WorkspaceEvent.BLOCK_PARAM_MOVED,ps));
             }
    		 setResizing(false);
    		 setPressed(false);
    		 repaint();
    	 }
    	 
    	 /**drag this when mouse is dragged*/
    	 public void mouseDragged(MouseEvent e) {	
    		 if(isResizing()){
    			 double ww = e.getX()>MINIMUM_WIDTH*zoom ? e.getX() : MINIMUM_WIDTH*zoom;
    			 double hh = e.getY()>MINIMUM_HEIGHT*zoom ? e.getY() : MINIMUM_HEIGHT*zoom;
    			 width = (int)ww;
    			 height = (int)hh;
    			 reformParam();
    			 //workspace.notifyListeners(new WorkspaceEvent(workspace,getParamSource().getParentWidget(), WorkspaceEvent.BLOCK_PARAM_RESIZED));
    		 }else{
	    		 jCompDH.mouseDragged(e);
	    		 arrow.updateArrow();
    			 //workspace.notifyListeners(new WorkspaceEvent(workspace,getParamSource().getParentWidget(), WorkspaceEvent.BLOCK_PARAM_MOVED));
    		 }
    	 }
     }
	 
	 /**
	  * Returns the comment background color
	  * @return
	  */
	 Color getBackgroundColor() {
		 return background;
	 }
	 
	 /**
	  * Returns the borderColor of this comment
	  * @return
	  */
	 Color getBorderColor() {
		 return borderColor;
	 }

	 /**
	  * access to the comment arrow object
	  * @return
	  */
	public ParamArrow getArrow() {
		return arrow;
	}


	
	/**
	 * @return the width
	 */
	int getMyWidth() {
		return width;
	}

	/**
	 * @param width the width to set
	 */
	public void setMyWidth(int width) {
		this.width = width;
	}

	/**
	 * @return the height
	 */
	int getMyHeight() {
		return height;
	}

	/**
	 * @param height the height to set
	 */
	public void setMyHeight(int height) {
		this.height = height;
	}

	/**
	 * @return the margin
	 */
	int getMargin() {
		return margin;
	}

	/**
	 * @param margin the margin to set
	 */
	void setMargin(int margin) {
		this.margin = margin;
	}

	/**
	 * Test application for comment.
	 * @param args
	 */
//	 public static void main(String[] args) {
//		 //Need comment source for comments
//		 
//		 /**
//			JFrame f = new JFrame();
//			f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//			f.setLayout(new BorderLayout());
//			f.setSize(400, 400);
//			JPanel p =new JPanel(null);
//			p.add(new Comment("", null,  Color.green, 1.0), 0);
//			f.add(p);
//			p.add(new Comment("", null,  Color.orange, 1.0), 0);
//			f.add(p);
//			p.add(new Comment("", null,  Color.red, 1.0), 0);
//			f.add(p);
//			p.add(new Comment("", null,  Color.blue, 1.0), 0);
//			f.add(p);
//			f.setVisible(true);
//		  */
//	 }

	/**
	 * @return the pressed true, if this comment has been pressed
	 */
	boolean isPressed() {
		return pressed;
	}

	/**
	 * @param pressed true if this comment has been pressed
	 */
	void setPressed(boolean pressed) {
		this.pressed = pressed;
	}

	/**
	 * @return the resizing, true if this comment is being resized
	 */
	boolean isResizing() {
		return resizing;
	}

	/**
	 * @param resizing true if this comment is being resized
	 */
	void setResizing(boolean resizing) {
		this.resizing = resizing;
	}

	/**
	 * returns whether this comment should be constrained to its parent's bounds
	 * @return the constrainComment
	 */
	public boolean isConstrainParam() {
		return constrainParam;
	}

	/**
	 * sets whether this comment should be constrained to its parent's bounds
	 * @param constrainComment the constrainComment to set
	 */
	public void setConstrainParam(boolean constrainParam) {
		this.constrainParam = constrainParam;
	}
	
	//08 March 2013
	public JTableX getParamTable() {
		return paramTable;
	}
	
	//10 March 2013
	public ParamTableModel getParamTableModel(){
		return (ParamTableModel)paramTable.getModel();
	}
	
	public void setTableModel(ParamTableModel ptm){
		paramTable.setModel(ptm);	
	}
	
	//16 March 2013	
	class ParameterTableListener implements javax.swing.event.TableModelListener{
		
		private final Workspace workspace;
		
		public ParameterTableListener(Workspace ws){
			workspace=ws;
		}
		
		@Override
		public void tableChanged(TableModelEvent e) {
			
			PreviousState psObject=new PreviousState(Param.this.getParamSource());
			ParamTableModel ptm=(ParamTableModel)paramTable.getModel();
			
			
			if(e.getType()==TableModelEvent.UPDATE){										
				int iRow=e.getFirstRow();
				int iCol=e.getColumn();
				//preparing previous state for undo-redo
				psObject.addOtherStates(iRow);
				psObject.addOtherStates(iCol);
				//Get the check box object
				DefaultCellEditor dce=(DefaultCellEditor)paramTable.getCellEditor(iRow, 0);
				JCheckBox jck=(JCheckBox) dce.getComponent();
				//Get the combo box object
				dce=(DefaultCellEditor)paramTable.getCellEditor(iRow, 2);				
				JComboBox jcb=(JComboBox) dce.getComponent();
				//Get the text box object
				dce=(DefaultCellEditor)paramTable.getCellEditor(iRow, 3);
				JTextField jtf=(JTextField) dce.getComponent();
				//
				ptm=(ParamTableModel)paramTable.getModel();
				//Get the state of this row's checkbox
								
				//1st column: status
				if(iCol==0){					
					//is the checkbox along it selected? Yes...
					boolean b=(Boolean)paramTable.getModel().getValueAt(iRow, 0);
					if(jck.isSelected() || b){
						jcb.setEnabled(true);
						jtf.setEnabled(true);
					}					
					else {						
						jcb.setEnabled(false);	
						jtf.setEnabled(false);
					}
																					
					if(ptm.getFireParamChangeEvent())
						Param.this.workspace.notifyListeners(new WorkspaceEvent(Param.this.workspace,getParamSource().getParentWidget(), WorkspaceEvent.BLOCK_PARAM_STATUS_CHANGED,psObject));
																					
				}
				//3rd column: class of parameter
				else if(iCol==2){
					//When the checkbox is selected
					boolean b=(Boolean)ptm.getValueAt(iRow, 0);
					if(b){
						//This event is a class event.
						//ptm=(ParamTableModel)paramTable.getModel();
						String sOld=alOldParameterValues.get(iRow);
						//add old value
						psObject.addOtherStates(sOld);
						String sNew=(String)paramTable.getModel().getValueAt(iRow, iCol);
						if(sOld!=null && sNew!=null){
							if(!sNew.equalsIgnoreCase(sOld)){
//								System.out.println("Value change");
								//add new value
								psObject.addOtherStates(sNew);
								//update our old value
								alOldParameterValues.set(iRow, sNew);
								if(ptm.getFireParamChangeEvent())
									Param.this.workspace.notifyListeners(new WorkspaceEvent(Param.this.workspace,getParamSource().getParentWidget(), WorkspaceEvent.BLOCK_PARAM_CLASS_CHANGED,psObject));
							}						
						}
					}						
				}
				//4th column: value of parameter
				else if(iCol==3){
					//When the checkbox is selected
					boolean b=(Boolean)ptm.getValueAt(iRow, 0);
					if(b){
						//This event is a value event.
						//ptm=(ParamTableModel)paramTable.getModel();						
						String sOld=alOldParameterClasses.get(iRow);
						//add old value
						psObject.addOtherStates(sOld);
						String sNew=(String)paramTable.getModel().getValueAt(iRow, iCol);
						if(sOld!=null && sNew!=null){
							if(!sNew.equalsIgnoreCase(alOldParameterClasses.get(iRow))){
//								System.out.println("Value change");
								//add new value
								psObject.addOtherStates(sNew);		
								//Update our old value
								alOldParameterClasses.set(iRow, sNew);
								if(ptm.getFireParamChangeEvent())
									Param.this.workspace.notifyListeners(new WorkspaceEvent(Param.this.workspace,getParamSource().getParentWidget(), WorkspaceEvent.BLOCK_PARAM_VALUE_CHANGED,psObject));
							}
						}
					}
				}
			}
			
		}
		
	}
	
} 

