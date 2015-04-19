
package cz.vsb.cs.sur096.despr.operations.managecolors;

import cz.vsb.cs.sur096.despr.model.operation.parameter.AInputParameter;
import cz.vsb.cs.sur096.despr.model.operation.parameter.AOutputParameter;
import cz.vsb.cs.sur096.despr.model.operation.parameter.EInputParameterType;
import cz.vsb.cs.sur096.despr.operations.AbstractImageOperation;
import cz.vsb.cs.sur096.despr.types.images.ColorImage;
import cz.vsb.cs.sur096.despr.types.images.GrayscaleImage;
import cz.vsb.cs.sur096.despr.view.operation.Displayable;
import java.awt.Image;

/**
 * Operace převede vstupní obrázek na obrázek v odstínech šedi.
 *
 * @author Martin Šurkovský, sur096 
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 * @version 2011/09/02/20:04
 */
public class Grayscale extends AbstractImageOperation implements Displayable {
    
    @AInputParameter(EInputParameterType.OUTER)
    private ColorImage inputImage;
    
    @AOutputParameter
    private GrayscaleImage outputImage;
    
    /**
     * Převede vstupní obrázek na obrázek v odstínech šedi.
     * @throws Exception pokud jsou vstupní parametry nekorektní.
     */
    @Override
    public void execute() throws Exception {
        
        checkInputImage(inputImage);
        
        int w = inputImage.getWidth();
        int h = inputImage.getHeight();
        outputImage = new GrayscaleImage(w, h);

        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                outputImage.setBrightness(i, j, toGray(inputImage.getRGB(i, j)));
            }
        }
    }
    
	/**
	 * Jako náhled je použit výstupní obrázek.
	 * @return výstupní obrázek.
	 */
    @Override
    public Image getThumbnail() {
        return outputImage;
    }
    
    ////////////////////////////////////////////////////////////
    // Get a set metody
    
    /**
     * Nastaví vstupní obrázek.
     * @param inputImage vstupní obrázek.
     * @throws NullPointerException pokud je vstupní obrázek prázdný.
     */
    public void setInputImage(ColorImage inputImage) 
            throws NullPointerException {
        
        checkInputImage(inputImage);
        this.inputImage = inputImage;
    }
    
    /**
     * Poskytne vstupní obrázek.
     * @return vstupní obrázek.
     */
    public ColorImage getInputImage() {
        return inputImage;
    }
    
    /**
     * Nastaví výstupní obrázek.
     * @param outputImage výstupní obrázek.
     * @throws UnsupportedOperationException vždy.
     * @deprecated metoda je definovaná pouze formálně.
     */
    @Deprecated
    public void setOutputImage(GrayscaleImage outputImage) 
            throws UnsupportedOperationException {
        
        throw new UnsupportedOperationException(
                getLocalizeMessage("exception.output_parameter_set_method"));
    }

    /**
     * Poskytne výstupní obrázek.
     * @return výstupní obrázek.
     */
    public GrayscaleImage getOutputImage() {
        return outputImage;
    }
    
    ////////////////////////////////////////////////////////////
    // soukrome pomocne metody
    
    private void checkInputImage(ColorImage inputImage) 
            throws NullPointerException {
        
        if (inputImage == null) {
            throw new NullPointerException(
                    getLocalizeMessage("exception.null_input_image"));
        }
    }
    
    ////////////////////////////////////////////////////////////
    
    private double lum(int color)
    {
        
        int r = ( color & 0x00ff0000 ) >> 16;
        int g = ( color & 0x0000ff00 ) >> 8;
        int b = color & 0x000000ff;

        return 0.299 * r + 0.587 * g + 0.114 * b;
    }
    
    /**
     * Metoda vracející hodnotu barvy v šedé škále.
     * @param color barva pixelu
     * @return barva v šedé škály
     */
    private int toGray(int color)
    {
        int y = ( int ) ( Math.round( lum( color ) ) );
        return y;
    }
}
