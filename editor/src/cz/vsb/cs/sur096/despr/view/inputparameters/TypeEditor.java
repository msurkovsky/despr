package cz.vsb.cs.sur096.despr.view.inputparameters;

import cz.vsb.cs.sur096.despr.model.operation.parameter.EInputParameterType;
import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.AbstractCellEditor;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;

/**
 * Editor typu vstupního parametru. Slouží pro přepínání mezi 
 * Vnitřním a vnějším typem vstupních parametrů. Využívá komponentu
 * {@code InputParameterTypePanel}.
 *
 * @author Martin Šurkovský, sur096 
 * <a href="mailto:martin.surkovky@gmail.com">martin.surkovsky at gmail.com</a>
 * @version 2011/08/15/19:00
 */
class TypeEditor 
            extends AbstractCellEditor 
            implements TableCellEditor, PropertyChangeListener {

    /** Editor typu vstupních parametru. */
    private InputParameterTypePanel choseTypePnl;
    
    /**
     * Poskytne aktuální nastavenou hodnotu typu./
     * @return aktuální hodnota nastaveného typu.
     */
    @Override
    public Object getCellEditorValue() {
        return choseTypePnl.getInputParametr();
    }

    /**
     * Poskytne editor hodnoty typu.
     * @param table tabulka ve které se editovaná hodnota nachází.
     * @param value hodnota.
     * @param isSelected je buňka vybrána?
     * @param row index řádku.
     * @param column index sloupce.
     * @return editor hodnoty.
     */
    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        
        choseTypePnl = new InputParameterTypePanel((EInputParameterType) value);
        choseTypePnl.addPropertyChangeListener(this);
        choseTypePnl.setBackground(table.getBackground());
        
        return choseTypePnl;
    }

    /**
     * Reaguje na ukončení editace typu vstupního parametru.
     * @param evt událost.
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals("edit_done")) {
            // editace vlastnosti zkoncila a hodnota se tak projevi
            fireEditingStopped();
        }
    }
}
