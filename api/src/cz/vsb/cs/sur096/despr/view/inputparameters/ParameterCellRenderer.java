package cz.vsb.cs.sur096.despr.view.inputparameters;

import javax.swing.table.TableCellRenderer;

/**
 * Rozhraní označující třídy, které tvoří renderer neboli způsob vyobrazení
 * hodnoty daného typu. Ty jsou dále využity pro zobrazení hodnot v tabulce pro
 * nastavení vstupních parametrů operací ({@code PropertiesTable}).
 * 
 * Příklad rendereru pro booleovský typ:
 * <pre>
 * <code>
 * public class BooleanCellRenderer implements ParameterCellRenderer {
 *
 *   JCheckBox renderer;
 *   
 *   public BooleanCellRenderer() {
 *       renderer = new JCheckBox();
 *   }
 *   
 *   &#064;Override
 *   public Component getTableCellRendererComponent(JTable table, Object value, 
 *            boolean isSelected, boolean hasFocus, int row, int column) {
 *       renderer.setEnabled(table.isEnabled());
 *       
 *       renderer.setSelected((Boolean) value);
 *       renderer.setBackground(table.getBackground());
 *       boolean editable = table.getModel().isCellEditable(row, column);
 *       if (!editable) {
 *           renderer.setEnabled(false);
 *       }
 *       return renderer;
 *   }
 * }
 * </code>
 * </pre>
 * @author Martin Šurkovský, sur096,
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 * @version 2012/02/09/10:52
 */
public interface ParameterCellRenderer extends TableCellRenderer {
    
}
