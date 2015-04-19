
package cz.vsb.cs.sur096.despr.types.editors;

import cz.vsb.cs.sur096.despr.gui.components.FilesSelector;
import cz.vsb.cs.sur096.despr.view.inputparameters.ParameterCellEditor;
import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JTable;

/**
 * Editor souborů {@code java.io.File}. Využívá komponentu 
 * {@code cz.vsb.cs.sur096.despr.gui.components.FilesSelector} pro výběr souboru.
 * Lze načítat jakékoliv soubory, složky, soubory jakéhokoliv typu. Pro upřesnění
 * je možné tento editor rozšířit a definovat selectoru typ souboru jaký má být
 * vybírán popř. definovat filtr. {@link cz.vsb.cs.sur096.despr.types.editors.ImageCellEditor}.
 * 
 * @author Martin Šurkovský, sur096,
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 * @version 2012/02/21/20:12
 */
public class FileCellEditor extends ParameterCellEditor implements PropertyChangeListener {

    protected FilesSelector editor;
    
    public FileCellEditor() {
        editor = new FilesSelector();
        editor.setSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        editor.addPropertyChangeListener(this);
    }
    
    @Override
    public Object getCellEditorValue() {
        return editor.getLastOpenFile();
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, 
            boolean isSelected, int row, int column) {
        
        if (value instanceof File) {
            editor.setFile((File) value);
        }
        return editor;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals("lastOpenFile")) {
            fireEditingStopped();
        }
    }
}
