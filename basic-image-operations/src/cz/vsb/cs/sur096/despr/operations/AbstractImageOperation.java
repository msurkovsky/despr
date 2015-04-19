
package cz.vsb.cs.sur096.despr.operations;

import cz.vsb.cs.sur096.despr.model.operation.IOperation;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Abstraktní implementace rozhraní {@code IOperation} poskytuje základní přístup
 * k lokalizačním zprávám, pokud existují. Lokalizační soubor musí být umístěn 
 * ve stejném adresáři jako zkompilovaný soubor ve formátu: 
 * {@code jméno třídy_jazyk_země}, př.: {@code Print_en_US.properties}.
 *
 * @author Martin Šurkovský, sur096,
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 * @version 2012/02/24/09:16
 */
public abstract class AbstractImageOperation implements IOperation {
    
    /**
     * Lokalizační zprávy.
     */
    private transient ResourceBundle messages;
    
    /**
     * Konstruktor se pokusí načíst lokalizační zprávy pro daný jazyk. 
	 * Pokud se mu to nepodaří, pokusí se ještě načíst anglickou lokalizaci:
	 * {@code Locale.US}.
     */
    public AbstractImageOperation() {
        
        try {
            // pokusim se nalezt lokalizacni sobour pro dany jazyk.
            messages = ResourceBundle.getBundle(getClass().getCanonicalName());
        } catch (MissingResourceException ex) {
            try {
                // pokud se to nepovede pokusim se najit anglicky lokalizacni
                // sobuor
                messages = ResourceBundle.getBundle(getClass().getCanonicalName(), 
                       Locale.US);
            } catch (MissingResourceException excp) {
                // pokud ani ten neexistuje vratim klic
                messages = null;
            }
        }
    }

    /**
     * Metoda vracející lokalizační zprávy pro dané hodnoty klíče.
     * @param key klič k lokalizační zprávě.
     * @return pokud je {@code key != null} a zároveň byl načten lokalizační soubor,
	 * metoda se pokusí nalézt příslušnou lokalizační zprávu. Pokud uspěje vrátí ji,
	 * pokud ne vrátí jméno třídy. Pokud by měl klíč nulovou hodnotu je vrácen
	 * prázdný řetězec.
     */
    @Override
    public String getLocalizeMessage(String key) {
        
        if (key == null) {
            return "";
        } else if (messages == null) {
            return null;
        }
        
        try {
            return messages.getString(key);
        } catch (MissingResourceException ex) {
            return null;
        }
    }
}
