
package cz.vsb.cs.sur096.despr.window.pluginmanager;

import cz.vsb.cs.sur096.despr.types.Copier;
import cz.vsb.cs.sur096.despr.types.Wrapper;
import cz.vsb.cs.sur096.despr.utils.DrawIcon;
import cz.vsb.cs.sur096.despr.view.inputparameters.ParameterCellEditor;
import cz.vsb.cs.sur096.despr.view.inputparameters.ParameterCellRenderer;
import java.awt.*;
import javax.swing.*;

/**
 * Renderer seznamu typových rozšíření. Typové rozšíření je reprezentováno
 * svým úplným názvem, ikonou indikující zda již bylo použito nebo ne
 * a barvou pozadí rozlišující jednotlivá typová rozšíření od sebe.
 *
 * @author Martin Śurkovský, sur096,
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 * @version 2012/02/18/15:28
 */
public class AvailableTypesListRenderer implements ListCellRenderer {
    
    private JPanel renderer, innerPanel;
    private JLabel typeName, lblIcon;
    private Color background, foreground, selectBackground, selectForeground;
    
    /**
     * Iniciuje renderer.
     */
    public AvailableTypesListRenderer() {
        
        foreground = UIManager.getColor("List.foreground");
        selectBackground = UIManager.getColor("List.selectionBackground");
        selectForeground =  UIManager.getColor("List.selectionForeground");
        
        renderer = new JPanel(new GridBagLayout());
        renderer.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.LINE_START;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(1, 1, 0, 1);
        
        innerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        innerPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        
        lblIcon = new JLabel();
        innerPanel.add(lblIcon);
        
        typeName = new JLabel();
        innerPanel.add(typeName);
        
        renderer.add(innerPanel, gbc);
    }
    
    /**
     * Poskytne grafickou komponentu která vizualizuje typové rozšíření.
     * @param list seznam typových rozšíření.
     * @param value vybraná hodnota.
     * @param index index vybrané hodnoty.
     * @param isSelected je hodnota vybrána?
     * @param cellHasFocus má focus?
     * @return grafickou komponentu reprezentující typové rozšíření.
     */
    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        
        if (value instanceof ExtensionType) {
            Class cls = ((ExtensionType) value).getType();
            if(Wrapper.class.isAssignableFrom(cls)) {
                background = DrawIcon.WRAPPER_COLOR;
            } else if (Copier.class.isAssignableFrom(cls)) {
                background = DrawIcon.COPIER_COLOR;
            } else if (ParameterCellRenderer.class.isAssignableFrom(cls)) {
                background = DrawIcon.RENDERER_COLOR;
            } else if (ParameterCellEditor.class.isAssignableFrom(cls)) {
                background = DrawIcon.EDITOR_COLOR;
            } else {
                background = Color.WHITE;
            }

            if (isSelected) {
                innerPanel.setBackground(selectBackground);
                typeName.setForeground(selectForeground);
            } else {
                innerPanel.setBackground(background);
                typeName.setForeground(foreground);
            }
            
            boolean used = ((ExtensionType) value).isUsed();
            Icon icon = new ImageIcon(DrawIcon.drawRectangleIcon(
                    7, 15, (used ? Color.BLACK : Color.WHITE), Color.BLACK));
            lblIcon.setIcon(icon);
            typeName.setText(cls.getCanonicalName());
            
        } else {
            typeName.setText("Unexpected type");
        }   
        return renderer;
    }
}