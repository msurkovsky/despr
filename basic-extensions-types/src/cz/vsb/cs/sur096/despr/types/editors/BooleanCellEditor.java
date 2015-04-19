
package cz.vsb.cs.sur096.despr.types.editors;

import cz.vsb.cs.sur096.despr.view.inputparameters.ParameterCellEditor;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JCheckBox;
import javax.swing.JTable;

/**
 * Editor booleovských ({@code java.lang.Boolean}) typů.
 * 
 * @author Martin Šurkovský, sur096 
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 * @version 2011/08/19/08:30
 */
public class BooleanCellEditor extends ParameterCellEditor {

    private final JCheckBox editor;
    
    public BooleanCellEditor() {
        editor = new JCheckBox();
        editor.addActionListener(new EditorAction());
    }
    
    @Override
    public Object getCellEditorValue() {
        return editor.isSelected();
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        boolean setValue = (Boolean) value;
        editor.setSelected(setValue);
        editor.setBackground(table.getBackground());
        return editor;
    }
    
    private class EditorAction implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            fireEditingStopped();
        }
    }
}
