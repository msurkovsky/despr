
package cz.vsb.cs.sur096.despr.types.editors;

import cz.vsb.cs.sur096.despr.view.inputparameters.ParameterCellEditor;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import javax.swing.*;

/**
 * Editor hodnoty barev.
 *
 * @author Martin Šurkovský, sur096,
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 * @version 2012/02/24/16:41
 */
public class ColorEditor extends ParameterCellEditor {
    
    private final JPanel editor;
    private final JButton btnChooseColor;
    private Color color;
    
    public ColorEditor() {
        color =  new Color(0,0,0,0);
        btnChooseColor = new JButton();
        btnChooseColor.setIcon(getIcon(30, 10, color));
        btnChooseColor.addActionListener(new ChooseColorAction());
        editor = new JPanel(new FlowLayout(FlowLayout.LEFT));
        editor.add(btnChooseColor);
    }
    
    @Override
    public Object getCellEditorValue() {
        return color;
    }
    
    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        if (value instanceof Color) {
            color = (Color) value;
        }
        editor.setBackground(table.getBackground());
        btnChooseColor.setIcon(getIcon(30, 10, color));
        return editor;
    }
    
    private Icon getIcon(int width, int height, Color color) {
        
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics g = (Graphics) image.getGraphics();
        g.setColor(color);
        g.fillRect(0, 0, width, height);
        g.setColor(Color.BLACK);
        g.drawRect(0, 0, width - 1, height - 1);
        
        return new ImageIcon(image);
    }
    
    private class ChooseColorAction extends AbstractAction {

        @Override
        public void actionPerformed(ActionEvent e) {
            Color newColor = JColorChooser.showDialog(null, "Color", color);
            if (newColor != null) {
                color = newColor;
                stopCellEditing();
            } else {
                cancelCellEditing();
            }
        }
    }
}
