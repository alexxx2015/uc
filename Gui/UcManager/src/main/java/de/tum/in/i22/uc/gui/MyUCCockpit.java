package de.tum.in.i22.uc.gui;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Iterator;
import java.util.LinkedList;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.DefaultTableModel;

import org.apache.log4j.Logger;

import de.tum.in.i22.uc.Controller;
import de.tum.in.i22.uc.cm.distribution.IPLocation;
import de.tum.in.i22.uc.cm.distribution.client.Any2PmpClient;
import de.tum.in.i22.uc.cm.distribution.client.Pep2PipClient;
import de.tum.in.i22.uc.thrift.client.ThriftClientFactory;


public class MyUCCockpit extends Controller{

	// private PDPMain myPDP;
	private boolean ucIsRunning = false;
	private final LinkedList<String> deployedPolicies = new LinkedList<String>();	
	private JFrame myFrame;
	private JTabbedPane jTabPane;
	private JPanel pdpPanel;
	private JPanel pipPanel;
	private JButton startBtn;
	private JButton stopBtn;
	private JButton pipRefresh;
	private JButton policyDeployBtn;
	private JButton pipPopulateBtn;
	private JTable deployedPolicyTable;
	private final JPopupMenu deployedPolicyPopup = new JPopupMenu();
	private JLabel pdpInfoLabel;
	private JLabel pipInfoLabel;
	private JTextArea pipTextArea;
	private static Logger _logger = Logger.getLogger(MyUCCockpit.class);

	private Thread pdpThread;	
	
	private ThriftClientFactory clientFactory;
	private Pep2PipClient pipClient;
	private Any2PmpClient pmpClient;


	/*
	 *	-Djava.rmi.server.hostname=172.16.195.143
	 *	static{
	 *		System.setProperty("java.rmi.server.hostname", "172.16.195.181");
	 *	}	 
	 * -agentlib:jdwp=transport=dt_socket ,suspend=y ,address=localhost:47163
	 * -Djava.library.path=/home/uc/workspace/pef/bin/linux/components/pdp
	 * -Dfile.encoding=UTF-8 -classpath
	 * /home/uc/workspace/pef/bin/java:/home/uc/workspace/LibPIP/bin
	 * de.fraunhofer.iese.pef.pdp.example.MyPDP
	 */

	public MyUCCockpit() {
		this.clientFactory = new ThriftClientFactory();
		if(this.clientFactory != null){
			this.pipClient = this.clientFactory.createPep2PipClient(new IPLocation("localhost", 21002));
			this.pmpClient = this.clientFactory.createAny2PmpClient(new IPLocation("localhost", 21001));
		}
	}

	
	public static void main(String args[]) {
		MyUCCockpit myUCC = new MyUCCockpit();
		myUCC.showGUI();
	}

