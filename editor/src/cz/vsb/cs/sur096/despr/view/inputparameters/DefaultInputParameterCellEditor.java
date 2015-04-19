
package cz.vsb.cs.sur096.despr.view.inputparameters;

import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JTable;

/**
 * Nejedná se ani tak o editor jako o zobrazení hodnoty. Pokud prostě daný typ
 * nemá explicitně definovaný editor, pak není možné jeho hodnotu editovat.
 * 
 * @author Martin Šurkovský, sur096,
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 * @version 2012/02/23/12:36
 */
class DefaultInputParameterCellEditor extends ParameterCellEditor {

    private final JLabel editor;
    private Object value;
    
    /**
     * Iniciuje editor.
     */
    public DefaultInputParameterCellEditor() {
        editor = new JLabel();
        editor.setEnabled(false);
    }
    
    /**
     * Poskytne nastavenou hodnotu.
     * @return nastavená hodnota.
     */
    @Override
    public Object getCellEditorValue() {
        return value;
    }

    /**
     * Poskytne editor hodnoty. V tomto případě je pouze hodnota zobrazena
	 * v {@code JLabel}. Politika editace hodnot je taková, že pokud není definován
	 * editor pro daný typ, tak není za editovatelnou považována.
     * @param table tabulka ve které se hodnota nachází.
     * @param value editovaná hodnota.
     * @param isSelected je buňka v tabulce vybrána.
     * @param row řádek tabulky.
     * @param column sloupec tabulky.
     * @return {@code JLabel} se zobrazenou hodnotou a nastavením {@code enabled=false}.
     */
    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        this.value = value;
        editor.setText(value.toString());
        return editor;
    }
}
