
package cz.vsb.cs.sur096.despr.window.pluginmanager;

import java.util.*;
import javax.swing.AbstractListModel;
import javax.swing.ListModel;

/**
 * Implementace tříděného modelu seznamu. 
 *
 * @author Martin Šurkovský, sur096,
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 */
public class SortedListModel extends AbstractListModel implements ListModel {
    
    /** Model seznamu. */
    SortedSet model;

    /**
     * Inicializace tříděného modelu seznamu.
     */
    public SortedListModel() {
        model = new TreeSet();
    }

    /**
     * Poskytne velikost seznamu.
     * @return velikost seznamu.
     */
    @Override
    public int getSize() {
        return model.size();
    }

    /**
     * Poskytne prvek na dané pozici.
     * @param index pozice.
     * @return objekt na dané pozici.
     */
    @Override
    public Object getElementAt(int index) {
        return model.toArray()[index];
    }

    /**
     * Přidá nový objekt do seznamu.
     * @param element nový objekt.
     */
    public void addElement(Object element) {
        if (model.add(element)) {
            fireContentsChanged(this, 0, getSize());
        }
    }

    /**
     * Přidá celý seznam nových objektů.
     * @param elements seznam nových objektů.
     */
    public void addAll(Object elements[]) {
        Collection c = Arrays.asList(elements);
        model.addAll(c);
        fireContentsChanged(this, 0, getSize());
    }

    /**
     * Smaže obsah seznamu.
     */
    public void clear() {
        model.clear();
        fireContentsChanged(this, 0, getSize());
    }

    /**
     * Zjistí zda je daný objekt obsažen v seznamu.
     * @param element objekt, který by měl být obsažen v seznamu.
     * @return {@code true} pokud je prvek v seznamu obsažen,
	 * jinak {@code false}.
     */
    public boolean contains(Object element) {
        return model.contains(element);
    }

    /**
     * Poskytne objekt na první pozici v seznamu.
     * @return element na první pozici.
     */
    public Object firstElement() {
        return model.first();
    }

    /**
     * Poskytne poslední prvek v seznamu.
     * @return poslední prvek v seznamu.
     */
    public Object lastElement() {
        return model.last();
    }

    /**
     * Poskytne iterátor přes elementy.
     * @return iterátor přes elementy v seznamu.
     */
    public Iterator iterator() {
        return model.iterator();
    }
    
    /**
     * Smaže daný element ze seznamu.
     * @param element element který má být smazán.
     * @return {@code true} pokud se smazání podařilo,
	 * jinak {@code false}.
     */
    public boolean removeElement(Object element) {
        boolean removed = model.remove(element);
        if (removed) {
            fireContentsChanged(this, 0, getSize());
        }
        return removed;   
    }
    
    /**
     * Poskytne index daného elementu pokud je v seznamu obsažen.
     * @param elemnt element, který by se měl v seznamu nacházet.
     * @return index pozice na které se nachází, pokud nebyl
	 * nalezen vrátí -1.
     */
    public int getIndex(Object elemnt) {
        Object[] items = model.toArray();
        for (int i = 0; i < items.length; i++) {
            if (items[i].equals(elemnt)) {
                return i;
            }
        }
        return -1;
    }
}