
package cz.vsb.cs.sur096.despr.types.editors;

import cz.vsb.cs.sur096.despr.view.inputparameters.ParameterCellEditor;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.*;

/**
 * Editor pro textové vstupy.
 * 
 * @author Martin Šurkovský, sur096,
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 * @version 2012/02/22/16:31
 */
public class StringCellEditor extends ParameterCellEditor {

    private final JTextField editor;
    
    public StringCellEditor() {
        editor = new JTextField();
        // pridani akce pro uknoceni editace. Lze ji klasicky ukoncit zmacknutim
        // enteru nebo taky tabulatorem.
        Action finishAction = new StopEditingAction();
        editor.addActionListener(finishAction);
        editor.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_TAB, 0), "stopEditing");
        editor.getActionMap().put("stopEditing", finishAction);
    }
    
    @Override
    public Object getCellEditorValue() {
        return editor.getText();
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        if (value instanceof String) {
            editor.setText((String) value);
        }
        return editor;
    }
    
    private class StopEditingAction extends AbstractAction {
        
        @Override
        public void actionPerformed(ActionEvent e) {
            stopCellEditing();
        }
    }
}
