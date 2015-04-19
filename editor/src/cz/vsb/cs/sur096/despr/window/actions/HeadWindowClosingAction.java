
package cz.vsb.cs.sur096.despr.window.actions;

import cz.vsb.cs.sur096.despr.Despr;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Akce uloží při zavření aplikace všechny své proměnné určující nastavení
 * aplikace.
 * @author Martin Šurkovský, sur096,
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 * @version 2012/03/06/20:57
 */
public class HeadWindowClosingAction extends WindowAdapter {
    
    /**
     * Uloží proměnné určující nastavení aplikace do souboru.
     * @param evt 
     */
    @Override
    public void windowClosed(WindowEvent evt) {
        Despr.saveDesprProperties();
        Despr.removeEmptyLogFile();
    }
}