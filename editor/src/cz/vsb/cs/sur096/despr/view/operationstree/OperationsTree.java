package cz.vsb.cs.sur096.despr.view.operationstree;

import cz.vsb.cs.sur096.despr.Despr;
import cz.vsb.cs.sur096.despr.model.operation.IOperation;
import cz.vsb.cs.sur096.despr.structures.LocalizeMessages;
import cz.vsb.cs.sur096.despr.view.TransferableOperations;
import java.awt.datatransfer.Transferable;
import java.util.logging.Level;
import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.TransferHandler;
import javax.swing.tree.TreePath;

/**
 * Implementace stromu uživatelských operací. Jedná se o strom, z kterého
 * jsou předávány operace na plátno grafu.
 *
 * @author Martin Šurkovský, sur096 
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 * @version 2011/08/24/09:53
 */
public class OperationsTree extends JTree {
    
    /** Seznam lokalizačních zpráv. */
    private transient LocalizeMessages messages;
    
    /**
     * Iniciuje strom operací s existujícím modelem.
     * @param model model stromu operací.
     */
    public OperationsTree(OperationsTreeModel model) {
        setModel(model);
        init();
    }
    
    private void init() {
        messages = Despr.loadLocalizeMessages(getClass(), null, false);
        setRootVisible(false);
        setDragEnabled(true);
        setTransferHandler(new TreeTransferHandler());
    }

    /**
     * Poskytne model stromu operací.
     * @return model stromu operací.
     */
    @Override
    public OperationsTreeModel getModel() {
        return (OperationsTreeModel) super.getModel();
    }
    
    /**
     * Implementace exportu operací ze stromu, pomocí funkce 
	 * Drag and Drop.
     */
    private class TreeTransferHandler extends TransferHandler {
        
        /**
         * Poskytne kód akce.
         * @param comp komponenta která obsahuje přenášená data.
         * @return {@code TransferHandler.COPY_OR_MOVE}.
         */
        @Override
        public int getSourceActions(JComponent comp) {
            return TransferHandler.COPY_OR_MOVE;
        }
        
        /**
         * Vytvoří přenositelný objekt obsahující jednou 
		 * uživatelskou operaci.
         * @param comp komponenta která uchovává přenášená data.
         * @return přenositelný objekt z vybranou operací.
         */
        @Override
        public Transferable createTransferable(JComponent comp) {
            
            Transferable t = null;
            
            if (comp instanceof OperationsTree) {
                OperationsTree opTree = (OperationsTree) comp;
                TreePath tp = opTree.getSelectionPath();
                Object obj = tp.getLastPathComponent();
                
                if (obj instanceof Category) {
                    Category cat = (Category) obj;
                    Object userObj = cat.getUserObject();
                    if (userObj instanceof IOperation) {
                        IOperation op = null;
                        try {
                            // musi se vytvorit nova instance nebo budou vsechny objekty sdilet uplne
                            // stejny model
                            op = (IOperation) userObj.getClass().newInstance();
                        } catch (InstantiationException ex) {
                        String title = messages.getString("title.instantiation_excp",
                                "Problem with instantiation class");
                            Despr.showError(title, ex, Level.WARNING, true);
                        } catch (IllegalAccessException ex) {
                            String title = messages.getString("title.illegal_access_excp",
                                    "Illegal Access");
                            Despr.showError(title, ex, Level.WARNING, true);
                        }

                        t = new TransferableOperations(new IOperation[] {op});
                    }
                }
            }
            return t;
        }
    }
}
