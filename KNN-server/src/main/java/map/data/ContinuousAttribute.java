/*
    La classe ContinuousAttribute modella un generico dato continuo, estendendo la classe astratta Attribute
 */
package map.data;

/**
 * Classe che estende Attribute e modella gli attributi continui
 * @see map.data.Attribute
 */
public class ContinuousAttribute extends Attribute{

    /**
     * min double che identifica l'attributo continuo più piccolo
     */
    private double min;
    /**
     * max double che identifica l'attributo continuo più grande
     */
    private double max;
    /**
     * flagMin boolean di supporto per impostare il valore min per la prima volta
     */
    private boolean flagMin = false;
    /**
     * flagMax boolean di supporto per impostare il valore max per la prima volta
     */
    private boolean flagMax = false;

    /**
     * Costruttore che richiama il costruttore della superclasse e l'avvalora con i parametri passati in input
     * @param name
     * @param index
     * @see map.data.Attribute#Attribute(String, int)
     */
    public ContinuousAttribute(String name, int index) {
        super(name, index);
    }

    /**
     * Imposta il valore min con il parametro passato in input, se quest'ultimo è più piccolo
     * @param v double valore ipoteticamente più piccolo
     */
    public void setMin(Double v) {
        if(!flagMin) {
            this.min = v;
            flagMin = true;
        }
        if (v < this.min)
            this.min = v;
    }

    /**
     * Imposta il valore max con il parametro passato in input, se quest'ultimo è più grande
     * @param v double valore ipoteticamente più grande
     */
    public void setMax(Double v) {
        if(!flagMax) {
            this.max = v;
            flagMax = true;
        }
        if (v > this.max)
            this.max = v;
    }

    /**
     * Metodo che trasforma il training set in modo che tutte le variabili indipendenti continue
     * abbiamo lo stesso range [0, 1]
     * @param value Double valore da scalare
     * @return il valore scalato
     */
    public double scale (Double value){
        return ((value - this.min)/(this.max - this.min));
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getIndex() {
        return index;
    }
}
