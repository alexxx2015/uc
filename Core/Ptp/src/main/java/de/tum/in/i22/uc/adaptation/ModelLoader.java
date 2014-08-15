package de.tum.in.i22.uc.adaptation;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import de.tum.in.i22.uc.adaptation.model.DomainModel;
import de.tum.in.i22.uc.adaptation.model.LayerModel;
import de.tum.in.i22.uc.policy.translation.Config;
import de.tum.in.i22.uc.utilities.PtpLogger;

public class ModelLoader {

	private static Config config;
	
	private DomainModel domainModel;
	
	private PtpLogger logger;
	
	public ModelLoader(){
		try {
			config = new Config();
			logger = PtpLogger.adaptationLoggerInstance();
		} catch (IOException e) {
		}	
	}
	
	public DomainModel loadBaseDomainModel(){
		String domainModelFile=config.getProperty("domainmodel");
		loadDomainModel(domainModelFile, "file");		
		return this.domainModel;
	}
	
	public DomainModel loadDomainModel(String file){
		String domainModelFile=file;
		loadDomainModel(domainModelFile, "file");
		return this.domainModel;
	}
	

	public DomainModel createDomainModel(String xmlModel){
		String domainModelFile=xmlModel;
		loadDomainModel(domainModelFile, "string");
		return this.domainModel;
	}
	
	
	private DomainModel loadDomainModel(String source, String sourceType){
		this.domainModel = new DomainModel(source);
		DomainModel result = this.domainModel;  
		
		try{
			String domainModelFile=source;
			Document xmlDocXpath=openXmlInput(domainModelFile, sourceType);
			
			XPathFactory factory=XPathFactory.newInstance();
			XPath xpath=factory.newXPath();
			
			/* process name of the layers */
			String sPimLayerName = "//pims";
			NodeList pimsList=(NodeList)xpath.evaluate(sPimLayerName, xmlDocXpath, XPathConstants.NODESET);
			Node pimRoot = pimsList.item(0);
			if(pimRoot!=null){
				String name = pimRoot.getAttributes().getNamedItem("name").getNodeValue();
				this.domainModel.getPimLayer().setName(name);
			}
			String sPsmLayerName = "//psms";
			NodeList psmsList=(NodeList)xpath.evaluate(sPsmLayerName, xmlDocXpath, XPathConstants.NODESET);
			Node psmRoot = psmsList.item(0);
			if(psmRoot!=null){
				String name = psmRoot.getAttributes().getNamedItem("name").getNodeValue();
				this.domainModel.getPsmLayer().setName(name);
			}
			String sIsmLayerName = "//isms";
			NodeList ismsList=(NodeList)xpath.evaluate(sIsmLayerName, xmlDocXpath, XPathConstants.NODESET);
			Node ismRoot = ismsList.item(0);
			if(ismRoot!=null){
				String name = ismRoot.getAttributes().getNamedItem("name").getNodeValue();
				this.domainModel.getIsmLayer().setName(name);
			}
			
			/* process DATA/CONTAINERS of the layers */
			String sPimDataExpression="//pimdata";
			NodeList pimDataList=(NodeList)xpath.evaluate(sPimDataExpression, xmlDocXpath, XPathConstants.NODESET);
			String sPsmContainerExpression="//psmcontainers";
			NodeList psmContainerList=(NodeList)xpath.evaluate(sPsmContainerExpression, xmlDocXpath, XPathConstants.NODESET);
			String sIsmContainerExpression="//ismcontainers";
			NodeList ismContainerList=(NodeList)xpath.evaluate(sIsmContainerExpression, xmlDocXpath, XPathConstants.NODESET);
			addDataContainers(pimDataList, psmContainerList, ismContainerList);
			
			/* process ACTIONS/TRANSFORMERS of the layers */
			String sPimActionExpression="//pimactions";
			NodeList pimActionList=(NodeList)xpath.evaluate(sPimActionExpression, xmlDocXpath, XPathConstants.NODESET);
			String sPsmTransformerExpression="//psmtransformers";
			NodeList psmTransformerList=(NodeList)xpath.evaluate(sPsmTransformerExpression, xmlDocXpath, XPathConstants.NODESET);
			String sIsmTransformerExpression="//ismtransformers";
			NodeList ismTransformerList=(NodeList)xpath.evaluate(sIsmTransformerExpression, xmlDocXpath, XPathConstants.NODESET);
			addActionTransformers(pimActionList, psmTransformerList, ismTransformerList);
			/* process SYSTEMS of the layers */
			String sPsmSystemExpression = "//psmsystems";
			NodeList psmSystemList=(NodeList)xpath.evaluate(sPsmSystemExpression, xmlDocXpath, XPathConstants.NODESET);
			String sIsmSystemExpression = "//ismsystems";
			NodeList ismSystemList=(NodeList)xpath.evaluate(sIsmSystemExpression, xmlDocXpath, XPathConstants.NODESET);
			addSystems(psmSystemList, ismSystemList);
		}
		catch( Exception ex ){
			ex.printStackTrace();
		}
		
		String logMsg = "Loaded DomainModel: \n" + this.domainModel.toString() +"\n";
		logger.infoLog(logMsg, null);
		
		return result;
	}
	

