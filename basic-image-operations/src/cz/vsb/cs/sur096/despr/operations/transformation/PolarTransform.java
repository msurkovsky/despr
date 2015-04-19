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

/**
 * Operace provede polární transformaci obrazu. Počítá s tím že obraz
 * je kruhového tvaru a provádí "rozmotání" obrazu okolo geometrického
 * středu.
 *
 * @author Martin Šurkovský, sur096 
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 * @version 2011/12/14/16:53
 */
public class PolarTransform extends AbstractImageOperation implements Displayable {
    
    @AInputParameter(value=EInputParameterType.INNER, enteringAs=1)
    private Integer polarWidth;
    
    @AInputParameter(value=EInputParameterType.OUTER, enteringAs=2)
    private ColorImage inputImage;
    
    @AOutputParameter(depends="inputImage")
    private ColorImage outputImage;
    
    /**
     * Inicializuje operaci s defaultní hodnotou šířky obrazu po transformaci.
     */
    public PolarTransform() {
        polarWidth = 360;
    }
    
    /**
     * Provede polární transformaci.
     * @throws Exception pokud nejsou vstupní parametry korektní.
     */   
    @Override
    public void execute() throws Exception {
        
        checkPolarWidth(polarWidth);
        checkInputImage(inputImage);
        
        int originalWidth = inputImage.getWidth();
        int originalHeight = inputImage.getHeight();
        
        // vyska vysledneho obrazu
        int polarHeight = (int) Math.floor(Math.min(originalWidth, originalHeight) / 2);
        
        outputImage = (ColorImage) DependsType.createNewInstance(
                ColorImage.class, inputImage.getClass(), polarWidth, polarHeight);
        
        // konstanta pro konverzi uhlu do rozsahu 360
        float conversionAngle = (float) 360 / polarWidth;

        // stred puvodniho obrazu na ose x
        int cx = (int) Math.floor((double) originalWidth / 2);
        // stred puvodniho obrazu na ose y
        int cy = (int) Math.floor((double) originalHeight / 2);

        for ( int theta = 0; theta < polarWidth; theta++ ) {
            for ( int r = 0; r < polarHeight; r++ ) {

                // x-ova a y-lonova souradnice bodu v puvodnim obrazu
                double x = r * Math.cos(Math.toRadians(-theta * conversionAngle)) + cx;
                double y = r * Math.sin(Math.toRadians(-theta * conversionAngle)) + cy;

                try {
                   if (x - Math.floor(x) == 0 && y - Math.floor(y) == 0) {
                       outputImage.setRGB(
                               theta, r, inputImage.getRGB((int) x, (int) y));
                   } else {
                       outputImage.setRGB(
                               theta, r, bilinearInterpolation(x,y).getRGB());
                   }
                } catch(ArrayIndexOutOfBoundsException e) {
                    throw new ArrayIndexOutOfBoundsException(
                            String.format("%s '[x,y] = [%f, %f]'", 
                            e.getMessage(), x, y));
                }
            }
        }
    }
    
	/**
	 * Jako náhled je použit výstupní obraz.
	 * @return výstupní obrázek.
	 */
    @Override
    public Image getThumbnail() {
        return outputImage;
    }
    
    ////////////////////////////////////////////////////////////////////////////
    // Get a Set methody

    /**
     * Poskytne šířku obrázku po transformaci.
     * @return šířka transformovaného obrázku.
     */
    public Integer getPolarWidth() {
        return polarWidth;
    }

    /**
     * Nastaví šířku obrázku po transformaci.
     * @param polarWidth šířka obrázku po transformaci.
     * @throws NullPointerException pokud je hodnota prázdná.
     * @throws IllegalArgumentException pokud je hodnota záporná.
     */
    public void setPolarWidth(Integer polarWidth) 
            throws NullPointerException, IllegalArgumentException {
        
        checkPolarWidth(polarWidth);
        this.polarWidth = polarWidth;
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
    
    ////////////////////////////////////////////////////////////////////////////
    // Soukrome metody
    
    private void checkPolarWidth(Integer polarWidth) 
            throws NullPointerException, IllegalArgumentException {
        
        if (polarWidth == null) {
            throw new NullPointerException(
                    getLocalizeMessage("exception_null_polar_width"));
        } else if (polarWidth <= 0) {
            throw new IllegalArgumentException(String.format("%s '%d'",
                    getLocalizeMessage("exception.negative_polar_width"),
                    polarWidth));
        }
    }
    
    private void checkInputImage(ColorImage inputImage) 
            throws NullPointerException {
        
        if (inputImage == null) {
            throw new NullPointerException(
                    getLocalizeMessage("exception.null_input_image"));
        }
    }
    ////////////////////////////////////////////////////////////////////////////
    /**
     * Vypocet interpolovane hodnoty danneho pixelu.
     *
     * @param p1 - prvni okolni bod
     * @param p2 - druhy okolni bod
     * @param p3 - treti okolni bod
     * @param p4 - ctvrty okolni bod
     * @param dx - desetinna cast z x-ove souradnice
     * @param dy - desetinna cast z y-lonove souradnice
     * @return
     */
    private int bICompute(int p1, int p2, int p3, int p4, double dx, double dy)
    {
        int c = (int) Math.floor(
                (1 - dx) * (1 - dy) * p1 +
                (1 - dx) * dy * p2 +
                dx * (1 - dy) * p3 +
                dx * dy * p4);

        return c;
    }

    /**
     * Vypocet barvy interpolovaneho pixelu.
     * 
     * @param x - x-ova souradnice
     * @param y - y-lonova souradnice
     * @return barva interpolovaneho pixelu
     */
    private Color bilinearInterpolation(double x, double y) {
        int xdi = (int) Math.floor(x);
        int ydi = (int) Math.floor(y);

        double dx = x - xdi;
        double dy = y - ydi;

        int x11 = xdi,     y11 = ydi;
        int x12 = xdi,     y12 = ydi + 1;
        int x21 = xdi + 1, y21 = ydi;
        int x22 = xdi + 1, y22 = ydi + 1;

        int p1Red   = inputImage.getRed(x11, y11);
        int p1Green = inputImage.getGreen(x11, y11);
        int p1Blue  = inputImage.getBlue(x11, y11);

        int p2Red   = inputImage.getRed(x12, y12);
        int p2Green = inputImage.getGreen(x12, y12);
        int p2Blue  = inputImage.getBlue(x12, y12);

        int p3Red   = inputImage.getRed(x21, y21);
        int p3Green = inputImage.getGreen(x21, y21);
        int p3Blue  = inputImage.getBlue(x21, y21);

        int p4Red   = inputImage.getRed(x22, y22);
        int p4Green = inputImage.getGreen(x22, y22);
        int p4Blue  = inputImage.getBlue(x22, y22);

        Color outColor = new Color( 
                bICompute(p1Red,   p2Red,   p3Red,   p4Red,   dx, dy),
                bICompute(p1Green, p2Green, p3Green, p4Green, dx, dy),
                bICompute(p1Blue,  p2Blue,  p3Blue,  p4Blue,  dx, dy));

        return outColor;
    }
}
