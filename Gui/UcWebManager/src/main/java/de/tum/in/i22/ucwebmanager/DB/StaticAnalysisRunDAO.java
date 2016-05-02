package de.tum.in.i22.ucwebmanager.DB;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class StaticAnalysisRunDAO {
	public static List<StaticAnalysisRun> getAllReports() throws ClassNotFoundException, SQLException {
		List<StaticAnalysisRun> reports = new ArrayList<StaticAnalysisRun>();
		Connection conn = DatabaseConnection.getConnection();
		try {
			Statement statement = conn.createStatement();
			ResultSet rs = statement.executeQuery(StaticAnalysisRunTable.GETALL);
			while (rs.next()) {
				StaticAnalysisRun report = new StaticAnalysisRun(rs.getInt(StaticAnalysisRunTable.COLUMN_ID),
						rs.getString(StaticAnalysisRunTable.COLUMN_NAME),
						rs.getInt(StaticAnalysisRunTable.COULMN_CONFIG_ID));
				reports.add(report);
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
		return reports;
	}

	public static StaticAnalysisRun getReportByID(int id) throws ClassNotFoundException, SQLException {
		StaticAnalysisRun config = null;
		Connection conn = DatabaseConnection.getConnection();
		try {
			Statement statement = conn.createStatement();
			ResultSet rs = statement.executeQuery("SELECT * FROM " + StaticAnalysisRunTable.TABLE + " WHERE "
					+ StaticAnalysisRunTable.COLUMN_ID + " = " + String.valueOf(id));
			if (rs.next()) {
				config = new StaticAnalysisRun(rs.getInt(StaticAnalysisRunTable.COLUMN_ID),
						rs.getString(StaticAnalysisRunTable.COLUMN_NAME),
						rs.getInt(StaticAnalysisRunTable.COULMN_CONFIG_ID));

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

	public static void saveToDB(StaticAnalysisRun report) throws ClassNotFoundException, SQLException {
		Connection conn = DatabaseConnection.getConnection();
		try {
			Statement statement = conn.createStatement();
			String s = "INSERT INTO " + StaticAnalysisRunTable.TABLE + "("
					+ StaticAnalysisRunTable.COLUMN_NAME + ","
					+ StaticAnalysisRunTable.COULMN_CONFIG_ID + ") VALUES('"
					+ report.getName() + "',"
					+ String.valueOf(report.getConfig_id()) + ")";
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
