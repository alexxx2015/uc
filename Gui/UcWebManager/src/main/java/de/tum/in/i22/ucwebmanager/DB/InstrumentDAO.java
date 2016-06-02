package de.tum.in.i22.ucwebmanager.DB;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class InstrumentDAO {
	public static List<Instrument> getAllInstruments() throws ClassNotFoundException, SQLException {
		List<Instrument> instruments = new ArrayList<Instrument>();
		Connection conn = DatabaseConnection.getConnection();
		try {
			Statement statement = conn.createStatement();
			ResultSet rs = statement.executeQuery(InstrumentTable.GETALL);
			while (rs.next()) {
				Instrument instrument = new Instrument(rs.getInt(InstrumentTable.COLUMN_ID),
						rs.getString(InstrumentTable.COLUMN_NAME),
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

	public static Instrument getIntrumentByID(int id) throws ClassNotFoundException, SQLException {
		Instrument instrument = null;
		Connection conn = DatabaseConnection.getConnection();
		try {
			Statement statement = conn.createStatement();
			ResultSet rs = statement.executeQuery("SELECT * FROM " + InstrumentTable.TABLE + " WHERE "
					+ InstrumentTable.COLUMN_ID + " = " + String.valueOf(id));
			if (rs.next()) {
				instrument = new Instrument(rs.getInt(InstrumentTable.COLUMN_ID),
						rs.getString(InstrumentTable.COLUMN_NAME),
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

	public static void saveToDB(Instrument instrument) throws ClassNotFoundException, SQLException {
		Connection conn = DatabaseConnection.getConnection();
		try {
			Statement statement = conn.createStatement();
			String s = "INSERT INTO " + InstrumentTable.TABLE + "("
					+ InstrumentTable.COLUMN_NAME + ","
					+ InstrumentTable.COULMN_REPORT_ID + ") VALUES('"
					+ instrument.getName() + "','"
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
