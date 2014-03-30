package de.tum.in.i22.uc;

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
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.ArrayList;

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

import de.tum.in.i22.uc.cm.IMessageFactory;
import de.tum.in.i22.uc.cm.MessageFactoryCreator;
import de.tum.in.i22.uc.cm.datatypes.IEvent;
import de.tum.in.i22.uc.cm.in.RequestHandler;
import de.tum.in.i22.uc.cm.pdp.core.IPdpMechanism;
import de.tum.in.i22.uc.cm.settings.Settings;
import edu.tum.XMLtools.OffsetParameter;
import edu.tum.XMLtools.OffsetTable;
import edu.tum.XMLtools.StaticAnalysis;


public class MyUCCockpit {

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
	private JTextArea pipTextFormula;

	private static Logger _logger = Logger.getLogger(Controller.class);

	private Thread pdpThread;

	private Controller pdpCtrl;

	// -Djava.rmi.server.hostname=172.16.195.143
	// static{
	// System.setProperty("java.rmi.server.hostname", "172.16.195.181");
	// }
	/*
	 * -agentlib:jdwp=transport=dt_socket ,suspend=y ,address=localhost:47163
	 * -Djava.library.path=/home/uc/workspace/pef/bin/linux/components/pdp
	 * -Dfile.encoding=UTF-8 -classpath
	 * /home/uc/workspace/pef/bin/java:/home/uc/workspace/LibPIP/bin
	 * de.fraunhofer.iese.pef.pdp.example.MyPDP
	 */
	public MyUCCockpit() {
		// this.myPDP = new PDPMain();
		// this.myPDP.showGUI();
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
			public void mouseClicked(MouseEvent e) {
			}

			@Override
			public void mousePressed(MouseEvent e) {
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				if (ucIsRunning == false) {
//					stopBtn.setEnabled(true);
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
							String pipModelText = "Start UC";
							if (ucIsRunning == true) {
								pipTextArea.setText(RequestHandler.getInstance().getPIP().toString());
								// pipModelText = pipHandler.printModel();
							}
							pipInfoLabel.setText("REFRESHED!");
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
					deployedPolicies.clear();
					pdpThread = new Thread(new Runpdp());
					pdpThread.start();
					pdpInfoLabel.setText("PDP running");
					ucIsRunning = true;
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
		// JLabel pipFormulaLab = new JLabel("Formula:");
		// _return.add(pipFormulaLab,gbc);

//		this.pipTextFormula = new JTextArea();
//		this.pipTextFormula.setEditable(true);
//		this.pipTextFormula.setText("isNotIn|D3|c0|2");

//		gbc.fill = GridBagConstraints.HORIZONTAL;
//		gbc.gridwidth = 5;
//		_return.add(new JScrollPane(this.pipTextFormula), gbc);
//		gbc.gridwidth = 1;

		gbc.gridy = 0;
		gbc.gridx = 1;
		this.pipRefresh = new JButton("Refresh");
		this.pipRefresh.setEnabled(false);
		_return.add(this.pipRefresh, gbc);


//		JButton pipNotifyInitEvent = new JButton("Add Data");
//		gbc.gridx = 2;
//		pipNotifyInitEvent.addMouseListener(new MouseListener() {
//
//			@Override
//			public void mouseClicked(MouseEvent e) {
//			}
//
//			@Override
//			public void mousePressed(MouseEvent e) {
//			}
//
//			@Override
//			public void mouseReleased(MouseEvent e) {
//				if (ucIsRunning == true) {
//					new Event("initEvent", false);
//				}
//				// pipTextArea.setText("New data generated\n "+
//				// pipHandler.printModel());
//			}
//
//			@Override
//			public void mouseEntered(MouseEvent e) {
//			}
//
//			@Override
//			public void mouseExited(MouseEvent e) {
//			}
//		});
//		_return.add(pipNotifyInitEvent, gbc);
//		final Event curEvent = new Event("testEvent", true,
//				System.currentTimeMillis());
//		curEvent.addStringParameter("p1", "C0");
//		curEvent.addStringParameter("p2", "C1");

//		JButton pipNotifyFakeEvent = new JButton("Eval (f, C0+=C1)");
//		gbc.gridx = 3;
//		pipNotifyFakeEvent.addMouseListener(new MouseListener() {
//
//			@Override
//			public void mouseClicked(MouseEvent e) {
//			}
//
//			@Override
//			public void mousePressed(MouseEvent e) {
//			}
//
//			@Override
//			public void mouseReleased(MouseEvent e) {
//				pipTextFormula.getText();
////				if (ucIsRunning == true) {
////					Decision d = lpdp.pdpNotifyEventJNI(curEvent);
////					System.out.println("decision: " + d);
////				}
//				// pipTextArea.setText("Evaluation of ["+predicate+"] simulating [CO+=C1] --> "+(Integer.parseInt(pipModelText)==1?"True":"False")+"\n"+pipHandler.printModel());
//			}
//
//			@Override
//			public void mouseEntered(MouseEvent e) {
//			}
//
//			@Override
//			public void mouseExited(MouseEvent e) {
//			}
//		});
//		_return.add(pipNotifyFakeEvent, gbc);

//		JButton pipNotifyRealEvent = new JButton("notify C0+=C1");
//		gbc.gridx = 4;
//		pipNotifyRealEvent.addMouseListener(new MouseListener() {
//
//			@Override
//			public void mouseClicked(MouseEvent e) {
//			}
//
//			@Override
//			public void mousePressed(MouseEvent e) {
//			}
//
//			@Override
//			public void mouseReleased(MouseEvent e) {
//				Decision d = new Decision();
////				if (ucIsRunning == true) {
////					d = lpdp.pdpNotifyEventJNI(curEvent);
////					System.out.println("decision: " + d);
////				}
//				// pipTextArea.setText("Sending Event "+curEvent.getEventAction()+" = "+d+" (res="+res+")\n"+pipHandler.printModel());
//			}
//
//			@Override
//			public void mouseEntered(MouseEvent e) {
//			}
//
//			@Override
//			public void mouseExited(MouseEvent e) {
//			}
//		});
//		_return.add(pipNotifyRealEvent, gbc);

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

	protected void populate(File f){

		RequestHandler req = RequestHandler.getInstance();

		StaticAnalysis.importXML(f.getAbsolutePath());

//		IKey _test_predicate_key = KeyBasic.createNewKey();
//		String _test_predicate = "isNotIn|TEST_D|TEST_C|0";
		IMessageFactory _messageFactory = MessageFactoryCreator
				.createMessageFactory();

//		Map<String, IKey> predicates = new HashMap<String, IKey>();
//		predicates.put(_test_predicate, _test_predicate_key);
//		_logger.debug("TEST: adding predicate via _core2pip");
//		req.getCore2Pip().addPredicates(predicates);

		// Initialize if model with TEST_C --> TEST_D
		_logger.debug("Initialize PIP with sources and sinkes from "+f.getAbsolutePath());


//		Map<String, OffsetTable> sinks = StaticAnalysis.getSinksMap();
//		Map<String, String> param = new HashMap<String, String>();
//		//add a special key named "PEP" with the packagename where the corresponding pip event is located in PIP-core project...
//		param.put("PEP", "Java");
//		param.put("type", "sink");
//		Iterator keyIt = sinks.keySet().iterator();
//		while(keyIt.hasNext()){
//			String key = (String)keyIt.next();
//			param.put("location", key);
//
//			OffsetTable offTab = sinks.get(key);
//
//			Map<Integer, OffsetParameter> mapTab = offTab.getSet();
//			Iterator keyMapTabIt = mapTab.keySet().iterator();
//			while(keyMapTabIt.hasNext()){
//				int keyMapTab = (int)keyMapTabIt.next();
//				OffsetParameter offParam = mapTab.get(keyMapTab);
//				String signature = offParam.getSignature();
//				param.put("signature", signature);
//
//				Map<Integer,OffsetNode> offParamNode = offParam.getType();
//				Iterator keyOffParamNodeIt = offParamNode.keySet().iterator();
//				while(keyOffParamNodeIt.hasNext()){
//					int keyOffParamNode = (int)keyOffParamNodeIt.next();
//					OffsetNode offSetNode = offParamNode.get(keyOffParamNode);
//					param.put("parampos",String.valueOf(keyOffParamNode));
//					String id = offSetNode.getId();
//					param.put("id", id);
//					IEvent initEvent = _messageFactory.createActualEvent("JoanaInitInfoFlow", param);
//					req.getPipHandler().notifyActualEvent(initEvent);
//				}
//			}
//		}

//		Map<String, OffsetTable> sources = StaticAnalysis.getSourcesMap();
//		param.put("PEP", "Java");
//		param.put("type", "source");
//		keyIt = sources.keySet().iterator();
//		while(keyIt.hasNext()){
//			String key = (String)keyIt.next();
//			param.put("location", key);
//
//			OffsetTable offTab = sinks.get(key);
//
//			Map<Integer, OffsetParameter> mapTab = offTab.getSet();
//			Iterator keyMapTabIt = mapTab.keySet().iterator();
//			while(keyMapTabIt.hasNext()){
//				int keyMapTab = (int)keyMapTabIt.next();
//				OffsetParameter offParam = mapTab.get(keyMapTab);
//				String signature = offParam.getSignature();
//				param.put("signature", signature);
//
//				Map<Integer,OffsetNode> offParamNode = offParam.getType();
//				Iterator keyOffParamNodeIt = offParamNode.keySet().iterator();
//				while(keyOffParamNodeIt.hasNext()){
//					int keyOffParamNode = (int)keyOffParamNodeIt.next();
//					OffsetNode offSetNode = offParamNode.get(keyOffParamNode);
//					param.put("parampos",String.valueOf(keyOffParamNode));
//					String id = offSetNode.getId();
//					param.put("id", id);
//					IEvent initEvent = _messageFactory.createActualEvent("JoanaInitInfoFlow", param);
//					req.getPipHandler().notifyActualEvent(initEvent);
//				}
//			}
//		}


		Map<String, String> param = new HashMap<String, String>();
		param.put("PEP", "Java");


		Map<String, OffsetTable> sourcesMap = StaticAnalysis.getSourcesMap();
		Map<String, OffsetTable> sinksMap = StaticAnalysis.getSinksMap();
		Map<String, String[]> flowsMap = StaticAnalysis.getFlowsMap();

		/*
		 * generate sources
		 */

		try {
			param.put("type", "source");
			for (String source : sourcesMap.keySet()) {
				OffsetTable ot = sourcesMap.get(source);
				for (int offset : ot.getSet().keySet()) {
					OffsetParameter on = ot.get(offset);

					String genericSig = on.getSignature();
					param.put("offset", String.valueOf(offset));
					param.put("location", source);
					param.put("signature", on.getSignature());
					for (int parameter : on.getType().keySet()) {
						String id = on.getIdOfPar(parameter);
						param.put("id", id);
						param.put("parampos", String.valueOf(parameter));
						IEvent initEvent = _messageFactory.createActualEvent("JoanaInitInfoFlow", param);
						req.getPIP().notifyActualEvent(initEvent);
					}
				}
			}

		} catch (Exception e) {
			System.err.println("Error while pasrsing sources. ");
			e.printStackTrace();
		}

//		/*
//		 * generate sinks
//		 */
		try {
			param.put("type", "sink");
			for (String sink : sinksMap.keySet()) {
				OffsetTable ot = sinksMap.get(sink);
				for (int offset : ot.getSet().keySet()) {
					OffsetParameter on = ot.get(offset);

					String genericSig = on.getSignature();
					param.put("offset", String.valueOf(offset));
					param.put("location", sink);
					param.put("signature", on.getSignature());
					for (int parameter : on.getType().keySet()) {
						String id = on.getIdOfPar(parameter);
						param.put("id", id);
						param.put("parampos", String.valueOf(parameter));
						IEvent initEvent = _messageFactory.createActualEvent("JoanaInitInfoFlow", param);
						req.getPIP().notifyActualEvent(initEvent);
					}
				}
			}

		} catch (Exception e) {
			System.err.println("Error while pasrsing sinks. ");
			e.printStackTrace();
		}
	}

	private JPanel createPDPPanel() {
		JPanel _return = new JPanel();
		_return.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();

		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.insets = new Insets(5, 5, 5, 5);
		gbc.anchor = GridBagConstraints.FIRST_LINE_START;

//		JLabel rmiServerLab = new JLabel("PDP RMI Server-IP");
//		_return.add(rmiServerLab, gbc);

//		gbc.gridx = 1;
//		gbc.gridwidth = GridBagConstraints.REMAINDER;
//		gbc.weightx = 1.0;
//		gbc.fill = GridBagConstraints.HORIZONTAL;
//		String hostAddress = "";
//
//		try {
//			Enumeration nis = NetworkInterface.getNetworkInterfaces();
//			while (nis.hasMoreElements()) {
//				NetworkInterface ni = (NetworkInterface) nis.nextElement();
//				Enumeration<InetAddress> inetAddress = ni.getInetAddresses();
//				while (inetAddress.hasMoreElements()) {
//					InetAddress ia = inetAddress.nextElement();
//					if (ia.getHostName().contains("vm9")) {
//						hostAddress = ia.getHostAddress();
//					}1
//				}
//			}
//		} catch (SocketException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
//		this.pdpRmiServerField = new JTextField(hostAddress);
//		_return.add(this.pdpRmiServerField, gbc);



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

						HashMap<String, ArrayList<IPdpMechanism>> ar = RequestHandler.getInstance().getPDP().listMechanisms();
						Iterator<String> arIt = ar.keySet().iterator();
						while (arIt.hasNext()) {
							String policyName = arIt.next();
							ArrayList<IPdpMechanism> mechanism = ar.get(policyName);
							Iterator<IPdpMechanism> mechIt = mechanism.iterator();
							while(mechIt.hasNext()){
								IPdpMechanism m = mechIt.next();
								((DefaultTableModel) deployedPolicyTable
										.getModel()).addRow(new Object[] {
										policyName,
										m.getMechanismName() });
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
			RequestHandler.getInstance().getPDP().revokeMechanism(policy.trim(), mechanism.trim());// /home/uc/Desktop/DontSendSmartMeterData.xml");

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
		RequestHandler.getInstance().getPDP().deployPolicy(policyFile);
	}

	public class Runpdp implements Runnable{

		@Override
		public void run() {
			// TODO Auto-generated method stub

			// load PDP properties
			Settings.getInstance();
			
			Controller.startUC();

			/*
			 * Guice.createInjector() takes your Modules, and returns a new Injector
			 * instance. Most applications will call this method exactly once, in
			 * their main() method.
			 */
//			Injector injector = Guice.createInjector(new PdpModuleMockTestPip());
//			Injector injector = Guice.createInjector(new PdpModule());

			/*
			 * Now that we've got the injector, we can build objects.
			 */
//			injector.getInstance(Controller.class).start();
		}

	}
}
