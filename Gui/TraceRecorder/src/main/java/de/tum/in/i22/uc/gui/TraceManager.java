package de.tum.in.i22.uc.gui;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.LinkedList;

import javafx.stage.FileChooser;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.border.LineBorder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.datatypes.interfaces.IEvent;

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

	private JTextField portTextBox;
	private JTextField traceLabel;

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
		panel_6.setLayout(new FlowLayout(FlowLayout.LEADING, 5, 5));

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

		JTextPane infoPane = new JTextPane();
		infoPane.setBackground(Color.LIGHT_GRAY);
		panel_3.add(infoPane);

		traceLabel = new JTextField("Trace");
		traceLabel.setEnabled(false);
		traceLabel.setEditable(false);
		frame.getContentPane().add(traceLabel);

		JPanel panel = new JPanel();
		frame.getContentPane().add(panel);
		panel.setLayout(new GridLayout(0, 1, 0, 0));

		JPanel panel_2 = new JPanel();
		panel.add(panel_2);
		panel_2.setLayout(new GridLayout(0, 3, 0, 0));

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

	protected void save() {
		JFileChooser fc = new JFileChooser();
		int f = fc.showSaveDialog(frame);
		if (f == JFileChooser.APPROVE_OPTION) {
			String path = fc.getSelectedFile().getPath();
			try {
				log.debug("Dumping trace to file " + path + " ...");
				traceLabel.setText(traceLabel.getText()
						+ "\nDumping trace to file " + path + " ...");
				FileOutputStream fout = new FileOutputStream(path);
				ObjectOutputStream oos = new ObjectOutputStream(fout);
				oos.writeObject(ll);
				oos.close();
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
			tr.saveTrace(fc.getSelectedFile().getPath());
		}
	}

	protected void play() {
		// TODO Auto-generated method stub

	}

	protected void stop() {
		if ((tr != null) && (tr.isRunning())) {
			tr.stop();
			refreshThread.interrupt();
		}
	}

	protected void start() {
		String port = portTextBox.getText();
		if ((port != null) && (!port.equals(""))) {
			int portInt = Integer.valueOf(port);
			tr = new TraceRecorder();
			tr.start(portInt);
			ll = tr.getLl();

			if (refreshThread != null)
				refreshThread.interrupt();
			refreshThread = new Thread() {
				public void run() {
					while (!isInterrupted()) {
						String res = "";
						for (IEvent e : ll) {
							res += e + "\n";
						}
						traceLabel.setText("REFRESHED!\n" + res);
						try {
							Thread.sleep(3000);
						} catch (NumberFormatException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			};
			refreshThread.start();
		}
	}

}
