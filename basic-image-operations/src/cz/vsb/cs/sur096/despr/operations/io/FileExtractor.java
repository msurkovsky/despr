
package cz.vsb.cs.sur096.despr.operations.io;

import cz.vsb.cs.sur096.despr.model.operation.parameter.AInputParameter;
import cz.vsb.cs.sur096.despr.model.operation.parameter.AOutputParameter;
import cz.vsb.cs.sur096.despr.model.operation.parameter.EInputParameterType;
import cz.vsb.cs.sur096.despr.operations.AbstractImageOperation;
import java.io.File;

/**
 * Operace složí pro rozdělení odkazu typu {@code java.io.File} na
 * jednotlivé části. Poskytne jméno rodičovské složky, jméno souboru,
 * příponu a absolutní cestu k souboru.
 *
 * @author Martin Šurkovský, sur096 
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 * @version 2011/12/26/13:31
 */
public class FileExtractor extends AbstractImageOperation {

    @AInputParameter(EInputParameterType.OUTER)
    private File file;
    
    @AOutputParameter(enteringAs=1)
    private String dir;
    @AOutputParameter(enteringAs=2)
    private String fileName;
    @AOutputParameter(enteringAs=3)
    private String suffix;
    @AOutputParameter(enteringAs=4)
    private String absolutePath;
    
    /**
     * Rozdělí vstupní soubor na několik částí.
     * @throws Exception pokud je vstupní parametr nekorektní.
     */
    @Override
    public void execute() throws Exception {
        
        checkFile(file);
        
        absolutePath = file.getAbsolutePath();
        dir = file.getParentFile().getName();

        String[] splitName = file.getName().split("\\.");
        
        // oddeleni jmena od pripony
        fileName = ""; // vynulovani jmena
        for (int i = 0; i < splitName.length - 2; i++) {
            fileName += splitName[i] + ".";
        }
        fileName += splitName[splitName.length - 2];
        
        // za priponu je povazovan retezec za posledni teckou
        suffix = splitName[splitName.length - 1];
    }

    ////////////////////////////////////////////////////////////////////////////
    // Get and set methods
    
    /**
     * Poskytne odkaz na soubor.
     * @return soubor.
     */
    public File getFile() {
        return file;
    }

    /**
     * Nastaví vstupní soubor.
     * @param file vstupní soubor.
     * @throws NullPointerException pokud je odkaz na vstupní soubor prázdný.
     */
    public void setFile(File file) 
            throws NullPointerException {
        
        checkFile(file);
        this.file = file;
    }

    /**
     * Poskytne jméno nadřazeného adresáře.
     * @return jméno nadřazeného adresáře.
     */
    public String getDir() {
        return dir;
    }

    /**
     * Nastaví jméno nadřazeného adresáře.
     * @param dir jméno nadřazeného adresáře.
     * @throws UnsupportedOperationException Vždy.
     * @deprecated tato metoda je definována pouze formálně.
     */
    @Deprecated
    public void setDir(String dir) 
            throws UnsupportedOperationException {
        
        throw new UnsupportedOperationException(
                getLocalizeMessage("exception.output_parameter_set_method"));
    }

    /**
     * Poskytne jméno souboru.
     * @return jméno souboru.
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * Nastaví jméno souboru.
     * @param fileName jméno souboru.
     * @throws UnsupportedOperationException Vždy.
     * @deprecated metoda je definována pouze formálně.
     */
    @Deprecated
    public void setFileName(String fileName) 
            throws UnsupportedOperationException {
        
        throw new UnsupportedOperationException(
                getLocalizeMessage("exception.output_parameter_set_method"));
    }

    /**
     * Poskytne jméno přípony.
     * @return jméno přípony souboru.
     */
    public String getSuffix() {
        return suffix;
    }

    /**
     * Nastaví jméno přípony.
     * @param sufix jméno přípony.
     * @throws UnsupportedOperationException Vždy.
     * @deprecated metoda je definována pouze formálně.
     */
    @Deprecated
    public void setSuffix(String sufix) 
            throws UnsupportedOperationException {
        
        throw new UnsupportedOperationException(
                getLocalizeMessage("exception.output_parameter_set_method"));
    }

    /**
     * Poskytne absolutní cestu k souboru jako řetězec.
     * @return absolutní cesta.
     */
    public String getAbsolutePath() {
        return absolutePath;
    }

    /**
     * Nastaví absolutní cestu.
     * @param absolutePath absolutní cesta.
     * @throws UnsupportedOperationException Vždy.
     * @deprecated metoda je definována pouze formálně.
     */
    @Deprecated
    public void setAbsolutePath(String absolutePath) 
            throws UnsupportedOperationException {
        
        throw new UnsupportedOperationException(
                getLocalizeMessage("exception.output_parameter_set_method"));
    }
    
    ////////////////////////////////////////////////////////////
    // Soukrome pomocne metody
    
    private void checkFile(File file) 
            throws NullPointerException {
        
        if (file == null) {
            throw new NullPointerException(
                    getLocalizeMessage("exception.null_file"));
        }
    }
}
