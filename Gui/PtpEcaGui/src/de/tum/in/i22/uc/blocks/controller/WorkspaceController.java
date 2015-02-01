package de.tum.in.i22.uc.blocks.controller;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.FileChannel;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import de.tum.in.i22.uc.blocks.codeblocks.Block;
import de.tum.in.i22.uc.blocks.codeblocks.BlockConnectorShape;
import de.tum.in.i22.uc.blocks.codeblocks.BlockGenus;
import de.tum.in.i22.uc.blocks.codeblocks.BlockLinkChecker;
import de.tum.in.i22.uc.blocks.codeblocks.CommandRule;
import de.tum.in.i22.uc.blocks.codeblocks.Constants;
import de.tum.in.i22.uc.blocks.codeblocks.SocketRule;
import de.tum.in.i22.uc.blocks.controller.Config;
import de.tum.in.i22.uc.blocks.workspace.SearchBar;
import de.tum.in.i22.uc.blocks.workspace.SearchableContainer;
import de.tum.in.i22.uc.blocks.workspace.SmartJMenuItem;
import de.tum.in.i22.uc.blocks.workspace.TrashCan;
import de.tum.in.i22.uc.blocks.workspace.Workspace;
import de.tum.in.i22.uc.blocks.workspace.ZoomSlider;
import de.tum.in.i22.uc.blocks.workspace.blockevents.CommandType;
import de.tum.in.i22.uc.blocks.workspace.blockevents.FileMenuAction;
import de.tum.in.i22.uc.blocks.workspace.blockevents.Invoker;
import de.tum.in.i22.uc.blocks.workspace.blockevents.SubController;
import de.tum.in.i22.uc.blocks.workspace.blockevents.WorkspaceInvokerClient;
import de.tum.in.i22.uc.policy.classes.Resource;
import de.tum.in.i22.uc.policy.classes.ResourceCreator;
//import de.tum.in.i22.uc.policy.deployment.JDDeploy;
//import de.tum.in.i22.uc.policy.translation.View;
import de.tum.in.i22.uc.utilities.XMLcombinedmodel;


//my additions to create file and edit menus, on 14.feb.2013
import java.util.Hashtable;

/**
 * Example entry point to OpenBlock application creation.
 *
 * @author Ricarose Roque
 */
public class WorkspaceController{

    private Element langDefRoot;
    private boolean isWorkspacePanelInitialized = false;
    protected JPanel workspacePanel;
    protected final Workspace workspace;
    protected SearchBar searchBar;
    
    private static String LANG_DEF_FILEPATH;
    
    public Workspace getWorkspace() {
        return this.workspace;
    }
    
    /*
	 * 26 May 2013. This is important to ensure that RMI
	 * can work. Without this, it gives some java.net.SocketPermission
	 * exception and our RMI Server fails to start.
	 */
    static{
    	System.setProperty("java.security.policy", "support/rmiAccessPolicy.policy");
    }

    //flag to indicate if a new lang definition file has been set
    private boolean langDefDirty = true;

    //flag to indicate if a workspace has been loaded/initialized
    private boolean workspaceLoaded = false;
    // last directory that was selected with open or save action
    private File lastDirectory;
    // file currently loaded in workspace
    private File selectedFile;
    // Reference kept to be able to update frame title with current loaded file
    private JFrame frame;
    /*24 February 2013
     * Subcontroller for handling menu related commands
     */
    SubController subController;
    /* 24 Feb 2013
     * This is a central part of the command pattern am using for handling events.
     * Since this pattern structure has 2 client: SubController and WorkspaceInvokerClient,
     * it has to be shared and passed to the 2.
     */
    static private Invoker invoker;
    /**
     * 
     */
    static Resource entity=null;
    
    /**
     * Constructs a WorkspaceController instance that manages the
     * interaction with the codeblocks.Workspace
     *
     */
    public WorkspaceController() {
        this.workspace = new Workspace();        
        
        //invoker
        invoker=new Invoker();
        //register my listener
        workspace.addWorkspaceListener(new WorkspaceInvokerClient(this,invoker));
        //Subcontroller for click events on menu items
    	subController=new SubController(this,invoker);    	
    }
    
    public boolean getWorkspaceLoadStatus(){
    	return workspaceLoaded;
    }
    

