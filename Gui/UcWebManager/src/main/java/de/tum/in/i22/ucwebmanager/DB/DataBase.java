package de.tum.in.i22.ucwebmanager.DB;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DataBase {
	public static void main(String[] args) throws ClassNotFoundException{
		Class.forName("org.sqlite.JDBC");
		
		Connection conn = null;
		try{
			conn = DriverManager.getConnection("jdbc:sqlite:UcWebManager.db");
			Statement statement = conn.createStatement();
		      //statement.setQueryTimeout(30);  // set timeout to 30 sec.	      
		      statement.executeUpdate("drop table if exists t_app");
		      statement.executeUpdate("create table t_app ("
		      						+ "id integer primary key autoincrement, "
		      						+ "s_name string, i_hashcode integer, s_path string,"
		      						+ "s_status string)");
		      statement.executeUpdate("create table t_staticanalysis ("
		      						+ "id integer primary key autoincrement,"
		      						+ "s_name string, s_path string,"
		      						+ "i_type integer," 			//0: configuration, 1: report.
		      						+ "i_app_id	integer,"
		      						+ "FOREIGN KEY (i_app_id) REFERENCES t_app(id))");
		}catch (SQLException e){
			System.err.println(e.getMessage());
		}
		finally
	    {
	      try
	      {
	        if(conn != null)
	          conn.close();
	      }
	      catch(SQLException e)
	      {
	        // connection close failed.
	        System.err.println(e);
	      }
	    }
	}
}
