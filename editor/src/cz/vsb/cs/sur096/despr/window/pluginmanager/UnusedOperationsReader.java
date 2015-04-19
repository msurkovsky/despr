
package cz.vsb.cs.sur096.despr.window.pluginmanager;

import cz.vsb.cs.sur096.despr.Despr;
import cz.vsb.cs.sur096.despr.DesprClassLoader;
import cz.vsb.cs.sur096.despr.model.operation.IOperation;
import cz.vsb.cs.sur096.despr.structures.LocalizeMessages;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import javax.swing.DefaultListModel;

/**
 * Slouží pro načtení nepoužitých operací z uloženého souboru.
 *
 * @author Martin Šurkovský, sur096,
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 * @version 2012/03/05/19:37
 */
public final class UnusedOperationsReader {
    
    /**
     * Jméno souboru kde jsou uloženy nepoužité operace načtených rozšíření.
     */
    public static final String UNUSED_OPERATIONS_LIST = "resources/unused_operations.list";

    /**
     * Načte seznam nepoužitých operací do předaného modelu.
     * @param unusedOperationsListModel model seznamu s nepoužitými operacemi.
     */
    public static void readUnusedOperations(DefaultListModel unusedOperationsListModel) {
        reader.pReadUnusedOperations(unusedOperationsListModel);
    }

    ////////////////////////////////////////////////////////////
    // Sourkome promene a metody
    
    private transient LocalizeMessages messages;
    private static UnusedOperationsReader reader; 
    static {
        reader = new UnusedOperationsReader();
    }
    
    private UnusedOperationsReader() {
        messages = Despr.loadLocalizeMessages(getClass(), null, false);
    }
    
    private void pReadUnusedOperations(DefaultListModel unusedOperationsListModel) {
        File f = new File("resources/unused_operations.list");
        List<Class> classes = pReadClassFromFile(f);
        for (Class cls : classes) {
            try {
                IOperation op = (IOperation) cls.newInstance();
                unusedOperationsListModel.addElement(op);
            } catch (InstantiationException ex) {
                String title = messages.getString("title.instantiation_excp",
                        "Problem with instantiation");
                Despr.showError(title, ex, Level.WARNING, true);
            } catch (IllegalAccessException ex) {
                String title = messages.getString("title.illegal_access_excp",
                        "Illegal access exception");
                Despr.showError(title, ex, Level.WARNING, true);
            }
        }
    }
    
    private List<Class> pReadClassFromFile(File file) {
        
        List<Class> list = new ArrayList<Class>();
        if (!file.exists()) {
            return Collections.emptyList(); // file is empty
        }
        
        try {
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            String line;
            while (br.ready()) {
                line = br.readLine();
                line = line.trim();
                try {
                    Class c = Class.forName(line, true, DesprClassLoader.getClassLoader());
                    list.add(c);
                } catch (ClassNotFoundException ex) {
                    String title = messages.getString("title.class_not_found_excp",
                            "Class not fond");
                    Despr.showError(title, ex, Level.WARNING, true);
                }
            }
            br.close();
            fr.close();
        } catch (FileNotFoundException ex) {
            String title = messages.getString("title.file_not_found_excp", "File not found");
            Despr.showError(title, ex, Level.WARNING, true);
        } catch (IOException ex) {
            String title = messages.getString("title.io_excp", "I/O problem");
            String message = String.format("%s '%s'", ex.getMessage(), file.getAbsoluteFile());
            Despr.showError(title, new IOException(message, ex), Level.WARNING, true);
        }
        
        return list;
    }
}
