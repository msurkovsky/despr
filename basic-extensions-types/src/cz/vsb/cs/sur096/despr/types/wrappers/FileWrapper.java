package cz.vsb.cs.sur096.despr.types.wrappers;

import cz.vsb.cs.sur096.despr.types.Wrapper;
import java.io.File;

/**
 * Wrapper pro typ {@code java.io.File} umožňuje ukládání a 
 * znovu načtení uložené hodnoty. Vytáhne z typu důležitou vlastnost,
 * a to cestu k souboru {@code path}. K ní pak definuje přístupové metody.
 *
 * @author Martin Šurkovský, sur096
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 * @version 2011/10/30/19:35
 */
public class FileWrapper implements Wrapper<File> {
    
	/** Cesta k souboru */
    private String path;
    
	/** Defaultní kostruktor */
    public FileWrapper() { }
    
	/**
	 * Metoda nastaví cestu k souboru.
	 * @param path cesta k souboru.
	 */
    public void setPath(String path) {
        this.path = path;
    }
    
	/**
	 * Vrátí cestu k souboru.
	 * @return cesta k souboru.
	 */
    public String getPath() {
        return path;
    }
    
	/**
	 * Metoda zabalí soubor do wrappru.
	 * @param f soubor, který má být uložen ve wrappru.
	 */
    @Override
    public void wrap(File f) {
        this.path = f.getAbsolutePath();
    }
    
	/**
	 * Metoda vydá uložený soubor.
	 * @return uložený soubor.
	 */
    @Override
    public File unwrap() {
        return new File(path);
    }

	/**
	 * Metoda vrátí hodnotu datového typu, který uchovává.
	 * @return uložený datový typ
	 */
    @Override
    public Class<File> getWrapType() {
        return File.class;
    }
}