	private void showGUI() {
		this.myFrame = new JFrame("Usage Control Cockpit");
		this.myFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		Container framePane = this.myFrame.getContentPane();
		framePane.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();

		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.insets = new Insets(5, 5, 5, 5);

		this.startBtn = new JButton("Start UC");
		this.startBtn.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {}

			@Override
			public void mousePressed(MouseEvent e) {}

			@Override
			public void mouseReleased(MouseEvent e) {
				if (ucIsRunning == false) {
//					stopBtn.setEnabled(true);
					pipInfoLabel.setText("");
					pipRefresh.setEnabled(true);
					pipPopulateBtn.setEnabled(true);
					pipRefresh.addMouseListener(new MouseListener() {

						@Override
						public void mouseClicked(MouseEvent e) {}

						@Override
						public void mousePressed(MouseEvent e) {}

						@Override
						public void mouseReleased(MouseEvent e) {
							String pipModelText = "Start UC";
							if (ucIsRunning == true) {
								String t="";
								if(pipClient != null){
									t = pipClient.toString();
								}
								pipTextArea.setText(t);
							}
							pipInfoLabel.setText("REFRESHED!");
						}

						@Override
						public void mouseEntered(MouseEvent e) {}

						@Override
						public void mouseExited(MouseEvent e) {}
					});

					deployedPolicyTable.setEnabled(true);
					policyDeployBtn.setEnabled(true);

					DefaultTableModel dtm = (DefaultTableModel) deployedPolicyTable
							.getModel();
					dtm.getDataVector().removeAllElements();
					dtm.fireTableDataChanged();
					deployedPolicies.clear();
					
					if(!isStarted())
						start();
					pdpInfoLabel.setText("PDP running");
					ucIsRunning = true;
					// myJta.setText(myJta.getText()+"PDP is running"+System.getProperty("line.separator"));
				}
			}

			@Override
			public void mouseEntered(MouseEvent e) {}

			@Override
			public void mouseExited(MouseEvent e) {}
		});
		framePane.add(this.startBtn, gbc);

		this.stopBtn = new JButton("Stop UC");
		this.stopBtn.setEnabled(false);
		this.stopBtn.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
			}

			@Override
			public void mousePressed(MouseEvent e) {
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				stopBtn.setEnabled(false);
				deployedPolicyTable.setEnabled(false);
				policyDeployBtn.setEnabled(false);
				if (ucIsRunning == true) {
					stopPDP();
					pdpInfoLabel.setText("PDP stopped");
					// myJta.setText(myJta.getText()+"PDP is running"+System.getProperty("line.separator"));
				}
			}

			@Override
			public void mouseEntered(MouseEvent e) {
			}

			@Override
			public void mouseExited(MouseEvent e) {
			}
		});
		gbc.gridx = 1;
		gbc.anchor = GridBagConstraints.LINE_START;
		framePane.add(this.stopBtn, gbc);

		this.jTabPane = new JTabbedPane();

		this.pdpPanel = this.createPDPPanel();
		this.jTabPane.addTab("PDP", this.pdpPanel);

		this.pipPanel = this.createPIPPanel();
		this.jTabPane.addTab("PIP", this.pipPanel);

		gbc.gridy = 1;
		gbc.gridx = 0;
		gbc.weighty = 1.0;
		gbc.weightx = 1.0;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		framePane.add(this.jTabPane, gbc);

		this.myFrame.pack();
		this.myFrame.setMinimumSize(new Dimension(500, 400));
		this.myFrame.setVisible(true);
		this.myFrame.setResizable(false);
	}

	private JPanel createPIPPanel() {
		JPanel _return = new JPanel();
		_return.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();

		gbc.weightx = 4.0;
		gbc.insets = new Insets(5, 5, 5, 5);
		gbc.anchor = GridBagConstraints.FIRST_LINE_START;

		gbc.gridy = 0;
		gbc.gridx = 0;
		this.pipPopulateBtn = new JButton("Populate PIP");
		this.pipPopulateBtn.setEnabled(false);
		this.pipPopulateBtn.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (ucIsRunning) {
					JFileChooser jfc = new JFileChooser();
					int sod = jfc.showOpenDialog(myFrame);
					if (sod == JFileChooser.APPROVE_OPTION) {
						String policy = jfc.getSelectedFile().getName();
						pipInfoLabel.setText(policy + " deployed");

//						populate(jfc.getSelectedFile());
					}
				}
			}

			@Override
			public void mousePressed(MouseEvent e) {}

			@Override
			public void mouseReleased(MouseEvent e) {}

			@Override
			public void mouseEntered(MouseEvent e) {}

			@Override
			public void mouseExited(MouseEvent e) {}
		});
		_return.add(this.pipPopulateBtn, gbc);

		gbc.gridy = 0;
		gbc.gridx = 1;
		this.pipRefresh = new JButton("Refresh");
		this.pipRefresh.setEnabled(false);
		_return.add(this.pipRefresh, gbc);



		if (this.ucIsRunning == true) {
			// pipModel = this.pipHandler.printModel();
		}
		this.pipTextArea = new JTextArea();
		this.pipTextArea.setText("");
		this.pipTextArea.setEditable(false);
		gbc.gridy = 1;
		gbc.gridx = 0;
		gbc.gridwidth = 2;
		gbc.weighty = 4.0;
		gbc.fill = GridBagConstraints.BOTH;
		_return.add(new JScrollPane(this.pipTextArea), gbc);
		// _return.add(jta,gbc);


		this.pipInfoLabel = new JLabel("Start UC");
		gbc.gridy = 2;
		gbc.gridx = 0;
		gbc.gridwidth = 2;
		gbc.weighty = 0;
		_return.add(this.pipInfoLabel, gbc);

		return _return;
	}

