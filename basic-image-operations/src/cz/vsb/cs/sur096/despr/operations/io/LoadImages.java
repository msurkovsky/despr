
package cz.vsb.cs.sur096.despr.operations.io;

import cz.vsb.cs.sur096.despr.model.operation.IRootOperation;
import cz.vsb.cs.sur096.despr.model.operation.parameter.AInputParameter;
import cz.vsb.cs.sur096.despr.model.operation.parameter.AOutputParameter;
import cz.vsb.cs.sur096.despr.model.operation.parameter.EInputParameterType;
import cz.vsb.cs.sur096.despr.operations.AbstractImageOperation;
import cz.vsb.cs.sur096.despr.types.Directory;
import cz.vsb.cs.sur096.despr.types.images.ColorImage;
import cz.vsb.cs.sur096.despr.view.operation.Displayable;
import java.awt.Image;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.*;
import javax.imageio.ImageIO;

/**
 * Operace načte obrázky z adresáře a po jednou je posléze zpřístupňuje.
 *
 * @author Martin Šurkovský, sur096 
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 * @version 2011/09/02/17:03
 */
public class LoadImages extends AbstractImageOperation implements IRootOperation, Displayable {

    private int filesCount;
    private int idx;
    private File[] files;
    private List<File> subdirs;
    private ImageFilter imgFileFilter;
    
    /**
     * Inicializuje operaci s defaultními hodnotami.
     */
    public LoadImages() {
        wasInit = false;
        recursiveBrowse = false;
        shuffle = false;
        subdirs = new ArrayList<File>();
        imgFileFilter = new ImageFilter();
        // defaultni hodnota at neni promena null!. kvuli nacitani ze souboru
        srcDir = new Directory(new File(System.getProperty("user.home")));
    }
    
    @AInputParameter(value=EInputParameterType.INNER, enteringAs=1, lock=true)
    private Directory srcDir;
    
    @AInputParameter(value=EInputParameterType.INNER, enteringAs=2, lock=true)
    private Boolean recursiveBrowse;
    
    @AInputParameter(value=EInputParameterType.INNER, enteringAs=3, lock=true)
    private Boolean shuffle;
    
    @AOutputParameter(enteringAs=1)
    private ColorImage outImg;
    
    @AOutputParameter(enteringAs=2)
    private File file;
    
    private boolean wasInit;
    
    /**
     * Vybere jeden obrázek z kolekce a pošle ho na výstup.
     * @throws Exception  pokud jsou vstupní parametry nekorektní nebo
	 * se selže načítání obrázku.
     */
    @Override
    public void execute() throws Exception {
        
        checkSrcDir(srcDir.getFile());
        checkRecursiveBrowse(recursiveBrowse);
        checkShuffle(shuffle);
        
        if (!wasInit) {
            init();
        }
        
        try {
            file = files[idx];
            outImg = new ColorImage(ImageIO.read(file));
        } catch(IOException ex) {
            
            throw new RuntimeException(String.format("%s '%s' (%s)",
                    getLocalizeMessage("exception.problem_with_load_file"),
                    file.getAbsoluteFile(), ex.getMessage()));
        }
    }
    
	/**
	 * Jako náhled je použít načtený obrázek.
	 * @return načtený obrázek.
	 */
    @Override
    public Image getThumbnail() {
        return outImg;
    }
    
    /**
     * Poskytne velikost načtené kolekce.
     * @return počet obrázků.
     */
    @Override
    public int getCount() {
        // tato je volana pokazde pred spustenim takze nastavi korektne
        // pocatecni podminky
        if (files == null) {
            init();
        }
        return filesCount;
    }
    
    /**
     * Zjistí zda je v kolekci další obrázek. 
     * @return {@code true} pokud je v kolekci další obrázek, jinak {@code false}.
     */
    @Override
    public boolean hasNext() {
        if (idx >= 0 && idx < filesCount) {
            return true;
        }
        return false;
    }

