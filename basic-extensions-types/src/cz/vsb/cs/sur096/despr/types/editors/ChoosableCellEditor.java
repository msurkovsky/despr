
package cz.vsb.cs.sur096.despr.types.editors;

import cz.vsb.cs.sur096.despr.types.Choosable;
import cz.vsb.cs.sur096.despr.view.inputparameters.ParameterCellEditor;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JTable;

/**
 * Editor vybratelné hodnoty. Jedná se o typy, reps. výčtové typy implementující
 * rozhraní {@code cz.vsb.cs.sur096.despr.types.Chooseable} z Despr-API.
 * 
 * @author Martin Śurkovský, sur096,
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 * @version 2012/02/21/18:46
 */
public class ChoosableCellEditor extends ParameterCellEditor {
    
    private Choosable choisedValue;
    private JComboBox editor;
    public ChoosableCellEditor() {
        editor = new JComboBox();
        editor.addActionListener(new EditorAction());
    }
    
    @Override
    public Object getCellEditorValue() {
        return choisedValue.getChoosedValue();
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
     
        if (value instanceof Choosable) {
            choisedValue = (Choosable) value;
            editor.setModel(new DefaultComboBoxModel(choisedValue.getAllPossibilities()));
            editor.setSelectedItem(choisedValue.getChoosedValue());
        }
        return editor;
    }
 
    class EditorAction implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            Object o = e.getSource();
            if (o instanceof JComboBox) {
                JComboBox combobox = (JComboBox) o;
                Object selected = combobox.getSelectedItem();
//                choisedValue.setChooseValue((Enum) selected);
                choisedValue = (Choosable) selected; // fix bug
                fireEditingStopped();
            }
        }
    }
}
