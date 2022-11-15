package map.server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Classe che gestisce le nuove connessioni con i client
 * @author Barbaro Vanessa
 * @author Capobianco Fabio
 */

public class MultiServer{
    /**
     * PORT intero che identifica la porta del server
     */
    private final int PORT;

    /**
     * Costruttore della classe
     * @param port un intero che definisce la porta da usare
     * @throws IOException gestisce errori di tipo Input/Output
     */
    public MultiServer(int port) throws IOException {
        this.PORT= port;
        run();
    }

    /**
     * Metodo che avvia il server e la procedura di connessione con i client
     * @throws IOException gestisce errori di tipo Input/Output
     */
    private void run() throws IOException {

        try (ServerSocket s = new ServerSocket(PORT)) {
            while (true) {
                System.out.println("In attesa di un client");
                // si blocca fino a quando non c’è una connessione
                Socket socket = s.accept();
                try {
                    new ServerOneClient(socket);
                    System.out.println("Il client ha chiuso la connessione");
                } catch (RuntimeException e) {
                    System.err.println(e.getMessage() + "\nConnessione con il client chiusa");
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Metodo di avvio dell'applicazione
     * @param args parametri in input del main
     * @throws IOException gestisce errori di tipo Input/Output
     */
    public static void main(String[] args) throws IOException {
        new MultiServer(2025);
    }
}
