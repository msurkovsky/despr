
package cz.vsb.cs.sur096.despr.structures;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * Tento objekt je možné využít pro uložení lokalizačních zprav. Je vhodný
 * pro případy kdy operace rozšiřuje již nějakou jinou operaci. Je tak možné
 * shluknout více lokalizačních souboru do jedné kolekce. A není třeba 
 * mít všechny lokalizační zprávy kopírované pro každou metodu zvlášť
 * 
 * @author Martin Šurkovský, sur096, 
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 * @version 2012/02/02/19:22
 */
public class LocalizeMessages {
    
    /**
     * Seznam lokalizovaných zpráv (klíč : hodnota).
     */
    private Map<String, String> messages;
    
    /**
     * Inicializuje prázdnou strukturu pro zprávy.
     */
    public LocalizeMessages() {
        messages = new HashMap<String, String>();
    }
    
    /**
     * Inicializuje seznam zprav a naplní je sadou zprav s propertíes.
     * @param properties seznam lokalizačních zpráv.
     */
    public LocalizeMessages(Properties properties) {
        this();
        putValues(properties);
    }
    
    /**
     * Vrátí lokalizační zprávu pro daný klíč.
     * @param key klíč zprávy.
     * @return lokalizovaná hodnota.
     */
    public String getString(String key) {
        return getString(key, null);
    }
    
    /**
     * Vrátí lokalizační zprávu pro daný klíč. Navíc je možné definovat 
     * defaultní hodnotu, která je použita pokud pro daný klíč není zpráva
     * nalezena.
     * @param key klíč zprávy.
     * @param defaultMessage zpráva, která bude použita pokud nebude
     * nalezena lokalizovaná hodnota.
     * @return lokalizovaná hodnota.
     */
    public String getString(String key, String defaultMessage) {
        if (messages.containsKey(key)) {
            return messages.get(key);
        } else {
            return defaultMessage;
        }
    }
    
    /**
     * Umožňuje přidat další pár klíč:lokalizační zpráva do kolekce.
     * @param key klíč zprávy.
     * @param value lokalizovaná hodnota.
     */
    public void putMessage(String key, String value) {
        messages.put(key, value);
    }
    
    /**
     * Přidá najednou celou seznam lokalizačních zpráv.
     * @param properties seznam lokalizačních zpráv.
     */
    public final void putValues(Properties properties) {
        Set keys = properties.keySet();
        for (Object key : keys) {
            String k = (String) key;
            messages.put(k, properties.getProperty(k));
        }
    }
    
    /**
     * Přidá najednou celou seznam lokalizačních zpráv.
     * @param lms seznam lokalizačních zpráv.
     */
    public void putValues(LocalizeMessages lms) {
        Set<String> keys = lms.getKeys();
        for (String key : keys) {
            messages.put(key, lms.getString(key));
        }
    }
    
    /**
     * Poskytne seznam všech klíčů k lokalizačním zprávám.
     * @return  seznam klíčů.
     */
    public Set<String> getKeys() {
        return messages.keySet();
    }
}
