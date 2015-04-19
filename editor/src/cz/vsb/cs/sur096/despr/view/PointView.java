	package cz.vsb.cs.sur096.despr.view;

import cz.vsb.cs.sur096.despr.structures.IdPoint;
import java.awt.*;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import javax.swing.JComponent;

/**
 * Komponenta znázorňující bod na hraně. Pomocí ní lze měnit
 * pozici daného bodu.
 * 
 * @author Martin Surkovsky, sur096
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky@gmail.com</a>
 * @version 2011/03/21/10:34
 */
public class PointView 
        extends JComponent
        implements Movable {

    /** Seznam posluchačů zajímajících se o změnu polohy bodu. */  
    private PropertyChangeSupport pcs;
    /** ID bodu. */
    private int id;
    /** Bod který komponenta zobrazuje. */
	private Point p;

    /**
     * Iniciuje pohled bodu na základě {@code IdPoint}.
     * @param idPoint bod který má být zobrazen.
     */
    public PointView(IdPoint idPoint) {
        this.p = idPoint.getLocation();
        init(idPoint.getId());
    }
    
    /**
     * Iniciuje pohled bodu na základě bodu a ID bodu.
     * @param p bod.
     * @param id ID bodu.
     */
    public PointView(Point p, int id) {
        this.p = p;
        init(id);
    }

    /**
     * Iniciuje pohled bodu na základě souřadnic a ID.
     * @param x <i>x</i> souřadnice bodu.
     * @param y <i>y</i> souřadnice bodu.
     * @param id ID bodu.
     */
    public PointView(int x, int y, int id) {
        this.p = new Point(x, y);
        init(id);
    }
    
    /**
     * Inicializuje pohled bodu, tak že požadovaný bod je uprostřed komponenty.
     * @param id ID bodu.
     */
    private void init(int id) {
        pcs = new PropertyChangeSupport(this);
        this.id = id;
        if (p != null) {
            Dimension prefSize = getPreferredSize();
            setLocation(p.x - prefSize.width / 2, p.y - prefSize.height / 2);
            setSize(prefSize);
            addMouseListener(new PointViewMouseHandler());
        }
    }
     
    /**
     * Poskytne bod který je prezentován.
     * @return model bodu.
     */
    public Point getPoint() {
        return p;
    }
    
    /**
     * Poskytne ID bodu.
     * @return ID bodu.
     */
    public int getId() {
        return id;
    }
    
    /**
     * Smaže bod, resp. pošle zprávu registrovanému posluchači (hraně),
	 * že má být bod smazán.
     */
    public void removePoint() {
        pcs.firePropertyChange("point_removed", null, this);
    }
    
    /**
     * Poskytne velikost bodu.
     * @return dvojici čísel v rozsahu 7 až 13 včetně. Jiné nastavení velikost
	 * je potlačeno (větší velikost bodů není nutná). Výchozí hodnota je 9x9px.
     */
    @Override
    public Dimension getPreferredSize() {
        Dimension preferred = super.getPreferredSize();

        if ( preferred != null &&
                preferred.width >= 7 && preferred.width <= 13 &&
                preferred.height >= 7 && preferred.height <= 13 ) {
            return preferred;
        } else { // jinak vratim defaultni velikost portu
            return new Dimension(9, 9);
        }
    }

    /**
     * Vykreslí komponentu, zelený čtvereček s černým orámováním.
     * @param g grafický kontext.
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);

        Dimension preferredSize = getPreferredSize();

        g2.fillRect(0, 0, preferredSize.width, preferredSize.height);
        g2.setStroke(new BasicStroke(2f));
        g2.setColor(Color.GREEN);
        g2.drawRect(1, 1, preferredSize.width - 3, preferredSize.height - 3);
    }

    /**
     * Nastaví pozici bodu.
     * @param p nová požadovaná pozice bodu.
     */
    @Override
    public void setLocation(Point p) {
        Point oldLocation = getLocation();
        super.setLocation(p);
        
        if (!oldLocation.equals(p)) {
            Dimension size = getSize();
            Point center = new Point(size.width/2, size.height/2);
            IdPoint newLocation = new IdPoint(p.x + center.x, p.y + center.y, id);
            pcs.firePropertyChange("point_changed_position", null, newLocation);
        }
    }

    /**
     * Přidá posluchače na změnu vlastností bodu.
     * @param listener posluchač.
     */
    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }
    
    /**
     * Smaže posluchače.
     * @param listener  posluchač.
     */
    @Override
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(listener);
    }
}