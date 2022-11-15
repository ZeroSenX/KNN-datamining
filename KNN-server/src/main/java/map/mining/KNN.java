package map.mining;

import map.data.Data;
import map.example.Example;
import map.example.ExampleSizeException;

import java.io.*;

import static java.lang.Double.NaN;

/**
 * Questa classe è il cuore dell'applicazione, gestisce il training set, la predizione degli attributi del training set
 * più vicini all'esempio inserito dall'utente e i metodi per salvare e caricare l'oggetto stesso in un file binario
 *
 */
public class KNN implements Serializable {
    /**
     * trainingSet, oggetto di tipo Data, contiene i dati su cui lavorare
     * @see map.data.Data
     */
    private final Data trainingSet; // modella il training set

    /**
     * Costruttore
     * @param trainingSet
     */
    // Avvalora il training set DA CONTROLLARE
    public KNN(Data trainingSet){
        this.trainingSet = trainingSet;
    }

    /**
     * Predice il valore target con un esempio recuperato dal metodo
     * readExample del trainingSet
     * @param out   ObjectOutputStream per comunicare messaggi in uscita al client
     * @param in    ObjectInputStream per ricevere messaggi in entrata dal client
     * @return Il risultato della predizione
     * @throws IOException in caso insorgano problemi di tipo Input/Output
     * @throws ClassNotFoundException nel caso in cui si cerchi di caricare una classe e
     *                                non si trovi la sua definizione nel suo classpath
     * @throws ClassCastException nel caso insorgano problemi di incompatibilità di cast di classi
     *
     * @see map.data.Data#readExample(ObjectOutputStream, ObjectInputStream)
     */

    public double predict (ObjectOutputStream out, ObjectInputStream in) throws
            IOException, ClassNotFoundException, ClassCastException {
        double result;
        Example e = trainingSet.readExample(out, in);
        int k;
        out.writeObject("@READK");
        out.writeObject("Inserisci valore k>=1:");
        k = (int) (in.readObject());
        try {
            result = trainingSet.avgClosest(e, k);
        } catch (ExampleSizeException ex) {
            ex.printStackTrace();
            System.err.println("Predict setted to NaN");
            result = NaN;
        }
        return result;
    }

    /**
     * Metodo per il salvataggio dell'oggetto KNN in un file binario
     * @param nomeFile nome del file binario in cui salvare l'oggetto
     * @throws IOException in caso insorgano problemi di tipo Input/Output
     */
    public void salva(String nomeFile) throws IOException{
        FileOutputStream outFile = new FileOutputStream(nomeFile);
        ObjectOutputStream outStream = new ObjectOutputStream(outFile);

        outStream.writeObject(this);
        outStream.close();
    }

    /**
     * Metodo statico che carica un oggetto KNN da un file binario, e lo restituisce
     * @param nomeFile nome del file binario da cui caricare l'oggetto
     * @return KNN caricato dal file
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public static KNN carica(String nomeFile) throws IOException, ClassNotFoundException{
        FileInputStream inFile = new FileInputStream(nomeFile);
        ObjectInputStream inStream = new ObjectInputStream(inFile);

        return (KNN) inStream.readObject();
    }

    @Override
    public String toString() {
        return trainingSet.toString();
    }
}
