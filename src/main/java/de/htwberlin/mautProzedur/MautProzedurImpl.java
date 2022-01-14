package de.htwberlin.mautProzedur;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import de.htwberlin.exceptions.AlreadyCruisedException;
import de.htwberlin.exceptions.DaoException;
import de.htwberlin.exceptions.InvalidVehicleDataException;
import de.htwberlin.exceptions.UnkownVehicleException;
import de.htwberlin.utils.JdbcUtils;

/**
 * Diese Klasse ruft die gespeicherte Prozedur BERECHNEMAUT im Package
 * MAUT_SERVICE auf und pr�ft gem�� der Paket-Spezifikation die Bedingungen zur
 * Mauterhebung. Sind alle Vorraussetzungen erf�llt wird die Maut f�r ein
 * bestimmtes Fahrzeug auf einem Mautabschnitt erhoben und gespeichert.
 **/

public class MautProzedurImpl implements IMautProzedur {

	private Connection connection;

	public void setConnection(Connection connection) {
		this.connection = connection;
	}

	protected Connection getConnection() {
		if (connection == null)
			throw new IllegalStateException(
					"Connection has not been set on DAO before usage");
		return connection;
	}

	@Override
	public void berechneMaut(int mautAbschnitt, int achszahl, String kennzeichen)
			throws UnkownVehicleException, InvalidVehicleDataException,
			AlreadyCruisedException {
		CallableStatement cstmt = null;
		try {
			cstmt = getConnection().prepareCall(
					"CALL maut_service.BERECHNEMAUT(?,?,?)");
			cstmt.setInt(1, mautAbschnitt);
			cstmt.setInt(2, achszahl);
			cstmt.setString(3, kennzeichen);
			cstmt.execute();
		} catch (SQLException exp) {
			if (exp.getSQLState().equals("72000")
					&& exp.getErrorCode() == 20001) {
				throw new UnkownVehicleException(exp);
			} else if (exp.getSQLState().equals("72000")
					&& exp.getErrorCode() == 20002) {
				throw new InvalidVehicleDataException(exp.getMessage());
			} else if (exp.getSQLState().equals("72000")
					&& exp.getErrorCode() == 20003) {
				throw new AlreadyCruisedException(exp.getMessage());
			} else {
				throw new DaoException(exp);
			}
		} finally {
			JdbcUtils.closeStatement(cstmt);
		}

	}

}