    /**
     * Sets the file path for the language definition file, if the
     * language definition file is located in
     */
    public void setLangDefFilePath(final String filePath) {
        InputStream in = null;
        try {        	
        	//String trueFilePath=System.getProperty("user.dir")+"/"+filePath;
            in = new FileInputStream(filePath);
            setLangDefStream(in);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        finally {
            if (in != null) {
                try {
                    in.close();
                }
                catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    /**
     * Sets language definition file from the given input stream
     * @param in input stream to read
     */
    public void setLangDefStream(InputStream in) {
        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();                        
        final DocumentBuilder builder;
        final Document doc;
        try {        	        	
            builder = factory.newDocumentBuilder();            
            doc = builder.parse(in);
            langDefRoot = doc.getDocumentElement();
            langDefDirty = true;
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        } catch (SAXException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Loads all the block genuses, properties, and link rules of
     * a language specified in the pre-defined language def file.
     * @param root Loads the language specified in the Element root
     */
    public void loadBlockLanguage(final Element root) {
        /* MUST load shapes before genuses in order to initialize
         connectors within each block correctly */
        BlockConnectorShape.loadBlockConnectorShapes(root);

        //load genuses
        BlockGenus.loadBlockGenera(workspace, root);

        //load rules
        BlockLinkChecker.addRule(workspace, new CommandRule(workspace));
        BlockLinkChecker.addRule(workspace, new SocketRule());

        //set the dirty flag for the language definition file
        //to false now that the lang file has been loaded
        langDefDirty = false;
    }

    /**
     * Resets the current language within the active
     * Workspace.
     *
     */
    public void resetLanguage() {
        BlockConnectorShape.resetConnectorShapeMappings();
        getWorkspace().getEnv().resetAllGenuses();
        BlockLinkChecker.reset();
    }

    /**
     * Returns the save string for the entire workspace.  This includes the block workspace, any
     * custom factories, canvas view state and position, pages
     * @return the save string for the entire workspace.
     */
    public String getSaveString() {
        try {
            Node node = getSaveNode();

            StringWriter writer = new StringWriter();
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(new DOMSource(node), new StreamResult(writer));
            return writer.toString();
        }
        catch (TransformerConfigurationException e) {
            throw new RuntimeException(e);
        }
        catch (TransformerException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns a DOM node for the entire workspace.  This includes the block workspace, any
     * custom factories, canvas view state and position, pages
     * @return the DOM node for the entire workspace.
     */
    public Node getSaveNode() {
        return getSaveNode(true);
    }

    /**
     * Returns a DOM node for the entire workspace. This includes the block
     * workspace, any custom factories, canvas view state and position, pages
     *
     * @param validate If {@code true}, perform a validation of the output
     * against the code blocks schema
     * @return the DOM node for the entire workspace.
     */
    public Node getSaveNode(final boolean validate) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);

            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.newDocument();

            Element documentElement = document.createElementNS(Constants.XML_CODEBLOCKS_NS, "cb:CODEBLOCKS");
            // schema reference
            documentElement.setAttributeNS(XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI, "xsi:schemaLocation", Constants.XML_CODEBLOCKS_NS+" "+Constants.XML_CODEBLOCKS_SCHEMA_URI);

            Node workspaceNode = workspace.getSaveNode(document);
            if (workspaceNode != null) {
                documentElement.appendChild(workspaceNode);
            }

            document.appendChild(documentElement);
            if (validate) {
                validate(document);
            }

            return document;
        }
        catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Validates the code blocks document against the schema
     * @param document The document to check
     * @throws RuntimeException If the validation failed
     */
    private void validate(Document document) {
        try {
            SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            URL schemaUrl = ClassLoader.getSystemResource("edu/mit/blocks/codeblocks/codeblocks.xsd");
            Schema schema = schemaFactory.newSchema(schemaUrl);
            Validator validator = schema.newValidator();
            validator.validate(new DOMSource(document));
        }
        catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        catch (SAXException e) {
            throw new RuntimeException(e);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Loads a fresh workspace based on the default specifications in the language
     * definition file.  The block canvas will have no live blocks.
     */
    public void loadFreshWorkspace() {
        if (workspaceLoaded) {
            resetWorkspace();
        }
        if (langDefDirty) {
            loadBlockLanguage(langDefRoot);
        }
        workspace.loadWorkspaceFrom(null, langDefRoot);
        workspaceLoaded = true;
        //my
        workspace.setIsLoaded(true);
    }

    /**
     * Loads the programming project from the specified file path.
     * This method assumes that a Language Definition File has already
     * been specified for this programming project.
     * @param path String file path of the programming project to load
     */
    public void loadProjectFromPath(final String path) {
        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        final DocumentBuilder builder;
        final Document doc;
        try {
            builder = factory.newDocumentBuilder();
            doc = builder.parse(new File(path));

            // XXX here, we could be strict and only allow valid documents...
            // validate(doc);
            final Element projectRoot = doc.getDocumentElement();
            //load the canvas (or pages and page blocks if any) blocks from the save file
            //also load drawers, or any custom drawers from file.  if no custom drawers
            //are present in root, then the default set of drawers is loaded from
            //langDefRoot
            workspace.loadWorkspaceFrom(projectRoot, langDefRoot);
            workspaceLoaded = true;
            //02 Feb 2013. My addition
            workspace.setIsLoaded(true);
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        } catch (SAXException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Loads the programming project from the specified element. This method
     * assumes that a Language Definition File has already been specified for
     * this programming project.
     *
     * @param element element of the programming project to load
     */
    public void loadProjectFromElement(Element elementToLoad) {
        workspace.loadWorkspaceFrom(elementToLoad, langDefRoot);
        workspaceLoaded = true;
        //02 Feb 2013. My addition
        workspace.setIsLoaded(true);
    }

    /**
     * Loads the programming project specified in the projectContents String,
     * which is associated with the language definition file contained in the
     * specified langDefContents.  All the blocks contained in projectContents
     * must have an associted block genus defined in langDefContents.
     *
     * If the langDefContents have any workspace settings such as pages or
     * drawers and projectContents has workspace settings as well, the
     * workspace settings within the projectContents will override the
     * workspace settings in langDefContents.
     *
     * NOTE: The language definition contained in langDefContents does
     * not replace the default language definition file set by: setLangDefFilePath() or
     * setLangDefFile().
     *
     * @param projectContents
     * @param langDefContents String XML that defines the language of
     * projectContents
     */
    public void loadProject(String projectContents, String langDefContents) {
        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        final DocumentBuilder builder;
        final Document projectDoc;
        final Document langDoc;
        try {
            builder = factory.newDocumentBuilder();
            projectDoc = builder.parse(new InputSource(new StringReader(projectContents)));
            final Element projectRoot = projectDoc.getDocumentElement();
            langDoc = builder.parse(new InputSource(new StringReader(projectContents)));
            final Element langRoot = langDoc.getDocumentElement();
            if (workspaceLoaded) {
                resetWorkspace();
            }
            if (langDefContents == null) {
                loadBlockLanguage(langDefRoot);
            } else {
                loadBlockLanguage(langRoot);
            }
            workspace.loadWorkspaceFrom(projectRoot, langRoot);
            workspaceLoaded = true;
            //02 Feb 2013. My addition
            workspace.setIsLoaded(true);
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        } catch (SAXException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Resets the entire workspace.  This includes all blocks, pages, drawers, and trashed blocks.
     * Also resets the undo/redo stack.  The language (i.e. genuses and shapes) is not reset.
     */
    public void resetWorkspace() {
        //clear all pages and their drawers
        //clear all drawers and their content
        //clear all block and renderable block instances
        workspace.reset();
    }

    /**
     * This method creates and lays out the entire workspace panel with its
     * different components.  Workspace and language data not loaded in
     * this function.
     * Should be call only once at application startup.
     */
    private void initWorkspacePanel() {
    	//adding the trash can
    	ImageIcon tc = new ImageIcon("support/images/trash.png");
        ImageIcon openedtc = new ImageIcon("support/images/trash_open.png");
        TrashCan trash = new TrashCan(workspace,tc.getImage(), openedtc.getImage());
        workspace.addWidget(trash, true, true);
        workspace.setTrashCanWidget(trash);
        
        workspacePanel = new JPanel();
        workspacePanel.setLayout(new BorderLayout());
        workspacePanel.add(workspace, BorderLayout.CENTER);
        isWorkspacePanelInitialized = true;
    }

    /**
     * Returns the JComponent of the entire workspace.
     * @return the JComponent of the entire workspace.
     */
    public JComponent getWorkspacePanel() {
        if (!isWorkspacePanelInitialized) {
            initWorkspacePanel();
        }
        return workspacePanel;
    }

    /**
     * Action bound to "Open" action.
     */
    private class OpenAction extends AbstractAction {

        private static final long serialVersionUID = -2119679269613495704L;

        OpenAction() {
            super("Open");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            JFileChooser fileChooser = new JFileChooser(lastDirectory);
            if (fileChooser.showOpenDialog((Component)e.getSource()) == JFileChooser.APPROVE_OPTION) {
                setSelectedFile(fileChooser.getSelectedFile());
                lastDirectory = selectedFile.getParentFile();
                String selectedPath = selectedFile.getPath();
                loadFreshWorkspace();
                loadProjectFromPath(selectedPath);
            }
        }
    }

    /**
     * Action bound to "Save" button.
     */
    private class SaveAction extends AbstractAction {
        private static final long serialVersionUID = -5540588250535739852L;
        SaveAction() {
            super("Save");
        }

        @Override
        public void actionPerformed(ActionEvent evt) {
            if (selectedFile == null) {
                JFileChooser fileChooser = new JFileChooser(lastDirectory);
                if (fileChooser.showSaveDialog((Component) evt.getSource()) == JFileChooser.APPROVE_OPTION) {
                    setSelectedFile(fileChooser.getSelectedFile());
                    lastDirectory = selectedFile.getParentFile();
                }
            }
            try {
                saveToFile(selectedFile);
            }
            catch (IOException e) {
                JOptionPane.showMessageDialog((Component) evt.getSource(),
                        e.getMessage());
            }
        }
    }

    /**
     * Action bound to "Save As..." button.
     */
    private class SaveAsAction extends AbstractAction {
         private static final long serialVersionUID = 3981294764824307472L;
        private final SaveAction saveAction;

        SaveAsAction(SaveAction saveAction) {
            super("Save As...");
            this.saveAction = saveAction;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            selectedFile = null;
            // delegate to save action
            saveAction.actionPerformed(e);
        }
    }

    /**
     * Saves the content of the workspace to the given file
     * @param file Destination file
     * @throws IOException If save failed
     */
    private void saveToFile(File file) throws IOException {
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(file);
            fileWriter.write(getSaveString());
        }
        finally {
            if (fileWriter != null) {
                fileWriter.close();
            }
        }
    }

    public void setSelectedFile(File selectedFile) {
        this.selectedFile = selectedFile;
        frame.setTitle("Openblocks - "+selectedFile.getPath());
    }

    /**
     * Return the lower button panel.
     */
    private JComponent getButtonPanel() {
        JPanel buttonPanel = new JPanel();
        // Open
        OpenAction openAction = new OpenAction();
        buttonPanel.add(new JButton(openAction));
        // Save
        SaveAction saveAction = new SaveAction();
        buttonPanel.add(new JButton(saveAction));
        // Save as
        SaveAsAction saveAsAction = new SaveAsAction(saveAction);
        buttonPanel.add(new JButton(saveAsAction));
        return buttonPanel;
    }

    /**
     * Returns a SearchBar instance capable of searching for blocks
     * within the BlockCanvas and block drawers
     */
    public JComponent getSearchBar() {
        final SearchBar sb = new SearchBar(
                "Search blocks", "Search for blocks in the drawers and workspace", workspace);
        for (SearchableContainer con : getAllSearchableContainers()) {
            sb.addSearchableContainer(con);
        }
        return sb.getComponent();
    }

    /**
     * Returns an unmodifiable Iterable of SearchableContainers
     * @return an unmodifiable Iterable of SearchableContainers
     */
    public Iterable<SearchableContainer> getAllSearchableContainers() {
        return workspace.getAllSearchableContainers();
    }    
    
    /*
     * Create menus, menu items and return menu bar so frame can add it.
     * */
    private JMenuBar getMenuBar(){
    	//our menu bar
    	JMenuBar mbMain=new JMenuBar();    	
    	
    	//create file menu and associated menu items, and link them
    	JMenu mFile=new JMenu("File");
    	mFile.setMnemonic(KeyEvent.VK_F);
    	
    	SmartJMenuItem smiNew=new SmartJMenuItem("New",CommandType.MENU_ORDINARY_COMMAND);
    	smiNew.setMnemonic(KeyEvent.VK_N);
    	smiNew.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));    	
    	SmartJMenuItem smiOpen=new SmartJMenuItem("Open...",CommandType.MENU_ORDINARY_COMMAND);
    	smiOpen.setMnemonic(KeyEvent.VK_O);
    	smiOpen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
    	SmartJMenuItem smiSave=new SmartJMenuItem("Save As...",CommandType.MENU_ORDINARY_COMMAND);
    	smiSave.setMnemonic(KeyEvent.VK_V);    	
    	SmartJMenuItem smiUpdate=new SmartJMenuItem("Save",CommandType.MENU_ORDINARY_COMMAND);
    	smiUpdate.setMnemonic(KeyEvent.VK_S);
    	smiUpdate.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
    	SmartJMenuItem smiExit=new SmartJMenuItem("Exit",CommandType.MENU_ORDINARY_COMMAND);
    	smiExit.setMnemonic(KeyEvent.VK_X);
    	smiExit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, ActionEvent.ALT_MASK));

    	//actions
    	smiNew.addActionListener(subController);
    	smiOpen.addActionListener(subController);
    	smiSave.addActionListener(subController);
    	smiUpdate.addActionListener(subController);    	
    	smiExit.addActionListener(subController);    	
    	
    	mFile.add(smiNew);
    	mFile.add(smiOpen);
    	mFile.add(smiSave);
    	mFile.add(smiUpdate);    	
    	mFile.add(smiExit);
    	
    	//create edit menu and associated menu items, and link them
    	JMenu mEdit=new JMenu("Edit");
    	mEdit.setMnemonic(KeyEvent.VK_E);
    	    	   
    	SmartJMenuItem smiUndo=new SmartJMenuItem("Undo",CommandType.UNDO_COMMAND);
    	smiUndo.setMnemonic(KeyEvent.VK_U);
    	smiUndo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, ActionEvent.CTRL_MASK));
    	SmartJMenuItem smiRedo=new SmartJMenuItem("Redo",CommandType.REDO_COMMAND);
    	smiRedo.setMnemonic(KeyEvent.VK_R);
    	smiRedo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, ActionEvent.CTRL_MASK));
    	
    	smiUndo.addActionListener(subController);
    	smiRedo.addActionListener(subController);    	
    	
    	mEdit.add(smiUndo);
    	mEdit.add(smiRedo);    	
    	
    	//Create Policy Menu
    	JMenu mPolicy=new JMenu("Policy");
    	mPolicy.setMnemonic(KeyEvent.VK_L);
    	
    	SmartJMenuItem smiTranslate=new SmartJMenuItem("Translate...", CommandType.MENU_ORDINARY_COMMAND);
    	smiTranslate.setMnemonic(KeyEvent.VK_T);
    	smiTranslate.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T, ActionEvent.CTRL_MASK));
    	smiTranslate.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
								
				JDTranslationView.launch(frame);
			}
    		
    	});
    	
       	SmartJMenuItem smiUpdateDeploy=new SmartJMenuItem("Update & Deploy", CommandType.MENU_ORDINARY_COMMAND);
    	smiUpdateDeploy.setMnemonic(KeyEvent.VK_D);
    	smiUpdateDeploy.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, ActionEvent.CTRL_MASK));
    	smiUpdateDeploy.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				
				//JDDeploy.launch(frame);
			}
    		
    	});
    	
    	mPolicy.add(smiTranslate);
    	mPolicy.add(smiUpdateDeploy);
    	
    	//add menus to our menu bar    	
    	mbMain.add(mFile);
    	mbMain.add(mEdit);
    	mbMain.add(mPolicy);
    	
    	return mbMain;
    }
    
    public JFrame getApplicationFrame(){
    	return frame;
    }
       

    /**
     * Create the GUI and show it.  For thread safety, this method should be
     * invoked from the event-dispatching thread.
     */
    private void createAndShowGUI() {
        frame = new JFrame("Usage Control Policy");
        //Setting window-level events        
        frame.addWindowListener(new WindowAdapter(){
        	@Override
        	public void windowClosing(WindowEvent arg0) {
        		
        		FileMenuAction menuAction=new FileMenuAction(WorkspaceController.this,invoker);
        		menuAction.exitProgram();
        	}
        });
        
        //frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.setBounds(100, 100, 500, 500);
        final SearchBar sb = new SearchBar("Search blocks",
                "Search for blocks in the drawers and workspace", workspace);
        for (final SearchableContainer con : getAllSearchableContainers()) {
            sb.addSearchableContainer(con);
        }
        
        //zoom slider
        ZoomSlider slider=new ZoomSlider(workspace);
        slider.reset();
        
        final JPanel topPane = new JPanel();        
        //set the layout for the top pane to accommodate menu bar and search bar        
        topPane.setLayout(new GridBagLayout());
        //top pane should stretch out        
        topPane.setSize(new Dimension((int)(frame.getWidth()), (int)(frame.getHeight()*.9)));
           
        //preparing space between search bar and zoom tool
        JLabel searchLabel = new JLabel("");
        Dimension d=new Dimension();
        d.width=(int)(topPane.getSize().width*0.95);
        d.height=slider.getPreferredSize().height;        
        searchLabel.setPreferredSize(d);
        
        //adjust size of search bar
        sb.getComponent().setPreferredSize(new Dimension((int)(.3*(topPane.getSize().width)), (int)(.05*(topPane.getSize().height))));               
        
        //adding search bar
        GridBagConstraints gbc=new GridBagConstraints();
        gbc.fill=GridBagConstraints.HORIZONTAL;
        gbc.gridx=0;
        gbc.gridy=0;        		
        topPane.add(sb.getComponent(),gbc);   
        
        //adding space
        gbc.gridx=1;
        gbc.weightx=0.1;	//important to ensure component occupies all space
        topPane.add(searchLabel,gbc);
        
        //add our zoom slider 
        gbc.gridx=2;
        gbc.weightx=0.1;	//important to push the slider to right
        topPane.add(slider,gbc);                
                
        //other additions. Not my creation
        frame.setJMenuBar(getMenuBar());
        frame.add(topPane, BorderLayout.PAGE_START);
        frame.add(getWorkspacePanel(), BorderLayout.CENTER);
        frame.setExtendedState(Frame.MAXIMIZED_BOTH);
        frame.setVisible(true);
        
    }
    
    public static void copyFile(File sourceFile, File destFile) throws IOException {
        if(!destFile.exists()) {
            destFile.createNewFile();
        }

        FileChannel source = null;
        FileChannel destination = null;

        try {
            source = new FileInputStream(sourceFile).getChannel();
            destination = new FileOutputStream(destFile).getChannel();
            destination.transferFrom(source, 0, source.size());
        }
        finally {
            if(source != null) {
                source.close();
            }
            if(destination != null) {
                destination.close();
            }
        }
    }

    public static void main(final String[] args) {    	    	           	
    	
    	//set look and feel
    	try {
    		if(args.length>=2){
        		entity=ResourceCreator.create(args);
        		if(entity!=null){
	        		System.out.println("Argument 1: "+args[0]);
	        		System.out.println("Argument 2: "+args[1]);
	        		System.out.println("Argument 3: "+args[2]);
	        		System.out.println("Data Class: "+entity.getDataClass());
	        		System.out.println("Data source: "+entity.getDataSource());
	        		System.out.println("Data Description: "+entity.getDescription());
        		}
        	}
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (UnsupportedLookAndFeelException e) {
			
			e.printStackTrace();
		} 
        
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override public void run() {
            	
            	 //TODO grab file path from args array

            	// copy lang_model file to lang_def file
            	Config cfg;
				try {
					cfg = new Config();
				         	
            	File sourceFile = new File(cfg.getProperty("langModelFile"));
            	File destFile = new File(cfg.getProperty("langDefFile"));            	
            	copyFile(sourceFile, destFile);         	

            	// add data and actions to the lang_def file
                
            	try {					
						new XMLcombinedmodel();
					} catch (TransformerException e) {
						
						e.printStackTrace();
					} catch (ParserConfigurationException e) {
					
					e.printStackTrace();
				}
            	
            	// set lang_def.xml file as the langugae definiton file
                LANG_DEF_FILEPATH = cfg.getProperty("langDefFile");
                
				} catch (IOException e1) {
					
					e1.printStackTrace();
				}
            	
            	
                final WorkspaceController wc = new WorkspaceController();
                System.out.println("Path of the language definition file: "+LANG_DEF_FILEPATH);
                wc.setLangDefFilePath(LANG_DEF_FILEPATH);                
                wc.loadFreshWorkspace();
                wc.createAndShowGUI();                                
                wc.workspace.setEntityInstance(entity);
            }
        });
    }	

}
