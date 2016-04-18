package de.tum.in.i22.ucwebmanager.DB;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class InstrumentDAO {
	public static List<Instrument> getAllInstruments() throws ClassNotFoundException {
		List<Instrument> instruments = new ArrayList<Instrument>();
		Class.forName("org.sqlite.JDBC");
		Connection conn = null;
		try {
			conn = DriverManager.getConnection("jdbc:sqlite:UcWebManager.db");
			Statement statement = conn.createStatement();
			ResultSet rs = statement.executeQuery(InstrumentTable.GETALL);
			while (rs.next()) {
				Instrument instrument = new Instrument(rs.getInt(InstrumentTable.COLUMN_ID),
						rs.getString(InstrumentTable.COLUMN_NAME),
						rs.getString(InstrumentTable.COLUMN_PATH),
						rs.getInt(InstrumentTable.COULMN_REPORT_ID));
				instruments.add(instrument);
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
		return instruments;
	}

	public static Instrument getIntrumentByID(int id) throws ClassNotFoundException {
		Instrument instrument = null;
		Class.forName("org.sqlite.JDBC");
		Connection conn = null;
		try {
			conn = DriverManager.getConnection("jdbc:sqlite:UcWebManager.db");
			Statement statement = conn.createStatement();
			ResultSet rs = statement.executeQuery("SELECT * FROM " + InstrumentTable.TABLE + "WHERE "
					+ InstrumentTable.COLUMN_ID + " = " + String.valueOf(id));
			if (rs.next()) {
				instrument = new Instrument(rs.getInt(InstrumentTable.COLUMN_ID),
						rs.getString(InstrumentTable.COLUMN_NAME),
						rs.getString(InstrumentTable.COLUMN_PATH),
						rs.getInt(InstrumentTable.COULMN_REPORT_ID));

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
		return instrument;
	}

	public static void saveToDB(Instrument instrument) throws ClassNotFoundException {
		Class.forName("org.sqlite.JDBC");
		Connection conn = null;
		try {
			conn = DriverManager.getConnection("jdbc:sqlite:UcWebManager.db");
			Statement statement = conn.createStatement();
			String s = "INSERT INTO " + InstrumentTable.TABLE + "("
					+ InstrumentTable.COLUMN_NAME + ","
					+ InstrumentTable.COLUMN_PATH + ","
					+ InstrumentTable.COULMN_REPORT_ID + ") VALUES('"
					+ instrument.getName() + "','"
					+ instrument.getPath() + "',"
					+ String.valueOf(instrument.getReport_id()) + ")";
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
