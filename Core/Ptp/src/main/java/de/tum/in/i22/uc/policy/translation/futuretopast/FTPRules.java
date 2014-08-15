package de.tum.in.i22.uc.policy.translation.futuretopast;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import de.tum.in.i22.uc.policy.translation.Filter.FilterStatus;
import de.tum.in.i22.uc.policy.translation.actionrefinement.ActionRefinement;
import de.tum.in.i22.uc.utilities.ConstantsAndEnums.Operators;

import de.tum.in.i22.uc.policy.translation.*;

/**
 * @author Prachi
 * the core of future to past translation
 *
 */
public class FTPRules {
	
	/*
	 * 11 April 2013. buf was initially created outside this constructor
	 * but brings problem of duplicate results
	 */
	public FTPRules(){
		buf=new StringBuffer();
	}
	
	 /**
     * Returns an escaped (safe) version of string.
     */
    private static String escape(String s) {
        return s.replaceAll("&", "&amp;")
                .replaceAll("<", "&lt;")
                .replaceAll(">", "&gt;");
    }
	

    /*
     * declare a generic string to remember the parent node's name as in this recursive
     * function, it is overwritten with every function call 
     */
    String parentnodename = null;
    
    /*
     * out of all the cardinalty operators, translation of replim involves one time
     * "no downgradation" of the number value parameter. This case is to be flagged so that
     * the number value parameter remains intact as needed
     * 
     */
    boolean exceptionalCase=false;
    
    /*
     * a static string buffer to maintain the translation
     * 
     */
    static StringBuffer buf; /*= new StringBuffer();*/
    
