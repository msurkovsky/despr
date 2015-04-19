
package cz.vsb.cs.sur096.despr.operations.managecolors;

import cz.vsb.cs.sur096.despr.model.operation.parameter.AInputParameter;
import cz.vsb.cs.sur096.despr.model.operation.parameter.AOutputParameter;
import cz.vsb.cs.sur096.despr.model.operation.parameter.EInputParameterType;
import cz.vsb.cs.sur096.despr.operations.AbstractImageOperation;
import cz.vsb.cs.sur096.despr.types.images.ColorImage;
import cz.vsb.cs.sur096.despr.view.operation.Displayable;
import java.awt.Color;
import java.awt.Image;

/**
 * Operace, která prohodí invertuje barvy v obraze. Aktuální hodnotu jasu
 * dané barvy odečte od 255 a použije jako novou hodnotu.
 * 
 * @author Martin Šurkovská, sur096,
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 * @version 2012/04/18/11:43
 */
public class InvertColors extends AbstractImageOperation implements Displayable {

    @AInputParameter(EInputParameterType.OUTER)
    private ColorImage input;
    
    @AOutputParameter
    private ColorImage output;
    
    /**
     * Metoda provede inverzi barev v obraze.
     * @throws Exception pokud je odkaz na vstupní obrázek prázdný
     */
    @Override
    public void execute() throws Exception {
        
        checkInputImage(input);
        
        int width = input.getWidth();
        int height = input.getHeight();
        
        output = new ColorImage(width, height);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int r = 255 - input.getRed(x, y);
                int g = 255 - input.getGreen(x, y);
                int b = 255 - input.getBlue(x, y);
                output.setRGB(x, y, new Color(r, g, b).getRGB());
            }
        }
    }

    /**
     * Poskytne náhled na výstupní obrázek
     * @return výstupní obrázek.
     */
    @Override
    public Image getThumbnail() {
        return output;
    }

    /**
     * Poskytne vstupní obrázek.
     * @return vstupní obrázek.
     */
    public ColorImage getInput() {
        return input;
    }

    /**
     * Nastaví vstupní obrázek.
     * @param input vstupní obrázek.
     * @throws NullPointerException pokud je odkaz na vstupní obrázek prázdný.
     */
    public void setInput(ColorImage input) throws NullPointerException {
        checkInputImage(input);
        this.input = input;
    }

    /**
     * Poskytne výstupní obrázek.
     * @return výstupní obrázek.
     */
    public ColorImage getOutput() {
        return output;
    }

    /**
     * Nastavý výstupní obráze, jedná se ale pouze o formální definici a metoda
     * vždy vyhodí vyjímku.
     * @param output výstupní obrázek
     * @throws UnsupportedOperationException  vždy.
     */
    public void setOutput(ColorImage output) 
            throws UnsupportedOperationException {
        
        throw new UnsupportedOperationException(
                getLocalizeMessage("exception.output_parameter_set_method"));
    }
    
    private void checkInputImage(ColorImage input) throws NullPointerException {
        if (input == null) {
            throw new NullPointerException(getLocalizeMessage("exception.null_input_image"));
        }
    }
}
