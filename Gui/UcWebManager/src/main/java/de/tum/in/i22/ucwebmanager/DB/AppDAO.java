package de.tum.in.i22.ucwebmanager.DB;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class AppDAO {
	private Connection conn;
	private String[] allColumns = {AppTable.COLUMN_ID,AppTable.COLUMN_NAME,AppTable.COLUMN_HASHCODE,AppTable.COLUMN_PATH,AppTable.COLUMN_STATUS};
	public AppDAO() throws ClassNotFoundException{
		Class.forName("org.sqlite.JDBC");
		try{
			conn = DriverManager.getConnection("jdbc:sqlite:UcWebManager.db");
		} catch(SQLException e){
			System.err.println(e.getMessage());
		}
	}
	
	public List<App> getAllApps(){
		List<App> apps = new ArrayList<App>();
		try {
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
		}
		return apps;
	}
}
