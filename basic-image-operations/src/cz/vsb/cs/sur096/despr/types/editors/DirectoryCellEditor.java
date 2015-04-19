
package cz.vsb.cs.sur096.despr.types.editors;

import cz.vsb.cs.sur096.despr.gui.components.FilesSelector;
import cz.vsb.cs.sur096.despr.types.Directory;
import cz.vsb.cs.sur096.despr.view.inputparameters.ParameterCellEditor;
import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JTable;

/**
 * Editor pro typ: {@code cz.vsb.cs.sur096.despr.types.Directory}.
 *
 * @author Martin Šurkovský, sur096,
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 * @version 2011/09/02/17:31
 */
public class DirectoryCellEditor extends ParameterCellEditor implements PropertyChangeListener {

    private final FilesSelector selectFilePnl;
    
    private Directory dir;
    
    public DirectoryCellEditor() {
        selectFilePnl = new FilesSelector();
        selectFilePnl.setSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        selectFilePnl.addPropertyChangeListener(this);
        dir = new Directory(selectFilePnl.getLastOpenFile());
    }
    
    @Override
    public Object getCellEditorValue() {
        return dir;
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        if (value instanceof Directory) {
            dir = (Directory) value;
            selectFilePnl.setFile(dir.getFile());
        }
        return selectFilePnl;
    }

    /**
     * Reaguje na změnu adresáře. Pokud byl adresář změněn je odchycena
	 * událost, nastaven nový adresář a vypnuta editace položky.
     * @param evt událost.
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals("lastOpenFile")) {
            Object o = evt.getNewValue();
            if (o instanceof File) {
                dir.setFile((File) o);
                fireEditingStopped();
            }
        }
    }
}
