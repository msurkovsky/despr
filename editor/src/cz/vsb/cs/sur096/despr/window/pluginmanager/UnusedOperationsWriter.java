
package cz.vsb.cs.sur096.despr.window.pluginmanager;

import cz.vsb.cs.sur096.despr.Despr;
import cz.vsb.cs.sur096.despr.structures.LocalizeMessages;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import javax.swing.DefaultListModel;

/**
 * Nástroj sloužící pro zápis informací o nepoužitých operacích do souboru.
 *
 * @author Martin Šurkovský, sur096,
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 * @version 2012/02/21/11:30
 */
public class UnusedOperationsWriter {
    
    /** Odkaz na soubor kde se nachází seznam nepoužitých operací.*/
    public static final String UNUSED_OPERATIONS_LIST = "resources/unused_operations.list";

    /**
     * Uloží seznam nepoužitých operací do souboru.
     * @param unusedODefaultListModel model seznamu nepoužitých operací.
     */
    public static void save(DefaultListModel unusedODefaultListModel) {
        uow.pSave(unusedODefaultListModel);
    }
    
    ////////////////////////////////////////////////////////////
    // Soukrome promene a metody
    
    private static UnusedOperationsWriter uow;
    static {
        uow = new UnusedOperationsWriter();
    }
    
    private transient LocalizeMessages messages;
    private UnusedOperationsWriter() {
        messages = Despr.loadLocalizeMessages(getClass(), null, false);
    }
    
    private void pSave(DefaultListModel unusedOperationsListModel) {
    File unusedOperationsFile = new File(UNUSED_OPERATIONS_LIST);
        if (!unusedOperationsFile.exists()) {
            try {
                unusedOperationsFile.createNewFile();
            } catch (IOException ex) {
                String title = messages.getString("title.io_excp", "I/O problem");
                String message = String.format("%s '%s'", ex.getMessage(), unusedOperationsFile.getAbsoluteFile());
                Despr.showError(title, new IOException(message, ex), Level.WARNING, true);
                return;
            }
        }
        
        FileWriter fw = null;
        BufferedWriter bw = null;
        try {
            fw = new FileWriter(unusedOperationsFile);
            bw = new BufferedWriter(fw);
            int count = unusedOperationsListModel.getSize();
            for (int i = 0; i < count; i++) {
                String line = String.format("%s\n", 
                        unusedOperationsListModel.get(i).getClass().getCanonicalName());
                bw.write(line);
            }
        } catch (IOException ex) {
            String title = messages.getString("title.io_excp", "I/O problem");
            String message = String.format("%s '%s'", ex.getMessage(), unusedOperationsFile.getAbsoluteFile());
            Despr.showError(title, new IOException(message, ex), Level.WARNING, true);
        } finally {
            try {
                if (bw != null) bw.close();
                if (fw != null) fw.close();
            } catch (IOException ex) {
                String title = messages.getString("title.io_excp", "I/O problem");
                String message = String.format("%s '%s'", ex.getMessage(), unusedOperationsFile.getAbsoluteFile());
                Despr.showError(title, new IOException(message, ex), Level.WARNING, true);
            }
        }
    }
}