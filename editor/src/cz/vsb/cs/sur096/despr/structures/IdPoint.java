
package cz.vsb.cs.sur096.despr.structures;

import cz.vsb.cs.sur096.despr.utils.ID;
import java.awt.Point;

/**
 * Rozšiřuje {@code java.awt.Point} o možnost uchování ID.
 *
 * @author Martin Šurkovský, sur096 
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 * @version 2012/01/15/14:20
 */
public class IdPoint extends Point {

	/** ID bodu */
    private int id;
    
	/**
     * Iniciuje bod.
     * @param p bod.
     * @param id ID bodu.
     */
    public IdPoint(Point p, int id) {
        super(p);
        this.id = id;
    }
    
    /**
     * Iniciuje bod s nově vygenerovaným ID.
     * @param p bod.
     */
    public IdPoint(Point p) {
        this(p, ID.getNextID(ID.POINT_GENERATOR));
    }
    
    /**
     * Iniciuje bod.
     * @param x souřadnice <i>x</i>.
     * @param y souřadnice <i>y</i>.
     * @param id ID bodu.
     */
    public IdPoint(int x, int y, int id) {
        super(x, y);
        this.id = id;
    }
    
    /**
     * Poskytne ID bodu.
     * @return ID bodu.
     */
    public int getId() {
        return id;
    }
    
    /**
     * Porovná body podle jejich ID. To znamená že dva různé body se stejným
     * ID se považují za totožné. 
     * @param o objekt k porovnání.
     * @return pokud se jedna o {@code IdPoint} pak vrátí {@code true}, pokud 
     * jsou shodná ID, jinak {@code false}. 
	 */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o instanceof IdPoint == false) return false;
        
        IdPoint idPoint = (IdPoint) o;
        return this.id == idPoint.getId();
    }

    /**
	 * Poskytne hodnotu hash code.
     * @return vygeneruje hash hodnotu na základě id.
	 */
    @Override
    public int hashCode() {
        return 37 * 17 + this.id;
    }
}
