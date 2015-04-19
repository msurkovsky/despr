package cz.vsb.cs.sur096.despr.view;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import javax.swing.JComponent;

/**
 * Stará se o změnu pozice komponenty typu {@code Movable}.
 * @author Martin Šurkovský, sur096 
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 * @version 2011/08/25/08:45
 */
public class MovableHandler extends  MouseAdapter implements MouseMotionListener{

    /** Počáteční bod pohybu. */
    private Point startPoint;
    
    private boolean pressed, entered;
    
    public MovableHandler() {
        pressed = false;
    }
    
    /**
     * Při stisknutí myši si uloží počáteční bod, vůči kterému
	 * se pak počítá posun.
     * @param e událost myši.
     */
    @Override
    public void mousePressed(MouseEvent e) {
        startPoint = e.getPoint();
        pressed = true;
    }
    
    @Override
    public void mouseReleased(MouseEvent e) {
        pressed = false;
        Object o = e.getSource();
        if (o instanceof JComponent) {
            Cursor c;
            if (entered) {
                c = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
            } else {
                c = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);
            }
            ((JComponent) o).setCursor(c);
        }
    }
    
    @Override
    public void mouseEntered(MouseEvent e) {
        Object o = e.getSource();
        if (!pressed && o instanceof JComponent) {
            ((JComponent) o).setCursor(Cursor.getPredefinedCursor(
                    Cursor.HAND_CURSOR));
        }
        entered = true;
    }
    
    @Override
    public void mouseExited(MouseEvent e) {
        Object o = e.getSource();
        if (!pressed && o instanceof JComponent) {
            ((JComponent) o).setCursor(Cursor.getPredefinedCursor(
                    Cursor.DEFAULT_CURSOR));
        }
        entered = false;
    }
    
    /**
     * Při tažení z komponentou se přepočítává její
	 * pozice.
     * @param e událost myši. 
     */
    @Override
    public void mouseDragged(MouseEvent e) {
        
        Object o = e.getSource();
        if (o instanceof JComponent) {
            ((JComponent) o).setCursor(Cursor.getPredefinedCursor(
                    Cursor.MOVE_CURSOR));
        }
        if (o instanceof Movable) {
            Movable movable = (Movable) o;
            Point loc = movable.getLocation();
            Point move = e.getPoint();
            if (o instanceof JComponent) {
                JComponent comp = (JComponent) o;
                if (comp.getParent() instanceof GraphCanvas) {
                    GraphCanvas gc = (GraphCanvas) comp.getParent();
                    Rectangle visibleRec = gc.getVisibleRect();
                    Point p = new Point(loc.x + move.x, loc.y + move.y);
                    // pokud se nachazim mimo viditelnou oblast tak budu
                    // prekreslovat okno, at se zbytecne nevola prekreslovaci
                    // metoda
                    if (visibleRec.width < p.x || visibleRec.height < loc.y ||
                            p.x < visibleRec.x || p.y < visibleRec.y) {
                        gc.scrollRectToVisible(new Rectangle(p.x, p.y, 1, 1));
                    }
                }
            }
            Point moveTo = new Point(loc.x + move.x - startPoint.x, loc.y + move.y - startPoint.y);
            moveTo = new Point(moveTo.x < 0 ? 0 : moveTo.x, moveTo.y < 0 ? 0 : moveTo.y);
            movable.setLocation(moveTo);
            
            // po posunuti zjistim zda komponenta neni mimo rozsah,
            // pokud ano zvetsi se platno
            if (o instanceof JComponent) {
                JComponent comp = (JComponent) o;
                Rectangle rec = comp.getBounds();
                int x = rec.x + rec.width;
                int y = rec.y + rec.height;

                Container parent = comp.getParent();
                if (parent != null) {
                    Dimension d = parent.getPreferredSize();
                    if (d.width < x || d.height < y) {
                        parent.setPreferredSize(
                                new Dimension(d.width < x ? (x + 50) : d.width, 
                                          d.height < y ? (y + 50) : d.height));
                    }
                }
            }
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
    }
}
