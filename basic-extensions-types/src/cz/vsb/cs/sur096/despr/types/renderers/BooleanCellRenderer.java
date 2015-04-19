
package cz.vsb.cs.sur096.despr.types.renderers;

import cz.vsb.cs.sur096.despr.view.inputparameters.ParameterCellRenderer;
import java.awt.Component;
import javax.swing.JCheckBox;
import javax.swing.JTable;

/**
 * Renderer booleovských (binárních) hodnot.
 * 
 * @author Martin Šurkovský, sur096 
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 * @version 2011/08/19/08:33
 */
public class BooleanCellRenderer implements ParameterCellRenderer {
    
    JCheckBox renderer;
    
    public BooleanCellRenderer() {
        renderer = new JCheckBox();
    }
    
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        boolean editable = table.isEnabled() & table.getModel().isCellEditable(row, column);
        renderer.setEnabled(editable);
        
        renderer.setSelected((Boolean) value);
        renderer.setBackground(table.getBackground());
        return renderer;
    }
}
