
package cz.vsb.cs.sur096.despr.model.operation;

import java.io.Serializable;

/**
 * Rozhraní pomocí nějž muže uživatel definovat vlastní operace.
 * 
 * @author Martin Šurkovský, sur096,
 * <a href="martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 * @version 2011/07/03/18:12
 */
public interface IOperation extends Serializable {
    
    /**
     * Metoda provádějící výkonný kód operace. Jedná se o metodu která by měla
      * transformovat vstupní parametry na výstupní. Po provedení by měly 
      * být veškeré parametry, operace označené jako výstupní, naplněny daty.
      * 
     * @throws Exception během provádění operace může nastat jakákoliv chyba.
     * Pokud se tak stane. Zpracovávání celého procesu se přeruší a dá
     * se uživateli vědět o co šlo. Pokud se uvnitř metody odchytávají jakékoliv
     * výjimky ovlivňující korektní výpočet výstupních parametrů. Je více než
     * žádoucí je přeposlat dále.
     */
    public void execute() throws Exception;
    
    /**
     * Spřistupňuje lokalizační záznamy k dané operaci. Pokud je operace 
     * lokalizována tato metoda zpřístupní lokalizované názvy. Pokud ne
     * metoda vrátí {@code null}.
     * 
     * 
     * @param key klič k lokalizované hodnotě.
     * <b>Defaultní hodnotý které by měl každý lokalizační soubor obsahovat:</b>
     * <ul>
     *  <li><b>name</b> - lokalizovaný název operace</li>
     *  <li><b>description</b> - stručný popisek co operace dělá</li>
     *  <li><b>jmena označných parametrů</b> - všechny parametry, které byly
     * označeny ať už jako vstupní, či výstupní by měly v lokalizačním souboru
     * obsahovat svůj lokalizovaný název</li>
     * </ul>
     * Tyto klíčové hodnoty jsou vyžadovány ze strany aplikace. Pokud nebudou
     * nalezeny (metoda vrátí prázdnou hodnotu {@code null}) bude použit
     * jejich programový název.
     * @return  lokalizovanou hodnotu klíče {@code key}, pokud neexistuje
     * lokalizace k danému klíčí vrací {@code null}.
     */
    public String getLocalizeMessage(String key);
}
