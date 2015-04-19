
package cz.vsb.cs.sur096.despr.window.pluginmanager;

import cz.vsb.cs.sur096.despr.Despr;
import cz.vsb.cs.sur096.despr.events.MessageEvent;
import cz.vsb.cs.sur096.despr.events.MessageListener;
import cz.vsb.cs.sur096.despr.events.MessageSupport;
import cz.vsb.cs.sur096.despr.model.operation.IOperation;
import cz.vsb.cs.sur096.despr.structures.LocalizeMessages;
import cz.vsb.cs.sur096.despr.view.TransferableOperations;
import cz.vsb.cs.sur096.despr.view.operationstree.Category;
import cz.vsb.cs.sur096.despr.view.operationstree.OperationsTreeEditor;
import cz.vsb.cs.sur096.despr.view.operationstree.OperationsTreeModel;
import cz.vsb.cs.sur096.despr.view.operationstree.OperationsTreeRenderer;
import cz.vsb.cs.sur096.despr.window.actions.EditCategoryAction;
import java.awt.Component;
import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Level;
import javax.swing.*;
import javax.swing.tree.*;

/**
 * Implementace editovatelného stromu operací. Slouží pro definici stromu
 * importu operací do něj a celkové úpravě uspořádání kategorií a operací v něm.
 *
 * @author Martin Šurkovský, sur096,
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 * @version 2012/02/12/16:47
 */
public class EditableOperationsTree extends JTree implements MessageListener {
    
    /** Seznam lokalizačních zpráv. */
    private transient LocalizeMessages messages;
    /** Seznam posluchačů zajímajících se o smazané položky stromu. */
    private List<DeletedItemsListener> deletedItemsListeners;
    /** Podpora zasílání zpráv.*/
    private MessageSupport messageSupport;
    
    /**
     * Iniciuje editovatelný strom operací na základě modelu.
     * @param model model stromu.
     */
    public EditableOperationsTree(OperationsTreeModel model) {
        init(model);
    }
    
    private void init(TreeModel model) {
        messages = Despr.loadLocalizeMessages(getClass(), null, false);
        deletedItemsListeners = new ArrayList<DeletedItemsListener>();
        messageSupport = new MessageSupport(this);
        setModel(model);
        setEditable(true);
        setRootVisible(true);
        setCellEditor(new OperationsTreeEditor());
        setDragEnabled(true);
        setDropMode(DropMode.ON_OR_INSERT);
        setTransferHandler(new ImportOperationTransferHandler());
        getSelectionModel().setSelectionMode(TreeSelectionModel.CONTIGUOUS_TREE_SELECTION);
        addMouseListener(new TreeImtemAction(this));
    }

    /**
     * Přidá posluchače reagujícího na smazání položky stromu.
     * @param l posluchač.
     */
    public void addDeletedItemsListener(DeletedItemsListener l) {
        if (deletedItemsListeners == null) {
            deletedItemsListeners = new ArrayList<DeletedItemsListener>();
        }
        
        deletedItemsListeners.add(l);
    }
    
    /**
     * Smaže posluchače reagujícího na smazání položky stromu.
     * @param l posluchač.
     */
    public void removeDeletedItemsListener(DeletedItemsListener l) {
        deletedItemsListeners.remove(l);
    }
    
    /**
     * Pošle všem posluchačům seznam smazaných položek.
     * @param items seznam smazaných položek.
     */
    private void fireDeletedItemsListener(Object[] items) {
        for (DeletedItemsListener l : deletedItemsListeners) {
            l.deltedItems(items);
        }
    }
    
    /**
     * Přidá posluchače reagujícího na zprávy zasílané ze stromu.
     * @param l posluchač.
     */
    public void addMessageListener(MessageListener l) {
        messageSupport.addMessageListener(l);
    }
    
    /**
     * Smaže posluchače reagujícího na zprávy zasílané ze stromu.
     * @param l posluchač.
     */
    public void removeMessageListener(MessageListener l) {
        messageSupport.removeMessageListener(l);
    }
    
