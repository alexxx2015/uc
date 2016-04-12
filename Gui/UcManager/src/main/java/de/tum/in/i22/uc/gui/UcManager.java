package de.tum.in.i22.uc.gui;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.Controller;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IEvent;
import de.tum.in.i22.uc.cm.distribution.IPLocation;
import de.tum.in.i22.uc.cm.distribution.client.Pep2PdpClient;
import de.tum.in.i22.uc.cm.distribution.client.Pmp2PmpClient;
import de.tum.in.i22.uc.cm.factories.IMessageFactory;
import de.tum.in.i22.uc.cm.factories.MessageFactoryCreator;
import de.tum.in.i22.uc.cm.settings.Settings;
import de.tum.in.i22.uc.gui.analysis.OffsetParameter;
import de.tum.in.i22.uc.gui.analysis.OffsetTable;
import de.tum.in.i22.uc.gui.analysis.StaticAnalysis;
import de.tum.in.i22.uc.thrift.client.ThriftClientFactory;

public class UcManager extends Controller {

	// private PDPMain myPDP;
	private boolean ucIsRunning = false;
	// private final LinkedList<String> deployedPolicie = new
	// LinkedList<String>();
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
	private static Logger _logger = LoggerFactory.getLogger(UcManager.class);

	private Thread pdpThread;

	private final ThriftClientFactory clientFactory;
	private Pmp2PmpClient pmpClient;
	private Pep2PdpClient pdpClient;

	/*
	 * -Djava.rmi.server.hostname=172.16.195.143 static{
	 * System.setProperty("java.rmi.server.hostname", "172.16.195.181"); }
	 * -agentlib:jdwp=transport=dt_socket ,suspend=y ,address=localhost:47163
	 * -Djava.library.path=/home/uc/workspace/pef/bin/linux/components/pdp
	 * -Dfile.encoding=UTF-8 -classpath
	 * /home/uc/workspace/pef/bin/java:/home/uc/workspace/LibPIP/bin
	 * de.fraunhofer.iese.pef.pdp.example.MyPDP
	 */

	public UcManager(String args[]) {
		super(args);
		this.clientFactory = new ThriftClientFactory();
	}

