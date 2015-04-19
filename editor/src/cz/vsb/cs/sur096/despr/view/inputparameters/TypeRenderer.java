package cz.vsb.cs.sur096.despr.view.inputparameters;

import cz.vsb.cs.sur096.despr.model.operation.parameter.EInputParameterType;
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 * Renderer typu vstupního parametru. Slouží pro vizualizaci toho, jaký
 * je nastaven typ vstupního parametru (vnitřní/vnější).
 *
 * @author Martin Šurkovský, sur096 
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 * @version 2011/08/15/16:43
 */
class TypeRenderer implements TableCellRenderer {

    /**
	 * Pro vizualizaci nastaveného typu je vyžita stejná komponenta 
	 * jako v editoru .
	 */
    private InputParameterTypePanel choseTypePnl;
   
    /**
     * Poskytne renderer typu vstupního parametru operace.
     * @param table tabulka ve které se hodnota nachází.
     * @param value hodnota parametru.
     * @param isSelected je buňka vybrána?
     * @param hasFocus má buňka focus?
     * @param row index řádku tabulky.
     * @param column index sloupce tabulky.
     * @return editor renderer typu parametru.
     */
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        choseTypePnl = new InputParameterTypePanel((EInputParameterType) value);
        choseTypePnl.setBackground(table.getBackground());
        boolean editable = table.getModel().isCellEditable(row, column) && table.isEnabled();
        choseTypePnl.setEnabled(editable);
        Component[] comps = choseTypePnl.getComponents();
        for (Component c : comps) {
            c.setBackground(table.getBackground());
            c.setEnabled(editable);
        }
        
        return choseTypePnl;
    }
}
