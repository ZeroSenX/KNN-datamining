package map.knnclient;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

/**
 * Classe che modella la GUI
 * @author Barbaro Vanessa
 * @author Capobianco Fabio
 */

public class ClientApplication extends Application{
    /**
     * c Client oggetto che gestisce la comunicazione con il server
     */
    static Client c;
    /**
     * mainMessage Label per messaggi principali
     */
    static Label mainMessage;
    /**
     * stage La stage principale
     */
    Stage stage;
    /**
     * vBox box verticale principale
     */
    static VBox vBox;
    
    static String[] ip;

    @Override
    public void start(Stage primaryStage) throws IOException, ClassNotFoundException, InterruptedException {
        this.stage = primaryStage;
        Scene knnSourceScene; //scena iniziale
        stage.setTitle("KNN - data mining");
        
        stage.getIcons().add(new Image(getClass().getResourceAsStream("knn-icon.png")));

        try {
            if(c == null) c = new Client(ip[0], Integer.parseInt(ip[1]));

            // choiceBox per la scelta del sorgente del KNN
            ChoiceBox <String> choiceKNNSource = new ChoiceBox<>();
            choiceKNNSource.getItems().addAll("File", "File binario", "Database");
            choiceKNNSource.setValue("File");
            choiceKNNSource.setId("choiceBox");
            Button b = new Button("OK");
            b.setId("button1");

            /* Evento collegato al bottone b
               che una volta premuto invia il messaggio al server
               e cambia la scena
            */
            b.setOnAction(actionEvent -> {
                int knnSource = 1 + choiceKNNSource.getItems().indexOf(choiceKNNSource.getValue());
                c.serverOut(knnSource);
                TableNameScene("Inserisci il nome della tabella (senza estensione)");
            });

            // La vbox della scena iniziale
            mainMessage = new Label("Scegli da dove vuoi caricare il training set");
            mainMessage.setId("mainMessage");
            mainMessage.setWrapText(true);
            HBox hBox = new HBox(choiceKNNSource, b);
            hBox.setId("hBox1");
            vBox = new VBox();
            vBox.setId("vBox1");
            vBox.getChildren().addAll(mainMessage, hBox);
            knnSourceScene = new Scene(vBox, 500, 500);
            knnSourceScene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("application.css")).toExternalForm());

