package console;

import java.io.IOException;
import java.io.OutputStream;

public class OutputStreamHandler implements Runnable {
	private DialogConsoleEventHandler dcev;
	private OutputStream out;
	private boolean run = true;

	public OutputStreamHandler(DialogConsoleEventHandler dcev, OutputStream out) {
		this.dcev = dcev;
		this.out = out;
	}

	public void run() {
		// TODO Auto-generated method stub
		while (run) {
			String text = dcev.getTfInput().getText();
			try {
				this.out.write(text.getBytes());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
