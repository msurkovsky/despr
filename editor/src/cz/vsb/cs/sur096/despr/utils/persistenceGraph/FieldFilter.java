
package cz.vsb.cs.sur096.despr.utils.persistenceGraph;

import java.lang.reflect.Field;

/**
 * Filtr parametrů operace.
 * @author Martin Šurkovský, sur096 
 * <a href="maitlo:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 * @version 2011/10/23/16:16
 */
public interface FieldFilter {
    
    /**
     * Zjistí zda je položka akceptována pro další zpracování.
     * @param field položka která má být filtrována.
     * @return {@code true} pokud je položka akceptována, jinak, {@code false}.
     */
    public boolean accept(Field field);
}
