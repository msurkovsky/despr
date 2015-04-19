
package cz.vsb.cs.sur096.despr.window.actions;

import cz.vsb.cs.sur096.despr.window.DesprIcons;
import cz.vsb.cs.sur096.despr.window.HeadWindow;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

/**
 * Akce slouží pro přerušení běhu zpracovávání grafu.
 *
 * @author Martin Šurkovský, sur096, 
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 * @version 2012/01/26/19:14
 */
public class StopExecutingAction extends BasicAbstractAction {

    private HeadWindow headWindow;
    
    /**
     * Iniciuje akci.
     * @param headWindow odkaz na hlavní okno.
     */
    public StopExecutingAction(HeadWindow headWindow) {
        super();
        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, KeyEvent.ALT_MASK));
        putValue(SMALL_ICON, DesprIcons.getIcon(DesprIcons.STOP_ICON, true));
        putValue(LARGE_ICON_KEY, DesprIcons.getIcon(DesprIcons.STOP_ICON, false));
        this.headWindow = headWindow;
    }
    
    /**
     * Přeruší průběh zpracovávání grafu.
     * @param e 
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        
        int resultQuestion = JOptionPane.showConfirmDialog(headWindow, 
                messages.getString("confirm.message"), 
                messages.getString("confirm.title"),
                JOptionPane.YES_OPTION, JOptionPane.QUESTION_MESSAGE);
        
        if (resultQuestion == JOptionPane.YES_OPTION) {
            headWindow.getGraphController().stopExecuting();
            headWindow.setDesprState(HeadWindow.PAINTING);
        }
    }
}
