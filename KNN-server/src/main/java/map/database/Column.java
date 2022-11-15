package map.database;

/**
 * Classe che modella colonne delle tabelle dei database
 */
public class Column{
	/**
	 * name String associata al nome della colonna
	 */
	private final String name;
	/**
	 * type String associata al tipo della colonna
	 */
	private final String type;

	/**
	 * Costruttore di classe, che associa i campi della classe con i parametri passati in input
	 * @param name String relativa al nome della colonna
	 * @param type String relativa al tipo della colonna
	 */
	Column(String name,String type){
		this.name=name;
		this.type=type;
	}

	/**
	 * @return il campo name
	 */
	public String getColumnName(){
		return name;
	}

	/**
	 * @return Verifica che la colonna sia di tipo numero
	 */
	public boolean isNumber(){
		return type.equals("number");
	}
	public String toString(){
		return name+":"+type;
	}
}