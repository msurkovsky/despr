package cz.vsb.cs.sur096.despr.types.renderers;

import cz.vsb.cs.sur096.despr.types.Choosable;
import cz.vsb.cs.sur096.despr.view.inputparameters.ParameterCellRenderer;
import java.awt.Component;
import java.awt.Font;
import javax.swing.JLabel;
import javax.swing.JTable;

/**
 * Renderer hodnoty kterou lze vybrat s konečné množiny možností. 
 * To jsou typy implementující rozhraní 
 * {@code cz.vsb.cs.sur096.despr.types.Chooseable} z Despr-API.
 * 
 * @author Martin Šurkovský, sur096, 
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 * @version 2012/02/21/17/08
 */
public class ChoosableCellRenderer implements ParameterCellRenderer {

    private JLabel renderer;
    
    public ChoosableCellRenderer() {
        renderer = new JLabel();
        Font f = renderer.getFont();
        renderer.setFont(f.deriveFont(Font.BOLD));
    }
    
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        
        if (value instanceof Choosable) {
            Choosable choosable = (Choosable) value;
            renderer.setText(choosable.getChoosedValue().name());
        }
        renderer.setBackground(table.getBackground());
        boolean isEditable = table.isEnabled() & table.getModel().isCellEditable(row, column);
        renderer.setEnabled(isEditable);
        
        return renderer;
    }
}
