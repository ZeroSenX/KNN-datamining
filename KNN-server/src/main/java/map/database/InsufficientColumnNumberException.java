package map.database;

/**
 * Eccezione che gestisce incongruenze tra la tabella del database e il trainingSet
 */
public class InsufficientColumnNumberException extends Exception {
	/**
	 * Costruttore della classe
	 * @param msg String che identifica l'errore
	 */
	public InsufficientColumnNumberException(String msg) {super(msg);}
}
