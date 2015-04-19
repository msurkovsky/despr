
package cz.vsb.cs.sur096.despr.window.pluginmanager;

import cz.vsb.cs.sur096.despr.Despr;
import cz.vsb.cs.sur096.despr.structures.LocalizeMessages;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.logging.Level;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.ListSelectionModel;
import javax.swing.TransferHandler;

/**
 * Implementace seznamu dostupných typových rozšíření. 
 *
 * @author Martin Šurkovský, sur096,
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 * @version 2012/02/18/15/26
 */
public class AvailableTypesList extends JList implements DeletedItemsListener {
    
    /** Seznam lokalizačních zpráv.*/
    private transient LocalizeMessages messages;
    
    /**
     * Iniciuje seznam dostupných typových rozšíření.
     */
    public AvailableTypesList() {
        messages = Despr.loadLocalizeMessages(getClass(), null, false);
        setCellRenderer(new AvailableTypesListRenderer());
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        setDragEnabled(true);
        setTransferHandler(new AvailableTypesListTransferHandler());
    }

    /**
     * Reaguje na smazání rozšíření ze svázaných typů. Pokud jsou
	 * smazány nějaká rozšíření u svázaného typu je informace o tom
	 * poslána do seznamu dostupných typů, který se aktualizuje.
     * @param items seznam smazaných položek.
     */
    @Override
    public void deltedItems(Object[] items) {
        AvailableTypesListModel model = (AvailableTypesListModel) getModel();
        for (int i = 0; i < items.length; i++) {
            Object item = items[i];
            if (item instanceof Class) {
                Class cls = (Class) item;
                for (int j = 0; j < model.getSize(); j++) {
                    ExtensionType type = (ExtensionType) model.getElementAt(j);
                    if (type.getType().getCanonicalName().equals(cls.getCanonicalName())) {
                        model.removeElement(type);
                        type.setUsed(false);
                        model.addElement(type);
                        break;
                    }   
                }
            }
        }
    }
    
    /**
     * Implementace Drag and Drop funkce pro export ze seznamu dostupných 
	 * typových rozšíření do seznamu svázaných typů.
     */
    private class AvailableTypesListTransferHandler extends TransferHandler {
        
        /** Index vybrané položka. */
        private int selectedIndex;
        
        /**
         * Poskytne zdrojovou akci.
         * @param comp komponenta která obsahuje přenášená data.
         * @return {@code TransferHandler.MOVE}.
         */
        @Override
        public int getSourceActions(JComponent comp) {
            return TransferHandler.MOVE;
        }
        
        /**
         * Vytvoří přenositelný objekt.
         * @param comp komponenta která obsahuje přenášená data.
         * @return přenositelný objekt, který obsahuje nějaké typové rozšíření.
         */
        @Override
        public Transferable createTransferable(JComponent comp) {
            
            Transferable t = null;
            
            if (comp instanceof AvailableTypesList) {
                AvailableTypesList types = (AvailableTypesList) comp;
                selectedIndex = types.getSelectedIndex();
                
                ExtensionType type = (ExtensionType) types.getSelectedValue();
                t = new TransferableExtensionType(type);
            }
            
            return t;
        }
        
        /**
         * Reakce na ukončení přenosu.	Přenesený typ označí jako
		 * použitý a znovu jej zatřídí v kolekci.
         * @param source zdrojová komponenta.
         * @param data přenášená data.
         * @param action použitá akce..
         */
        @Override
        protected void exportDone(JComponent source, Transferable data, int action) {
            if (action == MOVE && selectedIndex > -1) {
                AvailableTypesListModel model = (AvailableTypesListModel) 
                        ((JList) source).getModel();
                
                try {
                    ExtensionType type = (ExtensionType) data.getTransferData(
                            TransferableExtensionType.typeDataFlavor);
                    model.removeElement(type);
                    
                    type.setUsed(true);
                    model.addElement(type);
                    selectedIndex = model.getIndex(type);
                    if (selectedIndex > -1) {
                        ((JList) source).setSelectedIndex(selectedIndex);
                    }
                } catch (UnsupportedFlavorException ex) {
                    String title = messages.getString("title.unsupported_flavor_excp", 
                            "Unsupported Flavor");
                    Despr.showError(title, ex, Level.WARNING, true);
                } catch (IOException ex) {
                    String title = messages. getString("title.io_excp", "I/O problem");
                    Despr.showError(title, ex, Level.WARNING, true);
                }
                selectedIndex = -1;
            }
        }
        
    }
}
