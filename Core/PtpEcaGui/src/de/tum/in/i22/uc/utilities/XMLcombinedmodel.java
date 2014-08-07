package de.tum.in.i22.uc.utilities;
/**
 * XMLmodelparser parses the event declaration model, and merges it with 
 * the existing core osl model defined in lang_model.xml 
 * 
 * @author Prachi 
 */
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import de.tum.in.i22.uc.blocks.controller.Config;




public class XMLcombinedmodel {
	
	// initialize the declaration lists

	public List<String> actionName = new ArrayList<String>();
	public List<String> paramName = new ArrayList<String>();
	public List<String> paramValues = new ArrayList<String>();
	

	public XMLcombinedmodel() throws ParserConfigurationException, TransformerException{
		
	try{
		
	DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();   

	 
	Config cfg = new Config();
	//build DOMs		
	org.w3c.dom.Document sourceFile = builder.parse(new File(cfg.getProperty("eventDecl")));	
	org.w3c.dom.Document destFile  = builder.parse(new File(cfg.getProperty("langDefFile")));
	
	    //now get all action names
	    NodeList actionNode = sourceFile.getElementsByTagName("eventName");
	    for(int i = 0 ; i< actionNode.getLength() ; i++){
	        Node n = actionNode.item(i);           
            actionName.add(n.getTextContent());
	    }
	    
	    //now get all param names
	    NodeList pnameNode = sourceFile.getElementsByTagName("param");
	    for(int i = 0 ; i< pnameNode.getLength() ; i++){
	        Node n = pnameNode.item(i);  
	        // check if the entry already exists
	        if(!alreadyExistsInList(paramName, n.getAttributes().getNamedItem("paramName").getNodeValue()))
	        paramName.add(n.getAttributes().getNamedItem("paramName").getNodeValue());
	    }
	    
	    //now get all param values
	    NodeList pvalNode = sourceFile.getElementsByTagName("paramValue");
	    for(int i = 0 ; i< pvalNode.getLength() ; i++){
	        Node n = pvalNode.item(i); 
	        //check if the entry already exists
	        if(!alreadyExistsInList(paramValues, n.getTextContent()))
	        paramValues.add(n.getTextContent());
	    }

//	System.out.println("actioname in combined model: "+actionName);
//	System.out.println(paramName);
//	System.out.println(paramValues);

	// create all nodes for the actions, params and their values
	cloneNode(actionName, destFile, 0);
	cloneNode(paramName, destFile, 1);
	cloneNode(paramValues, destFile, 2);
	}
	catch (IOException e1) {
		
		e1.printStackTrace();
	} catch (SAXException e) {
		
		e.printStackTrace();
	}
			
 }
	/*
	 * returns if a string already 
	 * exists in a list
	 */
	public Boolean alreadyExistsInList(List<String>list, String string){
	Boolean bool = false;	
    for(int i=0; i<list.size(); i++){
    	if(string.equalsIgnoreCase(list.get(i))){
    		bool = true;
    	    break;
    }}
		return bool;
	}
	
	
	/**
	 * find the node with a specific attribute value in a doc
	 */
	public Element getNodeWithAttribute(org.w3c.dom.Document targetDoc, String attrName, String attrValue){
		// get actionName genus node in the lang_def file
//		Node root = targetDoc.getFirstChild();		
		NodeList genusList = targetDoc.getElementsByTagName("BlockGenus");
		Element element = null;
		for (int i = 0; i < genusList.getLength(); i++) {
	        Node n = genusList.item(i);
	        if (n instanceof Element) {
	            Element ele = (Element) n;
	            if (ele.getAttribute(attrName).equals(attrValue)) {
	            element = ele;
	            break;
            }
	    }}
		return element;	
	}
	
