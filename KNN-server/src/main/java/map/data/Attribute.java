/*
    La classe Attribute modella un generico dato discreto o continuo
    Pertanto è una classe astratta che verrà estesa dalle classi DiscreteAttribute e ContinuousAttribute
 */
package map.data;

import java.io.Serializable;

/**
 * Classe astratta che modella gli attributi degli esempi
 */

public abstract class Attribute implements Serializable {
    /**
     * name String che identifica il nome simbolico dell'attributo
     */
    protected String name; //nome simbolico dell'attributo
    /**
     * index int l'identificativo numerico dell'attributo
     */
    protected int index; //identificativo numerico dell'attributo

    /**
     * costruttore che associa i parametri passati in input ai campi della classe
     * @param name
     * @param index
     */
    // Costruttore della classe
    public Attribute(String name, int index) {
        this.name = name;
        this.index = index;
    }

    /**
     * Metodo che restituisce name
     * @return String
     */
    // Getters astratti della classe
    public abstract String getName();

    /**
     * Metodo che restituisce index
     * @return int
     */

    public abstract int getIndex();
}
