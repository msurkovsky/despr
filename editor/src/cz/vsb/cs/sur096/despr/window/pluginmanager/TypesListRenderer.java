
package cz.vsb.cs.sur096.despr.window.pluginmanager;

import java.awt.Component;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;

/**
 * Definice rendereru pro seznam typů je jejich typových rozšíření.
 *
 * @author Martin Šurkovský, sur096,
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 * @version 2012/02/17/17:42
 */
public class TypesListRenderer implements ListCellRenderer {

    
    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        if (value instanceof ExtensionsForType) {
            
            if (isSelected) {
                ExtensionsForTypePanel pnl = new ExtensionsForTypePanel((ExtensionsForType) value);
                pnl.setBackground(UIManager.getColor("List.selectionBackground"));
                pnl.setForeground(UIManager.getColor("List.selectionForeground"));
                return pnl;
            } else {
                ExtensionsForTypePanel pnl = new ExtensionsForTypePanel((ExtensionsForType) value);
                return pnl;
            }
        } else {
            return new JPanel();
        }
    }
}