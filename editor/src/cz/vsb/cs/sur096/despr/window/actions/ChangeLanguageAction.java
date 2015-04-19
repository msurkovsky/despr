
package cz.vsb.cs.sur096.despr.window.actions;

import cz.vsb.cs.sur096.despr.Despr;
import java.awt.event.ActionEvent;
import java.util.Locale;
import javax.swing.AbstractAction;

/**
 * Akce sloužící pro změnu lokalizace.
 *
 * @author Martin Šurkovský, sur096,
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 * @version 2012/03/06/20:46
 */
public class ChangeLanguageAction extends AbstractAction {
    
    private Locale locale;
    
	/**
	 * Iniciuje akci.
	 * @param locale požadovaná lokalizace.
	 */
    public ChangeLanguageAction(Locale locale) {
        this.locale = locale;
    }

	/**
	 * Změní proměnou aplikace uchovávající informaci o 
	 * lokalizaci. Po novém spuštění aplikace se použije nová
	 * lokalizace.
	 */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (locale != null) {
            Despr.setProperty(Despr.SET_LOCALE, locale);
            ShowMessageUtil.showRestartInfo();
        }
    }
}