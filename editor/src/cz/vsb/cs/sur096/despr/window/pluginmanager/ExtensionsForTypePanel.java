
package cz.vsb.cs.sur096.despr.window.pluginmanager;

import cz.vsb.cs.sur096.despr.utils.DrawIcon;
import java.awt.*;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Vizuální komponenta reprezentující struktur {@code ExtensionsForType}.
 *
 * @author Martin Šurkovský, sur096,
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 * @version 2012/02/07/19:29
 */
public class ExtensionsForTypePanel extends JPanel {
    
    private JPanel innerPanel;
    private JPanel pnlType;
    
    /**
     * Iniciuje vizuální komponentu s danou strukturou.
     * @param extType typ se svými rozšířeními.
     */
    public ExtensionsForTypePanel(ExtensionsForType extType) {
        setLayout(new GridBagLayout());
        super.setBackground(Color.WHITE);
        init(extType);
    }
    
    /**
     * Inicializuje komponentu a na naplní ji daty.
     * @param extType typ se svými rozšířeními.
     */
    private void init(ExtensionsForType extType) {
     
        innerPanel = new JPanel(new GridBagLayout());
        innerPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.LINE_START;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.weighty = 0.0;
        
        Font defaultFont = getFont();
        Font fontType = defaultFont.deriveFont(Font.BOLD);
        pnlType = new JPanel(new FlowLayout(FlowLayout.LEADING));
        JLabel lblType = new JLabel(extType.getType().getCanonicalName());
        lblType.setFont(fontType);
        pnlType.add(lblType);
        pnlType.setBackground(new Color(0, 0, 255, 30));
        gbc.gridx = 0;
        gbc.gridy = 0;
        innerPanel.add(pnlType, gbc);
        
        JPanel pnlWrapper = getPanelForType(extType.getWrapper(), 
                DrawIcon.WRAPPER_COLOR);
        gbc.gridy = 1;
        innerPanel.add(pnlWrapper, gbc);
        
        JPanel pnlCopier = getPanelForType(extType.getCopier(), 
                DrawIcon.COPIER_COLOR);
        gbc.gridy = 2;
        innerPanel.add(pnlCopier, gbc);
        
        JPanel pnlCellRenderer = getPanelForType(extType.getRenderer(), 
                DrawIcon.RENDERER_COLOR);
        gbc.gridy = 3;
        innerPanel.add(pnlCellRenderer, gbc);
        
        JPanel pnlCellEditor = getPanelForType(extType.getEditor(), 
                DrawIcon.EDITOR_COLOR);
        gbc.gridy = 4;
        innerPanel.add(pnlCellEditor, gbc);
        
        //////////
        gbc = new GridBagConstraints();
        gbc.weightx = 1.0;
        gbc.weighty = 0.0;
        gbc.insets = new Insets(1, 1, 10, 1);
        gbc.anchor = GridBagConstraints.PAGE_START;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(innerPanel, gbc);
    }
    
    /**
     * Vygeneruje panel pro jedno s rozšíření. 
     * @param type rozšířující typ.
     * @param color barva reprezentující dané typ rozšíření.
     * @return panel reprezentující typ.
     */
    private JPanel getPanelForType(Class<?> type, Color color) {
        JPanel txt = new JPanel(new FlowLayout(FlowLayout.LEFT));
        txt.setBackground(Color.WHITE);
        JLabel lbl = new JLabel();
        if (type != null) {
            Image iconImg = DrawIcon.drawRectangleIcon(15, 15, color, Color.BLACK);
            lbl.setIcon(new ImageIcon(iconImg));
            lbl.setText(type.getCanonicalName());
            txt.add(lbl);
        } else {
            Image iconImg = DrawIcon.drawRectangleIcon(15, 5, color, Color.BLACK);
            lbl.setIcon(new ImageIcon(iconImg));
            txt.add(lbl);
        }
        txt.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.BLACK));
        
        return txt;
    }

    /**
     * Nastaví barvu pozadí komponentě.
     * @param background barva pozadí.
     */
    @Override
    public void setBackground(Color background) {
        if (pnlType != null) {
            pnlType.setBackground(background);
        } else {
            super.setBackground(background);
        }
    }
    
    /**
     * Nastaví barvu popředí komponentě.
     * @param foregorund barva popředí.
     */
    @Override
    public void setForeground(Color foregorund) {
        if (pnlType != null) {
            pnlType.setForeground(foregorund);
            Component[] comps = pnlType.getComponents();
            for (Component comp : comps) {
                if (comp instanceof JLabel) {
                    comp.setForeground(foregorund);
                }
            }
        } else {
            super.setForeground(foregorund);
        }
    }
}