
package cz.vsb.cs.sur096.despr.window.pluginmanager;

import cz.vsb.cs.sur096.despr.Despr;
import cz.vsb.cs.sur096.despr.events.MessageListener;
import cz.vsb.cs.sur096.despr.events.MessageSupport;
import cz.vsb.cs.sur096.despr.structures.LocalizeMessages;
import cz.vsb.cs.sur096.despr.types.Copier;
import cz.vsb.cs.sur096.despr.types.Wrapper;
import cz.vsb.cs.sur096.despr.utils.ExtensionsOfTypes;
import cz.vsb.cs.sur096.despr.view.inputparameters.ParameterCellEditor;
import cz.vsb.cs.sur096.despr.view.inputparameters.ParameterCellRenderer;
import java.awt.Component;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import javax.swing.*;

/**
 * Komponenta pro seznam typu a jejich typových rozšíření.
 *
 * @author Martin Šurkovský, sur096,
 * @version 2012/02/17/17:37
 */
public class TypesList extends JList {

    private transient LocalizeMessages messages;
    private List<DeletedItemsListener> deleteItemListeners;
    private MessageSupport messageSupport;
    
    /**
     * Iniciace seznam typů.
     */
    public TypesList() {
        messages = Despr.loadLocalizeMessages(getClass(), null, false);
        deleteItemListeners = new ArrayList<DeletedItemsListener>();
        messageSupport = new MessageSupport(this);
        setCellRenderer(new TypesListRenderer());
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        addMouseListener(new TypesListAction());
        setTransferHandler(new TypesListTransferHandler());
    }
    
    /**
     * Přidá posluchače reagujícího na smazané položky.
     * @param l posluchač.
     */
    public void addDeleteItemListener(DeletedItemsListener l) {
        deleteItemListeners.add(l);
    }
    
    /**
     * Smaže posluchače reagujícího na smazané položky.
     * @param l posluchač.
     */
    public void removeDeleteItemListener(DeletedItemsListener l) {
        deleteItemListeners.remove(l);
    }
    
    /**
     * Přidá posluchače reagujícího na zprávy zasílané komponentou.
     * @param l posluchač.
     */
    public void addMessageListener(MessageListener l) {
        messageSupport.addMessageListener(l);
    }
    
    /**
     * Smaže posluchače reagujícího na zasílané zprávy.
     * @param l posluchač.
     */
    public void removeMessageListener(MessageListener l) {
        messageSupport.removeMessageListener(l);
    }
    
    /**
     * Zašle všem registrovaným posluchačům informaci o 
	 * smazaných položkách.
     * @param deleteItems smazané položky.
     */
    private void fireDeleteItemListener(Object[] deleteItems) {
        for (DeletedItemsListener l : deleteItemListeners) {
            l.deltedItems(deleteItems);
        }
    }
    
    /**
     * Definuje akci která zajistí vyvolání popup menu pro editaci položek.
     */
    class TypesListAction extends MouseAdapter {
        
        private final int WRAPPER = 1, COPIER = 2, RENDERER = 3, EDITOR = 4;
        
        @Override
        public void mousePressed(MouseEvent evt) {
            showPopupMenu(evt);
        }
        
        @Override
        public void mouseReleased(MouseEvent evt) {
            showPopupMenu(evt);
        }
        
        private void showPopupMenu(MouseEvent evt) {
            if (evt.isPopupTrigger()) {
                JList list = (JList) evt.getSource();
                int index = list.locationToIndex(evt.getPoint());
                list.setSelectedIndex(index);
                JPopupMenu menu = new JPopupMenu();
                JMenuItem removeItem = new JMenuItem(
                        messages.getString("popup.menu.remove", "Remove"));
                removeItem.addActionListener(new RemoveAllAction(list, index));
                menu.add(removeItem);
                
                menu.add(new JSeparator(JSeparator.HORIZONTAL));
                
                JMenuItem removeWrapper = new JMenuItem(
                        messages.getString("popup.menu.remove_wrapper", "Remove Wrapper"));
                removeWrapper.addActionListener(new RemoveItem(list, index, WRAPPER));
                menu.add(removeWrapper);
                
                JMenuItem removeCopier = new JMenuItem(
                        messages.getString("popup.menu.remove_copier", "Remove Copier"));
                removeCopier.addActionListener(new RemoveItem(list, index, COPIER));
                menu.add(removeCopier);
                
                JMenuItem removeRenderer = new JMenuItem(
                        messages.getString("popup.menu.remove_renderer", "Remove Renderer"));
                removeRenderer.addActionListener(new RemoveItem(list, index, RENDERER));
                menu.add(removeRenderer);
                
                JMenuItem removeEditor = new JMenuItem(
                        messages.getString("popup.menu.remove_editor", "Remove Editor"));
                removeEditor.addActionListener(new RemoveItem(list, index, EDITOR));
                menu.add(removeEditor);
                
                menu.show(list, evt.getX(), evt.getY());
            }
        }
        
        /**
         * Akce která smaže všechny navázána rozšíření k typy, vč. typu
		 * samotného ze struktury.
         */
        class RemoveAllAction implements ActionListener {

            JList list;
            int selectedIndex;
            
            public RemoveAllAction(JList list, int selectedIndex) {
                this.list = list;
                this.selectedIndex = selectedIndex;
            }
            
            @Override
            public void actionPerformed(ActionEvent e) {
                ExtensionsOfTypes model = (ExtensionsOfTypes) list.getModel();
                ExtensionsForType extType = (ExtensionsForType) list.getSelectedValue();
                Class<?>[] classes = new Class<?>[] {
                    extType.getWrapper(), 
                    extType.getCopier(),
                    extType.getRenderer(),
                    extType.getEditor()
                };
                model.removeType(extType.getType());
                fireDeleteItemListener(classes);
                messageSupport.sendMessage("types_has_changed");
            }
        }
        
