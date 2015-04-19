
package cz.vsb.cs.sur096.despr.window.pluginmanager;

/**
 * Definice posluchače reagujícího na smazání položky/položek.
 *
 * @author Martin Šurkovský, sur096,
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gamil.com</a>
 * @version 2012/02/16/10:52
 */
public interface DeletedItemsListener {
    
    /**
     * Reaguje na smazání položek z nějaké struktury.
     * @param items položky které byly smazány.
     */
    public void deltedItems(Object[] items);
}
