package cz.vsb.cs.sur096.despr.view.inputparameters;

import javax.swing.AbstractCellEditor;
import javax.swing.table.TableCellEditor;

/**
 * Abstraktní třída slouží pro označení tříd, které tvoří editor
 * daného typu. Ten je pak využit pro nastavování vstupních parametrů
 * v {@code ParametersTable}.
 * <br>
 * Příklad editoru pro booleovskou hodnotu.
 * <pre>
 * <code>
 * public class BooleanCellEditor extends ParameterCellEditor {
 *
 *   JCheckBox editor;
 *   boolean isSelected;
 *  
 *   public BooleanCellEditor() {
 *       editor = new JCheckBox();
 *       editor.addActionListener(new EditorAction());
 *   }
 *   
 *   &#064;Override
 *   public Object getCellEditorValue() {
 *       return editor.isSelected();
 *   }
 *
 *   &#064;Override
 *   public Component getTableCellEditorComponent(JTable table, Object value, 
 *            boolean isSelected, int row, int column) {
 * 
 *       isSelected = (Boolean) value;
 *       editor.setSelected(isSelected);
 *       editor.setBackground(table.getBackground());
 *       return editor;
 *   }
 *   
 *   private class EditorAction implements ActionListener {
 *
 *       &#064;Override
 *       public void actionPerformed(ActionEvent e) {
 *           fireEditingStopped();
 *       }
 *   }
 * }
 * </code>
 * </pre>
 * 
 * @author Martin Šurkovský, sur096,
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 * @version 2012/02/09/10:43
 */
public abstract class ParameterCellEditor 
            extends AbstractCellEditor implements TableCellEditor {
    
}
