
package cz.vsb.cs.sur096.despr.window.actions;

import cz.vsb.cs.sur096.despr.view.GraphCanvas;
import cz.vsb.cs.sur096.despr.view.SelectedObjects;
import cz.vsb.cs.sur096.despr.window.DesprIcons;
import cz.vsb.cs.sur096.despr.window.HeadWindow;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.KeyStroke;

/**
 * Akce smaže graf a vytvoří čisté plátno pro novou práci..
 *
 * @author Martin Šurkovský, sur096, 
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 * @version 2011/09/01/18:38
 */
public class NewGraphAction extends BasicAbstractAction {

    private GraphCanvas gCanvas;
    private HeadWindow hw;
    /**
     * Iniciuje akci.
     * @param gCanvas odkaz na plátno grafu. 
     */
    public NewGraphAction(HeadWindow hw, GraphCanvas gCanvas) {

        super();
        putValue(SMALL_ICON, DesprIcons.getIcon(DesprIcons.NEW_GRAPH_ICON, true));
        putValue(LARGE_ICON_KEY, DesprIcons.getIcon(DesprIcons.NEW_GRAPH_ICON, false));
        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_N, KeyEvent.CTRL_MASK));
        this.gCanvas = gCanvas;
        this.hw = hw;
    }
    
    /**
     * Smaže plátno grafu a vymaže správce vybratených objektů.
     * @param e 
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        SelectedObjects so = gCanvas.getSelectedObjects();
        so.setSelectObjects();
        gCanvas.clearCanvas();
        hw.setLastOpenGraph(null);
    }
}