	/**
	 * creates new nodes in the combined model
	 * by cloning action, param and values types 
	 * of nodes and their families
	 * 
	 */
	public void cloneNode(List<String> nodelist, org.w3c.dom.Document targetDoc, int option) throws TransformerException{
		Config cfg;
		try {
			cfg = new Config();
			  targetDoc.setXmlStandalone(true);
			  TransformerFactory tFactory = TransformerFactory.newInstance();
			  Transformer tformer = tFactory.newTransformer();
			  Source source = new DOMSource(targetDoc);
			  javax.xml.transform.Result result = new StreamResult(new File(cfg.getProperty("langDefFile")));

             // handle action, param and paramvalue cases
			  switch (option) {
			  
			  // first case: action
			  case 0:			  
				Element actionEle = getNodeWithAttribute(targetDoc, "name",
						"actionName");

				for (int i = 0; i < nodelist.size(); i++) {
					Element cloneEle = (Element) actionEle.cloneNode(true);
					cloneEle.setAttribute("name", nodelist.get(i));
					cloneEle.setAttribute("initlabel", nodelist.get(i));
					Node elePar = actionEle.getParentNode();
					elePar.insertBefore(cloneEle, actionEle.getNextSibling());
				}

				NodeList familyList = targetDoc
						.getElementsByTagName("BlockFamily");
				Node blkFamilies = familyList.item(0).getParentNode();

				Node afamilyEle = targetDoc.createElement("BlockFamily");
				blkFamilies.appendChild(afamilyEle);
				for (int i = 0; i <= nodelist.size(); i++) {
					Node famMem = targetDoc.createElement("FamilyMember");
					if (i == 0)
						famMem.setTextContent("actionName");
					else
						famMem.setTextContent(nodelist.get(i - 1));
					afamilyEle.appendChild(famMem);
				}
			  break;
			  
			  // second case: param name
			  case 1:
				Element paramEle = getNodeWithAttribute(targetDoc, "name", "paramName");  
				for (int i = 0; i < nodelist.size(); i++) {
					Element cloneEle = (Element) paramEle.cloneNode(true);
					cloneEle.setAttribute("name", nodelist.get(i));
					cloneEle.setAttribute("initlabel", nodelist.get(i));
					Node elePar = paramEle.getParentNode();
					elePar.insertBefore(cloneEle, paramEle.getNextSibling());
				}

				NodeList pfamilyList = targetDoc
						.getElementsByTagName("BlockFamily");
				Node blkFamilies1 = pfamilyList.item(0).getParentNode();

				Node pfamilyEle = targetDoc.createElement("BlockFamily");
				blkFamilies1.appendChild(pfamilyEle);
				for (int i = 0; i <= nodelist.size(); i++) {
					Node famMem = targetDoc.createElement("FamilyMember");
					if (i == 0)
						famMem.setTextContent("paramName");
					else
						famMem.setTextContent(nodelist.get(i - 1));
					pfamilyEle.appendChild(famMem);
				}
				
			  break;
				
			// third case: param values	
			  case 2:
				  Element pvalueEle = getNodeWithAttribute(targetDoc, "name", "paramValue");  
				  for (int i = 0; i < nodelist.size(); i++) {
						Element cloneEle = (Element) pvalueEle.cloneNode(true);
						cloneEle.setAttribute("name", nodelist.get(i));
						cloneEle.setAttribute("initlabel", nodelist.get(i));
						Node elePar = pvalueEle.getParentNode();
						elePar.insertBefore(cloneEle, pvalueEle.getNextSibling());
					}

					NodeList pvfamilyList = targetDoc
							.getElementsByTagName("BlockFamily");
					Node blkFamilies2 = pvfamilyList.item(0).getParentNode();

					Node pvfamilyEle = targetDoc.createElement("BlockFamily");
					blkFamilies2.appendChild(pvfamilyEle);
					for (int i = 0; i <= nodelist.size(); i++) {
						Node famMem = targetDoc.createElement("FamilyMember");
						if (i == 0)
							famMem.setTextContent("paramValue");
						else
							famMem.setTextContent(nodelist.get(i - 1));
						pvfamilyEle.appendChild(famMem);
					}
				break;
			  }					  
				  tformer.setOutputProperty(OutputKeys.INDENT,"yes");
				  //The line below is commented because there is no validation
				  //against the pointed schema.
				  //tformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, "lang_def.dtd");			 
				  tformer.transform(source, result);	
			  		
	} catch (IOException e) {
		
		e.printStackTrace();
	}
  }
		
}
