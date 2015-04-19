
package cz.vsb.cs.sur096.despr.operations.managecolors;

import cz.vsb.cs.sur096.despr.model.operation.parameter.AInputParameter;
import cz.vsb.cs.sur096.despr.model.operation.parameter.AOutputParameter;
import cz.vsb.cs.sur096.despr.model.operation.parameter.EInputParameterType;
import cz.vsb.cs.sur096.despr.operations.AbstractImageOperation;
import cz.vsb.cs.sur096.despr.types.images.GrayscaleImage;
import cz.vsb.cs.sur096.despr.view.operation.Displayable;
import java.awt.Image;

/**
 * Operace provede vyrovnání nebolí ekvalizaci histogramu.
 *
 * @author Martin Šurkovský, sur096 
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 * @version 2011/09/02/20:16
 */
public class HistogramEqualize extends AbstractImageOperation implements Displayable {
    
    @AInputParameter(EInputParameterType.OUTER)
    private GrayscaleImage inputImage;
    
    @AOutputParameter
    private GrayscaleImage outputImage;
    
    /**
     * Provede vyrovnání histogramu.
     * @throws Exception pokud je vstupní obrázek prázdny.
     */
    @Override
    public void execute() throws Exception {
        
        checkInputImage(inputImage);
        
        int w = inputImage.getWidth();
        int h = inputImage.getHeight();
        
        int size = 256;
        int[][] histo = new int[size][3];
        int cdf = 0;
        
        for (int i = 0; i < size; i++) {
            histo[i][0] = 0; //Počet bodů v jednotlivých odstínech šedi 0-255 (histogram)
            histo[i][1] = 0; //cdf funkce
            histo[i][2] = 0; //Nové odstíny šedi 0-255 (vyrovnání)
        }
        
        // pocet pixelu v obrazku
        int n = w * h;
        
        //Načteme hodnotu každého pixelu a zvýšíme počítadla v poli
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                int color_tran = inputImage.getBrightness(i, j);
                histo[color_tran][0] += 1;
            }
        }
        
        //Výpočet CDF + dosazení nenulovým prvkům
        for (int i = 0; i < size; i++) {
            cdf += histo[i][0];
            if (histo[i][0] != 0) {
                histo[i][1] = cdf;
            }
        }
        
        //Nalezení minimální CDF
        int min = 0;
        for (int i = 0; i < size; i++) {
            if (histo[i][1] != 0) {
                min = histo[i][1];
                break;
            }
        }
        
        //Výpočet nových hodnot pixelů
        for (int i = 0; i < size; i++) {
            double t = ((double)(histo[i][1] - min) / (n - min)) * 255;
            histo[i][2] = (int) Math.round(t);
        }

        outputImage = new GrayscaleImage(w, h);
        // nastavim nove hodnoty pixelu
       for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                int color_tran = inputImage.getBrightness(x, y);

                //Nastavíme novou barvu pixelu
                int c = histo[color_tran][2];
                outputImage.setBrightness(x, y, c);
            }
        }
    }
    
	/**
	 * Pro náhled je použit výstupní obrázek.
	 * @return výstupní obrázek.
	 */
    @Override
    public Image getThumbnail() {
        return outputImage;
    }
    
    //////////////////////////////////////////////////////
    // Get a set metody
    
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
    public GrayscaleImage getOutputImage() {
        return outputImage;
    }
    
    /**
     * Nastaví výstupní obrázek.
     * @param outputImage výstupní obrázek.
     * @throws UnsupportedOperationException vždy.
     * @deprecated metoda je definována pouze formálně.
     */
    @Deprecated
    public void setOutputImage(GrayscaleImage outputImage) 
            throws UnsupportedOperationException {
        
        throw new UnsupportedOperationException(
                getLocalizeMessage("exception.output_parameter_set_method"));
    }
    
    ////////////////////////////////////////////////////////////
    // Soukrome pomocne metody
    
    private void checkInputImage(GrayscaleImage inputImage) 
            throws NullPointerException {
        
        if (inputImage == null) {
            throw new NullPointerException(
                    getLocalizeMessage("exception.null_input_image"));
        }
    }
    
}
