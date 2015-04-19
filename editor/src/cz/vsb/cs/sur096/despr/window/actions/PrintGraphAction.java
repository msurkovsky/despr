
package cz.vsb.cs.sur096.despr.window.actions;

import cz.vsb.cs.sur096.despr.view.GraphCanvas;
import cz.vsb.cs.sur096.despr.window.DesprIcons;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.KeyStroke;

/**
 * Akce slouží pro vytisknutí grafu do textové formy.
 *
 * @author Martin Šurkovský, sur096, 
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 * @version 2011/11/30/18:22
 */
public class PrintGraphAction extends BasicAbstractAction {

    private GraphCanvas gCanvas;
    
    /**
     * Iniciace akce.
     * @param gCanvas odkaz na plátno grafu.
     */
    public PrintGraphAction(GraphCanvas gCanvas) {
        
        super();
        putValue(SMALL_ICON, DesprIcons.getIcon(DesprIcons.PRINT_ICON, true));
        putValue(LARGE_ICON_KEY, DesprIcons.getIcon(DesprIcons.PRINT_ICON, false));
        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_P, KeyEvent.CTRL_MASK + KeyEvent.ALT_MASK));
        
        this.gCanvas = gCanvas;
    }
    
    /**
     * Vytiskne na standardní výstup graf v textové formě.
     * @param e 
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        System.out.println(gCanvas.getModel().toString());
    }
}