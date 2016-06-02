package de.tum.in.i22.ucwebmanager.DB;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class AppDAO {
	private String[] allColumns = {AppTable.COLUMN_ID,AppTable.COLUMN_NAME,AppTable.COLUMN_HASHCODE,AppTable.COLUMN_STATUS};

	
	public static List<App> getAllApps() throws ClassNotFoundException, SQLException{
		List<App> apps = new ArrayList<App>();

		Connection conn = DatabaseConnection.getConnection();
		try {

			Statement statement = conn.createStatement();
			ResultSet rs = statement.executeQuery(AppTable.GETALL);
			while (rs.next()){
				App app = new App(rs.getInt(AppTable.COLUMN_ID),
								  rs.getString(AppTable.COLUMN_NAME),
								  rs.getString(AppTable.COLUMN_HASHCODE),
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
	public static App getAppById(int id) throws ClassNotFoundException, SQLException{
		Connection conn = DatabaseConnection.getConnection();
		App app = null;
		try{
			
			Statement statement = conn.createStatement();
			String s = "SELECT * FROM "
					 + AppTable.TABLE_APP 
					 + " WHERE "
					 + AppTable.COLUMN_ID
					 + " = " + String.valueOf(id);
			ResultSet rs = statement.executeQuery(s);
			if (rs.next()){
				app = new App(rs.getInt(AppTable.COLUMN_ID),
						  rs.getString(AppTable.COLUMN_NAME),
						  rs.getString(AppTable.COLUMN_HASHCODE),
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
	public static App getAppByHashCode(String hashCode) throws ClassNotFoundException, SQLException{
		Connection conn = DatabaseConnection.getConnection();
		App app = null;
		try{
			
			Statement statement = conn.createStatement();
			String s = "SELECT * FROM "
					 + AppTable.TABLE_APP 
					 + " WHERE "
					 + AppTable.COLUMN_HASHCODE
					 + " = '" + hashCode + "'";
			ResultSet rs = statement.executeQuery(s);
			if (rs.next()){
				app = new App(rs.getInt(AppTable.COLUMN_ID),
						  rs.getString(AppTable.COLUMN_NAME),
						  rs.getString(AppTable.COLUMN_HASHCODE),
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
	public static void saveToDB(App app) throws ClassNotFoundException, SQLException{
		Connection conn = DatabaseConnection.getConnection();
		try{

			Statement statement = conn.createStatement();
			String s = "INSERT INTO "+ AppTable.TABLE_APP+
					"("+ AppTable.COLUMN_NAME+","
					   + AppTable.COLUMN_HASHCODE+","
					   + AppTable.COLUMN_STATUS+") values('"
					 + app.getName()+"', '"
					 + app.getHashCode()+"' ,'"
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
	
	public static void updateStatus(App app)throws ClassNotFoundException, SQLException{
		Connection conn = DatabaseConnection.getConnection();
		try{

			Statement statement = conn.createStatement();
			String s = "UPDATE "+ AppTable.TABLE_APP 
					+ " SET " + AppTable.COLUMN_STATUS + "= '" + app.getStatus() 
					+ "' WHERE " + AppTable.COLUMN_ID + " = " + String.valueOf(app.getId());
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
