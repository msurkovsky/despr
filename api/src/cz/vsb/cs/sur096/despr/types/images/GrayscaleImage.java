package cz.vsb.cs.sur096.despr.types.images;

import java.awt.Color;
import java.awt.image.BufferedImage;

/**
 * Třída definující nový typ grayscale image, tj. obrázek, který je
 * v šedé škále. Hodnoty jednotlivých pixelů se pohybují v rozsahu od 0 do 255.
 * Slouží hlavně pro typovou kontrolu jednotlivých obrázků.
 * 
 * @author Martin Šurkovský, sur096,
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 * @version 2010/10/10/20:56
 */
public class GrayscaleImage 
        extends ColorImage {

    /**
     * Bez parametrický konstruktor, vytvoří obrázek o velikosti 1x1px.
     */
    public GrayscaleImage() { 
        super(1,1);
    }

    /**
     * Vytvoří šedotónový obrázek z objektu typu {@code BufferedImage}.
     * @param img obrázek, ke kterému se bude přistupovat jako by byl
     * v šedé škále.
     */
    public GrayscaleImage(BufferedImage img) {
        super(img);
    }
    
    /**
     * Konstruktor vytvoří nový šedotónový obrázek o dané šířce a výšce.
     * @param width sirka obrázku
     * @param height výška obrázku
     */
    public GrayscaleImage(int width, int height) {
        super(width, height);
    }

    /**
     * Metoda slouží pro získaní hodnoty jasu na pozici [x,y].
     * Měla by být používána místo metody {@code int getRGB(int, int)}. 
     * Bere hodnotu jasu modré barvy z původního obrázku, pokud je obrázek v
     * šedé škále jsou všechny hodnoty stejné, je vrácena pouze hodnota
     * jasu modré barvy.
     * @param x souřadnice bodu
     * @param y souřadnice bodu
     * @return hodnotu jasu na pozici [x,y] v rozmezí od 0 do 255
     */
    public int getBrightness(int x, int y) {
        return super.getRGB(x, y) & 0x000000ff;
    }
    
    /**
     * Metoda slouží pro nastavení jasu na pozici [x,y].
     * Měla by být používána místo metody {@code void setRGB(int, int, int)}.
     * @param x souřadnice bodu
     * @param y souřadnice bodu
     * @param brightness hodnota jasu v rozmezí od 0 do 255
     */
    public void setBrightness(int x, int y, int brightness)
				throws IllegalArgumentException {
        if (!(brightness >= 0 && brightness <= 255)) {
            throw new IllegalArgumentException("Brightness must be in the range: <0, 255>!");
        }

        super.setRGB(x, y,
                new Color(brightness, brightness, brightness).getRGB());
    }

    /**
     * Vytvoří kopii obrázku daného obrázku.
     * @return novou instanci na daný obrázek.
     */
    @Override
    public GrayscaleImage copy() {
        int width = getWidth();
        int height = getHeight();
        GrayscaleImage copyImg = new GrayscaleImage(width, height);
        
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                copyImg.setBrightness(x, y, getBrightness(x, y));
            }
        }
        
        return copyImg;
    }
}
