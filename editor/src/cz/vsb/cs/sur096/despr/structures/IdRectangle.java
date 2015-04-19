
package cz.vsb.cs.sur096.despr.structures;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;

/**
 * Rozšiřuje {@code java.awt.Rectangle} o možnost uchovat, číslo
 * identifikující objekt (ID).
 *
 * @author Martin Šurkovský, sur096, 
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 * @version 2012/01/06/08:04
 */
public class IdRectangle extends Rectangle {
    
    /** ID obdélníku. */
    private int id;
    
    /**
     * Iniciuje obdélník.
     * @param rectangle obdélník.
     * @param id ID obdélníku.
     */
    public IdRectangle(Rectangle rectangle, int id) {
        super(rectangle);
        this.id = id;
    }
    
    /**
     * Iniciuje obdélník.
     * @param location pozice levého horního rohu.
     * @param size rozměry.
     * @param id ID obdélníku.
     */
    public IdRectangle(Point location, Dimension size, int id) {
        super(location, size);
        this.id = id;
    }
    
    /**
     * Iniciuje obdélník.
     * @param x <i>x</i> souřadnice levého horního rohu.
     * @param y <i>y</i> souřadnice levého horního rohu.
     * @param width šířka obdélníku.
     * @param height výška obdélníku.
     * @param id ID obdélníku.
     */
    public IdRectangle(int x, int y, int width, int height, int id) {
        super(x, y, width, height);
        this.id = id;
    }
    
    /**
     * Poskytne ID obdélníku.
     * @return ID obdélníku.
     */
    public int getId() {
        return id;
    }
    
    /**
     * Porovná dva obdélníky podle ID.
     * @param o objekt se kterým má byt porovnáván. 
	 * @return {@code true} pokud je objekt typu 
	 * {@code IdRectangle} a jejich ID se shoduji, jinak {@code false}.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o instanceof IdRectangle == false) return false;
        
        IdRectangle idRec = (IdRectangle) o;
        return id == idRec.getId();
    }
    
    
    /**
	 * Poskytne hash kód na základě ID.
     * @return hash kód 
     */
    @Override
    public int hashCode() {
        return 37 * 17 + this.id;
    }
}
