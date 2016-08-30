package test;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class TestOutputStream {
//	public static void main(String[] args) {
//		ProcessBuilder pb = new ProcessBuilder("ls", "-la");
//		try {
//			Process process = pb.start();
//			String result = getIStream(process);
//			InputStream error = process.getErrorStream();
//			Map<Integer, String> message = new HashMap<Integer, String>();
//			message.put(1, result);
//			System.out.println(message);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
//	private static String getIStream(Process process){
//		BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
//		StringBuilder builder = new StringBuilder();
//		String line = null;
//		try {
//			while ((line = reader.readLine()) != null) {
//				builder.append(line);
//				builder.append(System.getProperty("line.separator"));
//			}
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		String result = builder.toString();
//		return result;
//	}

	
	public static void main(String[] args) {

		TestOutputStream obj = new TestOutputStream();

		String domainName = "google.com";
		
		//in mac oxs
		String command = "swig FullSWOF2D.i " + domainName;
		//in windows
		//String command = "ping -n 3 " + domainName;
		
		String output = obj.executeCommand(command);

		System.out.println(output);

	}

	private String executeCommand(String command) {

		StringBuffer output = new StringBuffer();

		Process p;
		try {
			p = Runtime.getRuntime().exec(command);
			p.waitFor();
			BufferedReader reader = 
                            new BufferedReader(new InputStreamReader(p.getErrorStream()));

                        String line = "";			
			while ((line = reader.readLine())!= null) {
				output.append(line + "\n");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return output.toString();

	}
}
