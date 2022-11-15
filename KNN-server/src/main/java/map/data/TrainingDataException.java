package map.data;

import java.util.NoSuchElementException;

/**
 * Classe che gestisce le eccezioni che riguardano il trainingSet
 */
public class TrainingDataException extends Exception {
    /**
     * Metodo per errori generici
     * @param s String esplicativa dell'errore
     */

    public TrainingDataException(String s){ System.err.println(s); }

    /**
     * In caso di errori relativi a una conversione non consenti da String a un tipo numero
     * @param e Eccezione catturata
     */
    public TrainingDataException(NumberFormatException e){ e.printStackTrace();}

    /**
     * In caso si provi ad accedere a un elemento che non esiste
     * @param e Eccezione catturata
     */
    public TrainingDataException(NoSuchElementException e){ e.printStackTrace();}

}
