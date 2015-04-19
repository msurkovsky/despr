package cz.vsb.cs.sur096.despr.view;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementace správce vybratelných objektů.
 *
 * @author Martin Šurkovský, sur096 
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 * @version 2011/08/25/10:59
 */
public class SelectedObjects implements PropertyChangeListener {
    
    /** Seznam vybratelných objektů. */
    private List<Selectable> selectedObjects;
    
    /** Seznam posluchačů zajímající se o změnu množiny vybratelných objektů. */
    private List<SelectedObjectsChangeListener> listeners;
    
    /**
     * Iniciuje nového správce.
     */
    public SelectedObjects() {
        selectedObjects = new ArrayList<Selectable>(30);
        listeners = new ArrayList<SelectedObjectsChangeListener>(5);
    }
    
    /**
     * Nastaví vybrané objekty.
     * @param objects seznam vybraných objektů
     */
    public void setSelectObjects(Selectable... objects) {
        cancelAllSelectedObjects();
        int countObjects = objects.length;
        for (int i = 0; i < countObjects; i++) {
            objects[i].setSelected(true);
            selectedObjects.add(objects[i]);
        }
        fireSelectedObjectsChange();
    }
        
    /**
     * Zruší všechny vybrané objekty.
     */
    private void cancelAllSelectedObjects() {
        int size = selectedObjects.size();
        for (int i = 0; i < size; i++) {
            Selectable obj = selectedObjects.get(i);
            obj.setSelected(false);
            selectedObjects.remove(i);
            i--;
            size--;
        }
    }
    
    /**
     * Přidá do seznamu vybratelný objekt.
     * @param obj vybratelný objekt.
     */
    public void addSelectObject(Selectable obj) {
        selectedObjects.add(obj);
        fireSelectedObjectsChange();
    }
    
    /**
     * Smaže ze seznamu vybratelný objekt.
     * @param obj vybratelný objekt.
     */
    public void cancelSelectedObject(Selectable obj) {
        obj.setSelected(false);
        selectedObjects.remove(obj);
        fireSelectedObjectsChange();
    }

    /**
     * Přidá posluchače reagující ho na změnu seznamu vybratlených objektů.
     * @param l posluchač.
     */
    public void addSelectedObjectsChangeListener(SelectedObjectsChangeListener l) {
        listeners.add(l);
    }
    
    /**
     * Smaže posluchače.
     * @param l posluchač.
     */
    public void removeSelectedObjectsChangeListener(SelectedObjectsChangeListener l) {
        listeners.remove(l);
    }
    
    /**
     * Informuje posluchače o změně seznamu.
     */
    private void fireSelectedObjectsChange() {
        for (SelectedObjectsChangeListener l : listeners) {
            l.selectedObjectsChange(selectedObjects);
        }
    }

    /**
     * Reaguje na změnu vlastnosti {@code selected}.
     * @param evt událost.
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals("selected")) {
            boolean isSelected = (Boolean) evt.getNewValue();
            boolean oldValue = (Boolean) evt.getOldValue();
            
            if (isSelected && isSelected != oldValue) {
                cancelAllSelectedObjects();
                selectedObjects.add((Selectable) evt.getSource());
                fireSelectedObjectsChange();
            }
        }
    }
}
