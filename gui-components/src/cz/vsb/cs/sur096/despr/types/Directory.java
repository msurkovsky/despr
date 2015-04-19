
package cz.vsb.cs.sur096.despr.types;

import java.io.File;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Objekt reprezentující adresář.
 *
 * @author Martin Šurkovský, sur096,
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 * @version 2012/02/14/22:16
 */
public final class Directory {
 
    private transient ResourceBundle messages;
    
    /** Okdaz na adresář. */
    private File file;
    
    /** Inicializuje adresář */
    public Directory() {
        // this constructor is only for loading from a save file
        try {
            messages = ResourceBundle.getBundle(getClass().getCanonicalName());
        } catch (MissingResourceException ex) {
            messages = ResourceBundle.getBundle(getClass().getCanonicalName(), 
                    Locale.US);
        }
    }
    
    /**
     * Inicializuje adresář podle soboru
	 * @param f soubor reprezentující adresář.
     */
    public Directory(File f) {
        this();
        setFile(f);
    }
    
    /**
     * Nastaví adresář.
     * @param file soubor reprezentující adresář.
	 * @throws IllegalArgumentException pokud nejedná o 
	 * adresář, nebo se jej nepodaří vytvořit.
     */
    public void setFile(File file) throws IllegalArgumentException {
        if (file != null) {
            if (file.exists()) {
                this.file = file;
            } else {
                this.file = new File(System.getProperty("user.home"));
            }
        } else {
            this.file = new File(System.getProperty("user.home"));
        }
    }
    
    /**
     * Vrátí odkaz na adresář.
     * @return odkaz na soubor reprezentující adresář.
     * @throws NullPointerException pokud je soubor {@code null}.
     */
    public File getFile() throws NullPointerException {
        if (file == null) {
            throw new NullPointerException(getString("exception.null_file", "File is NULL!"));
        }
        
        return file;
    }
    
    /**
     * Poskytne absolutní cestu k adresáři jako řetězec.
     * @return absolutní cestu k adresáři.
     */
    @Override
    public String toString() {
        if (file == null) {
            return "";
        } else {
            return file.getAbsolutePath();
        }
    }
    
    private String getString(String key, String defaultMessage) {
        if (messages != null) {
            if (key != null) {
                try {
                    return messages.getString(key);
                } catch (MissingResourceException ex) {
                    return defaultMessage;
                }
            } else {
                return defaultMessage;
            }
        } else {
            return defaultMessage;
        }
    }
}
