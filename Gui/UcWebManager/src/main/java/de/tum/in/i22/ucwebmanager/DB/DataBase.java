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
		      statement.setQueryTimeout(30);  // set timeout to 30 sec.	      
		      statement.executeUpdate("drop table if exists t_app");
		      statement.executeUpdate("create table t_app ("
		      						+ "id integer primary key autoincrement, "
		      						+ "s_name string, i_hashcode integer,"
		      						+ "s_status string)");
		      statement.executeUpdate("drop table if exists t_staticanalysis_config");
		      statement.executeUpdate("create table t_staticanalysis_config ("
		      						+ "id integer primary key autoincrement,"
		      						+ "s_name string,"
		      						+ "i_app_id	integer,"
		      						+ "FOREIGN KEY (i_app_id) REFERENCES t_app(id))");
		      statement.executeUpdate("drop table if exists t_staticanalysis_run");
		      statement.executeUpdate("create table t_staticanalysis_run ("
						+ "id integer primary key autoincrement,"
						+ "s_name string,"
						+ "i_config_id	integer,"
						+ "FOREIGN KEY (i_config_id) REFERENCES t_staticanalysis_config(id))");
		      statement.executeUpdate("drop table if exists t_instrumentation");
		      /**  CREATE TABLE t_instrumentation (
		      id integer primary key autoincrement,
		      s_name string,
		      i_report_id integer,
		      FOREIGN KEY (i_report_id) REFERENCES t_staticanalysis_report(id))"
		      **/
		      statement.executeUpdate("CREATE TABLE t_instrumentation ("
		    		  + "id integer primary key autoincrement,"
		    		  + "s_name string,"
		    		  + "i_report_id integer,"
		    		  + "FOREIGN KEY (i_report_id) REFERENCES t_staticanalysis_run(id))");
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
