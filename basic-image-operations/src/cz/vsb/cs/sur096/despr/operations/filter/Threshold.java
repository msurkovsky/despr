
package cz.vsb.cs.sur096.despr.operations.filter;

import cz.vsb.cs.sur096.despr.model.operation.parameter.AInputParameter;
import cz.vsb.cs.sur096.despr.model.operation.parameter.AOutputParameter;
import cz.vsb.cs.sur096.despr.model.operation.parameter.EInputParameterType;
import cz.vsb.cs.sur096.despr.operations.AbstractImageOperation;
import cz.vsb.cs.sur096.despr.types.images.BinaryImage;
import cz.vsb.cs.sur096.despr.types.images.GrayscaleImage;
import cz.vsb.cs.sur096.despr.view.operation.Displayable;
import java.awt.Image;

/**
 * Operace slouží pro prahování obrázku. Jako vstup je 
 * obrázek v odstínech šedi a jako výstup binární obrázek.
 *
 * @author Martin Šurkovský, sur096 
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 * @version 2011/09/02/21:37
 */
public class Threshold extends AbstractImageOperation implements Displayable {

    /**
     *  Defaultní konstruktor, který nastaví výchozí hodnotu prahu. 
	 *  Ta je 25.
     */
    public Threshold() {
        threshold = 25;
    }
    
    @AInputParameter(value=EInputParameterType.INNER, enteringAs=1)
    private Integer threshold;
    
    @AInputParameter(value=EInputParameterType.OUTER, enteringAs=2)
    private GrayscaleImage inputImage;
    
    @AOutputParameter
    private BinaryImage outputImage;
    
    /**
     * Provede zprahování obrázku, podle nastaveného prahu.
     * @throws Exception pokud nejsou vstupní parametry korektní.
     */
    @Override
    public void execute() throws Exception {
        
        checkInputImage(inputImage);
        checkThreshold(threshold);
        
        int w = inputImage.getWidth();
        int h = inputImage.getHeight();
        
        outputImage = new BinaryImage(w, h);
        
        for(int i = 0; i < w; i++) {
            for ( int j = 0; j < h; j++ ) {
                int c = inputImage.getBrightness(i, j);
                if (c >= threshold) {
                    outputImage.setWhite(i, j);
                } else {
                    outputImage.setBlack(i, j);
                }
            }
        }
    }
    
	/**
	 * Jako náhled je poskytnut výstupní obrázek.
	 * @return výstupní obrázek.
	 */
    @Override
    public Image getThumbnail() {
        return outputImage;
    }
    
    ////////////////////////////////////////////////////////////
    // Get a set metody
    
    /**
     * Poskytne hodnotu prahu.
     * @return hodnota prahu.
     */
    public Integer getThreshold() {
        return threshold;
    }
    
    /**
     * Nastaví hodnotu prahu.
     * @param threshold hodnota prahu.
     * @throws NullPointerException pokud je hodnota prahu prázdná.
     * @throws IllegalArgumentException pokud je práh mimo rozsah 0 až 255 vč.
     */
    public void setThreshold(Integer threshold) 
            throws NullPointerException, IllegalArgumentException {
        
        checkThreshold(threshold);
        this.threshold = threshold;
    }

    /**
     * Poskytne vstupní obrázek.
     * @return vstupní obrázek.
     */
    public GrayscaleImage getInputImage() {
        return inputImage;
    }
    
    /**
     * Nastaví vstupní obrázek.
     * @param inputImage vstupní obrázek.
     * @throws NullPointerException pokud je vstupní obrázek prázdný.
     */
    public void setInputImage(GrayscaleImage inputImage)
            throws NullPointerException {
        
        checkInputImage(inputImage);
        this.inputImage = inputImage;
    }

    /**
     * Poskytne výstupní obrázek.
     * @return výstupní obrázek.
     */
    public BinaryImage getOutputImage() {
        return outputImage;
    }
    
    /**
     * Nastaví výstupní obrázek.
     * @param outputImage výstupní obrázek.
     * @throws UnsupportedOperationException Vždy.
     * @deprecated je definována pouze formálně.
     */
    @Deprecated
    public void setOutputImage(BinaryImage outputImage) 
            throws UnsupportedOperationException {
        
        throw new UnsupportedOperationException(
                getLocalizeMessage("exception.output_parameter_set_method"));
    }
    
    ////////////////////////////////////////////////////////////
    // Soukrome pomocne metody
    private void checkThreshold(Integer threshold) 
            throws NullPointerException, IllegalArgumentException {
        
        if (threshold == null) {
            throw new NullPointerException(
                    getLocalizeMessage("exception.null_threshold"));
        } else if (threshold < 0 || threshold > 255) {
            throw new IllegalArgumentException(
                    getLocalizeMessage("exception.threshold_out_of_range"));
        }
    }
    
    private void checkInputImage(GrayscaleImage inputImage)
            throws NullPointerException {
        
        if (inputImage == null) {
            throw new NullPointerException(
                    getLocalizeMessage("exception.null_input_image"));
        }
    }
}