        /**
         * Akce která smaže vybrané rozšíření.
         */
        class RemoveItem implements ActionListener {
            JList list;
            int selectedIndex;
            int whichItem;
            
            /**
             * Iniciace akce.
             * @param list seznam položek.
             * @param selectedIndex index vybrané položky.
             * @param whichItem které rozšíření má být smazáno.
             */
            public RemoveItem(JList list, int selectedIndex, int whichItem) {
                this.list = list;
                this.selectedIndex = selectedIndex;
                this.whichItem = whichItem;
            }
            
            @Override
            public void actionPerformed(ActionEvent e) {
                ExtensionsOfTypes model = (ExtensionsOfTypes) list.getModel();
                ExtensionsForType extType = (ExtensionsForType) list.getSelectedValue();
                
                Class cls = null;
                
                switch (whichItem) {
                    case WRAPPER:
                        cls = extType.getWrapper();
                        model.connectWrapper(extType.getType(), null);
                        break;
                    case COPIER:
                        cls = extType.getCopier();
                        model.connectCopier(extType.getType(), null);
                        break;
                    case RENDERER:
                        cls = extType.getRenderer();
                        model.connectParameterCellRenderer(extType.getType(), null);
                        break;
                    case EDITOR:
                        cls = extType.getEditor();
                        model.connectParameterCellEditor(extType.getType(), null);
                        break;
                    default:
                        throw new IllegalArgumentException(
                                String.format("Illegal chose for deleting item. '%d'", 
                                whichItem));
                }
                
                if (cls != null) {
                    fireDeleteItemListener(new Object[] {cls});
                    messageSupport.sendMessage("types_has_changed");
                }
            }
        }
    }
    
    /**
     * Definuje možnost importu pomocí funkce Drag and Drop do seznamu
	 * typů a jejich rozšíření.
     */
    private class TypesListTransferHandler extends TransferHandler {
        
        /**
         * Zjistí zda je možné přenositelný objekt importovat.
         * @param supp informace o přenášeném objektu.
         * @return {@code true} pokud je možné objekt importovat, tj.
		 * pokud se jedná o typ {@code ExtensionsForType}, jinak {@code false}.
         */
        @Override
        public boolean canImport(TransferSupport supp) {
            
            if (!supp.isDrop()) {
                return false;
            }
            
            Component comp = supp.getComponent();
            if (comp instanceof TypesList) {
                TypesList types = (TypesList) comp;
                JList.DropLocation dl = (JList.DropLocation) supp.getDropLocation();
                int index = dl.getIndex();
                if (index == -1) {
                    return false;
                }
                types.setSelectedIndex(index);
                Object selected = types.getSelectedValue();
                if (!(selected instanceof ExtensionsForType)) {
                    return false;
                }
            } else {
                return false;
            }
            
            return true;
        }
        
        /**
         * Importuje přenášená data.
         * @param supp informace o přenášených datech.
         * @return {@code true} pokud se import zdařil, jinak
		 * {@code false}.
         */
        @Override
        public boolean importData(TransferSupport supp) {
            
            if (!canImport(supp)) {
                return false;
            }
            
            Transferable t = supp.getTransferable();
            try {
                Object o = t.getTransferData(TransferableExtensionType.typeDataFlavor);
                Class type = ((ExtensionType) o).getType();

                JList.DropLocation dl = (JList.DropLocation) supp.getDropLocation();
                int index = dl.getIndex();
                ExtensionsOfTypes model = (ExtensionsOfTypes) 
                        ((JList) supp.getComponent()).getModel();
                ExtensionsForType extType = (ExtensionsForType) model.getElementAt(index);

                if (Wrapper.class.isAssignableFrom(type)) {
                    Class oldWrapper = extType.getWrapper();
                    extType.setWrapper(type);
                    model.connectWrapper(extType.getType(), type);
                    if (oldWrapper != null) {
                        fireDeleteItemListener(new Object[] {oldWrapper});
                    }
                } else if (Copier.class.isAssignableFrom(type)) {
                    Class oldCopier = extType.getCopier();
                    extType.setCopier(type);
                    model.connectCopier(extType.getType(), type);
                    if (oldCopier != null) {
                        fireDeleteItemListener(new Object[] {oldCopier});
                    }
                } else if (ParameterCellRenderer.class.isAssignableFrom(type)) {
                    Class oldRenderer = extType.getRenderer();
                    extType.setRenderer(type);
                    model.connectParameterCellRenderer(extType.getType(), type);
                    if (oldRenderer != null) {
                        fireDeleteItemListener(new Object[] {oldRenderer});
                    }
                } else if (ParameterCellEditor.class.isAssignableFrom(type)) {
                    Class oldEditor = extType.getEditor();
                    extType.setEditor(type);
                    model.connectParameterCellEditor(extType.getType(), type);
                    if (oldEditor != null) {
                        fireDeleteItemListener(new Object[] {oldEditor});
                    }
                } else {
                    return false;
                }
            } catch (UnsupportedFlavorException ex) {
                String title = messages.getString("exception.unsupported_flavor_excp",
                        "Unsupported data flavor");
                Despr.showError(title, ex, Level.WARNING, true);
            } catch (IOException ex) {
                String title = messages.getString("exception.io_excp", "I/O exception");
                Despr.showError(title, ex, Level.WARNING, true);
            }
            
            messageSupport.sendMessage("types_has_changed");
            return true;
        }
    }
}