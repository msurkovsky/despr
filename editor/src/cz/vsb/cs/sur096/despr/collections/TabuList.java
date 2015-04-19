
package cz.vsb.cs.sur096.despr.collections;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Struktura, která si pamatuje u vložených objektů i počet
 * kolikrát se ve struktuře nachází. Tohoto je vyžito např.
 * u operací, které si pamatují seznam zakázaných operací do
 * kterých z nich nemůže vést hrana.
 *
 * @author Martin Šurkovský, sur096, 
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 * @version 2011/02/03/16:39
 */
public class TabuList<T> implements Iterable<T> {
    
    /** Tabulka která si pamatuje, kolikrát byl který objekt vložen.*/
    private Map<T, Integer> tabuList;
    
    /**
     * Konstruktor inicializuje prázdnou tabulku.
     */
    public TabuList() {
        tabuList = new HashMap<T, Integer>();
    }
    
    /**
     * Přidá objekt do do tabulky, pokud existuje 
	 * zvýší čítač, pokud ne vloží nový prvek.
     * @param value nová hodnota.
     */
    public void add(T value) {
        if (tabuList.containsKey(value)) {
            int count = tabuList.get(value);
            count += 1;
            tabuList.put(value, count);
        } else {
            tabuList.put(value, 1);
        }
    }
    
    /**
     * Přidá cely seznam hodnot.
     * @param list seznam hodnot.
     */
    public void addValues(TabuList<T> list) {
        for (T tabuValue : list) {
            add(tabuValue);
        }
    }
    
    public int getCount(T value) {
        return tabuList.get(value);
    }
    
    /**
     * Smaže hodnotu z tabulky. Pokud hodnota 
	 * v tabulce je vícekrát prvně dekrementuje
	 * čítač a až na razí na poslední tak pak
	 * teprve smaže.
     * @param value hodnota, která má být smazána.
     */
    public void remove(T value) {
        Integer count = tabuList.get(value);
        if (count != null) {
            if (count == 1) {
                tabuList.remove(value);
            } else {
                count -= 1;
                tabuList.put(value, count);
            }
        }
    }
    
    /**
     * Smaže celý seznam hodnot. Projde seznam a na každý prvek
	 * aplikuje metodu {@code remove(T value)}.
     * @param list seznam hodnot, které mají být smazány.
     */
    public void removeValues(TabuList<T> list) {
        for (T tabuValue : list) {
            int count = list.getCount(tabuValue);
            // je třeba smazat daný prvek určitý počet krát.
            for (int i = 1; i <= count; i++) {
                remove(tabuValue);
            }
        }
    }
    
    /**
     * Poskytne seznam objektů, ale bez počtu opakování.
     * @return poskytne množinu zakázaných klíčů.
     */
    public Set<T> getTabuList() {
        return tabuList.keySet();
    }

    /**
     * Kolekci je možné procházet po jedinečných objektech.
     * @return iterátor přes vložené objekty.
     */
    @Override
    public Iterator iterator() {
        return tabuList.keySet().iterator();
    }
}
