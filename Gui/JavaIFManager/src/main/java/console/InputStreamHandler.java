package console;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javafx.application.Platform;

public class InputStreamHandler implements Runnable {
	private DialogConsoleEventHandler dcev;
	private InputStream in;

	public InputStreamHandler(DialogConsoleEventHandler dcev, InputStream in) {
		this.dcev = dcev;
		this.in = in;
	}

	public void run() {
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		String line;
		while (!Thread.currentThread().isInterrupted()) {
			try {
				while ((line = reader.readLine()) != null) {
					Platform.runLater(new MyRunnable(line));
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	class MyRunnable implements Runnable {
		String line;

		public MyRunnable(String line) {
			this.line = line;
		}

		public void run() {
			dcev.getTaOutput().appendText(
					System.getProperty("line.separator") + line);

			dcev.getTaOutput().setScrollTop(Double.MAX_VALUE);

		}
	}

}
