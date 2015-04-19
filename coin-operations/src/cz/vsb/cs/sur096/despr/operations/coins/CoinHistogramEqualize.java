package cz.vsb.cs.sur096.despr.operations.coins;

import cz.vsb.cs.sur096.despr.model.operation.parameter.AInputParameter;
import cz.vsb.cs.sur096.despr.model.operation.parameter.AOutputParameter;
import cz.vsb.cs.sur096.despr.model.operation.parameter.EInputParameterType;
import cz.vsb.cs.sur096.despr.types.images.GrayscaleImage;
import cz.vsb.cs.sur096.despr.view.operation.Displayable;
import java.awt.Image;

/**
 * Metoda aplikující vyrování histogramu poze na vepsaný kruh ve čtvercovém
 * obrazu, tedy pouze na oblast, která představuje minci.
 * 
 * @author Martin Šurkovský, sur096,
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gamil.com</a>
 * @version 2012/04/18/10:41
 */
public class CoinHistogramEqualize extends AbstractCoinOperation implements Displayable {

    @AInputParameter(EInputParameterType.OUTER)
    private GrayscaleImage input;
    
    @AOutputParameter
    private GrayscaleImage output;
    
    /**
     * Provede vyrovnání histogramu na oblasti mince.
     * @throws Exception pokud se nejedná o čtvercový obraz, tj.
     * pokud není šířka a výška obrazu stejná.
     */
    @Override
    public void execute() throws Exception {
        int width = input.getWidth();
        int height = input.getHeight();
        
        if (width != height) {
            throw new IllegalArgumentException(getLocalizeMessage("exception.different_sizes"));
        }

        final int HIST = 0;
        final int CDF = 1;
        final int EQUALIZE_HIST = 2;
        
        int size = 256;
        int[][] histo = new int[size][3];
        int cdf = 0;
        
        int n = 0; // pocet pixelu, pocitace podle toho ktere pixely byly zarazeny
        // at je to presne, mohlo by se i podle vzorce pro vypcet obsahu
        // kruhu, jenze to by nebylo presne
        
        for (int i = 0; i < size; i++) {
            histo[i][HIST] = 0; //Počet bodů v jednotlivých odstínech šedi 0-255 (histogram)
            histo[i][CDF] = 0; //cdf funkce
            histo[i][EQUALIZE_HIST] = 0; //Nové odstíny šedi 0-255 (vyrovnání)
        }
        
        int r = width * width / 4; // r^2 (= r * r)
        int center = width / 2; // x and y coordiata is same;
        

        // vytvoreni histogramu
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int u = (center - x);
                int v = (center - y);
                int cpSize = u * u + v * v; // center-curent point size
                if (cpSize <= r) { // ok this condition is correct
                    int b = input.getBlue(x, y);
                    histo[b][HIST] += 1;
                    n++;
                }
            }
        }
        
        //Výpočet CDF + dosazení nenulovým prvkům
        for (int i = 0; i < size; i++) {
            cdf += histo[i][HIST];
            if (histo[i][HIST] != 0) {
                histo[i][CDF] = cdf;
            }
        }
        
        // Nalezení minimální CDF, postupne rostouci funkce, prvni nenulova
        // je tak minimalni
        int min = 0;
        for (int i = 0; i < size; i++) {
            if (histo[i][CDF] != 0) {
                min = histo[i][CDF];
                break;
            }
        }
        
        //Výpočet nových hodnot pixelů
        for (int i = 0; i < size; i++) {
            double t = ((double)(histo[i][1] - min) / (n - min)) * 255;
            histo[i][2] = (int) Math.round(t);
        }
        
        output = new GrayscaleImage(width, height);

        // nastavim nove hodnoty pixelu
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int b = input.getBrightness(x, y);

                int u = (center - x);
                int v = (center - y);
                int cpSize = u * u + v * v;
                if (cpSize <= r) {
                    //Nastavíme novou barvu pixelu
                    int c = histo[b][2];
                    output.setBrightness(x, y, c);
                } else {
                    output.setBrightness(x, y, 255);
                }
            }
        }
    }

    /**
	 * Pro náhled je použit výstupní obrázek.
	 * @return výstupní obrázek.
	 */
    @Override
    public Image getThumbnail() {
        return output;
    }
    
    /**
     * Poskytne odkaz na vstupní obrázek.
     * @return obrázek daný na vstupu.
     */
    public GrayscaleImage getInput() {
        return input;
    }

    /**
     * Nastaví vstupní obrázek.
     * @param input vstupní obrázek.
     */
    public void setInput(GrayscaleImage input) {
        this.input = input;
    }

    /**
     * Poskytne výstpní obrázek.
     * @return výstupní obrázek.
     */
    public GrayscaleImage getOutput() {
        return output;
    }

    /**
     * Nastaví výstupní obraz, avšak metoda by neměla být nikdy
     * použita, jedná se pouze o formální definici kvůli dodržení java bean
     * konvence.
     * @param output výstupní obraz.
     * @throws UnsupportedOperationException  vždy.
     */
    public void setOutput(GrayscaleImage output) 
            throws UnsupportedOperationException {
        
        throw new UnsupportedOperationException(
                getLocalizeMessage("exception.output_parameter_set_method"));
    }
}
