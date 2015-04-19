
package cz.vsb.cs.sur096.despr.types.renderers;

import cz.vsb.cs.sur096.despr.view.inputparameters.ParameterCellRenderer;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import javax.swing.*;

/**
 * Renderer pro typ {@code java.awt.Color}.
 *
 * @author Martin Šurkovský, sur096, 
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 * @version 2012/02/23/16:56
 */
public class ColorRenderer implements ParameterCellRenderer {

    private final JPanel renderer;
    private final JButton btnChooseColor;
    private Color color;
    
    public ColorRenderer() {
        color = new Color(0,0,0,0);
        renderer = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnChooseColor = new JButton();
        btnChooseColor.setIcon(getIcon(30, 10, color));
        renderer.add(btnChooseColor);
    }
    
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        if (value instanceof Color) {
            color = (Color) value;
            btnChooseColor.setIcon(getIcon(30, 10, color));
        }
        renderer.setBackground(table.getBackground());
        boolean enabled = table.isEnabled() & table.getModel().isCellEditable(row, column);
        btnChooseColor.setEnabled(enabled);
        return renderer;
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
}
