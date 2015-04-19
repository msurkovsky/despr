
package cz.vsb.cs.sur096.despr.exceptions;

/**
 * Výjimka která je vyvolána v případě kdy dva typy jsou nekompatibilní. 
 * To znamená, že buď nejsou stejné nebo jeden není rozšířením druhého.
 * Výjimka se využívá při porovnávání výstupního a vstupního portu mezi
 * dvěma operacemi, které mají být propojené hranou. V takovém případě
 * musí být porty stejného typu nebo výstupní port musí být potomkem
 * vstupního portu (ne naopak!).
 *
 * @author Martin Šurkovský, sur096 
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 * @version 2011/11/28/16:21
 */
public class IncompatibleDataTypeException extends Exception {

    /** První typ */
    private Class type1;
    
    /** Druhý typ*/
    private Class type2;
    
    /**
     * Konstruktor přebírající dva nekompatibilní typy.
     * @param type1 typ 1
     * @param type2 typ 2
     */
    public IncompatibleDataTypeException(Class type1, Class type2) {
        super();
        init(type1, type2);
    }

    /**
     * Přebírá zprávu a dva typy, které nejsou kompatibilní.
     * @param msg zpráva.
     * @param type1 typ 1.
     * @param type2 typ 2.
     */
    public IncompatibleDataTypeException(String msg, Class type1, Class type2) {
        super(msg);
        init(type1, type2);
    }
    
    /**
     * Přebírá zprávu, throwable objekt který je přibalen do výjimky a dva
	 * nekompatibilní typy.
     * @param message zpráva.
     * @param cause jiná výjimka, v rámci níž je vytvořena tento typ výjimky.
     * @param type1 typ 1
     * @param type2 typ 2
     */
    public IncompatibleDataTypeException(String message, Throwable cause, Class type1, Class type2) {
        super(message, cause);
        init(type1, type2);
    }
    
    /**
     * Přebírá throwable objekt, který je přibalen do výjimky a dva 
	 * nekompatibilní typy.
     * @param cause jiná výjimka, v rámci níž je vytvořena tento typ výjimky.
     * @param type1 typ 1
     * @param type2 typ 2
     */
    public IncompatibleDataTypeException(Throwable cause, Class type1, Class type2) {
        super(cause);
        init(type1, type2);
    }
    
    /**
     * Vrací první nekompatibilní typ.
     * @return první nekompatibilní typ.
     */
    public Class getType1() {
        return type1;
    }
    
    /**
	 * Vrací druhý nekompatibilní typ.
     * @return druhý nekompatibilní typ.
     */
    public Class getType2() {
        return type2;
    }
    
    private void init(Class type1, Class type2) {
        this.type1 = type1;
        this.type2 = type2;
    }
}
