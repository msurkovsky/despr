
package cz.vsb.cs.sur096.despr.window.actions;

import cz.vsb.cs.sur096.despr.Despr;
import cz.vsb.cs.sur096.despr.structures.LocalizeMessages;
import javax.swing.AbstractAction;

/**
 * Základní implementace akce v aplikaci. Stará se o načtení 
 * lokalizačních zpráv (jména a krátkého popisku).
 *
 * @author Martin Šurkovský, sur096, 
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 * @version 2012/01/26/18:26
 */
public abstract class BasicAbstractAction extends AbstractAction {

	/** 
	 * Seznam lokalizačních zpráv. Aplikace počítá se dvěma definovanými
	 * klíčí:
	 * <ol>
	 *  <li><b>name</b> jméno akce,</li>
	 *  <li><b>descriptio</b> krátký popisek akce.</li>
	 * </ol>
	 */
    protected LocalizeMessages messages;
    
    /**
     * Načte lokalizační soubor a z něj přiřadí akci jméno
	 * a krátký popisek.
     */
    public BasicAbstractAction() {
        messages = Despr.loadLocalizeMessages(getClass(), null, false);
        
        putValue(NAME, messages.getString("name", getClass().getSimpleName()));
        putValue(SHORT_DESCRIPTION, messages.getString("description"));
    }
}
