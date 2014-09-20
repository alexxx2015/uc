package de.tum.in.i22.uc.ptp.policy.translation.ecacreation;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Frame;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextArea;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import javax.swing.border.LineBorder;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JScrollPane;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Node;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;
import org.xml.sax.InputSource;

import java.awt.Insets;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.io.StringWriter;

public class XmlXpathEditor extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private Object parent;
	private JButton btnSave;
	private JButton btnCancel;
	private String sReason;
	private JScrollPane jspEditor;
	private JTextArea taEditor;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			XmlXpathEditor dialog = new XmlXpathEditor(null);
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void launch(String reason, Object parent){
		try{
			ECARulesCreatorView vParent=(ECARulesCreatorView)parent;			
			XmlXpathEditor editor=new XmlXpathEditor(vParent.getParent());
			editor.setView(parent);
			editor.sReason=reason;
			editor.populate();
			editor.setVisible(true);
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private void setView(Object view){
		parent=view;
	}
	
	private void populate(){
		ECARulesCreatorView par=(ECARulesCreatorView) parent;
		if(sReason.equalsIgnoreCase("subformula"))
//			taEditor.setText(par.getSubformula());
		taEditor.setText(indentXml(par.getSubformula()));
		else if(sReason.equalsIgnoreCase("subformula-xpath"))
		taEditor.setText(par.getSubformulaXpath());
	}
	
	/*private String indentXml(String text){
		BufferedReader bf;
		String sResult="";
		try{
			bf=new BufferedReader(new StringReader(serialize(text)));
			boolean eof = false;  			   
			while(!eof)  
			  {  
			  String lineIn = bf.readLine();  
			  if(lineIn == null)  
			    {  
			    eof = true;  
			    }  
			  else  
			    {  
				  if(sResult.equals("")) sResult=lineIn;
				  else sResult += System.getProperty("line.separator") + lineIn;
				  //else sResult += "\n" + lineIn;
					 // textarea.append(lineIn + System.getProperty("line.separator");  
			    }  
			  }
			
			bf.close();
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return sResult;
	}*/
	
	private String indentXml(String input){
		String sResult="";
		if(input.equals("")||input.equals(null)){}
		else{
		  try {
			  final InputSource src = new InputSource(new StringReader(input));
	            final Node document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(src).getDocumentElement();
	            final Boolean keepDeclaration = Boolean.valueOf(input.startsWith("<?xml"));

	        //May need this: System.setProperty(DOMImplementationRegistry.PROPERTY,"com.sun.org.apache.xerces.internal.dom.DOMImplementationSourceImpl");


	            final DOMImplementationRegistry registry = DOMImplementationRegistry.newInstance();
	            final DOMImplementationLS impl = (DOMImplementationLS) registry.getDOMImplementation("LS");
	            final LSSerializer writer = impl.createLSSerializer();

	            writer.getDomConfig().setParameter("format-pretty-print", Boolean.TRUE); // Set this to true if the output needs to be beautified.
	            writer.getDomConfig().setParameter("xml-declaration", keepDeclaration); // Set this to true if the declaration is needed to be outputted.

	            return writer.writeToString(document);
		    } catch (Exception e) {
		        e.printStackTrace();
		    }
		}
		  return sResult;
	}

	/**
	 * Create the dialog.
	 */
	public XmlXpathEditor(Container container) {
		super((Frame) container);
		setModal(true);
		setTitle("Edit Content");
		setBounds(100, 100, 528, 329);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		GridBagLayout gbl_contentPanel = new GridBagLayout();
		gbl_contentPanel.columnWidths = new int[]{0, 0, 0, 0, 0};
		gbl_contentPanel.rowHeights = new int[]{0, 0, 0};
		gbl_contentPanel.columnWeights = new double[]{1.0, 0.0, 0.0, 1.0, Double.MIN_VALUE};
		gbl_contentPanel.rowWeights = new double[]{1.0, 1.0, Double.MIN_VALUE};
		contentPanel.setLayout(gbl_contentPanel);
		{
			jspEditor = new JScrollPane();
			GridBagConstraints gbc_jspEditor = new GridBagConstraints();
			gbc_jspEditor.gridheight = 2;
			gbc_jspEditor.gridwidth = 4;
			gbc_jspEditor.insets = new Insets(0, 0, 5, 5);
			gbc_jspEditor.fill = GridBagConstraints.BOTH;
			gbc_jspEditor.gridx = 0;
			gbc_jspEditor.gridy = 0;
			contentPanel.add(jspEditor, gbc_jspEditor);
			{
				taEditor = new JTextArea();
				taEditor.setLineWrap(true);
				jspEditor.setViewportView(taEditor);
			}
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				btnSave = new JButton("SAVE");
				btnSave.setActionCommand("OK");
				buttonPane.add(btnSave);
				getRootPane().setDefaultButton(btnSave);				
			}
			{
				btnCancel = new JButton("Cancel");
				btnCancel.setActionCommand("Cancel");
				buttonPane.add(btnCancel);
			}
			
			btnSave.addActionListener(new ActionListener(){

				@Override
				public void actionPerformed(ActionEvent ae) {
					
					ECARulesCreatorView par=(ECARulesCreatorView) parent;
					if(sReason.equalsIgnoreCase("subformula"))
					par.setSubformula(taEditor.getText());
					else if(sReason.equalsIgnoreCase("subformula-xpath"))
					par.setSubformulaXpath(taEditor.getText());
					
					setVisible(false);
					dispose();
				}
				
			});
			
			btnCancel.addActionListener(new ActionListener(){

				@Override
				public void actionPerformed(ActionEvent ae) {
					
					setVisible(false);
					dispose();
				}
				
			});
		}
	}

}
