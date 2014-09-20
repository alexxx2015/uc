package de.tum.in.i22.uc.ptp.policy.translation.actionrefinement;
///**
// * 
// */
//package de.tum.in.i22.uc.policy.translation.actionrefinement;
//
//import java.io.IOException;
//import java.io.StringReader;
//import java.io.StringWriter;
//import java.util.ArrayList;
//import java.util.List;
//
//import javax.xml.parsers.DocumentBuilder;
//import javax.xml.parsers.DocumentBuilderFactory;
//import javax.xml.parsers.ParserConfigurationException;
//import javax.xml.transform.OutputKeys;
//import javax.xml.transform.Result;
//import javax.xml.transform.Source;
//import javax.xml.transform.Transformer;
//import javax.xml.transform.TransformerConfigurationException;
//import javax.xml.transform.TransformerException;
//import javax.xml.transform.TransformerFactory;
//import javax.xml.transform.dom.DOMSource;
//
//import org.w3c.dom.Element;
//import org.w3c.dom.Node;
//import org.w3c.dom.NodeList;
//import org.xml.sax.InputSource;
//import org.xml.sax.SAXException;
//
//import de.tum.in.i22.uc.utilities.ConstantsAndEnums.Operators;
//
///**
// * @author ladmin
// *
// */
//
//public class TauEvRules {
//
//	TauEvRules(){
//		buf = new StringBuffer();
//	}
//    /*
//     * declare a generic string to remember the parent node's name as in this recursive
//     * function, it is overwritten with every function call 
//     */
//    String parentnodename = null;
//    /*
//     * a static string buffer to maintain the translation
//     * 
//     */
//    static StringBuffer buf; /*= new StringBuffer();*/
//    
//    /**
//     * this function is recursively called to run TAUACTION on each operator in OSL.
//     * Intuitively, each call rips off the outermost node and moves the translation 
//     * to the next level until it finds an event or FORALL or EXISTS
//     * 
//     * @param str
//     */
//	protected void runTauEvRules(String str){
//		
//		if(!str.equalsIgnoreCase(null)){
//		
//		Operators operator = null; 
//		
//
//		String nodeName = null;
//		List<String> phi = new ArrayList<String>();
//		int count = 0;
//		
//		try {
//
//			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
//			DocumentBuilder builder = factory.newDocumentBuilder();
//			
//			InputSource inputSource = new InputSource( new StringReader( str ) );
//			org.w3c.dom.Document doc = builder.parse(inputSource);
//			
//			TransformerFactory tranFactory = TransformerFactory.newInstance(); 
//			Transformer aTransformer = tranFactory.newTransformer(); 			
//			
//			org.w3c.dom.Node outerNode = doc.getFirstChild(); 
//			nodeName = outerNode.getNodeName();
//			
//			NodeList childList = outerNode.getChildNodes();	
//			count = 0;
//			for (int i=0; i<childList.getLength(); i++){
//			    Node element = childList.item(i).cloneNode(true);  
//		   
//			   operator = Operators.valueOfIgnoreCase(nodeName);	
//			   
//			   /**
//			    * here starts the operator wise translation
//			    */
//			   switch (operator) {
//			
//			   case ACTION:
//				   // call action refinement
//				   PIMAction action=PIMAction.createAction(resource);	
//					if(action==null)
//						throw new ActionMatchingException("No matching action {"+resource.getExtraInformation().toString()+"} in domain file.");
//					
//					String sXmlOutputTauState;
//						
//					//init
//					preparePolicyParameters();
//					
//					//1st step: action refinement for events, i.e. PIM -> PSM -> ISM
//					//our result is in sXmlOutputTauEvent
//					tauEvent(action);					
//					
//					break;
//					
//			   case TIMESTEPS:
//					buf.append(str);
//					
//					break;
//				   
//			   case NUM:
//					buf.append(str);
//					
//					break;
//					
//			   case EVAL:
//					buf.append(str);
//					
//					break;
//				   
//			   case TRUE:
//					buf.append("<true/>");
//					
//					break;
//					
//			   case FALSE:
//					buf.append("<false/>");
//					
//					break;
//					
//			   case NOT:
//					buf.append("<not>");
//					getPastCondition(phi.get(0));
//					buf.append("</not>");	
//					
//					break;
//					
//			   case ALWAYS: //optimized translation
//					buf.append("<not>");
//					getPastCondition(phi.get(0));
//					buf.append("</not>");
//					break;
//					
//			   case AND:
//					buf.append("<and>");
//					getPastCondition(phi.get(0));
//				    getPastCondition(phi.get(1));
//					buf.append("</and>");
//					
//					break;
//					
//			   case OR:
//				   buf.append("<or>");
//				   getPastCondition(phi.get(0));
//				   getPastCondition(phi.get(1));
//				   buf.append("</or>");
//				   
//				   break;
//					
//			   case IMPLIES:
//				    buf.append("<implies>");
//					getPastCondition(phi.get(0));
//				    getPastCondition(phi.get(1));
//					buf.append("</implies>");
//					
//					break;
//					
//			   case UNTIL:    //optimized translation
//				    getPastCondition("<and><not>"+phi.get(0)+"</not><not>"+phi.get(1)+"</not></and>");
//										
//					break;
//					
//			   case AFTER:
//					buf.append("<and><before>");
//					buf.append(phi.get(0));
//					buf.append("<start/></before>");
//					buf.append("<not>");
//					getPastCondition(phi.get(1));
//					buf.append("</not></and>");
//					
//					break;
//					
//			   case WITHIN:
//					buf.append("<and><before>");
//					buf.append(phi.get(0));
//					buf.append("<start/></before>");
//					buf.append("<during>");
//					buf.append(phi.get(0));
//					getPastCondition("<not>"+phi.get(1)+"</not>");
//					buf.append("</during></and>");
//
//				    break;
//				
//			   case DURING:
//				    buf.append("<and><before>");
//					buf.append(phi.get(0));
//					buf.append("<start/></before>");
//					buf.append("<not><during>");
//					buf.append(phi.get(0));
//					getPastCondition(phi.get(1));
//					buf.append("</during></not></and>");
//
//					break;
//					
//			   case REPMAX:
//					buf.append("<and><not><repsince>");
//					parentnodename=operator.toString();
//					getPastCondition(phi.get(0));
//					buf.append("<start/>");
//					getPastCondition(phi.get(1));
//					buf.append("</repsince></not>");					
//					getPastCondition(phi.get(1));
//					buf.append("</and>");
//
//					break;
//					
//			   case REPLIM:
//					buf.append("<and><before>");
//					buf.append(phi.get(0));
//					buf.append("<start/></before>");
//					buf.append("<or>");
//					
//					// first formula inside 'or'
////					buf.append("<and>");
//					buf.append("<repsince>");
//					parentnodename=operator.toString();
//					getPastCondition(phi.get(1));
//					buf.append("<start/>");
//					getPastCondition(phi.get(3));
//					buf.append("</repsince>");
////					buf.append("<not>");
////					getPastCondition(phi.get(3));
////					buf.append("</not></and>");
//					
//					// second formula of 'or'
////					buf.append("<and>");
//					buf.append("<not><repsince>");
//					parentnodename=operator.toString();
//					exceptionalCase = true;
//					getPastCondition(phi.get(2));
//					exceptionalCase = false;
//					buf.append("<start/>");
//					getPastCondition(phi.get(3));
//					buf.append("</repsince></not>");
////					getPastCondition(phi.get(3));
////					buf.append("</and>");
//					
//					buf.append("</or></and>");
//
//					break;
//					
//			   case REPUNTIL:
//				    // first 'and'
//				    buf.append("<and>");
//				    
//				    // second 'and'
//				    buf.append("<and>");
//				    buf.append("<since><start/><not>");
//				    getPastCondition(phi.get(2));
//				    buf.append("</not></since>");
//				    
//				    buf.append("<not><repsince>");
//					parentnodename=operator.toString();
//					getPastCondition(phi.get(0));
//					buf.append("<start/>");
//					getPastCondition(phi.get(1));
//					buf.append("</repsince></not>");
//				    buf.append("</and>");
//				    // end of 'second and'
//				    
//				    getPastCondition(phi.get(1));
//					buf.append("</and>");
//
//					break;
//			
//			default:
//				buf.append(str);
//				System.out.println("wrong operator or translation not defined: "+operator.toString());
//				break;
//			}
//			   
//		} catch (ParserConfigurationException e) {
//			
//			e.printStackTrace();
//		} catch (IOException e) {
//			
//			e.printStackTrace();
//		} catch (SAXException e) {
//			
//			e.printStackTrace();
//		} catch (TransformerConfigurationException e) {
//			
//			e.printStackTrace();
//		} catch (TransformerException e) {
//			
//			e.printStackTrace();
//		}
//			 }
//			 
//	     }// end of rules for past translation    
//		
//
//
//}
