package de.tum.in.i22.uc.policy.translation.ecacreation;

import java.io.StringWriter;
import java.util.ArrayList;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * The not <or> elements of the refinement results
 * 
 * @author ELIJAH
 *
 */
public class Subformula {
	
	/**
	 * Create an instance of a subformula provided with an array of
	 * potential trigger events and an xml node source
	 * 
	 * @param triggerEvent
	 * @param source
	 */
	private Subformula(ArrayList<SubformulaEvent> events, Node source){
		this.alSubformulaEvent=events;
		nodeSource=source;
		alExecuteAction=new ArrayList<SubformulaEvent>();
	}
	
	/**
	 * Create an instance of subformula given an xml node
	 * 
	 * @param node
	 * @return Returns an instance of subformula for a given xml node
	 */
	public static Subformula createSubFormula(Node node){
		
		ArrayList<SubformulaEvent> alEvents=new ArrayList<SubformulaEvent>();
		
		/**
		 * We first check if this node is an <event> node. If not,
		 * we search for <event> nodes within it. If this fails, we
		 * simply use the node contents later
		 */
		//1. Is this node a trigger event?
		if(node.getNodeName().equalsIgnoreCase("event")){
			SubformulaEvent tEvent=SubformulaEvent.createTriggerEvent(node);
			alEvents.add(tEvent);
		}
		else{
			//2. Search for trigger <event> nodes within
			NodeList nlTriggerEvent=processXpathExpression(".//event",node);
			if(nlTriggerEvent!=null){
				if(nlTriggerEvent.getLength()>0)
				{
					for(int i=0; i<nlTriggerEvent.getLength(); ++i)
					{
						SubformulaEvent tEvent=SubformulaEvent.createTriggerEvent(nlTriggerEvent.item(i));
						alEvents.add(tEvent);
					}
				}
				else{
					/**
					 * There are no trigger <event> nodes. We
					 * will use this node's xml content later
					 */
				}
			}
		}
		
		return new Subformula(alEvents,node);
	}
	
	private ECARuleTemplate ecaTemplate;
	
	//rename this to subFormulaEvent
	private ArrayList<SubformulaEvent> alSubformulaEvent;
	private ECARule ecaRule;
	//Very helpful. In case we have no <event>
	//we just use this to get the xml of the node
	//later during ECA rule creation from UI
	private Node nodeSource;
	//Stores the radio button choice
	//for trigger event
	private Object oRbTriggerEvent;
	//Stores the radio button choice
	//for condition
	private Object oRbCondition;
	//The action option selected from
	//the dropdown
	private int iJcbAction;
	//In the Trigger Event area, users can type
	//events and specify parameters for it with
	//from a button. Those parameters and the
	//event name is stored here
	private SubformulaEvent oUserDefinedEvent;
	//In the action configuration area, the execute option
	//has the provision to specify actions. Each action has
	//a name and parameters. This definition is similar to
	//a subformula event hence the style below.
	private ArrayList<SubformulaEvent> alExecuteAction;
	
	public void setECATemplate(ECARuleTemplate template){
		this.ecaTemplate = template;
	}
	
	public ECARuleTemplate getECATemplate(){
		return this.ecaTemplate;
	}
	
	/**
	 * 
	 * @return Returns an array list of trigger events
	 */
	public ArrayList<SubformulaEvent> getEvents() {
		return alSubformulaEvent;
	}
	/**
	 * Sets the trigger events of this subformula
	 * 
	 * @param alTriggerEvent
	 */
	public void setEvents(ArrayList<SubformulaEvent> alEvents) {
		this.alSubformulaEvent = alEvents;
	}
	
	public boolean simpleEvent(){
		boolean result = this.alSubformulaEvent.size() <= 1;
		
		NodeList children = this.nodeSource.getChildNodes();
		
		
		
		return result;		
	}
	
	/**
	 * 
	 * @return Returns the ECARule for this subformula
	 */
	public ECARule getECARule() {
		return ecaRule;
	}
	
	public void setECARule(ECARule rule){
		ecaRule=rule;
	}
	
	public Node getNodeSource(){
		return nodeSource;
	}
	
