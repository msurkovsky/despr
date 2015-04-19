
package cz.vsb.cs.sur096.despr.operations.coins;

import cz.vsb.cs.sur096.despr.model.operation.IOperation;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Abstraktní implementace operace se stará o načtení lokalizačních 
 * zpráv.
 *
 * @author Martin Šurkovský, sur096,
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com<a/>
 * @version 2012/02/25/12:27
 */
public abstract class AbstractCoinOperation implements IOperation {
    
    private transient ResourceBundle messages;
    
    public AbstractCoinOperation() {
        try {
            messages = ResourceBundle.getBundle(getClass().getCanonicalName());
        } catch (MissingResourceException ex) {
            try {
                messages = ResourceBundle.getBundle(getClass().getCanonicalName());
            } catch (MissingResourceException excp) {
                messages = null;
            }
        }
    }
    
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
