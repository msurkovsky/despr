
package cz.vsb.cs.sur096.despr.operations.database;

import cz.vsb.cs.sur096.despr.model.operation.IOperation;
import cz.vsb.cs.sur096.despr.structures.LocalizeMessages;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;

/**
 * Abstraktní implementace operace se stará o načtení lokalizačních zpráv.
 *
 * @author Martin Šurkovský, sur096, 
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 * @version 2012/02/24/21:43
 */
public abstract class AbstractDatabaseOperation implements IOperation {
    
    private transient LocalizeMessages messages;
    
	/**
	 * Načte lokalizační zprávy, vč. zpráv patřících k
	 * nadřazeným třídám.
	 */
    public AbstractDatabaseOperation() {
        messages = loadLocalizeMessages(getClass(), null);
    }
    
	/**
	 * Poskytne lokalizační zprávu k danému klíči.
	 * @return lokalizovanou zprávu k danému klíči. Za podmínek, že 
	 * klíč není a seznam zpráv nejsou ({@code null}) a lokalizační 
	 * zpráva pro daný klíč existuje. Jinak vrátí {@code null}.
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
    
	/* Agreguje lokalizační soubory pro všechny nadtřídy až po 
	   {@code java.lang.Object} do jednoho objektu. */
    private LocalizeMessages loadLocalizeMessages(Class cls, LocalizeMessages lm) {

        if (lm == null) {
            lm = new LocalizeMessages();
        }
        
        if (Object.class.equals(cls)) {
            return lm;
        } else {
            ResourceBundle rb;
            try {
                rb = ResourceBundle.getBundle(cls.getName());
            } catch (MissingResourceException ex) {
                try {
                rb = ResourceBundle.getBundle(cls.getName(),
                        Locale.US);
                } catch (MissingResourceException excp) {
                    rb = null;
                }
            }
            if (rb != null) {
                Set<String> keys = rb.keySet();
                for (String key : keys) {
                    lm.putMessage(key, rb.getString(key));
                }
            }
            return loadLocalizeMessages(cls.getSuperclass(), lm);
        }
    }
    
}
