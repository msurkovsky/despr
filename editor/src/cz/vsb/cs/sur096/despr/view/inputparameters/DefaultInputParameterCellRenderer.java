
package cz.vsb.cs.sur096.despr.view.inputparameters;

import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * Výchozí renderer hodnoty v tabulce vstupních parametrů.
 *
 * @author Martin Šurkovský, sur096 
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 * @version 2011/08/17/19:20
 * 
 */
class DefaultInputParameterCellRenderer extends DefaultTableCellRenderer {
    
    /**
     * Poskytne jako renderer hodnoty {@code JLabel} s textovou reprezentací
	 * hodnoty.
     * @param table tabulka ve které se nachází parametr.
     * @param value hodnota parametru.
     * @param isSelected je buňka vybrána?
     * @param hasFocus má focus?
     * @param row index řádku.
     * @param column index sloupce.
     * @return {@code JLabel} s textovou reprezentací hodnoty.
     */
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
                          boolean isSelected, boolean hasFocus, int row, int column) {
        
        Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        boolean tableEnabled = table.isEnabled();
        boolean editable = tableEnabled && table.getModel().isCellEditable(row, column);
        comp.setEnabled(editable);
        return comp;
    }
}