	private void addDataContainers(NodeList pimData, NodeList psmContainers, NodeList ismContainers){
		LayerModel ism = this.domainModel.getIsmLayer();
		LayerLoader ismLoader = new LayerLoader(ism);
		ismLoader.addContainers(ismContainers);
		
		LayerModel psm = this.domainModel.getPsmLayer();
		LayerLoader psmLoader = new LayerLoader(psm);
		psmLoader.addContainers(psmContainers);
		
		LayerModel pim = this.domainModel.getPimLayer();
		LayerLoader pimLoader = new LayerLoader(pim);
		pimLoader.addContainers(pimData);
	}
	
	private void addActionTransformers(NodeList pimActionList, NodeList psmTransformerList, NodeList ismTransformerList) {
		LayerModel ism = this.domainModel.getIsmLayer();
		LayerLoader ismLoader = new LayerLoader(ism);
		ismLoader.addTransformers(ismTransformerList);
		
		LayerModel psm = this.domainModel.getPsmLayer();
		LayerLoader psmLoader = new LayerLoader(psm);
		psmLoader.addTransformers(psmTransformerList);
		
		LayerModel pim = this.domainModel.getPimLayer();
		LayerLoader pimLoader = new LayerLoader(pim);
		pimLoader.addTransformers(pimActionList);
	}
	
	private void addSystems(NodeList psmSystemList, NodeList ismSystemList) {
		LayerModel ism = this.domainModel.getIsmLayer();
		LayerLoader ismLoader = new LayerLoader(ism);
		ismLoader.addSystems(ismSystemList);
		
		LayerModel psm = this.domainModel.getPsmLayer();
		LayerLoader psmLoader = new LayerLoader(psm);
		psmLoader.addSystems(psmSystemList);
	}

	
	/**
	 * Provide help with reading from xml strings and file paths 
	 * 
	 * @param input
	 * @param type
	 * @return Returns an xml document
	 */
	private static Document openXmlInput(String input, String type){
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			Document doc=null;
			
			try {
				DocumentBuilder builder = factory.newDocumentBuilder();
				InputSource inputSource;
				if(type.equalsIgnoreCase("string"))
				 {
					inputSource = new InputSource(new StringReader(input));
					doc = builder.parse(inputSource);
				 }
				else if(type.equalsIgnoreCase("file"))
					doc=builder.parse(new File(input));		
							
			} catch (ParserConfigurationException e) {
				e.printStackTrace();
			} catch (SAXException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			Node root1 = doc.getFirstChild();
			removeWhitespaceNodes(root1);
			Node root2 = doc.getParentNode();
			removeWhitespaceNodes(root2);
			Node root3 = doc.getNextSibling();
			removeWhitespaceNodes(root3);
			
			return doc;
	}
	
	/**
	   * This method just removes the whitespaces from an xml document/node
	   * 
	   * @param node
	   */
	private static void removeWhitespaceNodes(Node node) {
		if (node != null) {
			NodeList children = node.getChildNodes();
			for (int i = children.getLength() - 1; i >= 0; i--) {
				Node child = children.item(i);
				if (child instanceof Text
						&& ((Text) child).getData().trim().length() == 0) {
					node.removeChild(child);
				} else if (child instanceof Element) {
					removeWhitespaceNodes((Element) child);
				}
			}
		}
	}
}
