
package cz.vsb.cs.sur096.despr.window.actions;

import cz.vsb.cs.sur096.despr.Despr;
import cz.vsb.cs.sur096.despr.controller.IGraphController;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.logging.Level;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

/**
 * Akce sloužící pro spuštění verifikace grafu.
 *
 * @author Martin Šurkovský, sur096, 
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 * @version 2011/09/01/17:48
 */
public class VerifyGraphAction extends BasicAbstractAction {

    private IGraphController gController;
    
    /**
     * Iniciace akce.
     * @param gController odkaz na kontroler grafu.
     */
    public VerifyGraphAction(IGraphController gController) {
        super();
        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.getKeyText(KeyEvent.VK_F6)));
        
        this.gController = gController;
    }
    
    /**
     * Spustí verifikaci grafu.
     * @param e 
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            gController.verifyGraph();
            JOptionPane.showMessageDialog(Despr.getHeadWindow(), 
                                       messages.getString("verify_result"), 
                                       messages.getString("verify_result_title"), 
                                       JOptionPane.INFORMATION_MESSAGE);
            
        } catch (Exception ex) {
            Despr.showError(messages.getString("title.verify_excp", 
                    "Problem with verification"), ex, Level.WARNING, false);
        }
    }
}