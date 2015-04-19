
package cz.vsb.cs.sur096.despr.operations.specialfunctions;

import cz.vsb.cs.sur096.despr.model.operation.parameter.AInputParameter;
import cz.vsb.cs.sur096.despr.model.operation.parameter.AOutputParameter;
import cz.vsb.cs.sur096.despr.model.operation.parameter.EInputParameterType;
import cz.vsb.cs.sur096.despr.types.images.BinaryImage;
import cz.vsb.cs.sur096.despr.types.images.ColorImage;
import cz.vsb.cs.sur096.despr.view.operation.Displayable;
import java.awt.Color;
import java.awt.Image;

/**
 * Operace vizualizuje normující úhel v polárním obrázku.
 * Vizualizace vypadá tak že je červenou linkou zvýrazněn sloupec
 * který reprezentuje úhel natočení.
 *
 * @author Martin Šurkovský, sur096 
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 * @version 2011/12/26/21:2;
 */
public class VizualizeAngleInPolarImage extends AbstractSpecialOperation implements Displayable {
    
    @AInputParameter(value=EInputParameterType.OUTER,enteringAs=1)
    private Double angle;
    
    @AInputParameter(value=EInputParameterType.OUTER,enteringAs=2)
    private BinaryImage inputImage;
    
    @AOutputParameter
    private ColorImage outputImage;

    /**
     * Vyznačí úhel v obrázku. 
     * @throws Exception pokud jsou vstupní parametry nekorektní.
     */
    @Override
    public void execute() throws Exception {
        
        checkInputImage(inputImage);
        checkAngle(angle);
        
        int w = inputImage.getWidth();
        int h = inputImage.getHeight();
        
        int poz = (int) ((double) (w * angle) / 360);
        outputImage = new ColorImage(w, h);
        
        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                outputImage.setRGB(x, y, inputImage.getRGB(x, y));
                if (x == poz) {
                    outputImage.setRGB(x, y, Color.RED.getRGB());
                }
            }
        }
    }
    
    /**
     * Jako náhled je použit obrázek se zvýrazněným úhlem.
     * @return výstupní obrázek.
     */
    @Override
    public Image getThumbnail() {
        return outputImage;
    }
    ////////////////////////////////////////////////////////////
    // Get a set metody

    /**
     * Poskytne úhel natočení.
     * @return úhel natočeni.
     */
    public Double getAngle() {
        return angle;
    }

    /**
     * Nastaví úhel natočení.
     * @param angle úhel natočení.
     * @throws NullPointerException pokud je hodnota prázdná.
     */
    public void setAngle(Double angle)
            throws NullPointerException {
        
        checkAngle(angle);
        if (angle < 0 || angle > 360) {
            angle = angle % 360;
            angle = (angle < 0 ? 360 + angle : angle);
        }
        this.angle = angle;
    }
    
    /**
     * Poskytne vstupní obrázek.
     * @return vstupní obrázek.
     */
    public BinaryImage getInputImage() {
        return inputImage;
    }

    /**
     * Nastaví vstupní obrázek po polární transformaci.
     * @param inputImage vstupní obrázek po polární transformaci.
     * @throws NullPointerException pokud je hodnota prázdná.
     */
    public void setInputImage(BinaryImage inputImage) 
            throws NullPointerException {
        
        checkInputImage(inputImage);
        this.inputImage = inputImage;
    }

    /**
     * Poskytne výstupní obrázek.
     * @return výstupní obrázek se zvýrazněným úhlem.
     */
    public ColorImage getOutputImage() {
        return outputImage;
    }

    /**
     * Nastaví výstupní obrázek.
     * @param outputImage výstupní obrázek.
	 * @throws UnsupportedOperationException vždy.
     * @deprecated metoda je definována pouze formálně.
     */
    @Deprecated
    public void setOutputImage(ColorImage outputImage) 
			throws UnsupportedOperationException {

        throw new UnsupportedOperationException(
                getLocalizeMessage("exception.output_parameter_set_method"));
    }
    
    ////////////////////////////////////////////////////////////
    // soukrome pomocne metody
    private void checkAngle(Double angle) 
            throws NullPointerException {
        
        if (angle == null) {
            throw new NullPointerException(
                    getLocalizeMessage("exception.null_angle"));
        }
    }
    
    private void checkInputImage(BinaryImage inputImage) 
            throws NullPointerException {
        
        if (inputImage == null) {
            throw new NullPointerException(
                    getLocalizeMessage("exception.null_input_image"));
        }
    }
    
}
