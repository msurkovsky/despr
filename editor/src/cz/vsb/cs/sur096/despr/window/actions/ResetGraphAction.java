
package cz.vsb.cs.sur096.despr.window.actions;

import cz.vsb.cs.sur096.despr.model.IRootOperationModel;
import cz.vsb.cs.sur096.despr.view.GraphCanvas;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.List;
import javax.swing.KeyStroke;

/**
 * Akce slouží pro restart iterátorů vstupních kořenových operací.
 *
 * @author Martin Šurkovský, sur096, 
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 * @version 2012/02/04/16:50
 */
public class ResetGraphAction extends BasicAbstractAction {

    private GraphCanvas gCanvas;
    
    /**
     * Iniciuje akci.
     * @param gCanvas odkaz na plátno grafu. 
     */
    public ResetGraphAction(GraphCanvas gCanvas) {
        this.gCanvas = gCanvas;
        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_R, KeyEvent.CTRL_MASK + KeyEvent.ALT_MASK));
    }
    
    /**
     * Provede restart iterátorů všech kořenových operací.
     * @param e 
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        List<IRootOperationModel> rootOperations = gCanvas.getModel().getRootOperations();
        for (IRootOperationModel rootOpModel : rootOperations) {
            rootOpModel.resetIterator();
        }
    }
}
