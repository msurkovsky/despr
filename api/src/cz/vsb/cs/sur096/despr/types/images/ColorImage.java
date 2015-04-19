
package cz.vsb.cs.sur096.despr.types.images;

import cz.vsb.cs.sur096.despr.types.Copyable;
import java.awt.image.BufferedImage;

/**
 * Třída definující typ color image, tj. barevný obrázek.
 * Tato třída slouží pouze k typovému rozdělení jednotlivých druhů obrázků.
 *
 * @author Martin Šurkovský, sur096,
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 * @version 2010/10/10/21:00
 */
public class ColorImage
        extends BufferedImage implements Copyable<ColorImage> {

    /**
     * Bez parametrický konstruktor vytvoří obrázek velikosti 1x1 pixel.
     */
    public ColorImage() { 
        super(1,1, BufferedImage.TYPE_INT_ARGB);
    }
    
    /**
     * Konstruktor vytvoří novou instanci z typu {@code BufferedImage}.
     * @param img 
     */
    public ColorImage(BufferedImage img) {
        super(img.getColorModel(), img.getRaster(), img.isAlphaPremultiplied(), null);
    }
    
    /**
    * Konstruktor vytvářející nový barevný obrázek o daných rozměrech.
    * @param width šířka obrázku.
    * @param height výška obrázku.
    */
    public ColorImage(int width, int height) {
        super(width, height,  BufferedImage.TYPE_INT_ARGB);
    }
    
    /**
     * Vrátí hodnotu alpha kanálu na daných souřadnicích.
     * @param x souřadnice bodu
     * @param y souřadnice bodu
     * @return hodnota alpha kanálu (0-255).
     */
    public int getAlpha(int x, int y) {
        return (getRGB(x, y) >> 24) & 0xFF;
    }
    
    /**
     * Vrátí hodnotu červené barvy na daných souřadnicích.
     * @param x souřadnice bodu
     * @param y souřadnice bodu
     * @return hodnota červené barvy (0-255).
     */
    public int getRed(int x, int y) {
        return (getRGB(x, y) >> 16) & 0xFF;
    }
    
    /**
     * Vrátí hodnotu zelené barvy na daných souřadnicích.
     * @param x souřadnice bodu
     * @param y souřadníce bodu
     * @return hodnota zelené barvy (0-255).
     */
    public int getGreen(int x, int y) {
        return (getRGB(x, y) >> 8) & 0xFF;
    }
    
    /**
     * Vrátí hodnotu modré barvy na daných souřadnicích.
     * @param x souřadnice bodu
     * @param y souřadnice bodu
     * @return hodnota modré barvy (0-255).
     */
    public int getBlue(int x, int y) {
        return getRGB(x, y) & 0xFF;
    }
    
    /**
     * Vytvoří kopii daného obrázku.
     * @return kopie obrázku.
     */
    @Override
    public ColorImage copy() {
        int width = getWidth();
        int height = getHeight();
        ColorImage copyImg = new ColorImage(width, height);
        
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                copyImg.setRGB(x, y, getRGB(x, y));
            }
        }
        return copyImg;
    }
}