    /**
     * Pošle zprávu všem registrovaným posluchačům.
     * @param message zpráva.
     */
    public void sendMessage(String message) {
        messageSupport.sendMessage(message);
    }

    /**
     * Reaguje na zprávu o změně lokalizačního souboru stromu.
	 * Strom přepíše změněný kategorii. pošle zprávu o registrovaným
	 * posluchačům.
     * @param event událost která ji zaslala.
     */
    @Override
    public void catchMessage(MessageEvent event) {
        if (event.getMessage().equals("localization_changed")) {
            // Diky tomu ze oba stromy operaci sdileji jak model tak rendrener
            // projevi si zmeny v obou zaroven
            ((OperationsTreeModel) getModel()).reload((
                    TreeNode) getSelectionPath().getLastPathComponent());
            
            
            // take je treba poslat zpravu do hlavniho panelu o tom, ze doslo
            // ke zmenene.
            messageSupport.sendMessage("operations_has_changed");
        }
    }
    
    /**
     * Nastaví renderer položky stromu.
     * @param renderer zobrazovač položky. 
     */
    @Override
    public void setCellRenderer(TreeCellRenderer renderer) {
        if (renderer instanceof OperationsTreeRenderer) {
            // Pokud se zmeni lokalizazcni sobour stromu. Pak renderer posle
            // zpravu o zmene a je treba aby byl strom prekreslen
            ((OperationsTreeRenderer) renderer).addMessageListener(this);
        }
        super.setCellRenderer(renderer);
    }
    
    /**
     * Definuje reakce stromu na akce myši.
     */
    private class TreeImtemAction extends MouseAdapter {
        
        /** Strom operací. */
        private JTree tree;

        /** 
         * Iniciuje akce definované stromem operací.
         * @param tree strom operací.
         */
        public TreeImtemAction(JTree tree) {
            this.tree = tree;
        }

        /**
         * Stará se o vyvolání popup menu.
         * @param evt událost.
         */
        @Override
        public void mousePressed(MouseEvent evt) {
            showPopup(evt);
        }
        
