package de.htwberlin.mautProzedur;

import de.htwberlin.exceptions.AlreadyCruisedException;
import de.htwberlin.exceptions.InvalidVehicleDataException;
import de.htwberlin.exceptions.UnkownVehicleException;

/**
 * Die Schnittstelle enthaelt eine Methode fuer den Aufruf von
 * Stored-Procedures.
 * 
 * @author Patrick Dohmeier
 */
public interface IMautProzedur {

	/***
	 * Die Methode realisiert einen Algorithmus, der die �bermittelten
	 * Fahrzeugdaten mit der Datenbank auf Richtigkeit �berpr�ft und f�r einen
	 * mautpflichtigen Streckenabschnitt die zu zahlende Maut f�r ein Fahrzeug
	 * im Automatischen Verfahren berechnet.
	 * 
	 * Zuvor wird �berpr�ft, ob das Fahrzeug registriert ist und �ber ein
	 * eingebautes Fahrzeugger�t verf�gt und die �bermittelten Daten des
	 * Kontrollsystems korrekt sind. Bei Fahrzeugen im Manuellen Verfahren wird
	 * dar�ber hinaus gepr�ft, ob es noch offene Buchungen f�r den Mautabschnitt
	 * gibt oder eine Doppelbefahrung aufgetreten ist. Besteht noch eine offene
	 * Buchung f�r den Mautabschnitt, so wird diese Buchung f�r das Fahrzeug auf
	 * abgeschlossen gesetzt.
	 * 
	 * Sind die Daten des Fahrzeugs im Automatischen Verfahren korrekt, wird
	 * anhand der Mautkategorie (die sich aus der Achszahl und der
	 * Schadstoffklasse des Fahrzeugs zusammensetzt) und der Mautabschnittsl�nge
	 * die zu zahlende Maut berechnet, in der Mauterhebung gespeichert und
	 * letztendlich zur�ckgegeben.
	 * 
	 * 
	 * @param mautAbschnitt
	 *            - identifiziert einen mautpflichtigen Abschnitt
	 * @param achszahl
	 *            - identifiziert die Anzahl der Achsen f�r das Fahrzeug das
	 *            durch ein Kontrollsystem erfasst worden ist
	 * @param kennzeichen
	 *            - idenfiziert das amtliche Kennzeichen des Fahrzeugs das durch
	 *            das Kontrollsystem erfasst worden ist
	 * @throws UnkownVehicleException
	 *             - falls das Fahrzeug weder registriert ist, noch eine offene
	 *             Buchung vorliegt
	 * @throws InvalidVehicleDataException
	 *             - falls Daten des Kontrollsystems nicht mit den hinterlegten
	 *             Daten in der Datenbank �bereinstimmt
	 * @throws AlreadyCruisedException
	 *             - falls eine Doppelbefahrung f�r Fahrzeuge im Manuellen
	 *             Verfahren vorliegt
	 * @return die berechnete Maut f�r das Fahrzeug im Automatischen Verfahren
	 *         auf dem Streckenabschnitt anhand der Fahrzeugdaten
	 */

	void berechneMaut(int mautAbschnitt, int achszahl, String kennzeichen)
			throws UnkownVehicleException, InvalidVehicleDataException,
			AlreadyCruisedException;

}