    /**
     * this function is recursively called to 
     * get past translations of each operator in OSL.
     * intuitively, each call rips off the outermost node,
     * translates the corresponding operator to 
     * its past equivalent and passes on
     * 'phi' for further translation as 'tau(phi)'
     * @param str
     */
	protected void getPastCondition(String str){
		
		if(!str.equalsIgnoreCase(null)){
		
		Operators operator = null; 
		

		String nodeName = null;
		List<String> phi = new ArrayList<String>();
		int count = 0;
		
		try {

			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			
			InputSource inputSource = new InputSource( new StringReader( str ) );
			org.w3c.dom.Document doc = builder.parse(inputSource);
			
			TransformerFactory tranFactory = TransformerFactory.newInstance(); 
			Transformer aTransformer = tranFactory.newTransformer(); 			
			
			org.w3c.dom.Node outerNode = doc.getFirstChild(); 
			nodeName = outerNode.getNodeName();
			
			/*
			 * in case of repmax, repuntil and replim, the counter
			 * value needs to be modified as n-1			 * 
			 * 
			 */
			// begin ----
			if(nodeName.equalsIgnoreCase("num") && (parentnodename.equalsIgnoreCase("repmax")||parentnodename.equalsIgnoreCase("repuntil")||parentnodename.equalsIgnoreCase("replim")))
			{
				if(!exceptionalCase){
		
				int i = Integer.parseInt(((Element) outerNode).getAttribute("value"));
				i--;
			  	((Element) outerNode).setAttribute("value",Integer.toString(i));
		 
			  	// now let us update the source string str
			  	   Source src = new DOMSource(outerNode);
				   StringWriter writer = new StringWriter();
				    Result dest = new javax.xml.transform.stream.StreamResult(writer);
				    aTransformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
	    			   
				    // now we transform into a bare string
				    aTransformer.transform(src, dest);
				    str=writer.toString();				    
		 	} 			
			}// end ----
			
			NodeList childList = outerNode.getChildNodes();	
			count = 0;
			for (int i=0; i<childList.getLength(); i++){
			    Node element = childList.item(i).cloneNode(true);      

			 if(element.hasChildNodes() || element.getNodeName().equalsIgnoreCase("true") || element.getNodeName().equalsIgnoreCase("false") || element.getNodeName().equalsIgnoreCase("num")){				 
			   Source src = new DOMSource(element);
			   StringWriter writer = new StringWriter();
			    Result dest = new javax.xml.transform.stream.StreamResult(writer);
	
			    /*
			     *  we don't want *** <?xml version="1.0" **** to appear in the policy string. 
			     *  so let's truncate it
			     */			    
			    // begin --- 
			    aTransformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			    			   
			    // now we transform into a bare string
			   aTransformer.transform(src, dest); // end---
			   
			   phi.add(count, writer.toString());
			   count++;
			   
			 }
			}
			   
			   operator = Operators.valueOfIgnoreCase(nodeName);	
			   
			   /**
			    * here starts the operator wise translation
			    */
			   switch (operator) {
			
			   case ACTION:
				   // call action refinement: here we merged the earlier action refinement filter with FTP translation
			    
					ActionRefinement actionRefinement=new ActionRefinement(str);
					actionRefinement.filter();
					
					TranslationController.sMessage=actionRefinement.getMessage();
					TranslationController.fStatus=actionRefinement.getFilterStatus();
					if(TranslationController.fStatus==FilterStatus.FAILURE) return;
				
				    buf.append(ActionRefinement.sOutput);
					break;
					
			   case TIMESTEPS:
					buf.append(str);					
					break;
				   
			   case NUM:
					buf.append(str);					
					break;
					
			   case EVAL:
				    /* this is a more generic translation. 
				     * But I don't think we will have temporal operators inside "eval" function
				     */
//				    buf.append("<eval>");
//					getPastCondition(phi.get(0));
//					buf.append("</eval>");
					
					// ... hence this simple translation 
					buf.append(str);					
					break;
				   
			   case TRUE:
					buf.append("<true/>");					
					break;
					
			   case FALSE:
					buf.append("<false/>");					
					break;
					
			   case NOT:
					buf.append("<not>");
					getPastCondition(phi.get(0));
					buf.append("</not>");	
					
					break;
					
			   case ALWAYS: //optimized translation
					buf.append("<not>");
					getPastCondition(phi.get(0));
					buf.append("</not>");
					break;
					
			   case AND:
					buf.append("<and>");
					getPastCondition(phi.get(0));
				    getPastCondition(phi.get(1));
					buf.append("</and>");
					
					break;
					
			   case OR:
				   buf.append("<or>");
				   getPastCondition(phi.get(0));
				   getPastCondition(phi.get(1));
				   buf.append("</or>");
				   
				   break;
					
			   case IMPLIES:
				    buf.append("<implies>");
					getPastCondition(phi.get(0));
				    getPastCondition(phi.get(1));
					buf.append("</implies>");
					
					break;
					
			   case UNTIL:    //optimized translation
				    getPastCondition("<and><not>"+phi.get(0)+"</not><not>"+phi.get(1)+"</not></and>");
										
					break;
					
			   case AFTER:
					buf.append("<and><before>");
					buf.append(phi.get(0));
					buf.append("<start/></before>");
					buf.append("<not>");
					getPastCondition(phi.get(1));
					buf.append("</not></and>");
					
					break;
					
			   case WITHIN:
					buf.append("<and><before>");
					buf.append(phi.get(0));
					buf.append("<start/></before>");
					buf.append("<during>");
					buf.append(phi.get(0));
					getPastCondition("<not>"+phi.get(1)+"</not>");
					buf.append("</during></and>");

				    break;
				
			   case DURING:
				    buf.append("<and><before>");
					buf.append(phi.get(0));
					buf.append("<start/></before>");
					buf.append("<not><during>");
					buf.append(phi.get(0));
					getPastCondition(phi.get(1));
					buf.append("</during></not></and>");

					break;
					
			   case REPMAX:
					buf.append("<and><not><repsince>");
					parentnodename=operator.toString();
					getPastCondition(phi.get(0));
					buf.append("<start/>");
					getPastCondition(phi.get(1));
					buf.append("</repsince></not>");					
					getPastCondition(phi.get(1));
					buf.append("</and>");

					break;
					
			   case REPLIM:
					buf.append("<and><before>");
					buf.append(phi.get(0));
					buf.append("<start/></before>");
					buf.append("<or>");
					
					// first formula inside 'or'

					buf.append("<repsince>");
					parentnodename=operator.toString();
					getPastCondition(phi.get(1));
					buf.append("<start/>");
					getPastCondition(phi.get(3));
					buf.append("</repsince>");
					
					// second formula of 'or'

					buf.append("<not><repsince>");
					parentnodename=operator.toString();
					exceptionalCase = true;
					getPastCondition(phi.get(2));
					exceptionalCase = false;
					buf.append("<start/>");
					getPastCondition(phi.get(3));
					buf.append("</repsince></not>");
					
					buf.append("</or></and>");

					break;
					
			   case REPUNTIL:
				    // first 'and'
				    buf.append("<and>");
				    
				    // second 'and'
				    buf.append("<and>");
				    buf.append("<since><start/><not>");
				    getPastCondition(phi.get(2));
				    buf.append("</not></since>");
				    
//				    buf.append("<not><repsince>");
//					parentnodename=operator.toString();
//					getPastCondition(phi.get(0));
//					buf.append("<start/>");
//					getPastCondition(phi.get(1));
//					buf.append("</repsince></not>");
//				    buf.append("</and>");
				    
				    buf.append("<not><repsince>");
							parentnodename=operator.toString();
							getPastCondition(phi.get(0));
							buf.append("<start/>");
							getPastCondition(phi.get(1));
							buf.append("</repsince></not>");
						    buf.append("</and>");
				    							    
				    // end of 'second and'
				    
				    getPastCondition(phi.get(1));
					buf.append("</and>");

					break;
			
			default:
				buf.append(str);
				System.out.println("wrong operator or translation not defined: "+operator.toString());
				break;
			}
			   
		} catch (ParserConfigurationException e) {
			
			e.printStackTrace();
		} catch (IOException e) {
			
			e.printStackTrace();
		} catch (SAXException e) {
			
			e.printStackTrace();
		} catch (TransformerConfigurationException e) {
			
			e.printStackTrace();
		} catch (TransformerException e) {
			
			e.printStackTrace();
		}
			 }
			 
	     }// end of rules for past translation    
		


	
}