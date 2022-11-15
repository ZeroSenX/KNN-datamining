package map.example;

/*
    Modella i valori degli attributi indipendenti di un esempio
 */

import map.data.Attribute;
import map.data.ContinuousAttribute;
import map.data.DiscreteAttribute;

import java.io.Serializable;
import java.util.*;

/**
 * Classe che modella gli esempi del trainingSet
 */
public class Example implements Serializable {
    /**
     * example List di Object che contiene un valore per ciascun attributo indipendente
     */
    private final List<Object> example; // Array di Object che contiene un valore per ciascun attributo indipendente

    /**
     * Costruttore che istanzia l’array example
     * @param size intero che indica la dimensione dell'array
     */
    // Il costruttore istanzia l’array example come array di dimensione size
    public Example(int size) {
        example = new ArrayList<>(size);
    }

    /**
     * Memorizza l'oggetto in example
     * @param obj oggetto da memorizzare
     * @param index posizione in cui memorizzare
     */
    // Memorizza obj nella posizione index di example
    public void set(Object obj, int index){
        if(this.getLength() > index)
            this.example.set(index, obj);
        else
            this.example.add(obj);
    }

    /**
     * Aggiunge un oggetto nell'esempio
     * @param obj oggetto da aggiungere
     */
    public void add(Object obj){
        this.example.add(obj);
    }

    /**
     * Restituisce un oggetto dell'esempio
     * @param index int che indica la posizione dell'oggetto da restituire
     * @return l'oggetto richiesto
     */
    // Restituisce il valore memorizzato nella posizione index di example
    public Object get(int index){
        return this.example.get(index);
    }

    /**
     * @return dimensione dell'esempio
     */
    public int getLength(){
        return this.example.size();
    }

    /**
     * scambia i valori contenuti nel campo example con i valori contenuti nell'esempio passato per input
     * @param e Esempio da scambiare
     * @throws ExampleSizeException in caso in cui i due esempi abbiano dimensione diversa
     */
    // scambia i valori contenuti nel campo example dell’ oggetto corrente con i valori contenuti nel campo
    // example del parametro e
    public void swap(Example e) throws ExampleSizeException {
        if(this.getLength() != e.getLength() ){
            throw new ExampleSizeException("Dimensione dell'esempio diversa ");
        }

        Object temp;
        for(int i= 0; i < this.getLength(); i++){
            temp = this.get(i);
            this.set(e.get(i), i);
            e.set(temp, i);
        }

    }

    /**
     * Metodo che calcola distanza tra l'esempio passato in input e tutti gli esempi dell'ogetto
     * @param e esempio su cui calcolare la distanza
     * @param explanatorySet Lista che determina le variabili indipendenti (discreto/continuo) degli esempi
     * @return la distanza calcolata
     * @throws ExampleSizeException in caso in cui gli esempi abbiano dimensione diversa
     */
    public double distance(Example e, List<Attribute> explanatorySet) throws ExampleSizeException{
        double distance = 0.0;
        if(this.getLength() != e.getLength())
            throw new ExampleSizeException("Dimensione dell'esempio diversa");

        /*
            Iteratori per le liste, rispettivamente:
                - la lista di esempi di classe
                - la lista dell'esempio passato in input
                - la lista dell'ExplanatorySet
         */
        Iterator<Object> thisExampleIterator = this.example.iterator();
        Iterator<Object> inIterator = e.example.iterator();


        for (Attribute attribute : explanatorySet) {
            /*
                Gli elementi degli Iteratori rispettivamente:
                    - iteratore esempi di classe
                    - iteratore dell'esempio passato input
                    - iteratore dell'ExplanatorySet
             */
            Object thisTemp = thisExampleIterator.next();
            Object inTemp = inIterator.next();
            Attribute EStemp = attribute;

            // In caso l'attributo sia DISCRETO, si calcola con la distanza di Hamming
            if (EStemp instanceof DiscreteAttribute) {
                if (!Objects.equals(thisTemp, inTemp))
                    distance++;
            }
            // In caso contrario si utilizza il min max scaler
            else if (EStemp instanceof ContinuousAttribute) {
                double scale = ((ContinuousAttribute) EStemp).scale((double) thisTemp);
                distance += Math.abs(scale - (double) inTemp);
            }
        }
        return distance;
    }

    @Override
    public String toString() {
        return example.toString();
    }
}