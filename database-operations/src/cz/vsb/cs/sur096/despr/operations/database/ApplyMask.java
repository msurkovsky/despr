
package cz.vsb.cs.sur096.despr.operations.database;

import cz.vsb.cs.sur096.despr.model.operation.parameter.AInputParameter;
import cz.vsb.cs.sur096.despr.model.operation.parameter.AOutputParameter;
import cz.vsb.cs.sur096.despr.model.operation.parameter.EInputParameterType;
import cz.vsb.cs.sur096.despr.types.EMask;
import cz.vsb.cs.sur096.despr.types.EVectorType;
import cz.vsb.cs.sur096.despr.types.images.BinaryImage;
import cz.vsb.cs.sur096.despr.types.images.GrayscaleImage;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 * Operace aplikuje kruhovou masku na obrázek. Masky které je
 * možné použít jsou uloženy v adresáři {@literal circle_masks}.
 *
 * @author Martin Šurkovský, sur096 
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 * @version 2011/12/09/12:46
 */
public class ApplyMask extends AbstractDatabaseOperation {
    
    /**
     * Konstruktor inicializuje operaci defaultními hodnotami.
     */
    public ApplyMask() {
        usedMask = EMask.CIRCLE_69;
        typeVector = EVectorType.ABSOLUTE_VALUES;
        sum = 0;
    }

    @AInputParameter(value=EInputParameterType.INNER, enteringAs=1)
    private EMask usedMask;
    
    @AInputParameter(value=EInputParameterType.INNER, enteringAs=2)
    private EVectorType typeVector;
    
    @AInputParameter(value=EInputParameterType.OUTER, enteringAs=3)
    private BinaryImage inputImage;
    
    @AOutputParameter(enteringAs=1)
    private Float[] outputVector;
    
    @AOutputParameter(enteringAs=2)
    private Integer sum;
    
    /**
     * Aplikuje masku na obrázek.
     * @throws Exception pokud nejsou vstupní parametry korektní 
	 * nebo se nepodaří načíst masku.
     */
    @Override
    public void execute() throws Exception {
        
        checkUsedMask(usedMask);
        checkTypeVector(typeVector);
        checkInputImage(inputImage);
        
        try 
        {
            sum = 0; // je nutne vzdy vynulovat pred kazdym znovu spustenim
            GrayscaleImage mask = null;
            int vectorLength = 0;
            switch (usedMask) {
                case CIRCLE_69:
                    mask = new GrayscaleImage(ImageIO.read(
                            getClass().getResource("circle_masks/mask_69.png")));
                    vectorLength = 69;
                    break;
                case CIRCLE_72:
                    mask = new GrayscaleImage(ImageIO.read(
                            getClass().getResource("circle_masks/mask_72.png")));
                    vectorLength = 72;
                    break;
            }
            
            outputVector = new Float[vectorLength];
            
            // v poli jsou ulozeny velikosti jetdnotlivych segmentu, 
            // slouzi nasledne pro zrelativizovani vektoru.
            
            int[] segmentsSize = new int[vectorLength];
            for (int i = 0; i < vectorLength; i++) {
                outputVector[i] = 0f;
                segmentsSize[i] = 0;
            }
            
            int width = inputImage.getWidth();
            int height = inputImage.getHeight();
            
            if (width != mask.getWidth() || height != mask.getHeight()) {
                BufferedImage resizedMask = resize(mask, width, height);
                mask = new GrayscaleImage(resizedMask);
            }
            
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    int segment = mask.getBrightness(x, y);
                    if (segment >= 0 && segment < vectorLength) {
                        if (inputImage.isWhite(x, y)) {
                            outputVector[segment]++;
                            sum++;
                        }
                        
                        segmentsSize[segment]++;
                    }
                }
            }
            
