
package cz.vsb.cs.sur096.despr.operations.io;

import cz.vsb.cs.sur096.despr.model.operation.parameter.AInputParameter;
import cz.vsb.cs.sur096.despr.model.operation.parameter.EInputParameterType;
import cz.vsb.cs.sur096.despr.operations.AbstractImageOperation;
import cz.vsb.cs.sur096.despr.types.Directory;
import cz.vsb.cs.sur096.despr.types.images.ColorImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.StringTokenizer;
import javax.imageio.ImageIO;

/**
 * Operace uloží obrázek na disk.
 * @author Martin Šurkovský, sur096 
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 * @version 2011/09/02/18:45
 */
public class SaveImage extends AbstractImageOperation {
   
    @AInputParameter(value=EInputParameterType.INNER,enteringAs=1)
    private Directory destDir;
    
    @AInputParameter(value=EInputParameterType.INNER,enteringAs=2)
    private String nameSpecification;
    
    @AInputParameter(value=EInputParameterType.OUTER,enteringAs=3)
    private ColorImage inputImg;
    
    @AInputParameter(value=EInputParameterType.INNER,enteringAs=4)
    private String subDirPath;
    
    @AInputParameter(value=EInputParameterType.OUTER,enteringAs=5)
    private String fileName;

    /**
     * Inicializuje operaci s defaultními hodnotami.
     */
    public SaveImage() {
        subDirPath = "";
        nameSpecification = "";
        // defaultni hodnota at neni promena null!. kvuli nacitani ze souboru
        destDir = new Directory(new File(System.getProperty("user.home")));
    }
     
    /**
     * Uloží obrázek na disk.
     * @throws Exception pokud nejsou vstupní parametry korektní nebo
	 * selže ukládaní na disk.
     */
    @Override
    public void execute() throws Exception {
        
        // Kontrola zda jsou vstupni parametry korkentni,
        // pokud ne metoda vyhodi vyjimku
        checkDestDir(destDir.getFile());
        // kontrola zda slozka opravdu existuje se provadi
        // az za behu. Jinak by nebylo mozne nacitat adresare z 
        // ulozenych souboru (ty uz nemusi existovat)
        if (!destDir.getFile().isDirectory()) {
            throw new IllegalArgumentException(
                    getLocalizeMessage("exception.not_directory"));
        }
        checkSubDirPath(subDirPath);
        checkNameSpecification(nameSpecification);
        checkInputImg(inputImg);
        checkFileName(fileName);
        // kontrola OK
        
        String sufix = "png";
        FileOutputStream fos = null;

        String absolutePath = destDir.getFile().getAbsolutePath();
        if (!subDirPath.equals("")) {
            if (subDirPath.charAt(0) == '/') {
                subDirPath = subDirPath.substring(1);
            }
            
            if (subDirPath.charAt(subDirPath.length()-1) == '/') {
                subDirPath = subDirPath.substring(0, subDirPath.length()-1);
            }
            
            StringTokenizer st = new StringTokenizer(subDirPath, "/");
            while (st.hasMoreTokens()) {
                absolutePath += File.separator + st.nextToken();
                File f = new File(absolutePath);
                if (!f.exists()) {
                    f.mkdir();
                }
            }
        }
        absolutePath += File.separator + fileName + 
                (!nameSpecification.equals("") ? "_" + nameSpecification : "") + 
                "." + sufix;
        try {
            fos = new FileOutputStream(absolutePath);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(String.format("%s '%s'", 
                    getLocalizeMessage("exception.file_not_found"), absolutePath));
        }
        
        try {
            ImageIO.write(inputImg, sufix, fos);
        } catch(IOException e) {
            throw new RuntimeException(String.format("%s '%s'", 
                    getLocalizeMessage("exception.file_not_saved"), absolutePath));
        }
    }
    
    ////////////////////////////////////////////////////////////
    // Get a set metody
    
    /**
     * Poskytne odkaz na cílový adresář.
     * @return odkaz na cílový adresář.
     */
    public Directory getDestDir() {
        return destDir;
    }
    
    /**
     * Nastaví cílový adresář.
     * @param dir cílový adresář.
     * @throws NullPointerException pokud je odkaz na cílový adresář
	 * prázdný.
     */
    public void setDestDir(Directory dir) 
           throws NullPointerException {
        
        checkDestDir(dir.getFile());
        this.destDir = dir;
    }
    
    /**
     * Poskytne jméno podadresáře/ů v rámci cílového adresáře.
     * @return jméno podadresáře/ů.
     */
    public String getSubDirPath() {
        return subDirPath;
    }

    /**
     * Nastaví jméno podadresáře/ů v rámci cílového adresáře.
     * @param subDirPath jméno podadresáře/ů.
     * @throws NullPointerException pokud je jméno podadresáře {@code null}.
     */
    public void setSubDirPath(String subDirPath) 
            throws NullPointerException {
        
        checkSubDirPath(subDirPath);
        this.subDirPath = subDirPath;
    }
    
    /**
     * Poskytne specifikaci jména. Jedná se o řetězec který je přidán před
	 * jméno před uložením.
     * @return specifikační jméno.
     */
    public String getNameSpecification() {
        return nameSpecification;
    }

    /**
     * Nastaví specifikaci jména. Jedná se o společný prefix, který bude
	 * použit pokaždé před uložením.
     * @param nameSpecfication specifikace jména.
     * @throws NullPointerException pokud je hodnota {@code null}.
     */
    public void setNameSpecification(String nameSpecfication) 
            throws NullPointerException {
        
        checkNameSpecification(nameSpecfication);
        this.nameSpecification = nameSpecfication;
    }
    
    /**
     * Poskytne vstupní obrázek.
     * @return vstupní obrázek.
     */
    public ColorImage getInputImg() {
        return inputImg;
    }
    
    /**
     * Nastaví vstupní obrázek.
     * @param inputImg vstupní obrázek.
     * @throws NullPointerException pokud je vstupní obrázek prázdný.
     */
    public void setInputImg(ColorImage inputImg) 
            throws NullPointerException {
        
        checkInputImg(inputImg);
        this.inputImg = inputImg;
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
     * @throws NullPointerException pokud je jméno souboru {@code null}.
     */
    public void setFileName(String fileName) 
            throws NullPointerException {
        
        checkFileName(fileName);
        this.fileName = fileName;
    }
    
    ////////////////////////////////////////////////////////////
    // Pomocne vnitrni metody
    private void checkDestDir(File dir) 
            throws NullPointerException {
        
        if (dir == null) {
            throw new NullPointerException(
                    getLocalizeMessage("exception.null_dest_dir"));
        }
    }
    
    private void checkSubDirPath(String subDirPath) 
            throws NullPointerException {
        
        if (subDirPath == null) {
            throw new NullPointerException(
                    getLocalizeMessage("exception.null_sub_dir_path"));
        }
    }
    
    private void checkNameSpecification(String nameSpecification) 
            throws NullPointerException {

        if (nameSpecification == null) {
            throw new NullPointerException(
                    getLocalizeMessage("exception.null_name_specification"));
        } 
    }
    
    private void checkInputImg(ColorImage inputImg) 
            throws NullPointerException {

        if (inputImg == null) {
            throw new NullPointerException(
                    getLocalizeMessage("exception.null_input_img"));
        } 
    }
    
    private void checkFileName(String fileName) 
            throws NullPointerException {
        
        if (fileName == null) {
            throw new NullPointerException(
                    getLocalizeMessage("exception.null_file_name"));
        }
    }
}
