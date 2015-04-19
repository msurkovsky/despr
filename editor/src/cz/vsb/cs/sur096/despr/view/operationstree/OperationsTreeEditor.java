
package cz.vsb.cs.sur096.despr.view.operationstree;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.EventObject;
import javax.swing.AbstractCellEditor;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.tree.TreeCellEditor;

/**
 * Editor položek ve stromu operací. Umožňuje editovat jména kategorií
 * ve stromu, nikoli však jména operací.
 *
 * @author Martin Šurkovský, sur096,
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 * @version 2012/02/14/09:34
 */
public class OperationsTreeEditor extends AbstractCellEditor implements TreeCellEditor {

    /** Editor položky. */
    private JTextField editor;
    /** Jméno kategorie.*/
    private String value;
    
    /**
     * Iniciuje editor položek stromu.
     */
    public OperationsTreeEditor() {
        editor = new JTextField();
        editor.addActionListener(new EditorActionListener());
        value = "";
    }
    
    /**
     * Poskytne nastavené jméno kategorie.
     * @return jména kategorie.
     */
    @Override
    public Object getCellEditorValue() {
        return value;
    }

    /**
     * Poskytne komponentu umožňující editaci položky stromu.
     * @param tree strom operací, jehož položky jsou editovány.
     * @param value hodnota položky (jméno kategorie).
     * @param isSelected je daný uzel vybrán?
     * @param expanded je daná kategorie rozbalena?
     * @param leaf jedná se o list stromu?
     * @param row index řádku.
     * @return {@code JTextField}, pomocí nějž je možné nastavit jméno.
     */
    @Override
    public Component getTreeCellEditorComponent(JTree tree, Object value, 
    boolean isSelected, boolean expanded, boolean leaf, int row) {
        
        if (value instanceof Category) {
            Object userObject = ((Category) value).getUserObject();
            editor.setText(userObject.toString());
        }
        
        return editor;
    }
    
    /**
     * Zjistí zda je možné položku editovat.
     * @param event událost.
     * @return {@code true} pokud je možné položku editovat a zárověn
	 * se jedná o kategorií, jinak {@code false}.
     */
    @Override
    public boolean isCellEditable(EventObject event) {
        
        if (event == null) return false;
        Object source = event.getSource();
        if (source == null || source instanceof JTree == false) return false;
        Object lastSelected = ((JTree) source).getLastSelectedPathComponent();
        if (lastSelected instanceof Category) {
            Category cat = (Category) lastSelected;
            if (cat.getParent() == null) {
                return false;
            } else if (cat.getUserObject() instanceof String) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
    
    /**
     * Definice akce pro editaci.
     */
    private class EditorActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JTextField txt = (JTextField) e.getSource();
            value = txt.getText();
            fireEditingStopped();
        }
    }
}