            switch(typeVector) {
                case ABSOLUTE_VALUES:
                    break;
                case RELATIVE_VALUES:
                    outputVector = relativizeValues(outputVector, segmentsSize);
                    break;
            }
        } catch (IOException ex) {
            String msg = ex.getMessage();
            String extendedMsg = String.format("%s %s", msg, 
                    getLocalizeMessage("exception.problem_with_load_mask"));
            throw new IOException(extendedMsg);
        }
    }
    
    ////////////////////////////////////////////////////////////
    // Get a set metody
    
    /**
     * Poskytne typ použité masky.
     * @return typ použité masky.
     */
    public EMask getUsedMask() {
        return usedMask;
    }
    
    /**
     * Nastaví typ masky jaký má být použit.
     * @param mask typ masky.
     * @throws NullPointerException pokud je hodnota prázdná.
     */
    public void setUsedMask(EMask mask) throws NullPointerException {
        checkUsedMask(usedMask);
        this.usedMask = mask;
    }

    /**
     * Poskytne typ vektoru.
     * @return typ vektoru.
     */
    public EVectorType getTypeVector() {
        return typeVector;
    }
    
    /**
     * Nastaví typ vektoru, tj. typ hodnot jaké má obsahovat
	 * výstupní vektor.
     * @param typeVector typ vektoru.
     * @throws NullPointerException  pokud je hodnota prázdná.
     */
    public void setTypeVector(EVectorType typeVector) 
            throws NullPointerException {
        
        checkTypeVector(typeVector);
        this.typeVector = typeVector;
    }

    /**
     * Poskytne vstupní obrázek.
     * @return 
     */
    public BinaryImage getInputImage() {
        return inputImage;
    }
    
    /**
     * Nastaví vstupní obrázek.
     * @param inputImage vstupní obrázek.
     * @throws NullPointerException pokud je hodnota prázdná.
     */
    public void setInputImage(BinaryImage inputImage) 
             throws NullPointerException {
         
        checkInputImage(inputImage); 
        this.inputImage = inputImage;
    }

    /**
     * Poskytne výstupní vektor.
     * @return výstupní vektor.
     */
    public Float[] getOutputVector() {
        return outputVector;
    }

    /**
     * Nastaví výstupní vektor
     * @param outputVector výstupní vektor
     * @throws UnsupportedOperationException vždy.
     * @deprecated metoda je definována pouze formálně.
     */
    @Deprecated
    public void setOutputVector(Float[] outputVector) 
            throws UnsupportedOperationException {
        
        throw new UnsupportedOperationException(
                getLocalizeMessage("exception.output_parameter_set_method"));
    }
    
    /**
     * Poskytne celkový součet všech hodnot výstupního vektoru.
     * @return součet hodnot výstupního vektoru.
     */
    public Integer getSum() {
        return sum;
    }

    /**
     * Nastaví součet výstupního vektoru.
     * @param sum součet výstupního vektoru.
     * @throws UnsupportedOperationException vždy.
     * @deprecated metoda je definována pouze formálně.
     */
    @Deprecated
    public void setSum(Integer sum) 
           throws UnsupportedOperationException {
        
        throw new UnsupportedOperationException(
                getLocalizeMessage("exception.output_parameter_set_method"));
    }
    
    ////////////////////////////////////////////////////////////
    // Soukrome pomocne metody
    
    private void checkUsedMask(EMask usedMask) 
            throws NullPointerException {
        
        if (usedMask == null) {
            throw new NullPointerException(
                    getLocalizeMessage("exception.null_used_mask"));
        }
    }
    
    private void checkTypeVector(EVectorType typeVector) 
            throws NullPointerException {
        
        if (typeVector == null) {
            throw new NullPointerException(
                    getLocalizeMessage("exception.null_type_vector"));
        }
    }
    
    private void checkInputImage(BinaryImage inputImage) 
            throws NullPointerException {
        
        if (inputImage == null) {
            throw new NullPointerException(
                    getLocalizeMessage("exception.null_input_image"));
        }
    }
    
    ////////////////////////////////////////////////////////////
    
    private Float[] relativizeValues(Float[] vector, int[] segmentsSize) {
        int vectorLength = vector.length;
        Float[] relativize = new Float[vectorLength];
        for (int i = 0; i < vectorLength; i++) {
            relativize[i] = (vector[i] * 100) / segmentsSize[i];
        }
        return relativize;
    }
    
    private BufferedImage resize(BufferedImage img, int width, int height) {
        
        BufferedImage outputImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = outputImage.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, 
                              RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.drawImage(img, 0, 0, width, height, 0, 0, img.getWidth(), img.getHeight(), null);
        return outputImage;
    }
}
