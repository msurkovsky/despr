
package cz.vsb.cs.sur096.despr.window.pluginmanager;

import cz.vsb.cs.sur096.despr.Despr;
import cz.vsb.cs.sur096.despr.DesprClassLoader;
import cz.vsb.cs.sur096.despr.structures.LocalizeMessages;
import java.io.*;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;

/**
 * Implementace modelu seznamu dostupných typových rozšíření.
 *
 * @author Martin Šurkovský, sur096,
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 * @version 2012/02/20/10:36
 */
public class AvailableTypesListModel extends SortedListModel {
    
    /** Odkaz na soubor který obsahuje uložená dostupná rozšíření. */
    public static final String AVAILABLE_EXTENSIONS = "resources/available_extensions.properties";
    
    /** Seznam lokalizačních zpráv. */
    private transient LocalizeMessages messages;
    
    /**
     * Iniciace modelu pro seznam dostupných typových rozšíření.
     */
    public AvailableTypesListModel() {
        messages = Despr.loadLocalizeMessages(getClass(), null, false);
        load();
    }
    
    /**
     * Načte dostupné typy z externího souboru.
     */
    private void load() {
        File file = new File(AVAILABLE_EXTENSIONS);
        if (!file.exists()) {
            return;
        }
        
        Properties props = new Properties();
        FileInputStream fis = null;
        try {
            
            fis = new FileInputStream(file);
            props.load(fis);
        } catch (FileNotFoundException ex) {
            String title = messages.getString("title.file_not_found_excp", 
                    "File not found");
            Despr.showError(title, ex, Level.WARNING, true);
        } catch (IOException ex) {
            String title = messages.getString("title.io_excp", "I/O problem");
            Despr.showError(title, ex, Level.WARNING, true);
        } finally {
            try {
                fis.close();
            } catch (IOException ex) {
                String title = messages.getString("title.io_excp", "I/O problem");
                Despr.showError(title, ex, Level.WARNING, true);
            }
        }
        
        Set keys = props.keySet();
        for (Object key : keys) {
            if (key instanceof String) {
                try {
                    String className = (String) key;
                    Class cls = Class.forName(className, true, DesprClassLoader.getClassLoader());
                    String countOfUseStr = props.getProperty(className);
                    int countOfUse = Integer.parseInt(countOfUseStr);
                    ExtensionType type = new ExtensionType(cls, countOfUse);
                    addElement(type);
                } catch (ClassNotFoundException ex) {
                    String title = messages.getString("title.class_not_found_excp", 
                            "Class not found");
                    Despr.showError(title, ex, Level.WARNING, true);
                } catch (NumberFormatException ex) {
                    String title = messages.getString("title.number_format_excp",
                            "Problem with converting number");
                    Despr.showError(title, ex, Level.WARNING, true);
                }
            }
        }
    }
    
    /**
     * Uloží seznam dostupných typových rozšíření do souboru.
     */
    public void save() {
        File file = new File(AVAILABLE_EXTENSIONS);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException ex) {
                String title = messages.getString("title.io_excp", "I/O problem");
                Despr.showError(title, ex, Level.WARNING, true);
                return;
            }
        }
        
        Properties props = new Properties();
        int size = getSize();
        for (int i = 0; i < size; i++) {
            Object obj = getElementAt(i);
            if (obj instanceof ExtensionType) {
                ExtensionType type = (ExtensionType) obj;
                props.put(type.getType().getCanonicalName(), 
                          Integer.toString(type.getCountOfUse()));
            }
        }
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            props.store(fos, "");
        } catch (FileNotFoundException ex) {
            String title = messages.getString("title.file_not_found_excp", 
                    "File not found");
            Despr.showError(title, ex, Level.WARNING, true);
        } catch (IOException ex) {
            String title = messages.getString("title.io_excp", "I/O problem");
            Despr.showError(title, ex, Level.WARNING, true);
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException ex) {
                    String title = messages.getString("title.io_excp", "I/O problem");
                    Despr.showError(title, ex, Level.WARNING, true);
                }
            }
        }
    }
    
    /**
     * Poskytne typové rozšíření pro daný typ, pokud existuje.
     * @param cls typ pro který by mělo existovat typové rozšíření.
     * @return typové rozšíření pro daný typ, pokud existuje, jinak {@code false}.
     */
    public ExtensionType getType(Class cls) {
        ExtensionType[] types = (ExtensionType[]) model.toArray(new ExtensionType[0]);
        int size = types.length;
        for (int i = 0; i < size; i++) {
            ExtensionType type = types[i];
            if (type.getType().getCanonicalName().equals(cls.getCanonicalName())) {
                return type;
            }
        }
        return null;
    }
}