	/**
	 * 
	 * @param expression
	 * @param document
	 * @return returns a node list of xml elements
	 */
	public static NodeList processXpathExpression(String expression, Object document){
		NodeList nodeList=null;				
		XPathFactory factory=XPathFactory.newInstance();
		XPath xpath=factory.newXPath();
		try {
			nodeList=(NodeList)xpath.evaluate(expression,document,XPathConstants.NODESET);
		} catch (XPathExpressionException e) {
			
			e.printStackTrace();
		}
		return nodeList;
	}
	
	/**
	 * 
	 * @return Returns a node and its content as an xml string
	 */
	public String getNodeString(){
		return getNodeString(nodeSource);	
	}
	
	public String getNodeString(Node node){
		StringWriter sw=null;
		StreamResult sr=null;		
		try{
			sw = new StringWriter();
			sr = new StreamResult(sw);
			DOMSource src = new DOMSource(node);			
			TransformerFactory transfac = TransformerFactory.newInstance();
			//
			transfac.setAttribute("indent-number", new Integer(2));
			Transformer trans = transfac.newTransformer();
			trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			//
			trans.setOutputProperty(OutputKeys.INDENT, "yes");
			trans.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
			trans.transform(src, sr);				
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return sw.toString();		
	}
	
	/**
	 * The next and previous buttons call this
	 */
	public void updateECARule(Object event, Object condition, Object action){		
		if(event instanceof String)
			ecaRule=new ECARule(new SubformulaEvent((String)event),condition,action);
		else 
			ecaRule=new ECARule((SubformulaEvent)event,condition,action);
	}
	
	/**
	 * 
	 * @return Returns the radio button trigger event selected by user.
	 * This is passed through the controller to the caller.
	 */
	public Object getRbTriggerEvent() {
		return oRbTriggerEvent;
	}

	/**
	 * Sets the particular radio button which was selected
	 * by the user during trigger event configuration.
	 * 
	 * @param oRbTriggerEvent
	 */
	public void setRbTriggerEvent(Object oRbTriggerEvent) {
		this.oRbTriggerEvent = oRbTriggerEvent;
	}

	/**
	 * 
	 * @return Returns the radio button for the selected condition.
	 * This is passed through the controller to the caller.
	 */
	public Object getRbCondition() {
		return oRbCondition;
	}
	
	/**
	 * Sets the particular radio button which was selected
	 * by the user during condition configuration.
	 * 
	 * @param oRbCondition
	 */
	public void setRbCondition(Object oRbCondition) {
		this.oRbCondition = oRbCondition;
	}
	
	/**
	 * Sets the index of the selected action in
	 * the combo-box
	 * 
	 * @param iAction
	 */
	public void setJcbAction(int iAction){
		this.iJcbAction=iAction;
	}
	
	/**
	 * 
	 * @return Returns the index of selected action 
	 */
	public int getJcbAction(){
		return this.iJcbAction;
	}
	
	/**
	 * 
	 * @return Returns the user-defined event specified
	 * by a user during trigger event configuration
	 */
	public SubformulaEvent getUserDefinedEvent(){
		return this.oUserDefinedEvent;
	}
	
	/**
	 * Sets the user defined event during trigger event
	 * configuration. This event is usually entered in
	 * a text-box.
	 * 
	 * @param event
	 */
	public void setUserDefinedEvent(SubformulaEvent event){
		oUserDefinedEvent=event;
	}
	
	/**
	 * 
	 * @return Return actions defined for execute action option
	 */
	public ArrayList<SubformulaEvent> getExecuteActions(){
		return alExecuteAction;
	}
	
	/**
	 * Updates the action in our list of actions for
	 * an execute option.
	 * 
	 * @param index
	 * @param event
	 */
	public void modifyExecuteAction(int index, SubformulaEvent event){		
		if(alExecuteAction.size()>0 && alExecuteAction.size()>index){
			alExecuteAction.set(index, event);
		}
	}
	
	/**
	 * Adds an action to the list of actions for an
	 * execute option.
	 * 
	 * @param event
	 */
	public void addExecuteAction(SubformulaEvent event){
		alExecuteAction.add(event);
	}
	
	public boolean equals(Object o){
		if( o == null)
			return false;
		if(!(o instanceof Subformula))
			return false;
		Subformula s = (Subformula) o;
		String ts = this.getNodeString();
		String os = s.getNodeString();
		return ts.equals(os);
	}
	
	public String toString(){
		return this.getNodeString();
	}
}
