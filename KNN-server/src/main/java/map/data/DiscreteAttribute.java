/*
    La classe DiscreteAttribute modella un generico dato discreto, estendendo la classe astratta Attribute
 */
package map.data;

/**
 * Classe che estende Attribute e modella gli attributi discreti
 * @see map.data.Attribute
 */
public class DiscreteAttribute extends Attribute{
    /**
     * Costruttore che richiama il costruttore della superclasse e l'avvalora con i parametri passati in input
     * @param name
     * @param index
     * @see map.data.Attribute#Attribute(String, int)
     */
    public DiscreteAttribute(String name, int index) {
        super(name, index);
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
