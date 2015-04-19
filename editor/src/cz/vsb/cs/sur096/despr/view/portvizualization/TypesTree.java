
package cz.vsb.cs.sur096.despr.view.portvizualization;

import cz.vsb.cs.sur096.despr.Despr;
import cz.vsb.cs.sur096.despr.structures.LocalizeMessages;
import cz.vsb.cs.sur096.despr.utils.ColorPalete;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Level;
import javax.swing.JColorChooser;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;

/**
 * Strom obsahující aktuální typovou strukturu, kterou využívají porty.
 *
 * @author Martin Šurkovský, sur096,
 * <a href="maitlo:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 * @version 2012/03/04/00:04
 */
public class TypesTree extends JTree {
    
    /** Lokalizační zprávy. */
    private transient LocalizeMessages messages;
    
    /**
     * Iniciuje strom typů.
     */
    public TypesTree() {
        messages = Despr.loadLocalizeMessages(getClass(), null, false);
        TreeNode root = new RootNode(Types.getTypes());
        TreeModel model = new DefaultTreeModel(root);
        setModel(model);
        setRootVisible(false);
        setCellRenderer(new TypesTreeRenderer());
        addMouseListener(new TreeItemAction());
    }
    
    /**
     * Jelikož typy netvoří normálně jeden strom a seznam stromů. Je definována
	 * společná kořenová operace, která ovšem neovlivní nastavení typů. To je 
	 * důvod proč není použitý stejný typ pro kořenovou operaci.
     */
    class RootNode implements TreeNode {

        List<ITypeNode> childeren;
        ITypeNodeComparator comparator = new ITypeNodeComparator();
        
        public RootNode(List<ITypeNode> childeren) {
            Collections.sort(childeren, comparator);
            this.childeren = childeren;
        }
        
        @Override
        public TreeNode getChildAt(int childIndex) {
            return childeren.get(childIndex);
        }

        @Override
        public int getChildCount() {
            return childeren.size();
        }

        @Override
        public TreeNode getParent() {
            return null;
        }

        @Override
        public int getIndex(TreeNode node) {
            return childeren.indexOf(node);
        }

        @Override
        public boolean getAllowsChildren() {
            return true;
        }

        @Override
        public boolean isLeaf() {
            return false;
        }

        @Override
        public Enumeration children() {
            return Collections.enumeration(childeren);
        }
        
        private class ITypeNodeComparator implements Comparator<ITypeNode> {

            @Override
            public int compare(ITypeNode o1, ITypeNode o2) {
                return o1.getType().getSimpleName().compareTo(o2.getType().getSimpleName());
            }
        }
    }
    
    /**
     * Portům lze měnit výchozí obarvení. Tato akce zobrazí
	 * u kořenových portů popup menu s možností editace barvy.
     */
    private class TreeItemAction extends MouseAdapter {
        
        @Override
        public void mousePressed(MouseEvent evt) {
            showPopup(evt);
        }
        
        @Override
        public void mouseReleased(MouseEvent evt) {
            showPopup(evt);
        }
        
        private void showPopup(MouseEvent evt) {
            if (evt.isPopupTrigger()) {
                JPopupMenu menu = new JPopupMenu("Edit");
                JMenuItem editColor = new JMenuItem(
                        messages.getString("popup.title.edit_color", "Edit color"));
                editColor.setToolTipText(
                        messages.getString("popup.edit_color.tooltip", "Display color chooser panel"));
                editColor.addActionListener(new EditColorAction());
                
                menu.add(editColor);
                Object lastSelected = getLastSelectedPathComponent();
                if (lastSelected instanceof ITypeNode && ((ITypeNode) lastSelected).getParent() == null) {
                    menu.show(evt.getComponent(), evt.getX(), evt.getY());
                }
            }
        }
        
        /**
         * Akce sloužící pro editaci barvy konkrétního typu.
         */
        class EditColorAction implements ActionListener {

            @Override
            public void actionPerformed(ActionEvent e) {
                Object o = getLastSelectedPathComponent();
                if (o instanceof ITypeNode) {
                    ITypeNode node = (ITypeNode) o;
                    Color newColor = JColorChooser.showDialog(
                            null, messages.getString("title.color", "Select color"), 
                            node.getColor());
                    
                    if (newColor != null && !node.getColor().equals(newColor)) {
                        if (ColorPalete.isColorUsed(newColor)) {
                            String title = messages.getString("title.color_is_used_excp",
                                                              "Color is used");
                            String message = messages.getString("exception_color_is_used",
                                    "Color is used");
                            Despr.showError(title, 
                                    new RuntimeException(
                                    String.format("%s 'Color = (%d,%d,%d)'",
                                            message, newColor.getRed(), 
                                            newColor.getGreen(), 
                                            newColor.getBlue())),
                                    Level.WARNING, false);
                        } else {
                            if (!node.getColor().equals(newColor)) {
                                ColorPalete.setUsed(node.getType(), node.getColor(), false);
                                node.setColor(newColor);
                                ColorPalete.setUsed(node.getType(), newColor, true);
                                repaint();
                            }
                        }
                    }
                }
            }
        }
    }
}