	public static void main(String args[]) {
		UcManagerFX.main(args);
//		UcManager myUCC = new UcManager(args);
//		myUCC.showGUI();
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
			public void mouseClicked(MouseEvent e) {
			}

			@Override
			public void mousePressed(MouseEvent e) {
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				startUcInf();
			}

			@Override
			public void mouseEntered(MouseEvent e) {
			}

			@Override
			public void mouseExited(MouseEvent e) {
			}
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
				stopUcInf();
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
		this.myFrame.setResizable(true);
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

						populate(jfc.getSelectedFile());
					}
				}
			}

			@Override
			public void mousePressed(MouseEvent e) {
			}

			@Override
			public void mouseReleased(MouseEvent e) {
			}

			@Override
			public void mouseEntered(MouseEvent e) {
			}

			@Override
			public void mouseExited(MouseEvent e) {
			}
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
		this.pipTextArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
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

	protected void populate(File f) {
		try {
			this.pdpClient.connect();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		StaticAnalysis.importXML(f.getAbsolutePath());

		IMessageFactory _messageFactory = MessageFactoryCreator
				.createMessageFactory();

		// Initialize if model with TEST_C --> TEST_D
		_logger.debug("Initialize PIP with sources and sinkes from "
				+ f.getAbsolutePath());

		Map<String, String> param = new HashMap<String, String>();
		param.put("PEP", "Java");

		Map<String, String> id2SinkMap = new HashMap<String, String>();
		Map<String, String> id2SourceMap = new HashMap<String, String>();

		Map<String, OffsetTable> sourcesMap = StaticAnalysis.getSourcesMap();
		Map<String, OffsetTable> sinksMap = StaticAnalysis.getSinksMap();
		Map<String, String[]> flowsMap = StaticAnalysis.getFlowsMap();

		// Generate Sources
		try {
			param.put("type", "source");
			for (String source : sourcesMap.keySet()) {
				OffsetTable ot = sourcesMap.get(source);
				for (int offset : ot.getSet().keySet()) {
					OffsetParameter on = ot.get(offset);

					param.put("offset", String.valueOf(offset));
					param.put("location", source);
					param.put("signature", on.getSignature());
					for (int parameter : on.getType().keySet()) {
						String id = on.getIdOfPar(parameter);
						id2SourceMap
								.put(id,
										source + ":" + offset + ":"
												+ on.getSignature());
						param.put("id", id);
						param.put("parampos", String.valueOf(parameter));
						IEvent initEvent = _messageFactory.createActualEvent(
								"JoanaInitInfoFlow", param);
						pdpClient.notifyEventSync(initEvent);
					}
				}
			}

		} catch (Exception e) {
			System.err.println("Error while pasrsing sources. ");
			e.printStackTrace();
		}

		// Generate Sinks
		try {
			param.clear();
			param.put("PEP", "Java");
			param.put("type", "sink");
			for (String sink : sinksMap.keySet()) {
				OffsetTable ot = sinksMap.get(sink);
				for (int offset : ot.getSet().keySet()) {
					OffsetParameter on = ot.get(offset);

					param.put("offset", String.valueOf(offset));
					param.put("location", sink);
					param.put("signature", on.getSignature());
					for (int parameter : on.getType().keySet()) {
						String id = on.getIdOfPar(parameter);
						id2SinkMap.put(id,
								sink + ":" + offset + ":" + on.getSignature());
						param.put("id", id);
						param.put("parampos", String.valueOf(parameter));
						IEvent initEvent = _messageFactory.createActualEvent(
								"JoanaInitInfoFlow", param);
						pdpClient.notifyEventSync(initEvent);
					}
				}
			}

		} catch (Exception e) {
			System.err.println("Error while pasrsing sinks. ");
			e.printStackTrace();
		}

		// Generate Flow
		param.clear();
		param.put("type", "iflow");
		param.put("PEP", "Java");
		for (String flow : flowsMap.keySet()) {
			if (!flow.toLowerCase().equals("")) {
				String[] s = flowsMap.get(flow);

				String sink = id2SinkMap.get(flow);

				param.put("sink", sink);
				String sources = "";
				for (String source : s) {
					sources += id2SourceMap.get(source)
							+ Settings.getInstance().getJoanaDelimiter1();
				}
				if (sources.length() > 0)
					sources = sources.substring(0, sources.length() - 1);
				param.put("source", sources);
				IEvent initEvent = _messageFactory.createActualEvent(
						"JoanaInitInfoFlow", param);
				pdpClient.notifyEventSync(initEvent);
			}
		}

		pipTextArea.setText(getIfModel());
	}

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
		// gbc.gridwidth = GridBagConstraints.REMAINDER;
		_return.add(deployedPolicyLab, gbc);
		gbc.fill = GridBagConstraints.NONE;

		// gbc.fill = GridBagConstraints.HORIZONTAL;
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
					JFileChooser jfc = new JFileChooser("../../Tests/Policies");

					int sod = jfc.showOpenDialog(myFrame);
					if (sod == JFileChooser.APPROVE_OPTION) {
						String policy = jfc.getSelectedFile().getName();
						pdpInfoLabel.setText(policy + " deployed");

						// if (deployedPolicies.size() == 0) {
						// deployedPolicies.add(policy);
						// deployPolicyFile(jfc.getSelectedFile()
						// .getAbsolutePath());
						// } else {
						// Iterator<String> policyIt = deployedPolicies
						// .iterator();
						// boolean add = true;
						// while (policyIt.hasNext()) {
						// if (policyIt.next().equals(policy)) {
						// add = false;
						// pdpInfoLabel.setText(policy
						// + " already deployed");
						// break;
						// }
						// }
						// if (add == true) {
						// deployedPolicies.add(policy);
						// deployPolicyFile(jfc.getSelectedFile()
						// .getAbsolutePath());
						// }
						// }
						deployPolicyFile(jfc.getSelectedFile()
								.getAbsolutePath());

						_logger.info("Deployed File "
								+ jfc.getSelectedFile().getAbsoluteFile());

						DefaultTableModel dtm = (DefaultTableModel) deployedPolicyTable
								.getModel();
						dtm.getDataVector().removeAllElements();
						dtm.fireTableDataChanged();

						Map<String, Set<String>> deployedMech = pmpClient
								.listMechanismsPmp();
						Iterator<String> arIt = deployedMech.keySet()
								.iterator();
						while (arIt.hasNext()) {
							String policyName = arIt.next();
							Set<String> mechanism = deployedMech
									.get(policyName);
							Iterator<String> mechIt = mechanism.iterator();
							while (mechIt.hasNext()) {
								String m = mechIt.next();
								((DefaultTableModel) deployedPolicyTable
										.getModel()).addRow(new Object[] {
										policyName, m });
							}
						}
					}
				}
			}

			@Override
			public void mousePressed(MouseEvent e) {
			}

			@Override
			public void mouseReleased(MouseEvent e) {
			}

			@Override
			public void mouseEntered(MouseEvent e) {
			}

			@Override
			public void mouseExited(MouseEvent e) {
			}
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
		this.deployedPolicyTable
				.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		gbc.gridy = 1;
		gbc.gridx = 0;
		gbc.weighty = 0.5;
		gbc.gridwidth = 2;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		JScrollPane jsp = new JScrollPane(this.deployedPolicyTable);
		_return.add(jsp, gbc);
		this.deployedPolicyTable.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {
			}

			@Override
			public void mousePressed(MouseEvent e) {
			}

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
					JMenuItem jmi = new JMenuItem("Revoke "
							+ source.getModel().getValueAt(row, 1));
					jmi.setActionCommand(String.valueOf(row));
					jmi.addActionListener(new DeployedPolicyMenuItemListener());
					deployedPolicyPopup.add(jmi);
					deployedPolicyPopup.show(e.getComponent(), e.getX(),
							e.getY());
				}
			}

			@Override
			public void mouseEntered(MouseEvent e) {
			}

			@Override
			public void mouseExited(MouseEvent e) {
			}
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
			// deployedPolicies.remove(Integer.parseInt(e.getActionCommand()));
		}
	}

	public void deployPolicyFile(String policyFile) {
		try {
			this.pmpClient.connect();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String policy = "";
		try {
			FileReader fr = new FileReader(policyFile);
			BufferedReader br = new BufferedReader(fr);
			String line;
			while ((line = br.readLine()) != null)
				policy += line;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// this.pmpClient.deployPolicyXMLPmp(policy);
		this.pmpClient.deployPolicyURIPmp(policyFile);
	}

	protected void stopUcInf() {
		if (ucIsRunning == true) {
			stop();
			ucIsRunning = false;

			pdpInfoLabel.setText("PDP stopped");
			// myJta.setText(myJta.getText()+"PDP is running"+System.getProperty("line.separator"));
		}
		deployedPolicyTable.setEnabled(false);
		((DefaultTableModel) deployedPolicyTable.getModel()).getDataVector()
				.removeAllElements();
		((DefaultTableModel) deployedPolicyTable.getModel())
				.fireTableDataChanged();
		// deployedPolicies.clear();
		stopBtn.setEnabled(false);
		startBtn.setEnabled(true);
		policyDeployBtn.setEnabled(false);
		pipTextArea.setText("");
		pipRefresh.setEnabled(false);
		pipPopulateBtn.setEnabled(false);
	}

	protected void startUcInf() {

		if (ucIsRunning == false) {
			stopBtn.setEnabled(true);
			startBtn.setEnabled(false);
			pipInfoLabel.setText("");
			pipRefresh.setEnabled(true);
			pipPopulateBtn.setEnabled(true);
			pipRefresh.addMouseListener(new MouseListener() {

				@Override
				public void mouseClicked(MouseEvent e) {
				}

				@Override
				public void mousePressed(MouseEvent e) {
				}

				@Override
				public void mouseReleased(MouseEvent e) {
					Thread t = new Thread() {
						@Override
						public void run() {
							while (true) {
								if (ucIsRunning == true) {
									pipTextArea.setText(getIfModel());
								}
								pipInfoLabel.setText("REFRESHED!");
								try {
									Thread.sleep(3000);
								} catch (InterruptedException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								}

							}
						}
					};
					t.start();
				}

				@Override
				public void mouseEntered(MouseEvent e) {
				}

				@Override
				public void mouseExited(MouseEvent e) {
				}
			});

			deployedPolicyTable.setEnabled(true);
			policyDeployBtn.setEnabled(true);

			DefaultTableModel dtm = (DefaultTableModel) deployedPolicyTable
					.getModel();
			dtm.getDataVector().removeAllElements();
			dtm.fireTableDataChanged();
			// deployedPolicies.clear();

			if (!isStarted()) {
				start();
				if (this.clientFactory != null) {
					int pmpPort=Settings.getInstance().getPmpListenerPort();
					int pdpPort=Settings.getInstance().getPdpListenerPort();
					this.pmpClient = this.clientFactory
							.createPmp2PmpClient(new IPLocation("localhost", pmpPort));
					this.pdpClient = this.clientFactory
							.createPep2PdpClient(new IPLocation("localhost", pdpPort));
				}
				if (this.clientFactory != null) {
					int pmpPort=Settings.getInstance().getPmpListenerPort();
					int pdpPort=Settings.getInstance().getPdpListenerPort();
					this.pmpClient = this.clientFactory
							.createPmp2PmpClient(new IPLocation("localhost", pmpPort));
					this.pdpClient = this.clientFactory
							.createPep2PdpClient(new IPLocation("localhost", pdpPort));
				}

			}
			pdpInfoLabel.setText("PDP running");
			ucIsRunning = true;
			// myJta.setText(myJta.getText()+"PDP is running"+System.getProperty("line.separator"));
		}
	}
}
