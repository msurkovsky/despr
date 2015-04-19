
package cz.vsb.cs.sur096.despr.view.portvizualization;

import cz.vsb.cs.sur096.despr.utils.DrawIcon;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;

/**
 * Renderer pro položky ve stromu typů. Jsou vypsány jednoduchá jména typů
 * kde je jako ikona použit čtvereček s použitou barvou a pokud je definována
 * specifikace pak je tučně vypsána mezi název a ikonu.
 *
 * @author Martin Šurkovský, sur096 
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 * @version 2012/03/04/00:29
 */
public class TypesTreeRenderer implements TreeCellRenderer {

    /** Renderer hodnoty */
    private JPanel renderer;
    /** Ikona typu. */
    private JLabel icon;
    /** Specifikace. */
    private JLabel specified;
    /** Jméno typu. */
    private JLabel name;
    /** Odkaz na defaultní renderer.*/
    private DefaultTreeCellRenderer defaultRenderer;
    
    /**
     * Iniciuje renderer.
     */
    public TypesTreeRenderer() {
        defaultRenderer = new DefaultTreeCellRenderer();
        renderer = new JPanel();
        icon = new JLabel();
        specified = new JLabel();
        specified.setFont(renderer.getFont().deriveFont(Font.BOLD));
        name = new JLabel();
        renderer.add(icon);
        renderer.add(specified);
        renderer.add(name);
    }
    
    /**
     * Poskytne komponentu která vyobrazí datový typ.
     * @param tree strom ve kterém jsou datové typy uloženy.
     * @param value konkrétní datový typ.
     * @param selected je položka vybrána?
     * @param expanded je položka rozbalena?
     * @param leaf jedná se o list stromu?
     * @param row index řádku.
     * @param hasFocus má focus?
     * @return renderer zobrazující typ s ikonou znázorňující použitou barvu
	 * tučně zvýrazněnou specifikací, pokud je definována jednoduchým jménem typu.
     */
    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, 
            boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        
        if (value instanceof TypesTree.RootNode) {
            name.setText(value.toString());
        } else if (value instanceof ITypeNode) {
            ITypeNode node = (ITypeNode) value;
            icon.setIcon(new ImageIcon(
                    DrawIcon.drawRectangleIcon(15, 15, node.getColor(), Color.BLACK)));
            if (!node.getSpecified().equals("")) {
                specified.setText(String.format("%s: ", node.getSpecified().toUpperCase()));
            } else {
                specified.setText("");
            }
            name.setText(node.getType().getSimpleName());
        }
        
        if (selected) {
            renderer.setBackground(defaultRenderer.getBackgroundSelectionColor());
            specified.setForeground(defaultRenderer.getTextSelectionColor());
            name.setForeground(defaultRenderer.getTextSelectionColor());
        } else {
            renderer.setBackground(defaultRenderer.getBackgroundNonSelectionColor());
            specified.setForeground(defaultRenderer.getTextNonSelectionColor());
            name.setForeground(defaultRenderer.getTextNonSelectionColor());
        }
        return renderer;
    }
}
