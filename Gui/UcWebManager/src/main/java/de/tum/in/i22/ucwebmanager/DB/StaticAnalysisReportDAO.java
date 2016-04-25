package de.tum.in.i22.ucwebmanager.DB;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class StaticAnalysisReportDAO {
	public static List<StaticAnalysisReport> getAllReports() throws ClassNotFoundException, SQLException {
		List<StaticAnalysisReport> reports = new ArrayList<StaticAnalysisReport>();
		Connection conn = DatabaseConnection.getConnection();
		try {
			Statement statement = conn.createStatement();
			ResultSet rs = statement.executeQuery(StaticAnalysisReportTable.GETALL);
			while (rs.next()) {
				StaticAnalysisReport report = new StaticAnalysisReport(rs.getInt(StaticAnalysisReportTable.COLUMN_ID),
						rs.getString(StaticAnalysisReportTable.COLUMN_NAME),
						rs.getString(StaticAnalysisReportTable.COLUMN_PATH),
						rs.getInt(StaticAnalysisReportTable.COULMN_CONFIG_ID));
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

	public static StaticAnalysisReport getReportByID(int id) throws ClassNotFoundException, SQLException {
		StaticAnalysisReport config = null;
		Connection conn = DatabaseConnection.getConnection();
		try {
			Statement statement = conn.createStatement();
			ResultSet rs = statement.executeQuery("SELECT * FROM " + StaticAnalysisReportTable.TABLE + " WHERE "
					+ StaticAnalysisReportTable.COLUMN_ID + " = " + String.valueOf(id));
			if (rs.next()) {
				config = new StaticAnalysisReport(rs.getInt(StaticAnalysisReportTable.COLUMN_ID),
						rs.getString(StaticAnalysisReportTable.COLUMN_NAME),
						rs.getString(StaticAnalysisReportTable.COLUMN_PATH),
						rs.getInt(StaticAnalysisReportTable.COULMN_CONFIG_ID));

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

	public static void saveToDB(StaticAnalysisReport report) throws ClassNotFoundException, SQLException {
		Connection conn = DatabaseConnection.getConnection();
		try {
			Statement statement = conn.createStatement();
			String s = "INSERT INTO " + StaticAnalysisReportTable.TABLE + "("
					+ StaticAnalysisReportTable.COLUMN_NAME + ","
					+ StaticAnalysisReportTable.COLUMN_PATH + ","
					+ StaticAnalysisReportTable.COULMN_CONFIG_ID + ") VALUES('"
					+ report.getName() + "','"
					+ report.getPath() + "',"
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
