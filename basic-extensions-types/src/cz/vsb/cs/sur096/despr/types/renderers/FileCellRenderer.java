
package cz.vsb.cs.sur096.despr.types.renderers;

import cz.vsb.cs.sur096.despr.gui.components.FilesSelector;
import cz.vsb.cs.sur096.despr.view.inputparameters.ParameterCellRenderer;
import java.awt.Component;
import java.io.File;
import javax.swing.JTable;

/**
 * Renderer souborů {@ java.io.File}. Využívá komponentu 
 * {@code cz.vsb.cs.sur096.despr.gui.components.FileSelector} pro zobrazení 
 * cesty k souboru.
 * 
 * @author Martin Šurkovský, sur096,
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 * @version 2012/02/21/20:03
 */
public class FileCellRenderer implements ParameterCellRenderer {
    
    private final FilesSelector renderer;
    
    public FileCellRenderer() {
        renderer = new FilesSelector();
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, 
            boolean isSelected, boolean hasFocus, int row, int column) {
        boolean editable = table.isEditing() & table.getModel().isCellEditable(row, column);
        renderer.setEnabled(editable);
        if (value instanceof File) {
            renderer.setFile((File) value);
        }
        return renderer;
    }
}
