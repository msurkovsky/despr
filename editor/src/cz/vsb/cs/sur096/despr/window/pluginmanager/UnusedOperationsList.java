
package cz.vsb.cs.sur096.despr.window.pluginmanager;

import cz.vsb.cs.sur096.despr.Despr;
import cz.vsb.cs.sur096.despr.model.operation.IOperation;
import cz.vsb.cs.sur096.despr.structures.LocalizeMessages;
import cz.vsb.cs.sur096.despr.view.TransferableOperations;
import java.awt.datatransfer.Transferable;
import java.util.logging.Level;
import javax.swing.*;

/**
 * Komponenta zobrazující seznam načtených operací s rozšíření, které ovšem
 * ještě nejsou použité ve stromě operací.
 *
 * @author Martin Šurkovský, sur096,
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 * @version 2012/02/12/19:28
 */
public final class UnusedOperationsList extends JList implements DeletedItemsListener {
    
    private transient LocalizeMessages messages;
    
    /**
     * Iniciuje seznam nepoužitých operací.
     */
    public UnusedOperationsList() {
        messages = Despr.loadLocalizeMessages(getClass(), null, false);
        setCellRenderer(new UnusedOperationsListRenderer());
        setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        setDragEnabled(true);
        setTransferHandler(new OperationsListTransferHandler());
    }

    /**
     * Reaguje na smazané položky ze stromu operací. 
     * @param items položky které byly smazány ve stromě operací.
     */
    @Override
    public void deltedItems(Object[] items) {
        DefaultListModel model = (DefaultListModel) getModel();
        for (Object item : items) {
            if (item instanceof IOperation) {
                model.addElement(item);
            }
        }
    }

    /**
     * Umožňuje jednotlivé položky ze seznamu pomocí drag and drop funkce
	 * přetáhnout do stromu operací.
     */
    private class OperationsListTransferHandler extends TransferHandler {
        
        /** */
        private Object[] selectedValues;
        
        /**
         * Poskytne kód akce která je s položkou prováděna.
         * @param comp komponenta která obsahuje přenášené položky.
         * @return {@code TransferHandler.MOVE}.
         */
        @Override
        public int getSourceActions(JComponent comp) {
            return TransferHandler.MOVE;
        }
        
        /**
         * Vytvoří přenositelný objekt.
         * @param comp komponenta která obsahuje přenášené informace.
         * @return přenositelný objekt obsahující seznam operací,
		 * které mají být přeneseny z seznamu nepoužitých operací
		 * do stromu operací.
         */
        @Override
        public Transferable createTransferable(JComponent comp) {
            
            Transferable t = null;
            
            if (comp instanceof UnusedOperationsList) {
                UnusedOperationsList opList = (UnusedOperationsList) comp;
                selectedValues = opList.getSelectedValues();
                
                int count = selectedValues.length;
                IOperation[] operations = new IOperation[count];
                for (int i = 0; i < count; i++) {
                    Object obj = selectedValues[i];
                    if (obj instanceof IOperation) {
                        try {
                            // musi se vytvorit nova instance nebo budou vsechny objekty sdilet uplne
                            // stejny model
                            IOperation op = (IOperation) obj.getClass().newInstance();
                            operations[i] = op;
                        } catch (InstantiationException ex) {
                            String title = messages.getString("title.instantiation_excp",
                                    "Instantiation problem");
                            Despr.showError(title, ex, Level.WARNING, true);
                        } catch (IllegalAccessException ex) {
                            String title = messages.getString("title.illegal_access_excp",
                                    "Illegal access");
                            Despr.showError(title, ex, Level.WARNING, true);
                        }

                    }
                }
                t = new TransferableOperations(operations);
            }
            return t;
        }
        
        /**
         * Reaguje na ukončení přenosu tak, že smaže přenesená data
		 * ze seznamu nepoužitých operací.
         * @param source komponenta obsahující zdrojová data.
         * @param data přenositelný objekt obsahující přenášená data.
         * @param action kód akce.
         */
        @Override
        protected void exportDone(JComponent source, Transferable data, int action) {
            if (action == MOVE && selectedValues != null) {
                for (int i = 0; i < selectedValues.length; i++) {
                    DefaultListModel model = (DefaultListModel) ((JList) source).getModel();
                    model.removeElement(selectedValues[i]);
                }
            }
        }
    }
}
