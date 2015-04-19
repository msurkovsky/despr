
package cz.vsb.cs.sur096.despr.window.actions;

import cz.vsb.cs.sur096.despr.events.MessageListener;
import cz.vsb.cs.sur096.despr.events.MessageSupport;
import cz.vsb.cs.sur096.despr.view.operationstree.Category;
import cz.vsb.cs.sur096.despr.view.operationstree.OperationsWriter;
import cz.vsb.cs.sur096.despr.window.pluginmanager.UnusedOperationsWriter;
import java.awt.event.ActionEvent;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;

/**
 * Akce sloužící pro  uložení změn ve stromě operací.
 *
 * @author Martin Šurkovský, sur096,
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 * @author 2012/02/16/18:52
 */
public class SaveOperationsChangesAction extends BasicAbstractAction {

    private MessageSupport messageSupport;
    private Category rootCategory;
    private DefaultListModel unusedOperations;
    
    /**
     * Iniciace akce.
     * @param rootCategory kořenová kategorie stromu.
     * @param unusedOperations seznam nepoužitých operací.
     */
    public SaveOperationsChangesAction(Category rootCategory, DefaultListModel unusedOperations) {
        super();
        
        messageSupport = new MessageSupport(this);
        this.rootCategory = rootCategory;
        this.unusedOperations = unusedOperations;
    }
    
    /**
     * Přidá posluchače zajímajícího se o zasílané zprávy.
     * @param listener posluchač.
     */
    public void addMessageListener(MessageListener listener) {
        messageSupport.addMessageListener(listener);
    }
    
    /**
     * Smaže posluchače zajímajícího se o zasílané zprávy.
     * @param listener posluchač.
     */
    public void removeMessageListener(MessageListener listener) {
        messageSupport.removeMessageListener(listener);
    }
    
	/**
	 * Uloží změny ve stromu operací a v seznamu nepoužitých operací.
	 */
    @Override
    public void actionPerformed(ActionEvent e) {
        // save unused operations list
        UnusedOperationsWriter.save(unusedOperations);
        // save operations tree
        OperationsWriter.saveOperationsTree(rootCategory);
        
        String message = messages.getString("dialog.message.save_done",
                "Operation tree and unused operations list has been saved.");
        String title = messages.getString("dialog.title.save_done", "Operation tree saved");
        JOptionPane.showMessageDialog(null, message, title, JOptionPane.INFORMATION_MESSAGE);

        messageSupport.sendMessage("operations_has_saved");
    }
}
