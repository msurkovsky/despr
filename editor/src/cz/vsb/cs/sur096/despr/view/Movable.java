
package cz.vsb.cs.sur096.despr.view;

import java.awt.Point;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

/**
 * Komponenty implementující toto rozhraní se mohou pohybovat po plátně, tažením
 * myši.
 * 
 * @author Martin Surkovsky, sur096 <martin.surkovsky at gmail.com>
 * @version 2011/08/24/17:32
 */
public interface Movable {
    
    /**
     * Vrátí pozici objektu.
     * @return pozice objektu.
     */
    public Point getLocation();
    
    /**
     * Nastaví pozici objektu.
     * @param location nová pozice objektu.
     */
    public void setLocation(Point location);
    
    /**
     * Objekt musí umět reagovat na kliknutí myši.
     * @param l posluchač kliknutí myši.
     */
    public void addMouseListener(MouseListener l);
    
    /**
     * Objekt musí umět reagovat na pohyb myši.
     * @param l posluchač pohybu muši.
     */
    public void addMouseMotionListener(MouseMotionListener l);
    
    /**
     * Smaže posluchače na kliknutí myši.
     * @param l posluchač kliknutí myši.
     */
    public void removeMouseListener(MouseListener l);
    
    /**
     * Smaže posluchače na pohyb miši.
     * @param l posluchač pohybu muši.
     */
    public void removeMouseMotionListener(MouseMotionListener l);

    /**
     * Poskytne seznam posluchačů myši.
     * @return seznam posluchačů myši.
     */
    public MouseListener[] getMouseListeners();
    
    /**
     * Poskytne seznam posluchačů pohybu myši.
     * @return seznam posluchačů pohybu myši.
     */
    public MouseMotionListener[] getMouseMotionListeners();
}
