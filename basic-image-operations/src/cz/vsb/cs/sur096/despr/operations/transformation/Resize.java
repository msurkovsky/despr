
package cz.vsb.cs.sur096.despr.operations.transformation;

import cz.vsb.cs.sur096.despr.model.operation.parameter.AInputParameter;
import cz.vsb.cs.sur096.despr.model.operation.parameter.AOutputParameter;
import cz.vsb.cs.sur096.despr.model.operation.parameter.EInputParameterType;
import cz.vsb.cs.sur096.despr.operations.AbstractImageOperation;
import cz.vsb.cs.sur096.despr.types.images.ColorImage;
import cz.vsb.cs.sur096.despr.utils.DependsType;
import cz.vsb.cs.sur096.despr.view.operation.Displayable;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;

/**
 * Operace provede změnu velikosti obrázku.
 *
 * @author Martin Šurkovský, sur096 
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 * @version 2011/07/03/19:03
 */
public class Resize extends AbstractImageOperation implements Displayable {
    
    @AInputParameter(value=EInputParameterType.INNER, enteringAs=1)
    private Integer width;

    @AInputParameter(value=EInputParameterType.INNER, enteringAs=2)
    private Integer height;
    
    @AInputParameter(value=EInputParameterType.OUTER, enteringAs=3)
    private ColorImage inputImage;
    
    @AOutputParameter(depends="inputImage")
    private ColorImage outputImage;

    /**
     * Inicializuje operaci s defaultními hodnotami.
     */
    public Resize() { 
        width = 255;
        height = 255;
    }
    
    /**
     * Provede změnu velikosti obrázku.
     * @throws Exception pokud nejsou vstupní parametry korektní.
     */
    @Override
    public void execute() throws Exception {
        
        checkWidth(width);
        checkHeight(height);
        checkInputImage(inputImage);
			
        outputImage = (ColorImage) DependsType.createNewInstance(
                ColorImage.class, inputImage.getClass(), width, height);
        
        Graphics2D g = ((ColorImage) outputImage).createGraphics();  
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);  
        g.drawImage(inputImage, 0, 0, width, height, 0, 0, inputImage.getWidth(), inputImage.getHeight(), null);  
        g.dispose();
    }
    
    /**
     * Jako náhled je použit výstupní obrázek.
     * @return  výstupní obrázek.
     */
    @Override
    public Image getThumbnail() {
        return outputImage;
    }
    
    ////////////////////////////////////////////////////////////
    // Get a set metody
    
    /**
     * Poskytne novou šířku obrázku.
     * @return nová šířka obrázku.
     */
    public Integer getWidth() {
        return width;
    }
    
    /**
     * Nastaví novou šířku obrázku.
     * @param width nová šířka obrázku
     * @throws NullPointerException pokud je hodnota prázdná.
     * @throws IllegalArgumentException pokud je hodnota záporná.
     */
    public void setWidth(Integer width) 
            throws NullPointerException, IllegalArgumentException {
        
        checkWidth(width);
        this.width = width;
    }

    /**
     * Poskytne novou výšku obrázku.
     * @return nová výška obrázku.
     */
    public Integer getHeight() {
        return height;
    }

    /**
     * Nastaví novou výšku obrázku.
     * @param height nová výška obrázku.
     * @throws NullPointerException pokud je hodnota prázdná.
     * @throws IllegalArgumentException pokud je hodnota záporná.
     */
    public void setHeight(Integer height) 
            throws NullPointerException, IllegalArgumentException {
        
        checkHeight(height);
        this.height = height;
    }
    
    /**
     * Poskytne vstupní obrázek.
     * @return vstupní obrázek.
     */
    public ColorImage getInputImage() {
        return inputImage;
    }

    /**
     * Nastaví vstupní obrázek.
     * @param inputImage vstupní obrázek.
     * @throws NullPointerException  pokud je vstupní obrázek prázdný.
     */
    public void setInputImage(ColorImage inputImage) 
            throws NullPointerException {

        checkInputImage(inputImage);
        this.inputImage = inputImage;
    }
    
    /**
     * Poskytne výstupní obrázek.
     * @return výstupní obrázek.
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
    // Soukrome pomocne metody
    
    private void checkWidth(Integer width) 
            throws NullPointerException, IllegalArgumentException {
        if (width == null) {
            throw new NullPointerException(
                    getLocalizeMessage("exception.null_width"));
        } else if (width < 0) {
            throw new IllegalArgumentException(String.format("%s '%d'",
                    getLocalizeMessage("exception.negative_width"), width));
        }
    }
    
    private void checkHeight(Integer height) 
            throws NullPointerException, IllegalArgumentException {
        
        if (height == null) {
            throw new NullPointerException(
                    getLocalizeMessage("exception.null_height"));
        } else if (height < 0) {
            throw new IllegalArgumentException(String.format("%s '%d'",
                    getLocalizeMessage("exception.negative_height"), height));
        }
    }
    
    private void checkInputImage(ColorImage inputImage) 
            throws NullPointerException {
        
        if (inputImage == null) {
            throw new NullPointerException(
                    getLocalizeMessage("exception.null_input_image"));
        }
    }
}
