/*
    La classe data modello il training set
 */
package map.data;
import map.database.*;
import map.example.Example;
import map.example.ExampleSizeException;

import java.io.*;
import java.sql.SQLException;
import java.util.*;

import static map.database.QUERY_TYPE.*;

/**
 * Classe che modella effettivamente il training set
 */

public class Data implements Serializable {
    /**
     * data List di oggetti istanza della classe 'Example'
     */
    private final List<Example> data;
    /**
     * target List di valori della variabile 'target',
     *             un valore per ogni esempio memorizzato in 'data'
     */
    private final List<Double> target;
    /**
     * explanatorySet List delle variabili indipendenti che definiscono lo schema degli oggetti istanza di Example
     */
    private final List<Attribute> explanatorySet;
    /**
     * numberOfExamples int che rappresenta il numero di esempi memorizzati in 'data'
     */
    private final int numberOfExamples;
    /**
     * classAttribute ContinuousAttribute che rappresenta l'attributo di 'target'
     */
    private ContinuousAttribute classAttribute;

    /**
     * Costruttore della classe che costruisce il trainingSet ricavato da un file
     * @param fileName String nome del file
     * @throws TrainingDataException eccezione in caso il file è vuoto, in un formato sbagliato o altri errori
     *                               che possono insorgere durante la costruzione del trainingSet
     * @throws FileNotFoundException in caso non sia possibile trovare il file indicato dall'input
     */
    public Data(String fileName) throws TrainingDataException, FileNotFoundException {
        File inFile = new File (fileName);
        Scanner sc;
        sc = new Scanner(inFile);

        String line;
        try { //Nel caso in cui il file del training set è vuoto
            line = sc.nextLine();
        } catch(NoSuchElementException e) {
             throw new TrainingDataException(e);
        }
        if(!line.contains("@schema"))
            throw new TrainingDataException("Errore nello schema");
        String[] s =line.split(" ");

        //popolare 'explanatorySet'
        try {
            explanatorySet = new ArrayList<>(Integer.parseInt(s[1]));
        } catch(NumberFormatException e){
            throw new TrainingDataException(e);
        }
        short iAttribute=0;
        line = sc.nextLine();
        while(!line.contains("@data")){
            s=line.split(" ");
            if(s[0].equals("@desc"))
            { // aggiungo l'attributo allo spazio descrittivo
                //@desc motor discrete
                if(s[2].equals("discrete"))
                    explanatorySet.add(new DiscreteAttribute(s[1],iAttribute));
                else
                    explanatorySet.add(new ContinuousAttribute(s[1], iAttribute));
            }
            else if(s[0].equals("@target"))
                classAttribute=new ContinuousAttribute(s[1], iAttribute);

            iAttribute++;
            line = sc.nextLine();

        }

        //avvalorare numero di esempi
        numberOfExamples=Integer.parseInt(line.split(" ")[1]);

        //popolare data e target
        data = new ArrayList<>(numberOfExamples);
        target = new ArrayList<>(numberOfExamples);

        while (sc.hasNextLine())
        {
            Example e=new Example(this.getNumberOfExplanatoryAttributes());
            line = sc.nextLine();
            s=line.split(","); //E,E,5,4, 0.28125095

            if(s.length != this.getNumberOfExplanatoryAttributes() + 1 ) {  //Se la lunghezza della stringa del Training Set non è @schema + 1
                 throw new TrainingDataException("Formato del Training Set non corretto. ");
            }
            for(short jColumn=0;jColumn<s.length-1;jColumn++) {
                if(explanatorySet.get((jColumn)) instanceof ContinuousAttribute){
                    e.add(Double.parseDouble(s[jColumn]));
                    ((ContinuousAttribute)explanatorySet.get(jColumn)).setMin(Double.parseDouble(s[jColumn]));
                    ((ContinuousAttribute)explanatorySet.get(jColumn)).setMax(Double.parseDouble(s[jColumn]));
                }
                else
                    e.add(s[jColumn]);

            }
            data.add(e);
            try {
                target.add(Double.parseDouble(s[s.length - 1]));
                classAttribute.setMax(Double.parseDouble(s[s.length - 1]));
                classAttribute.setMin(Double.parseDouble(s[s.length - 1]));
            } catch(NumberFormatException ex){
                throw new TrainingDataException(ex);
            }
        }
        sc.close();
    }

    /* Costruttore che inizializza una istanza di Data da una tabella "tableName"
       e in un database a cui si accede tramite l’istanza di "DbAccess"
     */

