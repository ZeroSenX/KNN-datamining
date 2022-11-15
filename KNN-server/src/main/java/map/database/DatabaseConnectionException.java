package map.database;

/**
 * Eccezione che gestisce eventuali errori di connessione al database
 */
public class DatabaseConnectionException extends Exception {
	/**
	 * Costruttore della classe
	 * @param msg String che identifica l'errore
	 */
	DatabaseConnectionException(String msg){
		super(msg);
	}
}
