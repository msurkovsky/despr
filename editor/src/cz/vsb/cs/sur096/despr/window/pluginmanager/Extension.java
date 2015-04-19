
package cz.vsb.cs.sur096.despr.window.pluginmanager;

import cz.vsb.cs.sur096.despr.model.operation.IOperation;
import cz.vsb.cs.sur096.despr.types.Copier;
import cz.vsb.cs.sur096.despr.types.Wrapper;
import cz.vsb.cs.sur096.despr.view.inputparameters.ParameterCellEditor;
import cz.vsb.cs.sur096.despr.view.inputparameters.ParameterCellRenderer;
import java.util.*;

/**
 * Struktura reprezentující jeden rozšířující JAR balík. Pří načítání rozšíření
 * se projdou všechny třídy v balíku a pokud se jedná o uživatelskou operaci
 * nebo typové rozšíření. Pak je uložen odkaz v této struktuře. Ta následně 
 * může poskytnout na požádání seznamy s jednotlivými definovanými operacemi
 * či typovými rozšířeními.
 *
 * @author Martin Šurkovský, sur096,
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 * @version 2012/02/16/09:07
 */
public class Extension {
        
    /** Dvojiece (zájmový typ, seznam typů (rozšíření)).*/
    private Map<Class<?>,List<Class>> interestingTypes;
    
    /** Seznam všech zbývajících typů. */
    private List<Class> otherTypes;
    
    /**
     * Iniciace rozšíření.
     */
    public Extension() {
        interestingTypes = new HashMap<Class<?>, List<Class>>();
        otherTypes = new ArrayList<Class>();
    }

    /**
     * Přidá k danému typu rozšíření.
     * @param <T> rozšiřující typ musí rozšiřovat klíčový typ.
     * @param key klíčový typ
     * @param value rozšíření.
     */
    <T> void add(Class<T> key, Class<? extends T> value) {
        if (!interestingTypes.containsKey(key)) {
            interestingTypes.put(key, new ArrayList<Class>());
        }
        interestingTypes.get(key).add(value);
    }

    /**
     * Poskytne seznam typů (rozšíření) pro klíčový typ.
     * @param key klíčový typ.
     * @return seznam rozšíření pro daný klíčový typ.
     */
    List<Class> get(Class key) {
        if (interestingTypes.containsKey(key)) {
            return interestingTypes.get(key);
        } else {
            return Collections.emptyList();
        }
    }
    
    /**
     * Přidá typ který je součástí balíku, ale nejedná se o jeden ze
     * zajímavých typů.
     * @param type typ který má být přidán.
     */
    void addAnotherType(Class type) {
        otherTypes.add(type);
    }

    /**
     * Poskytne seznam uživatelských operací v daném
	 * rozšiřujícím balíku.
     * @return seznam uživatelských operací.
     */
    public List<Class> getOperations() {
        return get(IOperation.class);
    }

    /**
     * Poskytne seznam rozšíření které umí kopírovat dané typy.
     * @return seznam rozšíření které umí kopírovat hodnoty daného typu.
     */
    public List<Class> getCopiers() {
        return get(Copier.class);
    }

    /**
     * Poskytne seznam wrapperů.
     * @return seznam wrapprů.
     */
    public List<Class> getWrappers() {
        return get(Wrapper.class);
    }

    /**
     * Poskytne seznam nově definovaných rendererů .
     * @return seznam rendererů.
     */
    public List<Class> getParameterCellRenderers() {
        return get(ParameterCellRenderer.class);
    }

    /**
     * Poskytne seznam nově definovaných editorů hodnot.
     * @return seznam editorů hodnot.seznam editorů hodnot.
     */
    public List<Class> getParameterCellEditors() {
        return get(ParameterCellEditor.class);
    }
    
    /**
     * Poskytne seznam zbývajících typů.
     * @return seznam zbývajících typů.
     */
    public List<Class> getOtherTypes() {
        return otherTypes;
    }
}
