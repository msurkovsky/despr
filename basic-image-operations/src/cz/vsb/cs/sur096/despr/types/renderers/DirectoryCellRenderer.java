
package cz.vsb.cs.sur096.despr.types.renderers;

import cz.vsb.cs.sur096.despr.gui.components.FilesSelector;
import cz.vsb.cs.sur096.despr.types.Directory;
import cz.vsb.cs.sur096.despr.view.inputparameters.ParameterCellRenderer;
import java.awt.Component;
import javax.swing.JTable;

/**
 * Renderer pro typ: {@code cz.vsb.cs.sur096.despr.types.Direcotry}.
 *
 * @author Martin Šurkovský, sur096,
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 * @version 2012/02/14/21:43
 */
public class DirectoryCellRenderer implements ParameterCellRenderer {

    private FilesSelector renderer;
    
    public DirectoryCellRenderer() {
        renderer = new FilesSelector();
    }
    
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        if (value instanceof Directory) {
            renderer.setFile(((Directory) value).getFile());
        }
        boolean enabled = table.isEnabled() & table.getModel().isCellEditable(row, column);
        renderer.setEnabled(enabled);
        return renderer;
    }
}
