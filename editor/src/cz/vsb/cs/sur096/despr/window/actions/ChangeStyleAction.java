
package cz.vsb.cs.sur096.despr.window.actions;

import cz.vsb.cs.sur096.despr.Despr;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

/**
 * Akce slouží pro změnu stylu který používá aplikace. Jsou k dispozici
 * všechny styly, ke kterým má aplikace přistup.
 *
 * @author Martin Šurkovský, sur096,
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 * @version 2012/03/06/10:23
 */
public class ChangeStyleAction extends AbstractAction {

    private String className;

    /**
     * Iniciuje aplikaci.
     * @param className jméno třídy styly který má být použit.
     */
    public ChangeStyleAction(String className) {
        this.className = className;
    }
    
    /**
     * Nastaví proměnou aplikace na novou hodnotu. Změny se projeví
	 * po znovu spuštění aplikace.
     * @param e 
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (className != null) {
            Despr.setProperty(Despr.STYLE, className);
            ShowMessageUtil.showRestartInfo();
        }
    }
}
