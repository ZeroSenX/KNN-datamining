package map.server;

import map.data.Data;
import map.data.TrainingDataException;
import map.database.DatabaseConnectionException;
import map.database.DbAccess;
import map.database.InsufficientColumnNumberException;
import map.mining.KNN;

import java.io.*;
import java.net.Socket;
import java.sql.SQLException;

/**
 * Classe che si interfaccia con il client e gestisce la comunicazione con esso
 */

public class ServerOneClient extends Thread{
    /**
     * socket Socket di comunicazione tra il server e il client
     */
    private final Socket socket;
    /**
     * in ObjectInputStream per ricevere messaggi in input dal client
     */
    private final ObjectInputStream in;
    /**
     * out ObjectOutputStream per inviare messaggi in output al client
     */
    private final ObjectOutputStream out;


    /**
     * Costruttore della classe, inizializza i campi e avvia la comunicazione
     * @param s inizializza la socket della classe
     * @throws IOException gestisce errori di tipo Input/Output
     */
    public ServerOneClient(Socket s) throws IOException{
        this.socket = s;
        in = new ObjectInputStream(socket.getInputStream());
        out= new ObjectOutputStream(socket.getOutputStream());
        run();
    }

    /**
     * Classe che gestisce effettivamente la comunicazione con il client
     * e guida la procedura di mining del trainingSet
     */
    public void run(){
        KNN knn=null;
        while (true) {
            int opt;
            try {
                System.out.println("In attesa che il client scelga l'opzione");
                opt = (int) in.readObject();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                opt = 5;
            }
            System.out.println("Echoing: " + opt);


            //load or train knn
            switch(opt) {
                case 1:{
                    Data trainingSet;
                    String file;
                    boolean flag = false;
                    do {
                        try {
                            file = (String) in.readObject();
                            trainingSet = new Data(file + ".dat");
                            out.writeObject("@OK");
                            flag = true;
                            knn = new KNN(trainingSet);
                            try {
                                knn.salva(file + ".dmp");
                            } catch (IOException exc) {
                                System.out.println(exc.getMessage());
                            }
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                            try {
                                out.writeObject("@ERROR " + e.getMessage());
                            } catch (IOException ex) {
                                throw new RuntimeException(ex);
                            }
                        } catch (TrainingDataException | IOException | ClassNotFoundException exc) {
                            throw new RuntimeException(exc);
                        }
                    }while(!flag);
                } break;

                case 2: {
                    String file = "";
                    boolean flag = false;
                    do {
                        try {
                            file = (String) in.readObject();
                            knn = KNN.carica(file + ".dmp");
                            out.writeObject("@OK");
                            flag = true;
                            try {
                                knn.salva(file + ".dmp");
                            } catch (IOException exc) {
                                System.out.println(exc.getMessage());
                            }
                        } catch (IOException | ClassNotFoundException exc) {
                            System.out.println(exc.getMessage());
                            try {
                                out.writeObject("@ERROR " + exc.getMessage());
                            } catch (IOException ex) {
                                throw new RuntimeException(ex);
                            }
                        }
                    }while (!flag);
                } break;

                case 3: {
                    Data trainingSet;
                    String table;
                    boolean flag = false;
                    do {
                        try {
                            System.out.print("Connecting to DB...");
                            DbAccess db = new DbAccess();
                            System.out.println("done!");
                            System.out.println("Nome tabella:");
                            try {
                                table = (String) in.readObject();
                                trainingSet = new Data(db, table);
                                System.out.println(trainingSet);
                                knn = new KNN(trainingSet);
                                out.writeObject("@OK");
                                flag = true;
                                try {
                                    knn.salva(table + "DB.dmp");
                                } catch (IOException exc) {
                                    System.out.println(exc.getMessage());
                                }
                            } catch (IOException | ClassNotFoundException exc) {
                                System.out.println(exc.getMessage());
                                flag = true;
                            } catch (SQLException | TrainingDataException e) {
                                e.printStackTrace();
                                try {
                                    out.writeObject("@ERROR " + e.getMessage());
                                } catch (IOException ex) {
                                    throw new RuntimeException(ex);
                                }
                            }
                            db.closeConnection();
                        } catch (InsufficientColumnNumberException | DatabaseConnectionException exc1) {
                            System.out.println(exc1.getMessage());
                            try {
                                out.writeObject("@ERROR " + exc1.getMessage());
                            } catch (IOException ex) {
                                throw new RuntimeException(ex);
                            }
                        }
                    }while(!flag);
                }break;

                case 4: {
                    try {
                        out.writeObject(knn.toString());
                        out.writeObject(knn.predict(out, in));
                    } catch (IOException | ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                }
                break;

                default: {
                    System.out.println("Il client ha terminato l'esecuzione");
                    return;
                }
            }
        }
    }
}