    /**
     * Nastaví ukazatel do kolekce na další obrázek.
     */
    @Override
    public void setNext() {
        idx++;
    }

    /**
     * Nastaví ukazatel do kolekce na nulu.
     */
    @Override
    public void resetIterator() {
        idx = 0;
    }
    
    /**
     * Inicializuje počáteční podmínky. To znamená načte vstupní kolekci dat. 
	 * @throws NullPointerException pokud je vstupní adresář prázdný ({@code null}.
	 * @throws IllegalArgumentException pokud se nejedná o adresář.
     */
    @Override
    public void init() throws NullPointerException, IllegalArgumentException {
        
        checkSrcDir(srcDir.getFile());
        if (!srcDir.getFile().isDirectory()) {
            throw new IllegalArgumentException(String.format("%s '%s'",
                    getLocalizeMessage("exception.source_is_not_directory"),
                    srcDir.getFile().getAbsolutePath()));
        }
        
        if (recursiveBrowse) {
            imagesInSubdir(srcDir.getFile());
            files = (File[]) subdirs.toArray(new File[0]);
            subdirs.clear(); // po pouziti metody imagesInSubDir je treba ji smazat
            // pro dalsi pouziti
        } else {
            files = srcDir.getFile().listFiles(imgFileFilter);
        }

        if (shuffle) {
            List<File> fs = Arrays.asList(files);
            // Random generator neni definovan uplne nahodne. Pokazde
            // zamicha data stejnym zpusobem. Aby se pristopovalo k datum
            // sice v nahodilem poradi, ale porad ve stejne nahodilem.
            // Hlavni duvod je kvuli testovani
            Collections.shuffle(fs, new Random(26542365489L));
            files = (File[]) fs.toArray(new File[0]);
        } else {
            Arrays.sort(files, new FileNameComparator());
        }
        
        idx = 0;
        filesCount = files.length;
        wasInit = true;
    }
    
    /**
     * Zjistí zda byla operace inicializována. Změnou vstupních parametrů, je
     * totiž nutné operaci znovu inicializovat.
     * @return {@code true} pokud byla operace inicializována a vstupní, parametry
     * se nezměnily, jinak {@code false}.
     */
    @Override
    public boolean wasInit() {
        return wasInit;
    }
    
    ////////////////////////////////////////////////////////////
    // Set a get metody
    
    /**
     * Nastaví zdrojový adresář.
     * @param srcDir zdrojový adresář.
     * @throws NullPointerException pokud je odkaz na adresář prázdný.
     */
    public void setSrcDir(Directory srcDir) 
            throws NullPointerException {
        
        checkSrcDir(srcDir.getFile());
        this.srcDir = srcDir;
        wasInit = false; // pri zmene slozky je treba znovu data inicializovat
    }
    
    /**
     * Poskytne zdrojový adresář.
     * @return zdrojový adresář.
     */
    public Directory getSrcDir() {
        return srcDir;
    }
    
    /**
     * Zjistí zda se mají načíst data i z podadresářů.
     * @return {@code true} pokud se mají data načíst i 
	 * z podadresářů, jinak {@code false}.
     */
    public Boolean isRecursiveBrowse() {
        return recursiveBrowse;
    }

    /**
     * Nastaví zda se mají procházet i podadresáře.
     * @param recursiveBrowse procházet podadresáře?
     * @throws NullPointerException pokud je hodnota prázdná.
     */
    public void setRecursiveBrowse(Boolean recursiveBrowse) 
            throws NullPointerException {
        
        checkRecursiveBrowse(recursiveBrowse);
        if (this.recursiveBrowse != recursiveBrowse) {
            wasInit = false; // pri zmene je treba znovu inicializovat data
        }
        this.recursiveBrowse = recursiveBrowse;
    }

    /**
     * Zjistí zda se má k datům přistupovat náhodně.
     * @return {@code true} pokud se má k datům přistupovat náhodně,
	 * jinak {@code false}.
     */
    public Boolean isShuffle() {
        return shuffle;
    }

