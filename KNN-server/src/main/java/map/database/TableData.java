package map.database;

import java.sql.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


import map.example.Example;

/**
 * Classe che modella le tabelle del database
 */
public class TableData {
	/**
	 * db gestisce l'accesso al database
	 */
	private DbAccess db;
	/**
	 * table String che identifica il nome della tabella
	 */
	private String table;
	/**
	 * tSchema Schema della tabella
	 */
	private TableSchema tSchema;
	/**
	 * transSet memorizza gli esempi contenuti nella tabella
	 */
	private List<Example> transSet;
	/**
	 * target List dei target
	 */
	private List target;

	/**
	 * Costruttore della classe
	 * @param db oggetto dell'accesso al database
	 * @param tSchema schema della tabella
	 * @throws SQLException gestisce eccezioni relative ad eventuali problemi con il database
	 */
	public TableData(DbAccess db, TableSchema tSchema) throws SQLException{
		this.db=db;
		this.tSchema=tSchema;
		this.table=tSchema.getTableName();
		transSet = new ArrayList();
		target= new ArrayList();	
		init();
	}

	/**
	 * Metodo che modella transSet e target
	 * @throws SQLException gestisce eccezioni relative ad eventuali problemi con il database
	 */

	private void init() throws SQLException{		
		String query="select ";
		
		Iterator<Column> it=tSchema.iterator();
		for(Column c:tSchema){			
			query += c.getColumnName();
			query+=",";
		}
		query +=tSchema.target().getColumnName();
		query += (" FROM "+table);
		
		Statement statement = db.getConnection().createStatement();
		ResultSet rs = statement.executeQuery(query);
		while (rs.next()) {
			Example currentTuple=new Example(tSchema.getNumberOfAttributes());
			int i=0;
			for(Column c:tSchema) {
				if(c.isNumber())
					currentTuple.set(rs.getDouble(i+1),i);
				else
					currentTuple.set(rs.getString(i+1),i);
				i++;
			}
			transSet.add(currentTuple);
			
			if(tSchema.target().isNumber())
				target.add(rs.getDouble(tSchema.target().getColumnName()));
			else
				target.add(rs.getString(tSchema.target().getColumnName()));
		}
		rs.close();
		statement.close();	
	}

	/**
	 * @return gli esempi
	 */
	public List<Example> getExamples(){
		return transSet; 
	}

	/**
	 * @return la lista dei target
	 */
	public List getTargetValues(){
		return target; 
	}

	/**
	 * Metodo che formula query al database, per individuare i valori MIN/MAX delle colonne
	 * @param column la colonna da analizzare
	 * @param aggregate il tipo di interrogazione da fare
	 * @return il risultato della query
	 */
	public Object getAggregateColumnValue(Column column, QUERY_TYPE aggregate){
		Object obj;
		try{
			Statement st = db.getConnection().createStatement();
			String s = "SELECT " + aggregate + "(" + column.getColumnName() + ") FROM " + tSchema.getTableName() + ";";
			System.out.println("query: " + s);
			ResultSet rs = st.executeQuery(s);
			rs.next();
			obj = rs.getDouble(1);

			rs.close();
			st.close();

		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return obj;
	}
}
