package cz.vsb.cs.sur096.despr.view;

import cz.vsb.cs.sur096.despr.Despr;
import cz.vsb.cs.sur096.despr.model.IEdge;
import cz.vsb.cs.sur096.despr.structures.IdPoint;
import cz.vsb.cs.sur096.despr.structures.IdRectangle;
import java.awt.*;
import java.awt.geom.GeneralPath;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.JComponent;

/**
 * Grafická komponenta reprezentující orientovanou hranu. Komponenta
 * implementuje dvě rozhraní, první {@code PropertyChangeListener} 
 * pomocí nějž reaguje na změny parametrů ovlivňující její vzhled. 
 * A {@code Selectable} což znamená že lze komponentu v rámci plátna
 * vybrat a komunikovat s ní prostřednictvím {@code SelectedObject} 
 * objektu.
 *
 * @author Martin Šurkovský
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky@gmail.com</a>
 * @version 2011/03/29/17:53
 */
public class Edge 
            extends JComponent 
            implements PropertyChangeListener, Selectable {

    // size of arrow head
    private final int ARROW_WIDTH = 5;
    private final int ARROW_HEIGHT = 12;
    
    private List<IdPoint> points;
    private List<PointView> pointsView;

    private IEdge model;
	/** 
	 * Ohraničení cílového portu. Pomocí nějž se
	 * vyhreslí zakončení hrany šipkou
	 */
    private IdRectangle inputPortBounds;
    
	/** Ohraničení hrany. */
	private Rectangle bounds;
	/** Šířka štětce */
	private float strokeSize;
	/** Příznak zda je hrana vybrána.*/
	private boolean selected;
	/**
	 * Barvou je myšlen hodnota jasu, slouží pro
	 * vizuální potlačení méně důležitých hran.
	 */
    private int color;

	/** 
	 * Při prvním vybrání hrany jsou na plátno vloženy
	 * malé čtverečky znázorňující záchytné body na hraně.
	 * Ty se v plátnu nacházejí po celou dobu existence hrany
	 * jen se mění jejich viditelnost na základě toho zda je čí
	 * není hrana vybrána.
	 */
    private boolean firstSelect;
    
    private PropertyChangeSupport pcs;
    
    /**
     * Iniciuje hranu na základě modelu a bodů ze kterých se má skládat. 
     * @param model model hrany.
     * @param points seznam bodu se kterých se má hrana skládat.
     * @param inputPortBounds ohraničení cílového portu.
     */
    public Edge(IEdge model, List<IdPoint> points, IdRectangle inputPortBounds) {
        this.model = model;
        init(points, inputPortBounds);
    }
    
    private void init(List<IdPoint> points, IdRectangle inputPortBounds) {
        model.addPropertyChangeListener(this);
        
        this.points = new ArrayList<IdPoint>(points);
        int size = points.size();
        this.pointsView = new ArrayList<PointView>(size - 1);
        for (int i = 1; i < size; i++) {
            addPointView(points.get(i));
        }
        
        this.inputPortBounds = inputPortBounds;
        Point lastPoint = this.points.get(size - 1);
        this.points.add(computeLastPoint(lastPoint, inputPortBounds));
        
		strokeSize = 1.5f;
		selected = false;
        color = 0;
        firstSelect = true;

		bounds = computeBounds();
        setBounds(bounds);
    }

    /**
     * Poskytne seznam bodů hrany.
     * @return seznam bodů hrany.
     */
    public List<IdPoint> getPoints() {
		return points;
	}
    
    /**
     * Poskytne seznam komponent starající se 
	 * o zobrazení hran na hraně.
     * @return seznam komponent reprezentujících hranu.
     */
    public List<PointView> getPointsView() {
        return pointsView;
    }
    
    /**
     * Poskytne model hrany.
     * @return model hrany.
     */
    public IEdge getModel() {
        return model;
    }
    
    /**
     * Poskytne ohraničení cílového portu.
     * @return ohraničení cílového portu.
     */
    public IdRectangle getInputPortBounds() {
        return inputPortBounds;
    }
    
    /**
     * Nastaví příznak hrany ovlivňující to
	 * zda je hrana vybrána, či nikoliv.
     * @param selected má být hrana vybrána.
     */
    @Override
	public void setSelected(boolean selected) {
        boolean oldSelected = this.selected;
        if (oldSelected != selected) {
            this.selected = selected;
            for (PointView pv : pointsView) {
                pv.setVisible(selected);
            }
            repaint();
            pcs.firePropertyChange("selected", oldSelected, selected);
        }
	}

    /**
     * Zjistí zda je hrana vybrána.
     * @return {@code true} pokud je hrana vybrána, 
	 * v opačném případě {@code false}.
     */
    @Override
	public boolean isSelected() {
		return selected;
	}
    
    /**
     * Zjistí hodnotu jasu hrany.
     * @return hodnota jasu hrany.
     */
    public int getColor() {
        return color;
    }
    
    /**
     * Nastaví hodnotu jasu hrany.
     * @param color hodnota jasu.
     */
    public void setColor(int color) {
        this.color = color;
    }
    
    /**
     * Zjistí zda je hrana poprvé vybrána, pokud ano
	 * jsou komponenty reprezentující body na hraně
	 * vloženy na plátno.
     * @return {@code true} pokud je hrana poprvé vybrána,
	 * jinak {@code false}.
     */
    public boolean isFirstSelect() {
        return firstSelect;
    }
    
    /**
     * Nastaví první vybrání hrana na {@code false}.
     */
    public void setFirstSelectToFalse() {
        firstSelect = false;
    }
    
    /**
     * Přidá novou komponentu reprezentující bod na hraně do
	 * seznamu.
     * @param point bod na hraně.
     * @return {@code PointView} reprezentující bod na hraně.
     */
    public PointView addNewPoint(IdPoint point) {
        List<IdPoint> newPoints = new ArrayList<IdPoint>();
        int i = 0;
        int size = points.size() - 1;
        PointView pv = null;
        // je treba umistit bod do seznamu na spravne poradi
        for (; i < size; i++) {
            IdPoint p1 = points.get(i);
            IdPoint p2 = points.get(i+1);
            if (EdgeUtils.isOnEdge(p1, p2, point)) {
                newPoints.add(p1);
                newPoints.add(point);
                
                pv = addPointView(point);
                
                i++;
                break;
            } else {
                newPoints.add(p1);
            }
        }
        for (; i < points.size(); i++) {
            newPoints.add(points.get(i));
        }
        
        points = newPoints;
        repaint();
        return pv;
    }
    
    ////////////////////////////////////////////////////////////////////////////
    // vykresleni komponenty

    /**
     * Vykreslí hranu.
     * @param g grafický kontext.
     */
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
									RenderingHints.VALUE_ANTIALIAS_ON);

        // kontrolni podkresleni
        // g2d.setColor(new Color(0, 255, 255, 70));
        // g2d.fillRect(0, 0, bounds.width, bounds.height);
        
		if (selected) {
			g2d.setColor(Despr.SELECT_OBJECT_COLOR);
            g2d.setStroke(new BasicStroke(strokeSize + 4f));
            
            int len = points.size() - 1;
            for (int i = 0; i < len; i++) {
                Point p1 = points.get(i);
                Point p2 = points.get(i+1);

                g2d.drawLine(p1.x - bounds.x, p1.y - bounds.y, p2.x - bounds.x, p2.y - bounds.y);
            }
		}
        
        if (model.isIncorrect()) {
            g2d.setColor(Color.RED);
        } else {
            g2d.setColor(new Color(color, color, color));
        }
		g2d.setStroke(new BasicStroke(strokeSize, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

		int len = points.size() - 1;
		for (int i = 0; i < len; i++) {
			Point p1 = points.get(i);
			Point p2 = points.get(i+1);
         
            g2d.drawLine(p1.x - bounds.x, p1.y - bounds.y, p2.x - bounds.x, p2.y - bounds.y);
		}
        
        Point p1 = points.get(len - 1);
        Point p2 = points.get(len);
        p1 = new Point(p1.x - bounds.x, p1.y - bounds.y);
        p2 = new Point(p2.x - bounds.x, p2.y - bounds.y);
        Point[] addedPoints = drawArrowHead(g2d, p1, p2, ARROW_WIDTH, ARROW_HEIGHT);
        if (addedPoints != null) {
            for (int i = 0; i < addedPoints.length; i++) {
                Point p = addedPoints[i];
                p.setLocation(p.x + bounds.x, p.y + bounds.y);
            }
            bounds = computeBounds(addedPoints);
            setBounds(bounds);
        }
	}
    
    /**
     * Reaguje na změny vlastností ovlivňující zobrazení hrany.
	 * Jsou to: změna pozice některého z bodů, smazání bodu,
	 * změnu korektnost a změnu barvy (jasu černé) hrany.
     * @param evt událost která změnu vyvolala.
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String propertyName = evt.getPropertyName();
        if (propertyName.equals("point_changed_position")) {
            IdPoint newLoc = null;
            if (evt.getSource() instanceof Port) {
                IdRectangle portBounds = (IdRectangle) evt.getNewValue();
                newLoc = new IdPoint(portBounds.x + portBounds.width / 2,
                                     portBounds.y + portBounds.height / 2,
                                     portBounds.getId());
            } else if (evt.getSource() instanceof PointView) {
                newLoc = (IdPoint) evt.getNewValue();
            }
            
            // body na hrane (bez posledniho, ten se pocita zvlast dale)
            int pointsLen = points.size() - 1;
            for (int i = 0; i < pointsLen; i++) {
                IdPoint point = points.get(i);
                if (newLoc.getId() == point.getId()) {
                    if (point.x != newLoc.x || point.y != newLoc.y) {
                        points.set(i, newLoc);
                        bounds = computeBounds();
                    }
                }
            }
            
            IdPoint penultimatePoint = points.get(pointsLen - 1);
            IdPoint lastPoint = points.get(pointsLen);
            
            // pozice posledniho bodu se bude zpracovavat, pokud
            // vstupni port zmenil pozici, a nebo se zmenila pozice
            // predposledniho bodu
            boolean processLastPoint = false;
            if (penultimatePoint.getId() == newLoc.getId()) {
                processLastPoint = true;
            }
            if (lastPoint.getId() == newLoc.getId()) {
                processLastPoint = true;
                inputPortBounds = (IdRectangle) evt.getNewValue();
            }
            
            if (processLastPoint) {
                lastPoint.setLocation(computeLastPoint(penultimatePoint, inputPortBounds));
            }
            
            bounds = computeBounds();
            setBounds(bounds);
            repaint();
        } else if (propertyName.equals("point_removed")) {
            Object o = evt.getNewValue();
            if (o instanceof PointView) {
                PointView pv = (PointView) o;
                int id = pv.getId();
                int pointsLen = points.size();
                for (int i = 0; i < pointsLen; i++) {
                    IdPoint point = points.get(i);
                    if (point.getId() == id) {
                        points.remove(i);
                        i--;
                        repaint();
                        break;
                    }
                }
                pointsView.remove(pv);
            }
        } else if (propertyName.equals("incorrect_changed")) {
            repaint();
        } else if (propertyName.equals("EdgeColorSlider.value")) {
            color = (Integer) evt.getNewValue();
            repaint();
        }
    }
    
    /**
     * Přidá posluchače na změnu stavu hrany co se týče toho jeslit
	 * je vybraná či nikoliv.
     * @param l posluchač.
     */
    @Override
    public void addPropertyChangeListener(PropertyChangeListener l) {
        if (pcs == null) {
            pcs = new PropertyChangeSupport(this);
        }
        
        pcs.addPropertyChangeListener(l);
    }
    
    /**
     * Smaže posluchače.
     * @param l posluchač.
     */
    @Override
    public void removePropertyChangeListener(PropertyChangeListener l) {
        pcs.removePropertyChangeListener(l);
    }
    
    ////////////////////////////////////////////////////////////
    // Soukrome pomocne meotdy
    
    private PointView addPointView(IdPoint p) {
        PointView pv = new PointView(p);
        pv.addPropertyChangeListener(this);
        pv.addMouseListener(new PointViewMouseHandler());
        pv.setVisible(selected);
        pointsView.add(pv);
        return pv;
    }
    
    private IdPoint computeLastPoint(Point perultimatePoint, IdRectangle inputPortBounds) {
        
        double centerInputPortX = inputPortBounds.x + inputPortBounds.width / 2;
        double centerInputPortY = inputPortBounds.y + inputPortBounds.height / 2;
        
        // u - vektor hrany
        double ux = centerInputPortX - perultimatePoint.x;
        double uy = centerInputPortY - perultimatePoint.y;
        
        // v - vektor portu
        double vx = centerInputPortX - inputPortBounds.x;
        double vy = centerInputPortY - inputPortBounds.y;
        
        double sizeU = Math.sqrt(ux * ux + uy * uy);
        double uxUnit = ux / sizeU;
        double uyUnit = uy / sizeU;
        
        double sizeV = Math.sqrt(vx * vx + vy * vy);
        double vxUnit = vx / sizeV;
        double vyUnit = vy / sizeV;
        
        // uxUnit * 1 + uyUnit * 0;
        double cosAlpha = uxUnit;
        // uxUnit * 0 + uyUnit * 1;
        double sinAlpha = uyUnit;
        // vxUnit je ten spravny rozlisujici zda jsem na x nebo y ose
        double c;
        if (Math.abs(cosAlpha) <= Math.abs(vxUnit)) {
        
            double a = (vyUnit * cosAlpha) / sinAlpha;
            a *= sizeV;
            c = Math.sqrt(a * a + vy * vy);
        } else {
            
            double b = (vxUnit * sinAlpha) / cosAlpha;
            b *= sizeV;
            c = Math.sqrt(b * b + vx * vx);
        }
        
        double minesX = uxUnit * (c);
        double minesY = uyUnit * (c);
        
        int x = perultimatePoint.x + (int) (ux - minesX);
        int y = perultimatePoint.y + (int) (uy - minesY);
        
        return new IdPoint(x, y, inputPortBounds.getId());
    }
     
    private Point[] drawArrowHead(Graphics2D g2d, Point a, Point b, int width, int height) {
        
        Point[] addedPoints = new Point[2];
        double ux = b.x - a.x;
        double uy = b.y - a.y;
        double uSize = Math.sqrt(ux * ux + uy * uy);
        if (uSize == 0) {
            return null;
        }
        
        double uxUnit = ux / uSize;
        double uyUnit = uy / uSize;
        
        double length = uSize - height;
        if (length > 0) {
            // bod v prostoru kde zacina hrana
            double arrowHeadStartX = a.x + uxUnit * length;
            double arrowHeadStartY = a.y + uyUnit * length;
            
            // normalove vektory v jednom a druhem smeru
            double nx1 = uyUnit * width;
            double ny1 = -uxUnit * width;
            
            double nx2 = -nx1;
            double ny2 = -ny1;
            
            double arrowHeadX1 = arrowHeadStartX + nx1;
            double arrowHeadY1 = arrowHeadStartY + ny1;
            double arrowHeadX2 = arrowHeadStartX + nx2;
            double arrowHeadY2 = arrowHeadStartY + ny2;
            addedPoints[0] = new Point((int)arrowHeadX1, (int)arrowHeadY1);
            addedPoints[1] = new Point((int)arrowHeadX2, (int)arrowHeadY2);
            
            GeneralPath triangle = new GeneralPath();
            triangle.moveTo(b.x, b.y);
            triangle.lineTo(arrowHeadX1, arrowHeadY1);
            triangle.lineTo(arrowHeadX2, arrowHeadY2);
            triangle.closePath();
            
            g2d.fill(triangle);
            
            return addedPoints;
        }
        
        return null;
    }
    
    
	private Rectangle computeBounds(Point... point) {
		List<Point> tmpP = new ArrayList<Point>(points);
        tmpP.addAll(Arrays.asList(point));
        
        int x1 = Integer.MAX_VALUE; // minX
        int x2 = 0;                 // maxX
        int y1 = Integer.MAX_VALUE; // minY
        int y2 = 0;                 // maxY
        
        for (Point p : tmpP) {
            if (p.x < x1) {
                x1 = p.x;
            }
            
            if (p.x > x2) {
                x2 = p.x;
            }
            
            if (p.y < y1) {
                y1 = p.y;
            }
            
            if (p.y > y2) {
                y2 = p.y;
            }
        }
        
        // vypocet okolik je treba zvetsit okraje ohraniceni aby byly videt cele cary
        int halfStrokeSize = (int) strokeSize / 2;
        int strokeSize2;    // zaokrouhlena velikost
        if ((strokeSize - (int) Math.floor(strokeSize)) > 0) {
            strokeSize2 = (int) Math.floor(strokeSize) + 1;
        } else {
            strokeSize2 = (int) Math.floor(strokeSize);
        }
        return new Rectangle(x1 - halfStrokeSize, y1 - halfStrokeSize, x2 - x1 + strokeSize2, y2 - y1 + strokeSize2);       
	}
}
