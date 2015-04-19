
package cz.vsb.cs.sur096.despr.collections;

import cz.vsb.cs.sur096.despr.model.IInputParameter;
import cz.vsb.cs.sur096.despr.model.IParameter;
import java.util.*;

/**
 * Implementace rozhraní {@code IParameters}.
 *
 * @author Martin Šurkovský, sur096 
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 * @version 2011/08/29/10:34
 * 
 */
public class Parameters<T extends IParameter> implements IParameters<T> {

	/** Seznam parametrů */
    private List<T> parameters;
    
	/**
	 * Konstruktor inicializuje prázdný seznam pro parametry.
	 */
    public Parameters() {
        parameters = new ArrayList<T>();
    }
    
	/**
	 * Konstruktor inicializuje prázdný seznam pro parametry
	 * o určíte velikosti.
	 * @param initialCapacity počáteční velikost seznamu.
	 */
    public Parameters(int initialCapacity) {
        parameters = new ArrayList<T>(initialCapacity);
    }
    
    /**
	 * Metoda vracející počet parametrů ve struktuře.
	 * @return počet parametrů.
	 */
    @Override
    public int size() {
        return parameters.size();
    }

    /**
	 * Metoda zjišťující zda není struktura prázdná.
	 * @return {@code true} pokud je struktura prázdna, jinak {@code false}.
	 */
    @Override
    public boolean isEmpty() {
        return parameters.isEmpty();
    }

    /**
	 * Metoda zjišťující zda je ve struktuře obsažen prvek s jménem {@code name}.
	 * @param name jméno parametru
	 * @return {@code true} pokud je hodnota ve struktuře obsažena, jinak {@code false}.
	 */
    @Override
    public boolean contain(String name) {
        int idx = searchByName(name);
        return idx > -1 ? true : false;
    }

    /**
	 * Metoda přidá do struktury nový parametr, pokud je parametr ve struktuře
     * již obsazen pak je stará hodnota přepsána.
	 * @param parameter nový parametr.
	 */
    @Override
    public void put(T parameter) {
        int idx = searchByName(parameter.getName());
        if (idx > -1) {
            parameters.remove(idx);
            parameters.add(parameter);
        } else {
            parameters.add(parameter);
        }
    }

    /**
	 * Metoda vrátí ze struktury parametr na základě klíče {@code name}.
	 * @param name jméno parametru.
	 * @return parametr pokud takový existuje s hledaným jménem, jinak {@code null}.
	 */
    @Override
    public T get(String name) {
        for (T parameter : parameters) {
            if (parameter.getName().equals(name)) {
                return parameter;
            }
        }
        return null;
    }

    /**
     * Poskytne parametr, který je uložen na pozici {@code index}.
     * @param index pozice parametru.
     * @return parametr uložený na pozici {@code index}.
     * @throws IndexOutOfBoundsException pokud je {@literal index < 0} nebo 
	 * {@literal index >= velikost kolekce}
     */
    @Override
    public T get(int index) 
            throws IndexOutOfBoundsException {
        
        if (parameters.size() >= index && index >= 0) {
            return parameters.get(index);
        }
        throw new IndexOutOfBoundsException(String.format(
                "Index is out of range '%d'!", index));
    }

    /**
	 * Metoda vracející hodnotu parametru na základě jména {@code name}.
	 * @param name jméno parametru.
	 * @return hodnota parametru, pokud je parametr s hledaným jménem ve
	 * struktuře obsažen, jinak {@code null}.
	 */
    @Override
    public Object getValue(String name) {
        if (contain(name)) {
            return get(name).getValue();
        } else {
            return null;
        }
    }

    /**
     * Setřídí parametry.
     */
    @Override
    public void sort() {
        Collections.sort(parameters, new ParametersComparator());
    }

    @Override
    public Iterator<T> iterator() {
        return parameters.iterator();
    }

    /**
     * Vyhledá parameter podle jména v kolikeci.
     * @param name jméno parameteru.
     * @return pokud parameter s hledaným jménem je v kolekci obsazžen vrácen
     * index pozice, pokud ne je vráceno -1.
     */
    private int searchByName(String name) {
        int size = parameters.size();
        for (int i = 0; i < size; i++) {
            if (parameters.get(i).getName().equals(name)) {
                return i;
            }
        }
        return -1;
    }
    
    /**
     * Komparátor parametru. V první řadě bere v úvahu pořadí
	 * definované uživatelem. Potom mají přednost vstupní parametry
	 * před výstupními a nakonec jsou řazeny podle jména.
     */
    private class ParametersComparator implements Comparator<T> {

        @Override
        public int compare(T p1, T p2) {
            int p1Order = p1.getOrder();
            int p2Order = p2.getOrder();
            
            if (p1Order < p2Order) {
                return -1;
            } else if (p1Order > p2Order) {
                return 1;
            } else {
                if (p1 instanceof IInputParameter && p2 instanceof IInputParameter){
                    IInputParameter ip1 = (IInputParameter) p1;
                    IInputParameter ip2 = (IInputParameter) p2;

                    int a = ip1.getType().compareTo(ip2.getType());
                    if (a == 0) {
                        int b = ip1.getDataType().getSimpleName().compareTo(ip2.getDataType().getSimpleName());
                        if (b == 0) {
                            String ip1Name = ip1.getDisplayName().trim().replaceAll("\\n", " ");
                            String ip2Name = ip2.getDisplayName().trim().replaceAll("\\n", " ");
                            return ip1Name.compareTo(ip2Name);
                        }
                        return b;
                    }
                    return a;
                } else {
                    int b = p1.getDataType().getSimpleName().compareTo(p2.getDataType().getSimpleName());
                    if (b == 0) {
                        String p1Name = p1.getDisplayName().trim().replaceAll("\\n", " ");
                        String p2Name = p2.getDisplayName().trim().replaceAll("\\n", " ");
                        return p1Name.compareTo(p2Name);
                    }
                    return b;
                }
            }
            
        }
    }
}
