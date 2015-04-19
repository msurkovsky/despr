
package cz.vsb.cs.sur096.despr.collections;

import cz.vsb.cs.sur096.despr.model.IParameter;

/**
 * Struktura pro parametry operací.
 *
 * @author Martin Šurkovský, sur096
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 * @param <T> do struktury je možné vkládat jakýkoliv parametr typu 
 * {@code despr.model.parameters.IParameter}
 * @version 2011/02/01/22:03
 */
public interface IParameters<T extends IParameter>
    extends Iterable<T> {

	/**
	 * Metoda vracející počet parametrů ve struktuře.
	 * @return počet parametrů.
	 */
	public int size();

	/**
	 * Metoda zjišťující zda není struktura prázdná.
	 * @return {@code true} pokud je struktura prázdna, jinak {@code false}.
	 */
	public boolean isEmpty();

	/**
	 * Metoda zjišťující zda je ve struktuře obsažen prvek s jménem {@code name}.
	 * @param name jméno parametru
	 * @return {@code true} pokud je hodnota ve struktuře obsažena, jinak {@code false}.
	 */
	public boolean contain(String name);

	/**
	 * Metoda přidá do struktury nový parametr, pokud je parametr ve struktuře
     * již obsazen pak je stará hodnota přepsána.
	 * @param parameter nový parametr.
	 */
	public void put(T parameter);

	/**
	 * Metoda vrátí ze struktury parametr na základě klíče {@code name}.
	 * @param name jméno parametru.
	 * @return parametr pokud takový existuje s hledaným jménem, jinak {@code null}.
	 */
	public T get(String name);
    
    /**
     * Poskytne parametr, který je uložen na pozici {@code index}.
     * @param index pozice parametru.
     * @return parametr uložený na pozici {@code index}.
     * @throws IndexOutOfBoundsException pokud je {@literal index < 0} nebo 
	 * {@literal index >= velikost kolekce}
     */
    public T get(int index) throws IndexOutOfBoundsException;

	/**
	 * Metoda vracející hodnotu parametru na základě jména {@code name}.
	 * @param name jméno parametru.
	 * @return hodnota parametru, pokud je parametr s hledaným jménem ve
	 * struktuře obsažen, jinak {@code null}.
	 */
	public Object getValue(String name);
    
    /**
     * Setřídí parametry.
     */
    public void sort();
}
