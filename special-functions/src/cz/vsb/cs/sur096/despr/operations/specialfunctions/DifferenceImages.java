
package cz.vsb.cs.sur096.despr.operations.specialfunctions;

import cz.vsb.cs.sur096.despr.model.operation.parameter.AInputParameter;
import cz.vsb.cs.sur096.despr.model.operation.parameter.AOutputParameter;
import cz.vsb.cs.sur096.despr.model.operation.parameter.EInputParameterType;
import cz.vsb.cs.sur096.despr.types.images.ColorImage;
import cz.vsb.cs.sur096.despr.view.operation.Displayable;
import java.awt.Color;
import java.awt.Image;

/**
 * Operace slouží pro porovnávání dvou obrázků, tak že je od sebe
 * odečte hodnoty pixelů na stejných pozicích.
 *
 * @author Martin Šurkovský, sur096 
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 * @version 2011/12/29/21:01
 */
public class DifferenceImages extends AbstractSpecialOperation implements Displayable {

    
    @AInputParameter(EInputParameterType.OUTER)
    private ColorImage input1;
    
    @AInputParameter(EInputParameterType.OUTER)
    private ColorImage input2;

    @AOutputParameter
    private transient ColorImage output;
    
    /**
     * Provede rozdíl dvou obrázku a výsledek uloží do výstupního obrázku.
     * @throws Exception pokud jsou vstupní parametry nekorektní, nebo
	 * nesouhlasí rozměry obrázků
     */
    @Override
    public void execute() throws Exception {
        checkInput1(input1);
        checkInput2(input2);
        
        int in1Width = input1.getWidth();
        int in1Height = input1.getHeight();

        int in2Width = input2.getWidth();
        int in2Height = input2.getHeight();


        if (in1Width == in2Width && in1Height == in2Height) {
            output = new ColorImage(in1Width, in1Height);
            for (int x = 0; x < in1Width; x++) {
                for (int y = 0; y < in1Height; y++) {
                    int r = Math.abs(input1.getRed(x, y)   - input2.getRed(x, y));
                    int g = Math.abs(input1.getGreen(x, y) - input2.getGreen(x, y));
                    int b = Math.abs(input1.getBlue(x, y)  - input2.getBlue(x, y));

                    output.setRGB(x, y, new Color(r,g,b).getRGB());
                }
            }
        } else {
            throw new RuntimeException(
                    getLocalizeMessage("exception.difference_sizes_images"));
        }
    }
    
    @Override
    public Image getThumbnail() {
        return output;
    }

    ////////////////////////////////////////////////////////////
    // Get a set metody
    
    /**
     * Poskytne první vstupní obrázek.
     * @return první vstupní obrázek.
     */
    public ColorImage getInput1() {
        return input1;
    }

    /**
     * Nastaví první vstupní obrázek.
     * @param input1 první vstupní obrázek.
     * @throws NullPointerException pokud je hodnota prázdná.
     */
    public void setInput1(ColorImage input1) 
            throws NullPointerException {
        
        checkInput1(input1);
        this.input1 = input1;
    }

    /**
     * Poskytne druhý vstupní obrázek.
     * @return druhý vstupní obrázek.
     */
    public ColorImage getInput2() {
        return input2;
    }

    /**
     * Nastaví druhý vstupní obrázek.
     * @param input2 druhý vstupní obrázek.
     * @throws NullPointerException pokud je hodnota prázdná.
     */
    public void setInput2(ColorImage input2) 
            throws NullPointerException {
        
        checkInput2(input2);
        this.input2 = input2;
    }

    /**
     * Poskytne rozdílový obrázek.
     * @return rozdílový obrázek.
     */
    public ColorImage getOutput() {
        return output;
    }

    /**
     * Nastaví výstupní obrázek.
     * @param output rozdílový obrázek.
     * @throws UnsupportedOperationException vždy.
     * @deprecated metoda je definována pouze formálně.
     */
    @Deprecated
    public void setOutput(ColorImage output) 
            throws UnsupportedOperationException {
        
        throw new UnsupportedOperationException(
                getLocalizeMessage("exception.output_parameter_set_method"));
    }
    
    ////////////////////////////////////////////////////////
    // Soukrome pomocne metody
    
    private void checkInput1(ColorImage input1) 
            throws NullPointerException {
        
        if (input1 == null) {
            throw new NullPointerException(
                    getLocalizeMessage("exception.null_input1"));
        }
    }
    
    private void checkInput2(ColorImage input2) 
            throws NullPointerException {
        
        if (input2 == null) {
            throw new NullPointerException(
                    getLocalizeMessage("exception.null_input2"));
        }
    }
}
