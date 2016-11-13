//package de.tum.in.i22.ucwebmanager.view;
//
//import java.io.BufferedReader;
//import java.io.File;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.sql.Connection;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.sql.Statement;
//import java.util.Properties;
//
//import com.vaadin.server.VaadinServlet;
//
//public class StaticAnalyser {
//	//String Projectlocation = new File("").getAbsolutePath();
//	String Projectlocation=VaadinServlet.getCurrent().getServletContext().getRealPath("/");
//	String databasedriver;
//	Connection conn = null;
//	Object mainObj;
//
//	public void analyseFile(String Applicationname, final String fileName, final String DbFileName, Object mainobj) {
//		try {
//			mainObj = mainobj;
//			Projectlocation = Projectlocation.replace('\\', '/');
//			
//			final String xmllocation = Projectlocation;
//			Properties prop = new Properties();
//
//			String propFileName = "Config.properties";
//
//			InputStream inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);
//			prop.load(inputStream);
//			String dbpath = Projectlocation + prop.getProperty("databasepath");
//			databasedriver=prop.getProperty("databasedriver");
//			String connectionstring = databasedriver + dbpath;
////			DatabaseConnection db = new DatabaseConnection();
////			conn = db.Createconnection(connectionstring);
//			final Statement statement = conn.createStatement();
//
//			// Run the child process in a new thread
//			Thread thread = new Thread() {
//				public void run() {
//					try {
//						ProcessBuilder pb = new ProcessBuilder("java", "-jar", xmllocation + "/flowanalyzer.jar",
//								fileName);
//						Process p = pb.start();
//
//						int count;
//						ResultSet rs = statement.executeQuery(
//								"SELECT count(*) AS rowcount1 FROM Staticanalysis WHERE Name='" + DbFileName + "'");
//						count = rs.getInt("rowcount1");
//						if (count == 0)
//							statement.executeUpdate("insert into Staticanalysis values('" + DbFileName
//									+ "',DateTime('now'), 'Static Analysis Started')");
//						else if (count == 1)
//							statement.executeUpdate(
//									"UPDATE Staticanalysis SET status ='Static analysis Started' WHERE Name='"
//											+ DbFileName + "'");
//						statement.close();
//						/*
//						 * } catch (SQLException e) { // TODO Auto-generated
//						 * catch block e.printStackTrace(); }
//						 */
//
//						BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
//						String s = "";
//						while ((s = in.readLine()) != null) {
//							System.out.println(s);
//						}
//					
//						int status = p.waitFor();
//
//						if (status == 0) {
//							// try {
//							// statement = conn.createStatement();
//							// try {
//							// count;
//							rs = statement.executeQuery(
//									"SELECT count(*) AS rowcount1 FROM Staticanalysis WHERE Name='" + DbFileName + "'");
//							count = rs.getInt("rowcount1");
//							if (count == 0)
//								statement.executeUpdate("insert into Staticanalysis values('" + DbFileName
//										+ "',DateTime('now'), 'Static Analysis Done')");
//							else if (count == 1)
//								statement.executeUpdate(
//										"UPDATE Staticanalysis SET status ='Static analysis Done' WHERE Name='"
//												+ DbFileName + "'");
//							statement.close();
//							/*
//							 * } catch (SQLException e) { // TODO Auto-generated
//							 * catch block e.printStackTrace(); }
//							 */
//							statement.close();
////							mainObj.navigator.navigateTo("/Refresh");
//						} else if (status == 1) {
//							// try {
//							// count;
//							rs = statement.executeQuery("SELECT count(*)  AS rowcount1 FROM Staticanalysis WHERE Name='"
//									+ DbFileName + "'");
//							count = rs.getInt("rowcount1");
//							if (count == 0)
//								statement.executeUpdate("insert into Staticanalysis values('" + DbFileName
//										+ "',DateTime('now'), 'Static Analysis Error')");
//							else if (count == 1)
//								statement.executeUpdate(
//										"UPDATE Staticanalysis SET status ='Static analysis Error' WHERE Name='"
//												+ DbFileName + "'");
//
//							/*
//							 * } catch (SQLException e) { // TODO Auto-generated
//							 * catch block e.printStackTrace(); }
//							 */
////							mainObj.navigator.navigateTo("/Refresh");
//						}
//						// child process ends
//
//						System.out.println("Exited status: " + status);
//					}
//
//					catch (Exception ex) {
//
//					}
//				}
//			};
//
//			thread.start();
//			// thread.getId();
//			// thread.interrupt();
//
//		} catch (IOException | SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
//
//}
