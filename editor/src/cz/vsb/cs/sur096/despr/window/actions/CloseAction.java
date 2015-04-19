
package cz.vsb.cs.sur096.despr.window.actions;

import cz.vsb.cs.sur096.despr.window.HeadWindow;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.KeyStroke;

/**
 * Akce sloužící pro uzavření aplikace.
 *
 * @author Martin Šurkovský, sur096, 
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 * @version 2012/01/30/09:24
 */
public class CloseAction extends BasicAbstractAction {
    
	/** Okdaz na hlavní okno. */
    private HeadWindow hw;
    
	/**
	 * Iniciuje akci s odkazem na hlavní okno.
	 * @param hw odkaz na hlavní okno aplikace.
	 */
    public CloseAction(final HeadWindow hw) {
        super();
        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_F4, KeyEvent.ALT_MASK));
        this.hw = hw;
    }

	/**
	 * Zavolá na hlavní okno {@code dispose}.
	 */
    @Override
    public void actionPerformed(ActionEvent e) {
        hw.dispose();
    }
}
