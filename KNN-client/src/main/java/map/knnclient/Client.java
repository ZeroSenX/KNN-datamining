package map.knnclient;


import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Classe che gestisce la comunicazione con il server
 */
public class Client {
	/**
	 * socket Socket di comunicazione tra il client e il server
	 */
	private final Socket socket;
	/**
	 * out ObjectOutputStream per inviare messaggi in output al client
	 */
	private final ObjectOutputStream out;
	/**
	 * in ObjectInputStream per ricevere messaggi in input dal client
	 */
	private final ObjectInputStream in;

	/**
	 * Costruttore della classe, inizializza i campi e avvia la comunicazione
	 * @param address indirizzo del server
	 * @param port porta del server
	 * @throws IOException gestisce errori di tipo Input/Output
	 */
	Client(String address, int port) throws IOException{
			socket = new Socket(address, port);
			out = new ObjectOutputStream(socket.getOutputStream());
			in = new ObjectInputStream(socket.getInputStream()); // stream con richieste del client
	}
	
	/**
	 * Metodo che invia messaggi al server
	 * @param output Oggetto da inviare
	 */
	public void serverOut(Object output){
		try {
			out.writeObject(output);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Metodo per la ricezione dei messaggi dal server
	 * @return l'oggetto ricevuto
	 */

	public Object serverIn(){
		try {
			return in.readObject();
		} catch (IOException | ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	public void close(){
		try {
			socket.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
