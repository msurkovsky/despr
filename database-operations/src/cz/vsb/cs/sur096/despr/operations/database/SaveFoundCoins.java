
package cz.vsb.cs.sur096.despr.operations.database;

import cz.vsb.cs.sur096.despr.model.operation.parameter.AInputParameter;
import cz.vsb.cs.sur096.despr.model.operation.parameter.AOutputParameter;
import cz.vsb.cs.sur096.despr.model.operation.parameter.EInputParameterType;
import cz.vsb.cs.sur096.despr.types.CoinInformation;
import cz.vsb.cs.sur096.despr.types.Directory;
import cz.vsb.cs.sur096.despr.types.images.ColorImage;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 *
 * @author Martin Šurkovský, sur096,
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 * @version 2012/04/22/18:12
 */
public class SaveFoundCoins extends AbstractDatabaseOperation {

    public SaveFoundCoins() {
        targetDirectory = new Directory(new File(System.getProperty("user.home")));
        searchedFileName = "";
    }
    
    @AInputParameter(value=EInputParameterType.INNER, enteringAs=1)
    private Directory targetDirectory;
    
    @AInputParameter(value= EInputParameterType.OUTER, enteringAs=2)
    private ColorImage searchedImage;
    
    @AInputParameter(value=EInputParameterType.OUTER, enteringAs=3, lock=true)
    private CoinInformation[] foundCoins;
    
    @AInputParameter(value=EInputParameterType.OUTER, enteringAs=4)
    private String searchedFileName;
    
    @Override
    public void execute() throws Exception {
        
        checkFoundCoins(foundCoins);
        checkTargetDirectory(targetDirectory);
        if (!targetDirectory.getFile().exists()) {
            throw new IllegalArgumentException(
                    getLocalizeMessage("exception.target_directory_does_not_exist"));
        }
        checkSearhedFileName(searchedFileName);
        if (searchedFileName.equals("")) {
            throw new IllegalAccessException(
                    getLocalizeMessage("exception.searcher_fle_name_must_not_be_empty_string"));
        }
        checkSearchedImage(searchedImage);
        
        int foundCoinsSize = foundCoins.length;
        String targetDirectoryName = targetDirectory.getFile().getAbsolutePath() +
                    File.separator + searchedFileName;
        File specifiedTargetDirectory = new File(targetDirectoryName);
        
        if (!specifiedTargetDirectory.exists() && !specifiedTargetDirectory.mkdirs()) {
            throw new Exception(
                    getLocalizeMessage("exception.fail_creating_target_directory"));
        }
        
        ImageIO.write(searchedImage, "png", new FileOutputStream(String.format(
                "%s%s00_searched_%s.png", 
                specifiedTargetDirectory.getAbsolutePath(),
                File.separator, searchedFileName)));
        
        for (int i = 0; i < foundCoinsSize; i++) {
            CoinInformation coin = foundCoins[i];
            File source = new File(coin.getSourceImage());
            if (!source.exists()) {
                throw new FileNotFoundException(String.format("%s '%s'", 
                        getLocalizeMessage("exception.file_not_found"),
                        source.getAbsolutePath()));
            }
            BufferedImage img = ImageIO.read(source);
            String targetFileName = String.format("%s%s%d_%s.%s", 
                    specifiedTargetDirectory.getAbsolutePath(),
                    File.separator, i, coin.getCoinName(), "png");
            ImageIO.write(img, "png", new FileOutputStream(targetFileName));
        }
    }

    public CoinInformation[] getFoundCoins() {
        return foundCoins;
    }

    public void setFoundCoins(CoinInformation[] foundCoins) 
            throws NullPointerException {
        
        checkFoundCoins(foundCoins);
        this.foundCoins = foundCoins;
    }

    public Directory getTargetDirectory() {
        return targetDirectory;
    }

    public void setTargetDirectory(Directory targetDirectory) 
            throws NullPointerException {
        
        checkTargetDirectory(targetDirectory);
        this.targetDirectory = targetDirectory;
    }

    public String getSearchedFileName() {
        return searchedFileName;
    }

    public void setSearchedFileName(String searchedFileName) 
            throws NullPointerException {
        
        checkSearhedFileName(searchedFileName);
        this.searchedFileName = searchedFileName;
    }

    public ColorImage getSearchedImage() {
        return searchedImage;
    }

    public void setSearchedImage(ColorImage searchedImage) 
            throws NullPointerException {
        
        checkSearchedImage(searchedImage);
        this.searchedImage = searchedImage;
    }
    
    
    ////////////////////////////////////////////////////////////
    
    private void checkFoundCoins(CoinInformation[] foundCoins) 
            throws NullPointerException {
        
        if (foundCoins == null) {
            throw new NullPointerException(
                    getLocalizeMessage("exception.null_found_coins"));
        } else {
            for (CoinInformation ci : foundCoins) {
                if (ci == null) {
                    throw new NullPointerException(
                            getLocalizeMessage("exception.null_item_found_coins"));
                }
            }
        }
    }
    
    private void checkTargetDirectory(Directory targetDirectory) 
            throws NullPointerException {
        
        if (targetDirectory == null) {
            throw new NullPointerException(
                    getLocalizeMessage("exception.null_target_directory"));
        }
    }
    
    private void checkSearhedFileName(String searchedFileName) 
            throws NullPointerException {
        
        if (searchedFileName == null) {
            throw new NullPointerException(
                    getLocalizeMessage("exception.null_searched_file_name"));
        }
    }
    
    private void checkSearchedImage(ColorImage searchedImage) 
            throws NullPointerException {
        
        if (searchedImage == null) {
            throw new NullPointerException(
                    getLocalizeMessage("exception.null_searched_image"));
        }
    }
    
    ////////////////////////////////////////////////////////////
}    