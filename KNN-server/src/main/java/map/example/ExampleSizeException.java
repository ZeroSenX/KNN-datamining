package map.example;

/**
 * @throws ExampleSizeException in caso in cui gli esempi abbiano dimensione diversa
 */
public class ExampleSizeException extends Exception{
    public ExampleSizeException(String s){ System.out.println(s); }
}
