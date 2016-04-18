package de.tum.in.i22.ucwebmanager.DB;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class AppDAO {
	private String[] allColumns = {AppTable.COLUMN_ID,AppTable.COLUMN_NAME,AppTable.COLUMN_HASHCODE,AppTable.COLUMN_PATH,AppTable.COLUMN_STATUS};

	
	public static List<App> getAllApps() throws ClassNotFoundException{
		List<App> apps = new ArrayList<App>();
		Class.forName("org.sqlite.JDBC");
		Connection conn = null;
		try {
			conn = DriverManager.getConnection("jdbc:sqlite:UcWebManager.db");
			Statement statement = conn.createStatement();
			ResultSet rs = statement.executeQuery(AppTable.GETALL);
			while (rs.next()){
				App app = new App(rs.getInt(AppTable.COLUMN_ID),
								  rs.getString(AppTable.COLUMN_NAME),
								  rs.getInt(AppTable.COLUMN_HASHCODE),
								  rs.getString(AppTable.COLUMN_PATH),
								  rs.getString(AppTable.COLUMN_STATUS));
				apps.add(app);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				// connection close failed.
				System.err.println(e);
			}
	}
		return apps;
	}
	public static App getAppById(int id) throws ClassNotFoundException{
		Class.forName("org.sqlite.JDBC");
		Connection conn = null;
		App app = null;
		try{
			
			conn = DriverManager.getConnection("jdbc:sqlite:UcWebManager.db");
			Statement statement = conn.createStatement();
			String s = "SELECT * FROM "
					 + AppTable.TABLE_APP 
					 + "WHERE"
					 + AppTable.COLUMN_ID
					 + " = " + String.valueOf(id);
			ResultSet rs = statement.executeQuery(s);
			if (rs.next()){
				app = new App(rs.getInt(AppTable.COLUMN_ID),
						  rs.getString(AppTable.COLUMN_NAME),
						  rs.getInt(AppTable.COLUMN_HASHCODE),
						  rs.getString(AppTable.COLUMN_PATH),
						  rs.getString(AppTable.COLUMN_STATUS));;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				// connection close failed.
				System.err.println(e);
			}
	}
		return app;

	}
	public static void saveToDB(App app) throws ClassNotFoundException{
		Class.forName("org.sqlite.JDBC");
		Connection conn = null;
		try{
			
			conn = DriverManager.getConnection("jdbc:sqlite:UcWebManager.db");
			Statement statement = conn.createStatement();
			String s = "INSERT INTO "+ AppTable.TABLE_APP+
					"("+ AppTable.COLUMN_NAME+","
					   + AppTable.COLUMN_HASHCODE+","
					   + AppTable.COLUMN_PATH+","
					   + AppTable.COLUMN_STATUS+") values('"
					 + app.getName()+"',"
					 + String.valueOf(app.getHashCode())+",'"
					 + app.getPath()+"','"
					 + app.getStatus()+"')";
			statement.executeUpdate(s);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				// connection close failed.
				System.err.println(e);
			}
	}
	}
	
}
