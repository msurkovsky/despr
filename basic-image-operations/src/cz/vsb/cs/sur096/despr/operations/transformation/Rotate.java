
package cz.vsb.cs.sur096.despr.operations.transformation;

import cz.vsb.cs.sur096.despr.model.operation.parameter.AInputParameter;
import cz.vsb.cs.sur096.despr.model.operation.parameter.AOutputParameter;
import cz.vsb.cs.sur096.despr.model.operation.parameter.EInputParameterType;
import cz.vsb.cs.sur096.despr.operations.AbstractImageOperation;
import cz.vsb.cs.sur096.despr.types.images.ColorImage;
import cz.vsb.cs.sur096.despr.utils.DependsType;
import cz.vsb.cs.sur096.despr.view.operation.Displayable;
import java.awt.Color;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

/**
 * Operace otočí obrázek kolem středu. Obrázek si po otočení zachová původní
 * rozměry. To znamená že pokud se nějaká část během otočení dostane
 * mimo viditelnou část, pak bude oříznuta.
 *
 * @author Martin Šurkovský, sur096 
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 * @version 2011/09/02/21:03
 */
public class Rotate extends AbstractImageOperation implements Displayable {

    @AInputParameter(value=EInputParameterType.INNER, enteringAs=1)
    private Double angle;
    
    @AInputParameter(value=EInputParameterType.INNER, enteringAs=2)
    private Boolean combineBackground;
    
    @AInputParameter(value=EInputParameterType.OUTER, enteringAs=3)
    private ColorImage inputImage;
    
    @AOutputParameter(depends="inputImage")
    private ColorImage outputImage;

    /**
     * Inicializuje operaci s defaultními hodnotami.
     */
    public Rotate() {
        combineBackground = true;
        angle = 0.0;
    }
    
    /**
     * Provede otočení obrázku o daný úhel podle středu.
     * @throws Exception  pokud jsou vstupní parametry nekorektní.
     */
    @Override
    public void execute() throws Exception {
        
        checkAngle(angle);
        checkCombineBackground(combineBackground);
        checkInputImage(inputImage);
        
        int w = inputImage.getWidth();
        int h = inputImage.getHeight();
        
        // prevod na radiany
        double degree_of_rotation = Math.toRadians(angle);

        // kotevni bod, podle ktereho se bude obrazek otecet
        double anchor_x = (double) w / 2;
        double anchor_y = (double) h / 2;


        BufferedImage tmpImg = new BufferedImage(w, h, BufferedImage.TYPE_3BYTE_BGR);
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                tmpImg.setRGB(i, j, inputImage.getRGB(i, j));
            }
        }
        
        // nastaveni výstupního obrázku
        outputImage = (ColorImage) DependsType.createNewInstance(
                ColorImage.class, inputImage.getClass(), w, h);

        AffineTransform at = new AffineTransform();

        // rotace obrázku o danný úhel okolo kotevního bodu [anchor_x, anchor_y]
        at.rotate(degree_of_rotation, anchor_x, anchor_y);
        // TYPE_BILINEAR zajisti vyhlazeni po natoceni
        AffineTransformOp ato = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
        // aplikace filtru na obrázek;
        ato.filter(tmpImg, outputImage);
        
        // nastavím jednotné pozadí
        if (combineBackground) {
            combineBackground(w, h);
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
     * Poskytne úhel natočení.
     * @return úhel natočení.
     */
    public Double getAngle() {
        return angle;
    }
    
    /**
     * Nastaví úhel natočení.
     * @param angle úhel natočení.
     * @throws NullPointerException pokud je úhel natočení prázdný.
     */
    public void setAngle(Double angle) 
            throws NullPointerException {
        
        checkAngle(angle);
        //prevod stupne do rozsahu <0, 360);
        if (angle < 0 || angle > 360) {
            angle = angle % 360;
            angle = (angle < 0 ? 360 + angle : angle);
        }
        this.angle = angle;
    }

    /**
     * Zjistí zda má být doplněno pozadí. Metoda byla navržena
	 * na otáčení mincí, počítá tedy s obrázky kruhového tvaru
	 * a pokud je nastavena hodnota na {@code true} pak je okolo
	 * tohoto kruhu doplněna bílá barva.
     * @return {@code true} pokud má být doplněno pozadí, 
	 * jinak {@code false}.
     */
    public Boolean isCombineBackground() {
        return combineBackground;
    }

    /**
     * Nastaví to zda má být doplněno pozadí okolo mince čí ne.
     * @param combineBackground má být doplněno pozadí?
     * @throws NullPointerException pokud je hodnota prázdná.
     */
    public void setCombineBackground(Boolean combineBackground) 
            throws NullPointerException {
        
        checkCombineBackground(combineBackground);
        this.combineBackground = combineBackground;
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
     * @throws NullPointerException pokud je vstupní obrázek prázdný.
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
    // soukrome pomocne metody

    private void checkAngle(Double angle) 
            throws NullPointerException {
        
        if (angle == null) {
            throw new  NullPointerException(
                    getLocalizeMessage("exception.null_angle"));
        }
    }
    
    private void checkCombineBackground(Boolean combineBackground) 
            throws NullPointerException {
        
        if (combineBackground == null) {
            throw new NullPointerException(
                    getLocalizeMessage("exception.null_combine_background"));
        }
    }
    
    private void checkInputImage(ColorImage inputImage) 
            throws NullPointerException {
        
        if (inputImage == null) {
            throw new NullPointerException(
                    getLocalizeMessage("exception.null_input_image"));
        }
    }
    
    ////////////////////////////////////////////////////////////
    /**
     * Funkce doplní jednotné pozadí po otočení mince.
     */
    private void combineBackground(int w, int h)
    {
        
        int r = (int) Math.floor(Math.max(w, h) / 2);

        int cx = (int) Math.floor(w / 2);
        int cy = (int) Math.floor(h / 2);

        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                // posunuti souradnic
                int x = i - cx;
                int y = j - cy;
                
                int r_akt = (int) Math.round(Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2)));

                if (r_akt > r) {
                    outputImage.setRGB(i, j, Color.WHITE.getRGB());
                }
            }
        }
    }
}
