package map.database;

import io.github.cdimascio.dotenv.Dotenv;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Gestisce l'accesso al DB per la lettura dei dati di training
 * @author Map Tutor
 */
public class DbAccess {
	/**
	 * DRIVER_CLASS_NAME String che presenta il classpath del driver per la connessione con un database
	 */

	private final String DRIVER_CLASS_NAME = "com.mysql.cj.jdbc.Driver";
	/**
	 * DBMS String che rappresenta il database management system
	 */
	private final String DBMS = "jdbc:mysql";
	/**
	 * SERVER String che rappresenta l'indirizzo del database
	 */
	private String SERVER;
	/**
	 * PORT int che indica la porta d'accesso
	 */
	private int PORT;
	/**
	 * DATABASE String che indica il nome del database
	 */
	private String DATABASE;
	/**
	 * USER_ID String che indica l'utente di accesso al db
	 * PASSWORD String che indica la password di accesso al db
	 */
	private String USER_ID;
	private String PASSWORD;
	/**
	 * conn Connection oggetto che gestisce la connessione con il db sql
	 */
	private Connection conn;

	/**
	 * Inizializza una connessione al DB
	 */
	public DbAccess() throws DatabaseConnectionException{
		Dotenv config = Dotenv.configure().load();
		SERVER = config.get("SERVER");
		PORT = Integer.parseInt(config.get("PORT"));
		DATABASE = config.get("DATABASE");
		USER_ID = config.get("USER_ID");
		PASSWORD = config.get("PASSWORD");

		String connectionString =  DBMS + "://" + SERVER + ":" + PORT + "/" + DATABASE
				+ "?user=" + USER_ID + "&password=" + PASSWORD + "&serverTimezone=UTC";
		System.out.println("\n\n\nSQL:\n" + connectionString);

		try {
			conn = DriverManager.getConnection(connectionString, USER_ID, PASSWORD);
			
		} catch (SQLException e) {
			System.out.println("Impossibile connettersi al DB");
			e.printStackTrace();
			throw new DatabaseConnectionException(e.toString());
		}
		
	}

	/**
	 * @return la connessione con il db
	 */
	public  Connection getConnection(){
		return conn;
	}

	/**
	 * Termina la connessione con il DB
	 */
	public  void closeConnection() {
		try {
			conn.close();
		} catch (SQLException e) {
			System.out.println("Impossibile chiudere la connessione");
		}
	}

}
