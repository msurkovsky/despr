package cz.vsb.cs.sur096.despr.types.images;

import java.awt.image.BufferedImage;

/**
 * Třída definuje nový typ binary image, tj. obrázek jeho hodnoty mohou být
 * pouze černá a bílá. Slouží hlavně pro typovou kontrolu jednotlivých obrázků.
 *
 * @author Martin Šurkovský, sur096,
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 * @version 2010/10/10/20:40
 */
public class BinaryImage
        extends GrayscaleImage {

    /**
     * Bez parametrický konstruktor, vytvoří obrázek o velikosti 1x1px.
     */
    public BinaryImage() { 
        super(1,1);
    }

    /**
     * Vytvoří černobílí obrázek z objektu typu {@code BufferedImage}
     * @param img 
     */
    public BinaryImage(BufferedImage img) {
        super(img);
    }
    
    /**
     * Konstruktor vytvoří nový černobílí obrázek o daných rozměrech.
     * @param width šířka obrázku
     * @param height výška obrázku
     */
    public BinaryImage(int width, int height) {
        super(width, height);
    }

    /**
     * Zjistí zda hodnota barvy na daném bodě je bílá
     * @param x  souřadnice bodu
     * @param y  souřadnice bodu
     * @return {@code true} pokud je bílá, tzn. hodnota jasu = 255,
     * jinak {@code false}.
     */
    public boolean isWhite(int x, int y) {
        int b = super.getBrightness(x, y);
        if (b == 255) {
            return true;
        } else {
            return false;
        }
    }
    
    /**
     * Metoda pro nastaveni bíle barvy na pozici [x,y].
     * Měla by být používána míst metody {@code void setRGB(int, int)}.
     * @param x souřadnice bodu
     * @param y souřadnice bodu
     */
    public void setWhite(int x, int y) {
        super.setBrightness(x, y, 255);
    }
    
    /** Zjistí zda hodnota barvy na daném bodě je bílá
     * @param x souřadnice bodu
     * @param y souřadnice  bodu
     * @return {@code true} pokud je bílá, tzn. hodnota jasu = 255,
     * jinak {@code false}.
     */
    public boolean isBlack(int x, int y) {
        int b = super.getBrightness(x, y);
        if (b == 0) {
            return true;
        } else {
            return false;
        }
    }
    
    /**
     * Metoda pro nastaveni černé barvy na pozici [x,y].
     * Měla by být používána míst metody {@code void setRGB(int, int)}.
     * @param x souřadnice bodu
     * @param y souřadnice bodu
     */
    public void setBlack(int x, int y) {
        super.setBrightness(x, y, 0);
    }

    /**
     * Vytvoří kopii obrázku daného obrázku.
     * @return novou instanci na daný obrázek.
     */
    @Override
    public BinaryImage copy() {
        int width = getWidth();
        int height = getHeight();
        
        BinaryImage copyImg = new BinaryImage(width, height);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int color = getBrightness(x, y);
                if (color == 255) {
                    copyImg.setWhite(x, y);
                } else {
                    copyImg.setBlack(x, y);
                }
            }
        }
        return copyImg;
    }
}