            stage.setScene(knnSourceScene);
            stage.show();
            stage.setOnCloseRequest(e -> {
                stage.close();
                c.serverOut(5);
            });
        } catch (IOException e) {
        	System.out.println("Connessione con il server non riuscita");
            e.printStackTrace();
            mainMessage = new Label("Connessione con il server non riuscita");
            mainMessage.setId("mainMessage");
            Button b = new Button("Riprova");
            b.setId("button1");
            b.setOnAction(actionEvent -> {
                try {
                    start(stage);
                } catch (IOException | ClassNotFoundException | InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
            });
            vBox = new VBox(mainMessage, b);
            vBox.setId("vBox1");
            knnSourceScene = new Scene(vBox, 500, 600);
            knnSourceScene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("application.css")).toExternalForm());
            stage.setScene(knnSourceScene);
            stage.show();
        }
    }

    /**
     * Metodo che crea la scena per la scelta del nome della tabella
     * @param message stringa per impostare il messaggio principale
     */
    void TableNameScene(String message){
        Scene scene;
        TextArea area = new TextArea();
        area.setPrefSize(1, 15);
        area.setText("provaC");
        area.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode().equals(KeyCode.ENTER)) {
                c.serverOut(area.getText().replace("\n", ""));
                String risposta;
                risposta = (String) c.serverIn();
                if(risposta.contains("@OK")) {
                    c.serverOut(4);
                    trainingSetScene();
                }
                else TableNameScene(risposta + "\nInserisci il nome della tabella (senza estensione)");
            }
        });
        Button b = new Button("OK");
        b.setId("button1");
        /*Quando il pulsante b viene premuto, invia al server il nome della tabella
            e aspetto una risposta
            in caso positivo dico al server di continuare, e avvio la nuova scena
            in caso negativo riavvio questa scena, impostando il messaggio di errore
         */
        b.setOnAction(actionEvent -> {
            c.serverOut(area.getText());
            String risposta;
            risposta = (String) c.serverIn();
            if(risposta.contains("@OK")) {
                c.serverOut(4);
                trainingSetScene();
            }
            else TableNameScene(risposta + "\nInserisci il nome della tabella (senza estensione)");
        });
        mainMessage.setText(message);
        vBox = new VBox();
        vBox.setSpacing(20);
        vBox.setPadding(new Insets(20, 50, 50, 60));
        vBox.getChildren().addAll(mainMessage, area, b);
        scene = new Scene(vBox, 500, 600);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("application.css")).toExternalForm());
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Metodo che definisce la scena per la rappresentazione del trainingSet
     */

    public void trainingSetScene(){
        mainMessage.setText("Training Set");
        mainMessage.setFont(new Font(15));
        // Recupero dal server il training set
        TextArea TStext = new TextArea((String) c.serverIn());
        TStext.setEditable(false);
        TStext.setMaxWidth(400);
        TStext.setMaxHeight(500);

        vBox = new VBox();
        vBox.setId("vBox1");

        vBox.setSpacing(20);
        vBox.setPadding(new Insets(20, 50, 50, 60));


        vBox.getChildren().addAll(mainMessage, TStext);
        Scene scene = new Scene(vBox, 500, 600);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("application.css")).toExternalForm());
        stage.setScene(scene);
        stage.show();
        example();
    }
    /**
     * Metodo per la costruzione dell'esempio creato dall'utente
     * sulla base degli input del server
     */
    public void example(){
        StringBuilder risposta = new StringBuilder();
        // Label attribute per ricevere informazioni dal server riguardo la costruzione dell'esempio
        Label attribute = new Label();
        attribute.setId("attribute");
        TextArea area = new TextArea();
        area.setPrefSize(1, 10);
        Button b = new Button("OK");
        b.setId("button1");

        vBox.getChildren().addAll(attribute, area, b);
        buildExample(risposta, false);

        // Imposto l'azione INVIO della TextArea
        area.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode().equals(KeyCode.ENTER)) {
                double x;
                int k;
                if(risposta.toString().contains("@READDOUBLE")){
                    try {
                        x = Double.parseDouble(area.getText().replace("\n", ""));
                        c.serverOut(x);
                        buildExample(risposta, false);
                    }
                    catch (NumberFormatException e) {
                        buildExample(risposta, true);
                    }
                }
                else if (risposta.toString().contains("@READSTRING")) {
                    c.serverOut(area.getText().replace("\n", ""));
                    buildExample(risposta, false);
                }
                else if (risposta.toString().contains("@READK")){
                    try{
                        k = Integer.parseInt(area.getText().replace("\n", ""));
                        if(k >= 1) {
                            c.serverOut(k);
                            vBox.getChildren().remove(area);
                            vBox.getChildren().remove(b);
                            prediction(attribute);
                            confirm(vBox);
                        }
                        else buildExample(risposta, true);
                    }
                    catch (NumberFormatException e){
                        k = Integer.parseInt(area.getText());
                        buildExample(risposta, true);
                    }
                }
                area.clear();
            }
        });

        // Imposto l'azione del bottone
        b.setOnAction(actionEvent -> {
            double x;
            int k;
            if(risposta.toString().contains("@READDOUBLE")){
                try {
                    x = Double.parseDouble(area.getText());
                    c.serverOut(x);
                    buildExample(risposta, false);
                }
                catch (NumberFormatException e) {
                    buildExample(risposta, true);
                }
            }
            else if (risposta.toString().contains("@READSTRING")) {
                c.serverOut(area.getText());
                buildExample(risposta, false);
            }
            else if (risposta.toString().contains("@READK")){
                try{
                    k = Integer.parseInt(area.getText());
                    if(k >= 1) {
                        c.serverOut(k);
                        vBox.getChildren().remove(area);
                        vBox.getChildren().remove(b);
                        prediction(attribute);
                        confirm(vBox);
                    }
                    else buildExample(risposta, true);
                }
                catch (NumberFormatException e){
                    buildExample(risposta, true);
                }
            }
            area.clear();
        });

    }

    /**
     * Metodo di supporto per la costruzione dell'esempio creato dall'utente
     * @param risposta String ricevuta dal server che determina l'attributo da selezionare
     * @param flag boolean di supporto in caso d'input incongruenti con il modello del trainingSet
     */
    void buildExample(StringBuilder risposta, boolean flag){
        Label attribute = (Label) vBox.getChildren().get(2);
        String msg;
        // flag: in caso il READDOUBLE abbia avuto esito negativo
        if(!flag) {
            if(!risposta.isEmpty()) risposta.delete(0, risposta.length());
            risposta.append((String) c.serverIn());
        }
        if(!risposta.toString().contains("@ENDEXAMPLE") && !risposta.toString().contains("@READK")) {
            // sto leggendo l'esempio
            if (!flag) msg = (String)(c.serverIn());
            else msg = "Esempio continuo errato, reinserire il valore";
            if(risposta.toString().contains("@READSTRING"))  //leggo una stringa (ATTRIBUTO DISCRETO)
                attribute.setText(msg);
            else //leggo un numero (ATTRIBUTO CONTINUO)
            {
                attribute.setText(msg);
            }
        }
        else {
            if(!flag) {
                if (!risposta.isEmpty()) risposta.delete(0, risposta.length());
                risposta.append((String) c.serverIn());
                msg = (String) c.serverIn();
            }
            else msg = "Valore k non valido, inserire valore >=1";
            attribute.setText(msg);
        }
    }

    /**
     * Metodo che riceve la predizione e la stampa a video
     * @param text Label per la presentazione della predizione
     */
    void prediction (Label text){
        double pred = (double) c.serverIn();
        if(!Double.isNaN(pred))
            text.setText("Predizione: " + pred);
        else
            text.setText("La predizione non ha prodotto risultati");
    }

    /**
     * Metodo, che viene richiamato al termine della scoperta,
     * per offrire all'utente la possibilità di ripetere la predizione con l'attuale trainingSet o uno nuovo
     * @param vBox box principale
     */
    public void confirm(VBox vBox){
        Label request = new Label();
        request.setFont(new Font(17));
        request.setText("Scoperta completata");
        Button restartExample, restartTrainingSet;

        restartExample = new Button("Ripetere predizione");
        restartExample.setId("button1");
        restartTrainingSet = new Button("Nuovo Training Set");
        restartTrainingSet.setId("button1");

        restartExample.setOnAction(actionEvent -> {
            c.serverOut(4);
            // chiamare buildexample
            // cambiare i parametri di buildexample con vBox
            trainingSetScene();
        });

        restartTrainingSet.setOnAction(actionEvent -> {
            stage.close();
            Platform.runLater(()-> {
                try {
                    new ClientApplication().start(new Stage());
                } catch (IOException | ClassNotFoundException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
        });

        vBox.getChildren().addAll(request, restartExample, restartTrainingSet);
    }

    /**
     * Metodo principale che avvia l'applicazione
     * @param args parametri generali d'input
     */
    public static void main(String[] args) {

    	if(args.length == 0) {
    		ip = new String[2];
    		ip[0] = "127.0.0.1";
    		ip[1] = "2025";
    		System.out.println("Parametri di connessione in input mancanti, il programma si connetterà al localhost.");
    	}
    	else ip = args;
        System.out.println("Connessione con il server in corso...");
        launch();
    }
    
}