        /**
         * Stará se o vyvolání popup menu.
         * @param evt událost.
         */
        @Override
        public void mouseReleased(MouseEvent evt) {
            showPopup(evt);
        }
        /** 
         * Zobrazí popup menu s možnostmi úpravy.
         * @param e událost.
         */
        private void showPopup(MouseEvent e) {
            if (e.isPopupTrigger()) {
                Point p = e.getPoint();
                TreePath path = tree.getPathForLocation(p.x, p.y);
                if (path != null) {
                    setSelectionPath(path);
                    Category cat = (Category) path.getLastPathComponent();
                    JPopupMenu itemMenu = new ItemPopupMenu(cat);
                    itemMenu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        }
        
        /** 
		 * Definuje popup menu pro jednotlivé položky stromu.
		 */
        class ItemPopupMenu extends JPopupMenu {
            
            /** Vybraná kategorie ve stromu. */
            private Category selectCategory;
            
            /**
             * Iniciuje popup menu s vybranou položkou ve stromě.
             * @param selectCategory vybraná položka stromu.
             */
            public ItemPopupMenu(Category selectCategory) {
                this.selectCategory = selectCategory;
                Object userObject = selectCategory.getUserObject();
                if (userObject instanceof IOperation) {
                    initOperationMenu();
                } else if (userObject instanceof String) {
                    initDirectoryMenu();
                }
            }
            
			/**
			 * Iniciuje menu pro operace.
			 */
            private void initOperationMenu() {
                JMenuItem removeOperation = new JMenuItem(
                        messages.getString("popup.title.remove", "Remove"));
                removeOperation.setToolTipText(
                        messages.getString("popup.remove.tooltip", 
                                           "Delete select operation"));
                removeOperation.addActionListener(new RemoveAction());
                add(removeOperation);
            }
            
			/** 
			 * Iniciuje menu pro složku (kategorii) operací
			 */
            private void initDirectoryMenu() {
                JMenuItem addDirectory = new JMenuItem(
                        messages.getString("popup.title.add", "Add"));
                addDirectory.setToolTipText(
                        messages.getString("popup.add.tooltip", "Add new category"));
                addDirectory.addActionListener(new AddAction());
                add(addDirectory);
                
                JMenuItem editDirectory = new JMenuItem(
                        new EditCategoryAction(EditableOperationsTree.this, 
                        (String) selectCategory.getUserObject()));
                
                add(editDirectory);
                
                JMenuItem removeDirectory = new JMenuItem(
                        messages.getString("popup.title.remove_category", 
                                           "Remove category"));
                removeDirectory.setToolTipText(
                        messages.getString("popup.remove_category.tooltip", 
                                           "Delete select category"));
                removeDirectory.addActionListener(new RemoveAction());
                add(removeDirectory);
                
				// korenovou kategrii nelze smazat
                if (selectCategory.getParent() == null) {
                    removeDirectory.setEnabled(false);
                }
            }
            
            /** Definice akce pro smazaní kategorie či operace. */
            class RemoveAction implements ActionListener {

                @Override
                public void actionPerformed(ActionEvent e) {
                    if (selectCategory != null) {
                        
                        Object userObj = selectCategory.getUserObject();
                        OperationsTreeModel dtm = (OperationsTreeModel) tree.getModel();
                        if (userObj instanceof IOperation) {
                            // treba presunout odkaz do do seznamu nepouzitych
                            // operaci
                            dtm.removeNodeFromParent(selectCategory);
                            fireDeletedItemsListener(new Object[] {userObj});
                            messageSupport.sendMessage("operations_has_changed");
                        } else if (userObj instanceof String) {
                            // budou vymazany i vsechny operace
                            List<IOperation> deletedOperations = selectCategory.getAllOperations();
                            dtm.removeNodeFromParent(selectCategory);
                            fireDeletedItemsListener(deletedOperations.toArray());
                            messageSupport.sendMessage("operations_has_changed");
                        }
                    }
                }
            }
            
            /** Definice akce pro přidání nové kategorie. */
            class AddAction implements ActionListener {

                @Override
                public void actionPerformed(ActionEvent e) {
                    if (selectCategory != null) {
                        Category newCategory = new Category();
                        newCategory.setUserObject("new_category");
                        ((OperationsTreeModel)tree.getModel()).insertNodeInto(newCategory, selectCategory, 0);
                        messageSupport.sendMessage("operations_has_changed");
                    }
                }
            }
        }
    }
    
    /**
     * Definice {@code TransferHandler} pro strom operací tak ať je možné
	 * položky ve stromu přesouvat a přidávat na něj nové operace.
     */
    private class ImportOperationTransferHandler extends TransferHandler {
        
        /** Seznam kategorií které mají být vymazány.*/
        private Category[] categoriesToRemove;
        
        /** Definice {@code DataFlavor} pro seznam kategorií.*/
        private final DataFlavor categoriesFlavor = new DataFlavor(
                    String.format("%s;class=\"%s\"", 
                                  DataFlavor.javaJVMLocalObjectMimeType,
                                  Category[].class.getName()), 
                    "Local category tranfer");
        
        /**
         * Poskytne zdrojovou akci.
         * @param comp komponenta která uchovává data co mají být přeneseny.
         * @return číslo reprezentující akci. V tomto případě vždy:
		 * {@code TransferHandler.COPY_OR_MOVE}.
         */
        @Override
        public int getSourceActions(JComponent comp) {
            return TransferHandler.COPY_OR_MOVE;
        }
        
        /**
         * Vytvoří přenositelný objekt.
		 * @param comp komponenta která obsahuje data co mají
		 * být přenesena.
		 * @return zabalený seznam kategorii které byly vybrány, pokud 
		 * není výběr ve stromu prázdný pak vrátní {@code null}.
         */
        @Override
        public Transferable createTransferable(JComponent comp) {
            
            JTree tree = (JTree) comp;
            TreePath[] paths = tree.getSelectionPaths();
            if (paths != null) {
                List<Category> copies = new ArrayList<Category>();
                List<Category> toRemove = new ArrayList<Category>();
                
                Category cat = (Category) paths[0].getLastPathComponent();
                Category copy = copy(cat);
                copies.add(copy);
                toRemove.add(cat);
                
                for (int i = 1; i < paths.length; i++) {
                    Category next = (Category) paths[i].getLastPathComponent();
                    if (next.getLevel() > cat.getLevel()) {
                        copy.add(copy(next));
                    } else {
                        copies.add(copy(next));
                        toRemove.add(next);
                    }
                }
                
                Category[] categories = copies.toArray(new Category[copies.size()]);
                categoriesToRemove = toRemove.toArray(new Category[toRemove.size()]);
                
                return new CategoriesTransferable(categories);
            }
            return null;
        }
        
        /**
         * Reaguje na ukončení přenosu.
         * @param c komponenta která uchovává přenášená data.
         * @param t přenášená data.
         * @param action typ akce.
         */
        @Override
        public void exportDone(JComponent c, Transferable t, int action) {
            if (action == MOVE && categoriesToRemove != null) {
                JTree tree = (JTree) c;
                OperationsTreeModel model = (OperationsTreeModel) tree.getModel();
                for (Category cat : categoriesToRemove) {
                    model.removeNodeFromParent(cat);
                }
            }
        }
        
        /**
         * Zjistí zda je možné přenášená data importovat.
         * @param supp informace o přenášených datech.
         * @return {@code true} pokud se jedná o seznam kategorií,
		 * nebo o objekt typu {@code IOperation}, jinak {@code false}}.
         */
        @Override
        public boolean canImport(TransferSupport supp) {
            
            if (!supp.isDrop()) {
                return false;
            }
            
            JTree.DropLocation dl = (JTree.DropLocation) supp.getDropLocation();
            JTree tree = (JTree) supp.getComponent();
            TreePath dest = dl.getPath();
            Category destCategory = (Category) dest.getLastPathComponent();
            
            if (supp.isDataFlavorSupported(categoriesFlavor)) {

                // Blok zabranuje vlozit vybrany objekt do nejakeho
                // s dalsich vybranych, je mozne najednou presunovat vice radku
                int dropRow = tree.getRowForPath(dest);
                int[] selRows = tree.getSelectionRows();
                for (int i =0 ; i < selRows.length; i++) {
                    if (selRows[i] == dropRow) {
                        return false;
                    }
                }
                
                TreePath selectionPath = tree.getSelectionPath();
                Category selectionCategory = (Category) selectionPath.getLastPathComponent();
                if (selectionCategory.getUserObject() instanceof IOperation &&
                        destCategory.getParent() == null) {
                    return false; // do rootovskeho adresare lze vkladat pouze adresare
                                  // nikoliv operace
                }

                if (destCategory.isNodeChild(selectionCategory)) {
                    return false; // nelze vlozit vybranou slozku do ne ktere
                                // se svych dcerinych slozek
                }
            } else if (supp.isDataFlavorSupported(TransferableOperations.operationsLocalFlavor)) {
                if (destCategory.getParent() == null) {
                    return false;   // do rotovskeho adresare nelze vkadat operace
                }
                // continue
            } else {
                return false;
            }
            
            if (destCategory.getUserObject() instanceof IOperation) {
                return false; // do kategorii reprezentujici operace neni mozne
                              // jine oprace ci skupiny operaci
            }
            
            return true;
        }
        
        /**
         * Importuje přenášená data do komponenty. 
         * @param supp informace o přenášených datech.
         * @return {@code trupe} pokud se import zdařil, jinak {@code false}.
         */
        @Override
        public boolean importData(TransferSupport supp) {
            if (!canImport(supp)) {
                return false;
            }
            
            Category[] categories = null;
            
            Transferable t = supp.getTransferable();
            
            if (t.isDataFlavorSupported(TransferableOperations.operationsLocalFlavor)) {
                Component comp = supp.getComponent();
                if (comp instanceof EditableOperationsTree) {

                    try {
                        IOperation[] baseOps = (IOperation[]) t.getTransferData(
                                TransferableOperations.operationsLocalFlavor);
                        
                        int count = baseOps.length;
                        categories = new Category[count];
                        for (int i = 0; i < count; i++) {
                            IOperation baseOp = baseOps[i];
                            Category newCat = new Category();
                            newCat.setUserObject(baseOp);
                            categories[i] = newCat;
                        }
                    } catch (UnsupportedFlavorException ex) {
                        String title = messages.getString("title.unsupported_data_flavor_excp",
                                "Unsupported data flavor");
                        Despr.showError(title, ex, Level.WARNING, true);
                    } catch (IOException ex) {
                        String title = messages.getString("title.io_excp",
                                "I/O problem");
                        Despr.showError(title, ex, Level.WARNING, true);
                    }
                }
            } else if (t.isDataFlavorSupported(categoriesFlavor)) {
                try {
                    categories = (Category[]) t.getTransferData(categoriesFlavor);
                } catch (UnsupportedFlavorException ex) {
                        String title = messages.getString("title.unsupported_data_flavor_excp",
                                "Unsupported data flavor");
                        Despr.showError(title, ex, Level.WARNING, true);
                    } catch (IOException ex) {
                        String title = messages.getString("title.io_excp",
                                "I/O problem");
                        Despr.showError(title, ex, Level.WARNING, true);
                    }

            } 

            if (categories != null) {
                JTree.DropLocation dl = (JTree.DropLocation) supp.getDropLocation();
                int childIndex = dl.getChildIndex();
                TreePath dest = dl.getPath();
                Category parent = (Category) dest.getLastPathComponent();
                JTree tree = (JTree) supp.getComponent();
                OperationsTreeModel model = (OperationsTreeModel) tree.getModel();
                int index = childIndex;
                if (childIndex == -1) {
                    index = parent.getChildCount();
                }

                for (int i = 0; i < categories.length; i++) {
                    model.insertNodeInto(categories[i], parent, index++);
                }
                messageSupport.sendMessage("operations_has_changed");
                return true;
            } else {
                return false;
            }
        }
        
        /**
         * Vytvoří kopii kategorie.
         * @param cat kategorie která má být zkopírována.
         * @return kopii kategorie.
         */
        private Category copy(Category cat) {
            Category copy;
            if (cat.getChildCount() > 0) {
                Enumeration childeren = cat.children();
                List l = new ArrayList();
                while (childeren.hasMoreElements()) {
                    l.add(childeren.nextElement());
                }
                copy = new Category(l);
            } else {
                copy = new Category();
            }
            
            copy.setUserObject(cat.getUserObject());
            return copy;
        }
        
        /**
         * Definuje obal pro seznam přenášených kategorií.
         */
        class CategoriesTransferable implements  Transferable {
            
            /** Seznam podporovaných {@code DataFlavor} objektů.*/
            private DataFlavor[] flavors;
            /** Seznam kategoríí které jsou přenášeny.*/
            Category[] categories;
            
            /**
             * Iniciuje objekt který je přenášen seznamem kategorií.
             * @param categories seznam kategorií který je přenášen.
             */
            public CategoriesTransferable(Category[] categories) {
                this.categories = categories;
                flavors = new DataFlavor[] {categoriesFlavor};
            }
            
            /**
             * Poskytne seznam podporovaných {@code DataFlavor}.
             * @return seznam podporovaných {@code DataFlavor}.
             */
            @Override
            public DataFlavor[] getTransferDataFlavors() {
                return flavors;
            }
            
            /**
             * Zjistí zda je daný {@code DataFlavor} podporván.
             * @param flavor
             * @return {@code true} pokud je podporován, jinak {@code false}.
             */
            @Override
            public boolean isDataFlavorSupported(DataFlavor flavor) {
                for (DataFlavor f : flavors) {
                    if (f.equals(flavor)) {
                        return true;
                    }
                }
                return false;
            }

            /**
             * Poskytne zabalená data.
             * @param flavor {@code DataFlavor} popisující typ zabalených dat.
             * @return zabalená data.
             * @throws UnsupportedFlavorException pokud se jedná o nepodporovaná
			 * {@code DataFlavor}.
             */
            @Override
            public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException {
                if (!isDataFlavorSupported(flavor)) {
                    throw new UnsupportedFlavorException(flavor);
                } 
                return categories;
            }
        }
    }
}