
package cz.vsb.cs.sur096.despr.view.inputparameters;

import cz.vsb.cs.sur096.despr.model.IInputParameter;
import cz.vsb.cs.sur096.despr.model.operation.parameter.EInputParameterType;
import java.awt.Component;
import java.awt.Container;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * Renderer jména parametru. Slouží pro zobrazení jména parametru v tabulce
 * pro editaci vstupních parametrů. Jedná se o první sloupec v tabulce.
 *
 * @author Martin Šurkovský, sur096 
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 * @version 2011/08/16/8:09
 */
class NameRenderer extends DefaultTableCellRenderer {
    
    /**
     * Poskytne komponentu reprezentující renderer jména parametru.
	 * Je využit výchozí renderer. 
     * @param table tabulka.
     * @param value jméno parametru.
     * @param isSelected je hodnota vybrána?
     * @param hasFocus má focus?
     * @param row index řádku.
     * @param column index slupce.
     * @return renderer pro jméno parametru
     */
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

        IInputParameter param = ((IInputParameter) value);
        Component component = super.getTableCellRendererComponent(table, param.getDisplayName(), isSelected, hasFocus, row, column);
        
        component.setBackground(table.getBackground());
        component.setForeground(table.getForeground());
        setBorder(null);
        
        boolean tableEnabled = table.isEnabled();
        boolean editable = tableEnabled && (param.getType() == EInputParameterType.INNER || !param.isUsed());
        setEnableComponent(editable, component);
        
        return component;
    }
    
    private void setEnableComponent(boolean enable, Component component) {
        component.setEnabled(enable);
        if (component instanceof Container) {
            Container cont = (Container) component;
            Component[] components = cont.getComponents();
            for (Component comp : components) {
                setEnableComponent(enable, comp);
            }
        }
    }
}