    /**
     * Costruttore della classe che costruisce il trainingSet ricavato da un database
     * @param db Gestisce l'accesso al database per la lettura dei dati di training
     * @param tableName String che indica il nome della tabella del DB
     * @throws TrainingDataException in caso insorgano problemi relativi nella costruzione del trainingSet
     * @throws InsufficientColumnNumberException nel caso in cui la tabella risulti incompatibile con la struttura di Data
     * @throws SQLException per problemi relativi all'esecuzione di query sql
     */
    public Data(DbAccess db, String tableName) throws TrainingDataException, InsufficientColumnNumberException, SQLException {
        TableSchema tschema = new TableSchema(tableName, db);
        TableData tdata = new TableData(db, tschema);
        numberOfExamples = tdata.getExamples().size();


        Iterator<Column> iC = tschema.iterator();
        int index = 0;
        explanatorySet = new ArrayList<>(tschema.getNumberOfAttributes());
        while (iC.hasNext()){
            Column c = iC.next();
            if(c.isNumber())
                explanatorySet.add(new ContinuousAttribute(c.getColumnName(), index));
            else
                explanatorySet.add(new DiscreteAttribute(c.getColumnName(), index));

            index++;
        }

        data = new ArrayList<>(numberOfExamples);



        // Popolo data
        data.addAll(tdata.getExamples());


        target = tdata.getTargetValues();

        classAttribute = new ContinuousAttribute(tschema.target().getColumnName(), explanatorySet.size());


        //Imposto il valore minimo e massimo dell'explnatory set e di target
        Iterator<Column> IC = tschema.iterator();
        Iterator<Attribute> IA = explanatorySet.iterator();
        while(IC.hasNext()){
            Column c = IC.next();
            if(IA.hasNext()){
                Attribute a = IA.next();
                if(a instanceof ContinuousAttribute){
                    ((ContinuousAttribute) a).setMin((double) tdata.getAggregateColumnValue(c, MIN));
                    ((ContinuousAttribute) a).setMax((double) tdata.getAggregateColumnValue(c, MAX));
                }
            }
            else{
                classAttribute.setMin((double) tdata.getAggregateColumnValue(c, MIN));
                classAttribute.setMax((double) tdata.getAggregateColumnValue(c, MAX));
            }
        }

    }

    /*
            Restituisce la lunghezza dell'array explanatorySet[]
     */

    /**
     * @return il numero delle variabili indipendenti, contenuti nell'explanatorySet
     */
    private int getNumberOfExplanatoryAttributes(){
        return this.explanatorySet.size();
    }

    /*
     * Partiziona data rispetto all'elemento x di key e restituisce il punto di separazione
     */

    /**
     * Metodo di supporto di quickSort
     * @param key Lista da ordinare
     * @param inf intero estremo inferiore
     * @param sup intero estremo superiore
     * @return intero elemento di partizione
     * @throws ExampleSizeException in caso venga richiesto un elemento non esistente nel trainingSet
     * @see #quicksort(List, int, int)
     */
    private int partition(List <Double> key, int inf, int sup) throws ExampleSizeException {
        int i,j;

        i=inf;
        j=sup;
        int	med=(inf+sup)/2;

        double x= key.get(med);

        data.get(inf).swap(data.get(med));

        double temp=target.get(inf);
        target.set(inf, target.get(med));
        target.set(med, temp);

        temp=key.get(inf);
        key.set(inf, key.get(med));
        key.set(med, temp);

        while (true)
        {

            while(i<=sup && key.get(i)<=x){
                i++;

            }

            while(key.get(j)>x) {
                j--;

            }

            if(i<j) {
                data.get(i).swap(data.get(j));
                temp=target.get(i);
                target.set(i, target.get(j));
                target.set(j, temp);

                temp = key.get(i);
                key.set(i, key.get(j));
                key.set(j, temp);


            }
            else break;
        }
        data.get(inf).swap(data.get(j));

        temp=target.get(inf);
        target.set(inf, target.get(j));
        target.set(j, temp);

        temp = key.get(inf);
        key.set(inf, key.get(j));
        key.set(j, temp);

        return j;

    }

    /*
     * Algoritmo quicksort per l'ordinamento di data
     * usando come relazione d'ordine totale "<=" definita su key
     * @param A
     */

    /**
     * Algoritmo quicksort per l'ordinamento di data
     * usando come relazione d'ordine totale "<=" definita su key
     * @param key lista da ordinare
     * @param inf intero estremo inferiore
     * @param sup intero estremo superiore
     * @throws ExampleSizeException in caso venga richiesto un elemento non esistente nel trainingSet
     */
    private void quicksort(List <Double> key, int inf, int sup) throws ExampleSizeException{

        if(sup>=inf){
            int pos;
            pos=partition(key, inf, sup);

            if ((pos-inf) < (sup-pos+1)) {
                quicksort(key, inf, pos-1);
                quicksort(key, pos+1,sup);
            }
            else
            {
                quicksort(key, pos+1, sup);
                quicksort(key, inf, pos-1);
            }
        }
    }



