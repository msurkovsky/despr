
package cz.vsb.cs.sur096.despr.types.wrappers;

import cz.vsb.cs.sur096.despr.types.Wrapper;
import java.awt.Color;

/**
 * Wrapper pro typ: {@code java.awt.Color}. Umožňuje tak tento 
 * typ ukládat a znovu načítat v rámci aplikace. Z barvy vytáhne
 * hodnoty jasu jednotlivých barevných složek, plus hodnotu 
 * alpha kanálu.
 *
 * @author Martin Šurkovský, sur096, 
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 * @version 2012/02/24/18:54
 */
public class ColorWrapper implements Wrapper<Color> {

    private int red, green, blue, alpha;
    
    public ColorWrapper() { }

    /**
     * Poskytne hodnotu jasu červené složky barvy.
     * @return hodnota jasu červené barvy.
     */
    public int getRed() {
        return red;
    }
    
    /**
     * Nastaví hodnotu červené barvy.
     * @param red hodnota červené
     */
    public void setRed(int red) {
        this.red = red;
    }

    /**
     * Poskytne hodnotu jasu zelené barvy.
     * @return hodnota jasu zelené barvy.
     */
    public int getGreen() {
        return green;
    }

    /**
     * Nastaví hodnotu jasu zelené barvy.
     * @param green hodnota jasu zelené barvy.
     */
    public void setGreen(int green) {
        this.green = green;
    }

    /**
     * Poskytne hodnotu jasu modré barvy.
     * @return hodnota jasu modré barvy.
     */
    public int getBlue() {
        return blue;
    }

    /**
     * Nastaví hodnotu jasu modré barvy.
     * @param blue hodnota jasu modré barvy.
     */
    public void setBlue(int blue) {
        this.blue = blue;
    }

    /**
     * Poskytne hodnotu alpha kanálu.
     * @return hodnota alpha kanálu.
     */
    public int getAlpha() {
        return alpha;
    }

    /**
     * Nastaví hodnotu alpha kanálu.
     * @param alpha hodnota alpha kanálu.
     */
    public void setAlpha(int alpha) {
        this.alpha = alpha;
    }
    
    /**
     * Vezme barvu vytáhne z ní důležité informace a uchová je 
	 * zde.
     * @param t barva.
     */
    @Override
    public void wrap(Color t) {
        red   = t.getRed();
        green = t.getGreen();
        blue  = t.getBlue();
        alpha = t.getAlpha();
    }

    /**
     * Na základě uložených hodnot jednotlivých jasů, plus
	 * hodnoty alpha kanálu zreprodukuje barvu.
     * @return barva.
     */
    @Override
    public Color unwrap() {
        return new Color(red, green, blue, alpha);
    }

    /**
     * Poskytne datový typ barvy.
     * @return odkaz na třídu {@code java.awt.Color}
     */
    @Override
    public Class<Color> getWrapType() {
        return Color.class;
    }
}
