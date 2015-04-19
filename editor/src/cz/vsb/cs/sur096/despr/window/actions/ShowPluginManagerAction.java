
package cz.vsb.cs.sur096.despr.window.actions;

import cz.vsb.cs.sur096.despr.view.operationstree.OperationsTree;
import cz.vsb.cs.sur096.despr.window.pluginmanager.PluginManagerPanel;
import java.awt.Window;
import java.awt.event.ActionEvent;
import javax.swing.JDialog;

/**
 * Akce vyvolá okno s plug-in managerem.
 *
 * @author Martin Šurkovský, sur096,
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 * @version 2012/02/12/17:36
 */
public class ShowPluginManagerAction extends BasicAbstractAction {

    private Window own;
    private OperationsTree operationsTree;
    
    /** 
     * Iniciace akce.
     * @param own vlastník dialogu.
     * @param operationsTree odkaz na strom operací.
     */
    public ShowPluginManagerAction(Window own, OperationsTree operationsTree) {
        super();
        this.own = own;
        this.operationsTree = operationsTree;
    }
    
    /**
     * Vyvolá okno s plug-in managerem. Okno je nastaveno na {@code modal=true}
     * @param e 
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        JDialog dialog = new PluginManagerPanel(own, operationsTree);
        dialog.setTitle(messages.getString("name", "Plug-in manager"));
        dialog.pack();
        dialog.setVisible(true);
    }
}
