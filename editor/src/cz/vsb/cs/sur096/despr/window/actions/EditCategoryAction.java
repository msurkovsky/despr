
package cz.vsb.cs.sur096.despr.window.actions;

import cz.vsb.cs.sur096.despr.view.operationstree.OperationsTreeRenderer;
import cz.vsb.cs.sur096.despr.window.pluginmanager.EditableOperationsTree;
import cz.vsb.cs.sur096.despr.window.pluginmanager.LocalizeCategoryPanel;
import java.awt.event.ActionEvent;
import javax.swing.JDialog;

/**
 * Akce pro editaci jména kategorie ve stromě operací.
 *
 * @author Martin Šurkovský, sur096,
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 * @version 2012/02/17/09:30
 */
public class EditCategoryAction extends BasicAbstractAction {

    /** Odkaz na editovatelný strom operací.*/
    private EditableOperationsTree tree;
    /** Jméno kategorie která má být editována.*/
    private String key;
    
    /**
     * Iniciuje akci.
     * @param tree strom operací.
     * @param key jméno kategorie které má být editováno.
     */
    public EditCategoryAction(EditableOperationsTree tree, String key) {
        super();
        this.tree = tree;
        this.key = key;
    }

    /**
     * Zobrazí dialog s lokalizační tabulkou kde je možné upravit
	 * názvy kategorií.
     * @param e 
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        JDialog dialog = new LocalizeCategoryPanel(
                key, (OperationsTreeRenderer) tree.getCellRenderer());
        dialog.pack();
        dialog.setVisible(true);
    }
}
