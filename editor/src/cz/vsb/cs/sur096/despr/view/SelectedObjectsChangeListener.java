package cz.vsb.cs.sur096.despr.view;

import java.util.List;

/**
 * Definice posluchače zajímající ho se o změnu seznamu vybratelných
 * objektů.
 *
 * @author Martin Šurkovský, sur096 
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 * @version 2011/08/25/11:01
 */
public interface SelectedObjectsChangeListener {
    
    /**
     * Reaguje na změnu seznamu vybratelných objektů.
     * @param selectedObjects seznam vybratelných objektů.
     */
    public void selectedObjectsChange(List<Selectable> selectedObjects);
}
