
package cz.vsb.cs.sur096.despr.utils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;

/**
 * Nástroj pro kreslení ikon použitých v aplikaci.
 *
 * @author Martin Šurkovský, sur096 
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 * @version 2011/09/03/10:58
 */
public class DrawIcon {
    
	/** Zabrání vytvoření instance */
    private DrawIcon() { }
    
    public static final Color WRAPPER_COLOR;
    public static final Color COPIER_COLOR;
    public static final Color RENDERER_COLOR;
    public static final Color EDITOR_COLOR;
    static {
        WRAPPER_COLOR  = new Color(165, 255, 165);
        COPIER_COLOR   = new Color(70,  255, 250);
        RENDERER_COLOR = new Color(255, 165, 165);
        EDITOR_COLOR   = new Color(255, 255, 75 );
    }
    
	/**
     * Načte ikonu z disku.
     * @param urlIcon url ikony.
     * @param emptyIconWidth šířka ikony.
     * @param emptyIconHeight výška ikony.
     * @return poskytne načtenou ikonu, pokud ji nenajde vytvoří prázdnou
	 * ikonu o zadaných rozměrech.
     */
    public static Icon loadIcon(URL urlIcon, int emptyIconWidth, int emptyIconHeight) {
        
        BufferedImage imgIcon = null;
        if (urlIcon != null) {
            try {
                imgIcon = ImageIO.read(urlIcon);
            } catch (IOException ex) {
                imgIcon = null;
            }
        }
        
        if (imgIcon != null) {
            return new ImageIcon(imgIcon);
            
        } else {
            return drawEmptyIcon(emptyIconWidth, emptyIconHeight);
        }
    }
    
    /**
     * Načte ikonu z disku.
     * @param f soubor reprezentující ikonu na disku.
     * @param emptyIconWidth šířka ikony.
     * @param emptyIconHeight výška ikony.
     * @return poskytne ikonu načtenou z disku, pokud ji nenajde vytvoří prázdnou
	 * ikonu o zadaných rozměrech.
     */
    public static Icon loadIcon(File f, int emptyIconWidth, int emptyIconHeight) {
        BufferedImage imgIcon = null;
        if (f.exists()) {
            try {
                imgIcon = ImageIO.read(f);
            } catch (IOException ex) {
                imgIcon = null;
            }
        }
        
        if (imgIcon != null) {
            return new ImageIcon(imgIcon);
            
        } else {
            return drawEmptyIcon(emptyIconWidth, emptyIconHeight);
        }
    }
    
    /**
     * Vytvoří reprezentaci prázdné ikony. Bílá ikona s červeným křížem uprostřed.
     * @param width šířka ikony.
     * @param height výška ikony.
     * @return ikonu reprezentující prázdnou ikonu.
     */
    public static Icon drawEmptyIcon(int width, int height) {
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = (Graphics2D) img.getGraphics();
        g2.setRenderingHint(
                  RenderingHints.KEY_ANTIALIASING,
                  RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, width - 1, height - 1);
        g2.setColor(Color.BLACK);
        g2.drawRect(0, 0, width - 1, height - 1);
        // cerveny kriz se vykresli pouze i ikon vetsich jak 10x10px.
        if (width > 10 && height > 10) {
            g2.setStroke(new BasicStroke(2));
            g2.setColor(Color.red);
            g2.drawLine(5, 5, width-5, height-5);
            g2.drawLine(width-5, 5, 5, height-5);
        }
        return new ImageIcon(img);
    }
    
    /**
     * Nakreslí obdélníkovou ikonu o zadaných rozměrech.
     * @param width šířka ikony.
     * @param height výška ikony.
     * @param bg barva pozadí.
     * @param border barva orámování (1px).
     * @return ikonu o zadaných parametrech.
     */
    public static Image drawRectangleIcon(int width, int height, Color bg, Color border) {
        
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics g = (Graphics) img.getGraphics();
        g.setColor(bg);
        g.fillRect(0, 0, width, height);
        g.setColor(border);
        g.drawRect(0, 0, width-1, height-1);
        return img;
    }
}