//	protected void populate(File f){
//
//		StaticAnalysis.importXML(f.getAbsolutePath());
//
//		IMessageFactory _messageFactory = MessageFactoryCreator
//				.createMessageFactory();
//
//		// Initialize if model with TEST_C --> TEST_D
//		_logger.debug("Initialize PIP with sources and sinkes from "+f.getAbsolutePath());
//
//		Map<String, String> param = new HashMap<String, String>();
//		param.put("PEP", "Java");
//
//
//		Map<String, OffsetTable> sourcesMap = StaticAnalysis.getSourcesMap();
//		Map<String, OffsetTable> sinksMap = StaticAnalysis.getSinksMap();
//		Map<String, String[]> flowsMap = StaticAnalysis.getFlowsMap();
//
//		try {
//			param.put("type", "source");
//			for (String source : sourcesMap.keySet()) {
//				OffsetTable ot = sourcesMap.get(source);
//				for (int offset : ot.getSet().keySet()) {
//					OffsetParameter on = ot.get(offset);
//
//					String genericSig = on.getSignature();
//					param.put("offset", String.valueOf(offset));
//					param.put("location", source);
//					param.put("signature", on.getSignature());
//					for (int parameter : on.getType().keySet()) {
//						String id = on.getIdOfPar(parameter);
//						param.put("id", id);
//						param.put("parampos", String.valueOf(parameter));
//						IEvent initEvent = _messageFactory.createActualEvent("JoanaInitInfoFlow", param);
//						req.getPIP().update(initEvent);
//					}
//				}
//			}
//
//		} catch (Exception e) {
//			System.err.println("Error while pasrsing sources. ");
//			e.printStackTrace();
//		}
//
////		/*
////		 * generate sinks
////		 */
//		try {
//			param.put("type", "sink");
//			for (String sink : sinksMap.keySet()) {
//				OffsetTable ot = sinksMap.get(sink);
//				for (int offset : ot.getSet().keySet()) {
//					OffsetParameter on = ot.get(offset);
//
//					String genericSig = on.getSignature();
//					param.put("offset", String.valueOf(offset));
//					param.put("location", sink);
//					param.put("signature", on.getSignature());
//					for (int parameter : on.getType().keySet()) {
//						String id = on.getIdOfPar(parameter);
//						param.put("id", id);
//						param.put("parampos", String.valueOf(parameter));
//						IEvent initEvent = _messageFactory.createActualEvent("JoanaInitInfoFlow", param);
//						req.getPIP().update(initEvent);
//					}
//				}
//			}
//
//		} catch (Exception e) {
//			System.err.println("Error while pasrsing sinks. ");
//			e.printStackTrace();
//		}
//	}

	private JPanel createPDPPanel() {
		JPanel _return = new JPanel();
		_return.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();

		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.insets = new Insets(5, 5, 5, 5);
		gbc.anchor = GridBagConstraints.FIRST_LINE_START;

		JLabel deployedPolicyLab = new JLabel("Deployed Mechanisms");
		deployedPolicyLab.setVerticalAlignment(JLabel.BOTTOM);
		deployedPolicyLab.setVerticalTextPosition(JLabel.BOTTOM);
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.fill = GridBagConstraints.VERTICAL;
//		gbc.gridwidth = GridBagConstraints.REMAINDER;
		_return.add(deployedPolicyLab, gbc);
		gbc.fill = GridBagConstraints.NONE;

//		gbc.fill = GridBagConstraints.HORIZONTAL;
		this.policyDeployBtn = new JButton("Deploy Policy");
		this.policyDeployBtn.setEnabled(false);
		gbc.gridy = 0;
		gbc.gridx = 0;
		this.policyDeployBtn.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (ucIsRunning == false) {
					pdpInfoLabel.setText("Start PDP!");
				} else {
					JFileChooser jfc = new JFileChooser();
					int sod = jfc.showOpenDialog(myFrame);
					if (sod == JFileChooser.APPROVE_OPTION) {
						String policy = jfc.getSelectedFile().getName();
						pdpInfoLabel.setText(policy + " deployed");

						if (deployedPolicies.size() == 0) {
							deployedPolicies.add(policy);
							deployPolicy(jfc.getSelectedFile()
									.getAbsolutePath());
						} else {
							Iterator<String> policyIt = deployedPolicies
									.iterator();
							boolean add = true;
							while (policyIt.hasNext()) {
								if (policyIt.next().equals(policy)) {
									add = false;
									pdpInfoLabel.setText(policy
											+ " already deployed");
									break;
								}
							}
							if (add == true) {
								deployedPolicies.add(policy);
								deployPolicy(jfc.getSelectedFile()
										.getAbsolutePath());
							}
						}
						System.out.println("FILE: "
								+ jfc.getSelectedFile().getAbsoluteFile());

						DefaultTableModel dtm = (DefaultTableModel) deployedPolicyTable
								.getModel();
						dtm.getDataVector().removeAllElements();
						dtm.fireTableDataChanged();
					}
				}
			}

			@Override
			public void mousePressed(MouseEvent e) {}

			@Override
			public void mouseReleased(MouseEvent e) {}

			@Override
			public void mouseEntered(MouseEvent e) {}

			@Override
			public void mouseExited(MouseEvent e) {}
		});
		_return.add(this.policyDeployBtn, gbc);

		Object[] colNames = { "Policy Name", "Mechanism" };
		DefaultTableModel dtm = new DefaultTableModel(new Object[][] {},
				colNames) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		this.deployedPolicyTable = new JTable(dtm);
		this.deployedPolicyTable.setEnabled(false);
		gbc.gridy = 1;
		gbc.gridx = 0;
		gbc.weighty = 0.5;
		gbc.gridwidth = 2;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		JScrollPane jsp = new JScrollPane(this.deployedPolicyTable);
		_return.add(jsp, gbc);
		this.deployedPolicyTable.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {}

			@Override
			public void mousePressed(MouseEvent e) {}

			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub
				deployedPolicyPopup.removeAll();
				if (e.getButton() == MouseEvent.BUTTON3) {
					JTable source = (JTable) e.getSource();
					int row = source.rowAtPoint(e.getPoint());
					int col = source.columnAtPoint(e.getPoint());
					if (!source.isRowSelected(row)) {
						source.changeSelection(row, col, false, false);
					}
					JMenuItem jmi = new JMenuItem("Remove "
							+ source.getModel().getValueAt(row, 0));
					jmi.setActionCommand(String.valueOf(row));
					jmi.addActionListener(new DeployedPolicyMenuItemListener());
					deployedPolicyPopup.add(jmi);
					deployedPolicyPopup.show(e.getComponent(), e.getX(),
							e.getY());
				}
			}

			@Override
			public void mouseEntered(MouseEvent e) {}

			@Override
			public void mouseExited(MouseEvent e) {}
		});

		this.pdpInfoLabel = new JLabel(" ");
		gbc.gridy = 2;
		gbc.weighty = 0;
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		// gbc.fill = GridBagConstraints.BOTH;
		_return.add(this.pdpInfoLabel, gbc);

		return _return;
	}

	private class DeployedPolicyMenuItemListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub

			Component c = (Component) e.getSource();
			JPopupMenu menu = (JPopupMenu) c.getParent();
			JTable table = (JTable) menu.getInvoker();

			String mechanism = (String) table.getModel().getValueAt(
					table.getSelectedRow(), 1);
			String policy = (String) table.getModel().getValueAt(
					table.getSelectedRow(), 0);
			pmpClient.revokeMechanismPmp(policy.trim(), mechanism.trim());// /home/uc/Desktop/DontSendSmartMeterData.xml");

			((DefaultTableModel) table.getModel()).removeRow(Integer.parseInt(e
					.getActionCommand()));
			deployedPolicies.remove(Integer.parseInt(e.getActionCommand()));
		}
	}

	public void stopPDP() {
		if (this.ucIsRunning) {
			pdpThread.stop();
//			this.pdpCtrl.stop();
			this.ucIsRunning = false;
		}
	}

	public void deployPolicy(String policyFile) {
		this.pmpClient.deployPolicyURIPmp(policyFile);
	}
}