    /**
     * Calcola la distanza media tra il trainingSet ordinato e l'esempio inserito dall'utente
     * @param e Esempio creato dall'utente
     * @param k intero che determina il range della predizione
     * @return la distanza media calcolata
     * @throws ExampleSizeException in caso si provi ad agire su elementi non esistenti nel trainingSet
     */
    /*
        1) Avvalora key con le distanze calcolate tra ciascuna istanza di Example memorizzata in data
           ed 'e' (usare il metodo distance di Example)
        2) ordina data, target e key in accordo ai valori contenuti in key (usare quicksort)
        3) identifica gli esempi di data che sono associati alle k distanze più piccole in key
        4) calcola e restituisce la media dei valori memorizzati in target in corrispondenza degli esempi
           isolati al punto 3
     */
    public double avgClosest(Example e, int k)throws ExampleSizeException{
        double avg= 0.0;
        List <Double> key = new ArrayList<>(data.size());
        Iterator <Example> dataIterator = data.iterator();

        e = scaledExample(e);

        while(dataIterator.hasNext())  //1. avvalora key con le distanze calcolate
            key.add(dataIterator.next().distance(e, explanatorySet));

        quicksort( key, 0, data.size()-1 );     //2. Ordinamento di data, target e key


        int count = 0; //Conta gli elementi di key minori di k
        for (Double aDouble : key)
            if (aDouble < k) count++;

        // 4
        Iterator<Double> targetIterator = target.iterator();
        int index = 0;
        while(index++ < count && targetIterator.hasNext())
            avg += targetIterator.next();

        avg /= count;

        return avg;
    }

    /**
     * Metodo che genera un esempio creato dall'utente
     * @param out   ObjectOutputStream per comunicare messaggi in uscita al client
     * @param in    ObjectInputStream per ricevere messaggi in entrata dal client
     * @return l'esempio creato
     * @throws IOException in caso insorgano problemi di tipo Input/Output
     * @throws ClassNotFoundException nel caso in cui si cerchi di caricare una classe e
     *                                non si trovi la sua definizione nel suo classpath
     * @throws ClassCastException nel caso insorgano problemi di incompatibilità di cast di classi
     */
    public Example  readExample(ObjectOutputStream out, ObjectInputStream in)
            throws IOException, ClassNotFoundException, ClassCastException {
        // che comunica con il client per acquisire i valori di un’istanza di Example
        Example e = new Example(getNumberOfExplanatoryAttributes());
        for(Attribute a:explanatorySet)
        {
            if(a instanceof DiscreteAttribute) {
                out.writeObject("@READSTRING");
                out.writeObject("Inserisci valore discreto "+ a.getName() + "[" + a.getIndex() + "]:");
                e.set(in.readObject(), a.getIndex());
            }
            else {
                double x;
                do{
                    out.writeObject("@READDOUBLE");
                    out.writeObject("Inserisci valore continuo " + a.getName() + "[" + a.getIndex() + "]:");
                    x = (double) in.readObject();
                }
                while(Double.isNaN(x));
                e.set(x, a.getIndex());
            }
        }
        out.writeObject("@ENDEXAMPLE");
        return e;
    }

    /**
     * Metodo che scala l'esempio sfruttando scale()
     * @param e Example da scalare
     * @return l'esempio scalato
     * @see ContinuousAttribute#scale(Double)
     */
    private Example scaledExample(Example e){
        Example eS = new Example(e.getLength());

        Iterator<Attribute> itE = explanatorySet.iterator();
        int i = 0;
        while (itE.hasNext()){
            Attribute temp = itE.next();
            if(temp instanceof ContinuousAttribute){
                double tempD = (double) e.get(i);
                tempD = ((ContinuousAttribute) temp).scale(tempD);
                eS.add(tempD);
            }
            else
                eS.add(e.get(i));
            i++;
        }
        return eS;
    }

    @Override
    public String toString() {
        String s = "";
        Iterator <Example> dataIterator = data.iterator();
        Iterator <Double> targetIterator = target.iterator();
        int i = 0;
        while(dataIterator.hasNext() && targetIterator.hasNext()){
            s += "[" + i++ + "] " + dataIterator.next() + ", " + targetIterator.next() + "\n";
        }
        return s;
    }

}