    /**
     * Nastaví to zda se má k datům přistupovat náhodně.
     * @param shuffle má se k datům přistupovat náhodně?
     * @throws NullPointerException pokud je hodnota prázdná.
     */
    public void setShuffle(Boolean shuffle) 
            throws NullPointerException {
        
        checkShuffle(shuffle);
        if (this.shuffle != shuffle) {
            wasInit = false;
        }
        this.shuffle = shuffle;
    }
    
    /**
     * Poskytne načtený obrázek.
     * @return načtený obrázek.
     */
    public ColorImage getOutImg() {
        return outImg;
    }
    
    /**
     * Nastaví načtený obrázek.
     * @param outImg načtený obrázek.
     * @throws UnsupportedOperationException vždy.
     * @deprecated metoda je definována pouze formálně.
     */
    @Deprecated
    public void setOutImg(ColorImage outImg) 
            throws UnsupportedOperationException {
        
        throw new UnsupportedOperationException(
                getLocalizeMessage("exception.output_parameter_set_method"));
    }
    
    /**
     * Poskytne odkaz na soubor, který byl načten.
     * @return odkaz na načtený soubor. 
     */
    public File getFile() {
        return file;
    }
    
    /**
     * Nastaví načtený soubor.
     * @param file načtený soubor.
     * @throws UnsupportedOperationException vždy.
     * @deprecated metoda je definována pouze formálně.
     */
    @Deprecated
    public void setFile(File file) 
            throws UnsupportedOperationException {
        
        throw new UnsupportedOperationException(
                getLocalizeMessage("exception.output_parameter_set_method"));
    }
    
    ////////////////////////////////////////////////////////////
    // Soukrome pomocne metody

    private void checkSrcDir(File srcDir) 
            throws NullPointerException {
        
        if (srcDir == null) {
            throw new NullPointerException(
                    getLocalizeMessage("exception.null_source_dir"));
        }
    }
    
    private void checkRecursiveBrowse(Boolean recursiveBrowse) 
            throws NullPointerException {
        
        if (recursiveBrowse == null) {
            throw new NullPointerException(
                    getLocalizeMessage("exception.null_recursive"));
        }
    }
    
    private void checkShuffle(Boolean shuffle) 
            throws NullPointerException {
        
        if (shuffle == null) {
            throw new NullPointerException(
                    getLocalizeMessage("exception.null_shuffle"));
        }
    }
    
    ////////////////////////////////////////////////////////////
    
    private void imagesInSubdir(File rootDir) {
        List<File> images = Arrays.asList(rootDir.listFiles(new ImageFilter()));
        subdirs.addAll(images);
        File[] dirs = rootDir.listFiles(new DirFilter());
        
        for (File dir : dirs) {
            imagesInSubdir(dir);
        }
    }

    
    
    ////////////////////////////////////////////////////////////
    // Filtry vstupnich souboru
    protected class ImageFilter implements FileFilter {

        @Override
        public boolean accept(File pathname) {
            String lowerFileName = pathname.getName().toLowerCase();
            
            return lowerFileName.endsWith(".png") || 
                    lowerFileName.endsWith(".jpg") || 
                    lowerFileName.endsWith(".jpeg");
        }
    }
    
    protected class DirFilter implements FileFilter {

        @Override
        public boolean accept(File pathname) {
            return pathname.isDirectory();
        }
    }
    
    protected class FileNameComparator implements Comparator<File> {

        @Override
        public int compare(File o1, File o2) {
            String srcDirAbsolutePath = srcDir.getFile().getAbsolutePath();
            int length = srcDirAbsolutePath.length();
            
            String f1 = o1.getAbsolutePath().substring(length);
            String f2 = o2.getAbsolutePath().substring(length);
            
            return f2.compareTo(f1);
        }
    }
}
