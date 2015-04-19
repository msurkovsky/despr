
package cz.vsb.cs.sur096.despr.operations.specialfunctions;

import cz.vsb.cs.sur096.despr.model.operation.IOperation;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Abstraktní implementace operace poskytuje načtení lokalizačních zpráv.
 *
 * @author Martin Šurkovský, sur096, 
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 * @version 2012/02/25/11:46
 */
public abstract class AbstractSpecialOperation implements IOperation {
    
    private transient ResourceBundle messages;
    
    /**
     * Konstruktor inicializuje operaci a načte lokalizační zprávy.
     */
    public AbstractSpecialOperation() {
        try {
            messages = ResourceBundle.getBundle(getClass().getCanonicalName());
        } catch (MissingResourceException ex) {
            try {
                messages = ResourceBundle.getBundle(getClass().getCanonicalName(),
                    Locale.US);
            } catch (MissingResourceException excp) {
                messages = null;
            }
        }
    }
    
    /**
     * Poskytne lokalizovanou zprávu, pro klíč, za podmínek, 
	 * že lokalizační zprávy a klíč nejsou {@code null}.
     * @param key klíč zprávy.
     * @return pokud pokud pro daný klič zpráva existuje pak ji vrátní,
	 * jinak vrátí {@code null}.
     */
    @Override
    public String getLocalizeMessage(String key) {
        if (key == null || messages == null) {
            return null;
        }
        
        try {
            return messages.getString(key);
        } catch (MissingResourceException ex) {
            return null;
        }
    }
}
