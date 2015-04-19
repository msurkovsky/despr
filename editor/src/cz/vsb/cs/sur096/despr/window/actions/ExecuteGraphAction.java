package cz.vsb.cs.sur096.despr.window.actions;

import cz.vsb.cs.sur096.despr.window.DesprIcons;
import cz.vsb.cs.sur096.despr.window.HeadWindow;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.KeyStroke;

/**
 * Akce slouží pro spuštění zpracování grafu.
 *
 * @author Martin Šurkovský, sur096, 
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 * @version 2011/09/01/18:16
 */
public class ExecuteGraphAction extends BasicAbstractAction {

    /** Odkaz na hlavní okno. */
    private HeadWindow headWindow;
    
    /**
     * Iniciuje akci. 
     * @param headWindow odkaz na hlavní okno.
     */
    public ExecuteGraphAction(HeadWindow headWindow) {
        super();
        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.getKeyText(KeyEvent.VK_F5)));
        putValue(SMALL_ICON, DesprIcons.getIcon(DesprIcons.PLAY_ICON, true));
        putValue(LARGE_ICON_KEY, DesprIcons.getIcon(DesprIcons.PLAY_ICON, false));
        
        this.headWindow = headWindow;
    }
    
    /**
     * Spustí zpracování grafu.
     * @param e 
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        headWindow.runGraph();
    }    
}