package de.tum.in.i22.uc.adaptation.engine;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import de.tum.in.i22.uc.adaptation.model.DomainModel;
import de.tum.in.i22.uc.adaptation.model.LayerModel;
import de.tum.in.i22.uc.policy.translation.Config;
import de.tum.in.i22.uc.utilities.PublicMethods;

public class ModelLoader {

	private static Config config;
	
	private DomainModel domainModel;
	
	private static final Logger logger = LoggerFactory.getLogger(ModelLoader.class);
	
	private static int backupCounter = 0;
	
	public ModelLoader(){
		try {
			config = new Config();
		} catch (IOException e) {
		}	
	}
	
	public DomainModel loadBaseDomainModel() throws InvalidDomainModelFormatException{
		String domainModelFile=config.getProperty("domainmodel");
		String userDir = config.getUserDir();
		domainModelFile = userDir + File.separator + domainModelFile;
		loadDomainModel(domainModelFile, "file");		
		return this.domainModel;
	}
	
	public DomainModel loadDomainModel(String file) throws InvalidDomainModelFormatException{
		String domainModelFile=file;
		loadDomainModel(domainModelFile, "file");
		return this.domainModel;
	}
	

	public DomainModel createDomainModel(String xmlModel) throws InvalidDomainModelFormatException{
		String domainModelFile=xmlModel;
		loadDomainModel(domainModelFile, "string");
		return this.domainModel;
	}
	
	/**
	 * Perform a backup of the existing base domain model.
	 * The backup is stored in the location defined in config.cfg
	 * @param fileDomainModelSource
	 */
	public void backupBaseDomainModel(String fileDomainModelSource){
		String domainmodelbackupDestination = config.getProperty("domainmodelbackup");
		String userDir = config.getUserDir();
		domainmodelbackupDestination = userDir + File.separator + domainmodelbackupDestination;
		String data = "";
		String timestamp = PublicMethods.timestamp()+"_"+backupCounter++;
		domainmodelbackupDestination += File.separator + timestamp +"_sns.xml";
		try {
			data = PublicMethods.readFile(fileDomainModelSource);
			PublicMethods.writeFile(domainmodelbackupDestination, data);
		} catch (IOException e) {
			logger.error("BaseDomainModel backup failed", e);
		}
		logger.info("BaseDomainModel backup successful: "+ domainmodelbackupDestination);
	}
	
	/**
	 * Perform a backup of the existing base domain model.
	 * The backup is stored in the location defined in config.cfg
	 */
	public void backupBaseDomainModel(){
		String domainModelFile=config.getProperty("domainmodel");
		String userDir = config.getUserDir();
		domainModelFile = userDir + File.separator + domainModelFile;
		backupBaseDomainModel(domainModelFile);
	}
	
	private DomainModel loadDomainModel(String source, String sourceType) throws InvalidDomainModelFormatException{
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
			logger.error("Trying to load domain model", ex);
			throw new InvalidDomainModelFormatException(ex.getMessage());
		}
		
		String logMsg = "Loaded DomainModel: \n" + this.domainModel.toString() +"\n";
		logger.info(logMsg);
		
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
	
	private void addActionTransformers(NodeList pimActionList, NodeList psmTransformerList, NodeList ismTransformerList) throws InvalidDomainModelFormatException {
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

	public String storeXmlBaseDomainModel(DomainModel baseDm) throws InvalidDomainModelFormatException{
		String domainModelFile=config.getProperty("domainmodel");
		String userDir = config.getUserDir();
		domainModelFile = userDir + File.separator + domainModelFile;
		String mergedDomain = this.storeXmlDomainModel(domainModelFile, baseDm);
		return mergedDomain;
	}
	
	/**
	 * The Adaptation Engine assumes there is a template of the Domain Model already stored.
	 * A template means a file with no data, containers, actions, transformers, systems.
	 * The nodes for PIM, PSM and ISM must be defined.
	 * If the destination already contains some nodes, these will be updated.
	 * @param destination
	 * @param domain
	 * @return 
	 */
	public String storeXmlDomainModel(String destination, DomainModel domain) throws InvalidDomainModelFormatException{
		try{
			String domainModelFile=destination;
			Document xmlDocXpath= null; 
			xmlDocXpath = openXmlInput(domainModelFile, "file");
			
			XPathFactory factory=XPathFactory.newInstance();
			XPath xpath=factory.newXPath();
			
			/* process name of the layers */
			String sPimLayerName = "//pims";
			NodeList pimsList=(NodeList)xpath.evaluate(sPimLayerName, xmlDocXpath, XPathConstants.NODESET);
			Node pimRoot = pimsList.item(0);
			
			String sPsmLayerName = "//psms";
			NodeList psmsList=(NodeList)xpath.evaluate(sPsmLayerName, xmlDocXpath, XPathConstants.NODESET);
			Node psmRoot = psmsList.item(0);
			
			String sIsmLayerName = "//isms";
			NodeList ismsList=(NodeList)xpath.evaluate(sIsmLayerName, xmlDocXpath, XPathConstants.NODESET);
			Node ismRoot = ismsList.item(0);
			
			Node layerRoot;
			
			if(pimRoot!=null){
				layerRoot = pimRoot.getParentNode();
				layerRoot.removeChild(pimRoot);
				layerRoot.removeChild(psmRoot);
				layerRoot.removeChild(ismRoot);
			}
			else{
				layerRoot = xmlDocXpath.getFirstChild();
			}
			
			Element pimXML = domain.getPimLayer().getXmlNode(xmlDocXpath);
			Element psmXML = domain.getPsmLayer().getXmlNode(xmlDocXpath);
			Element ismXML = domain.getIsmLayer().getXmlNode(xmlDocXpath);
			
			layerRoot.appendChild(pimXML);
			layerRoot.appendChild(psmXML);
			layerRoot.appendChild(ismXML);
			
			String xmlData = PublicMethods.prettyPrint(xmlDocXpath);
			String logMsg = ">>>Adaptation Complete: DomainModel: \n"
							+ "\n###############################\n" 
							+ xmlData +"\n";
			logger.info(logMsg);
			PublicMethods.writeFile(destination, xmlData);
			return xmlData;
		}
		catch (Exception ex){
			logger.error("Trying to load domain model", ex);
			throw new InvalidDomainModelFormatException(ex.getMessage());
		}
	}
	
	/**
	 * Provide help with reading from xml strings and file paths 
	 * 
	 * @param input
	 * @param type
	 * @return Returns an xml document
	 * @throws ParserConfigurationException 
	 * @throws IOException 
	 * @throws SAXException 
	 */
	private static Document openXmlInput(String input, String type) throws ParserConfigurationException, SAXException, IOException{
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			Document doc=null;
			
			DocumentBuilder builder = factory.newDocumentBuilder();
			InputSource inputSource;
			if(type.equalsIgnoreCase("string"))
			 {
				inputSource = new InputSource(new StringReader(input));
				doc = builder.parse(inputSource);
			 }
			else if(type.equalsIgnoreCase("file"))
				doc=builder.parse(new File(input));		
							
			Node root1 = doc.getFirstChild();
			PublicMethods.removeWhitespaceNodes(root1);
			Node root2 = doc.getParentNode();
			PublicMethods.removeWhitespaceNodes(root2);
			Node root3 = doc.getNextSibling();
			PublicMethods.removeWhitespaceNodes(root3);
			
			return doc;
	}
	
	
}
