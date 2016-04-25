package de.tum.in.i22.ucwebmanager.DB;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class StaticAnalysisConfigDAO {

	public static List<StaticAnalysisConfig> getAllConfigs() throws ClassNotFoundException, SQLException {
		List<StaticAnalysisConfig> configs = new ArrayList<StaticAnalysisConfig>();
		Connection conn = DatabaseConnection.getConnection();
		try {
			Statement statement = conn.createStatement();
			ResultSet rs = statement.executeQuery(StaticAnalysisConfigTable.GETALL);
			while (rs.next()) {
				StaticAnalysisConfig config = new StaticAnalysisConfig(rs.getInt(StaticAnalysisConfigTable.COLUMN_ID),
						rs.getString(StaticAnalysisConfigTable.COLUMN_NAME),
						rs.getString(StaticAnalysisConfigTable.COLUMN_PATH),
						rs.getInt(StaticAnalysisConfigTable.COULMN_APP_ID));
				configs.add(config);
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
		return configs;
	}

	public static StaticAnalysisConfig getConfigByID(int id) throws ClassNotFoundException, SQLException {
		StaticAnalysisConfig config = null;
		Connection conn = DatabaseConnection.getConnection();
		try {
			Statement statement = conn.createStatement();
			ResultSet rs = statement.executeQuery("SELECT * FROM " + StaticAnalysisConfigTable.TABLE + " WHERE "
					+ StaticAnalysisConfigTable.COLUMN_ID + " = " + String.valueOf(id));
			if (rs.next()) {
				config = new StaticAnalysisConfig(rs.getInt(StaticAnalysisConfigTable.COLUMN_ID),
						rs.getString(StaticAnalysisConfigTable.COLUMN_NAME),
						rs.getString(StaticAnalysisConfigTable.COLUMN_PATH),
						rs.getInt(StaticAnalysisConfigTable.COULMN_APP_ID));

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
		return config;
	}

	public static void saveToDB(StaticAnalysisConfig config) throws ClassNotFoundException, SQLException {
		Connection conn = DatabaseConnection.getConnection();
		try {
			Statement statement = conn.createStatement();
			String s = "INSERT INTO " + StaticAnalysisConfigTable.TABLE + "("
					+ StaticAnalysisConfigTable.COLUMN_NAME + ","
					+ StaticAnalysisConfigTable.COLUMN_PATH + ","
					+ StaticAnalysisConfigTable.COULMN_APP_ID + ") VALUES('"
					+ config.getName() + "','"
					+ config.getPath() + "',"
					+ String.valueOf(config.getApp_id()) + ")";
			statement.executeUpdate(s);
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
	}
}
