package de.tum.in.i22.uc.gui;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.LinkedList;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.border.LineBorder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.Controller;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IEvent;
import de.tum.in.i22.uc.cm.distribution.IPLocation;
import de.tum.in.i22.uc.cm.distribution.client.Pep2PdpClient;
import de.tum.in.i22.uc.thrift.client.ThriftClientFactory;

public class TraceManager {

	private TraceRecorder tr = null;
	private PlayingClient pl = null;
	private LinkedList<IEvent> ll = null;
	private Thread refreshThread;
	private static Logger log = LoggerFactory.getLogger(TraceManager.class);

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					TraceManager window = new TraceManager();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	private JFrame frame;
	private JTextField ipTextBox;
	private JTextPane infoPane;
	private JTextField portTextBox;
	private JTextArea traceLabel;

	/**
	 * Create the application.
	 */
	public TraceManager() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new GridLayout(0, 1, 0, 0));

		JPanel panel_1 = new JPanel();
		frame.getContentPane().add(panel_1);
		panel_1.setLayout(new GridLayout(0, 1, 0, 0));

		JPanel panel_3 = new JPanel();
		panel_1.add(panel_3);
		panel_3.setLayout(new BoxLayout(panel_3, BoxLayout.X_AXIS));

		JPanel panel_6 = new JPanel();
		panel_3.add(panel_6);
		panel_6.setLayout(new GridLayout(0, 1, 0, 0));

		JPanel panel_4 = new JPanel();
		panel_6.add(panel_4);
		panel_4.setBorder(new LineBorder(new Color(0, 0, 0)));

		JLabel IpLabel = new JLabel("PDP IP: ");
		panel_4.add(IpLabel);

		ipTextBox = new JTextField();

		panel_4.add(ipTextBox);
		ipTextBox.setColumns(10);

		JPanel panel_5 = new JPanel();
		panel_6.add(panel_5);
		panel_5.setBorder(new LineBorder(new Color(0, 0, 0)));

		JLabel label = new JLabel("PDP Port: ");
		panel_5.add(label);

		portTextBox = new JTextField();
		portTextBox.setColumns(10);
		panel_5.add(portTextBox);

		infoPane = new JTextPane();
		infoPane.setBackground(Color.LIGHT_GRAY);
		panel_3.add(infoPane);

		traceLabel = new JTextArea("Trace");
		traceLabel.setEditable(false);
		JScrollPane scroll = new JScrollPane(traceLabel,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		frame.getContentPane().add(scroll);

		JPanel panel = new JPanel();
		frame.getContentPane().add(panel);
		panel.setLayout(new GridLayout(0, 1, 0, 0));

		JPanel panel_2 = new JPanel();
		panel.add(panel_2);
		panel_2.setLayout(new GridLayout(0, 4, 0, 0));

		JButton stopButton = new JButton("Stop");
		stopButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				stop();
			}
		});

		JButton startButton = new JButton("Start");
		startButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				start();
			}
		});
		panel_2.add(startButton);
		panel_2.add(stopButton);

		JButton playButton = new JButton("Play");
		playButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				play();
			}
		});
		panel_2.add(playButton);

		JButton playRemoteButton = new JButton("Play Remote");
		playRemoteButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				playRemote();
			}
		});
		panel_2.add(playRemoteButton);

		JButton loadButton = new JButton("Load trace");
		loadButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				load();
			}
		});
		panel.add(loadButton);

		JButton saveButton = new JButton("Save trace as...");
		saveButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				save();
			}
		});
		panel.add(saveButton);
	}

	protected void playRemote() {
		String ip = ipTextBox.getText();
		String port = portTextBox.getText();
		if ((ip == null) || (ip.equals(""))) {
			log.error("No IP provided");
			infoPane.setText("Please provide a valid IP");
			return;
		}
		if ((port == null)
				|| (port.equals(""))) {
			log.error("No port provided");
			infoPane.setText("Please provide a valid port");
			return;
		}
		String remoteAddr = ip + ":" + port;
		if (ll != null) {
			infoPane.setText("Starting playing trace to remote PDP ("
					+ remoteAddr + ")....");
			ThriftClientFactory tcf = new ThriftClientFactory();
			Pep2PdpClient pdp = tcf
					.createPep2PdpClient(new IPLocation(ip,
							Integer.valueOf(port)));
			try {
				pdp.connect();
				pl = new PlayingClient(pdp);
				pl.setTrace(ll);
				long startTime = System.nanoTime();
				pl.playTrace();
				long endTime = System.nanoTime();
				infoPane.setText("Simulation ended. Total time : "
						+ (endTime - startTime) / 1000 / 1000 + "msec");
				pdp.disconnect();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			log.error("Load or generate a trace first. Impossible to play empty trace");
			infoPane.setText("Load or generate a trace first. Impossible to play empty trace");
		}
	}

	protected void save() {
		JFileChooser fc = new JFileChooser();
		int f = fc.showSaveDialog(frame);
		if (f == JFileChooser.APPROVE_OPTION) {
			stop();
			String path = fc.getSelectedFile().getPath();
			try {
				log.debug("Dumping trace to file " + path + " ...");
				traceLabel.setText(traceLabel.getText()
						+ "\nDumping trace to file " + path + " ...");
				infoPane.setText("Saving trace (~" + ll.size()
						+ " events) on file " + path + " ...");
				FileOutputStream fout = new FileOutputStream(path);
				ObjectOutputStream oos = new ObjectOutputStream(fout);
				oos.writeObject(ll);
				oos.close();
				infoPane.setText("Saving trace (~" + ll.size()
						+ " events) on file " + path + " ... completed!");
				log.debug("Dumping trace to file " + path + " completed !");
				traceLabel.setText(traceLabel.getText()
						+ "\nDumping trace to file " + path + " completed !");

			} catch (Exception ex) {
				ex.printStackTrace();
			}

		}
	}

	protected void load() {
		JFileChooser fc = new JFileChooser();
		int f = fc.showOpenDialog(frame);
		if (f == JFileChooser.APPROVE_OPTION) {
			try {
				stop();
				String path = fc.getSelectedFile().getPath();
				log.debug("Loading trace from file " + path + " ...");
				infoPane.setText("Loading trace from file  " + path + " ...");
				FileInputStream fin = new FileInputStream(path);
				ObjectInputStream ois = new ObjectInputStream(fin);
				ll = (LinkedList<IEvent>) ois.readObject();
				ois.close();
				infoPane.setText("Loading trace (~" + ll.size()
						+ " events) from file " + path + " ... completed!");
				log.debug("Loading trace from file " + path + " completed !");
				printLl();
			} catch (Exception ex) {
				ex.printStackTrace();
			}

		}

	}

	protected void play() {
		if (ll != null) {
			infoPane.setText("Starting simulation....");
			Controller pdp = new Controller();
			pdp.start();
			while (!pdp.isStarted())
				;
			pl = new PlayingClient(pdp);
			pl.setTrace(ll);
			long startTime = System.nanoTime();
			pl.playTrace();
			long endTime = System.nanoTime();
			infoPane.setText("Simulation ended. Total time : "
					+ (endTime - startTime) / 1000 / 1000 + "msec");
			pdp.stop();
		}
	}

	protected void stop() {
		if ((tr != null) && (tr.isRunning())) {
			refreshThread.interrupt();
			tr.stop();
			infoPane.setText("Server stopped!");
		} else {
			infoPane.setText("Server is not running, it can't be stopped!");
		}
	}

	protected void start() {
		String port = portTextBox.getText();
		if ((port != null) && (!port.equals(""))) {
			final int portInt = Integer.valueOf(port);
			tr = new TraceRecorder();
			new Thread() {
				public void run() {
					tr.start(portInt);
				}
			}.start();
			// tr.start(portInt);

			infoPane.setText("Server started recording on port " + port);

			if (refreshThread != null)
				refreshThread.interrupt();
			refreshThread = new Thread() {
				public void run() {
					while (!isInterrupted()) {
						ll = tr.getLl();
						printLl();
						try {
							Thread.sleep(2000);
						} catch (NumberFormatException e) {
							// TODO Auto-generated catch block
							// e.printStackTrace();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							// e.printStackTrace();
						}
					}
					tr = null;
				}
			};
			refreshThread.start();
		}
	}

	protected void printLl() {
		String res = "";
		if (ll != null) {
			int n = 1;
			for (IEvent e : ll) {
				res += (n++) + " - " + e.getName() + "[" + e.getParameters()
						+ "]\n";
			}
		}
		if (ll != null)
			traceLabel.setText(res + "\n" + ll.size() + " EVENTS");
		traceLabel.setCaretPosition(traceLabel.getText().length());

	}

}
