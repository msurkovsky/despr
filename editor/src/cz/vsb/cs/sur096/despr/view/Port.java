
package cz.vsb.cs.sur096.despr.view;

import cz.vsb.cs.sur096.despr.model.IParameter;
import cz.vsb.cs.sur096.despr.structures.IdRectangle;
import cz.vsb.cs.sur096.despr.utils.ColorPalete;
import cz.vsb.cs.sur096.despr.utils.DrawIcon;
import cz.vsb.cs.sur096.despr.utils.ID;
import cz.vsb.cs.sur096.despr.view.portvizualization.PVIChangeListener;
import cz.vsb.cs.sur096.despr.view.portvizualization.PortVisualInformation;
import cz.vsb.cs.sur096.despr.view.portvizualization.Types;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.JPanel;

/**
 * Představuje vizuální reprezentaci portu operace.
 *
 * @author Martin Šurkovský, sur096 
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 * @version 2011/11/27/17:44
 */
public class Port<T extends IParameter> 
            extends JComponent 
            implements PropertyChangeListener, PVIChangeListener {

    private final int MIN_HEIGHT = 15;
    private final int MIN_WIDTH = 35;
    private final int USED_ICON_WIDTH = 6;
    private final int USED_ICON_HEIGHT = 11;
    private final int GAP = 3; // gap between components on a port
    private final Font FONT = new Font(Font.SANS_SERIF, Font.PLAIN, 11);
    
    private int id;
    
    private transient PropertyChangeSupport pcs;
    private transient List<PortViewListener> pvl;
    
    private T model;
    
    private PortVisualInformation pvi;
    
    /**
	 * Iniciuje port
     * @param model parametr operace.
     */
    public Port(T model) {
        this.model = model;
        init();
    }
    
    private void init() {
        pcs = new PropertyChangeSupport(this);
        pvl = new ArrayList<PortViewListener>();
        id = ID.getNextID(ID.POINT_GENERATOR);
        addMouseListener(new PortMouseClickedListener());
        revalidatePVI();
    }
    
    /**
     * Nastaví model portu.
     * @param model model portu.
     */
    public void setModel(T model) {
        this.model = model;
    }
    
    /**
     * Poskytne model portu.
     * @return model portu.
     */
    public T getModel() {
        return model;
    }
    
    /**
     * Poskytne datový typ parametru.
     * @return datový typ.
     */
    public Class getDataType() {
        return model.getDataType();
    }
    
    /**
     * Zjistí zda je port použit či nikoliv.
     * @return je port použit?
     */
    public boolean isUsed() {
        return model.isUsed();
    }

    /**
     * Získá hodnotu z parametru.
     * @return hodnota parametru.
     */
    public Object getValue() {
        return model.getValue();
    }
    
    /**
     * Získá ID portu.
     * @return ID portu.
     */
    public int getId() {
        return id;
    }
    
    /**
     * Znovu vyhodnotí vizuální informace o portu,
	 * PVI = Port Visual Information.
     */
    private void revalidatePVI() {
        if (pvi != null) {
            pvi.removePVIChangeListener(this);
        }
        pvi = Types.getPVI(model.getDataType());
        pvi.addPVIChangeListener(this);
    } 
       
    ////////////////////////////////////////////////////////////
    
    /**
     * Pokud nějakým způsobem port změní svou pozici vůči plátnu, 
     * ať už pohnutím operace, nebo změnou počtu portů na operaci, 
     * metoda informuje o teto změně hrany na port napojené.
     * o změně pozice.
     */
    public void changePosition() {
        Point location = getLocation();
        Dimension size = getSize();
        
        IdRectangle bounds = new IdRectangle(location, size, id);
        
        // informuje napojene hrany o zmene pozice
        pcs.firePropertyChange("point_changed_position", null, bounds);
    }
    
    /**
     * Poskytne lokaci portu v rámci plátna.
     * @return lokace portu v rámci plátna.
     */
    @Override
    public Point getLocation() {
        Point location = super.getLocation();
        Container portPanel = getParent(); // port panel
        if (portPanel != null) {
            Container operation = portPanel.getParent();
            Point portPanelLoc = portPanel.getLocation();
            location = new Point(location.x + portPanelLoc.x,
                                 location.y + portPanelLoc.y);
            
            if (operation != null) {
                Point operationLoc = operation.getLocation();
                location = new Point(location.x + operationLoc.x, 
                                     location.y + operationLoc.y);
            }
        }
        return location;
    }
    
    /**
     * Nastaví pozici portu.
     * @param p pozice portu.
     */
    @Override
    public void setLocation(Point p) {
        super.setLocation(p);
        changePosition();
    }
    
    /**
     * Nastaví pozici portu.
     * @param x <i>x</i> souřadnice
     * @param y <i>y</i> souřadnice
     */
    @Override
    public void setLocation(int x, int y) {
        super.setLocation(x, y);
        changePosition();
    }
    
    /**
	 * Nastaví velikost portu.
     * @param size rozměry portu.
     */
    @Override
    public void setSize(Dimension size) {
        Dimension oldSize = getSize();
        super.setSize(size);
        if (oldSize.width != size.width || oldSize.height != size.height) {
            // pokud se zmeni rozmer portu informuji o tom panel
            // ve kterem je vlozen aby se mohl korektne prekreslit a 
            // snim i operace
            firePortViewListeners();
        }
    }
    
    /**
     * Nastaví velikost portu.
     * @param width šířka portu.
     * @param height výška portu.
     */
    @Override
    public void setSize(int width, int height) {
        Dimension oldSize = getSize();
        super.setSize(width, height);
        if (oldSize.width != width || oldSize.height != height) {
            firePortViewListeners();
        }
    }
    
    /**
     * Reaguje na změny typu parametru, jeho použitelnosti a 
	 * datového typu.
     * @param evt událost.
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals("used")) {
            // pokud je port pouzity preskrne se
            repaint();
        } else if (evt.getPropertyName().equals("type")) {
            model.getParent().computeLevel();
            // meni se informace pro vykresleni portu
            revalidatePVI();
            // pri zmene typu se mysli zmena typu vstupnich portu
            // z vnitriho na vnejsi nebo obracene. V tom pripade
            // je treba port smazat nebo pridat z/na panel.
            firePortViewListeners();
        } else if (evt.getPropertyName().equals("data_type")) {
            revalidatePVI();
            // zde je mozne ze se meni i velikost portu, v pripade
            // ze novy dataovy typ je pole
            setSize(getPreferredSize());
            repaint();
        }
    }
    
    /**
     * Reaguje na změnu vizuální reprezentace portu.
     */
    @Override
    public void pviChanged() {
        setSize(getPreferredSize());
        repaint();
    }
    
    ////////////////////////////////////////////////////////////
    // vykresleni komponenty
    
    /**
     * Nakreslí port. Port má obdélníkový tvar, s nějakou barvou pozadí
	 * která specifikuje typ, bílím či černým menším obdélníkem uprostřed
	 * znázorňujícím použitelnost portu a číslem vlevo od středu a písmenem
	 * vpravo od středu. Tyto informace blíže specifikují datový typ.
     * @param g grafický kontext.
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                             RenderingHints.VALUE_ANTIALIAS_ON);
        FontMetrics fm = getFontMetrics(FONT);
        g2d.setFont(FONT);
        
        Dimension dim = getPreferredSize();
        
        g2d.setColor(pvi.getColor());
        g2d.fillRect(0, 0, dim.width, dim.height);
        
        g2d.setColor(Color.BLACK);
        g2d.drawRect(0, 0, dim.width - 1, dim.height - 1);
        
        
        Color textColor = ColorPalete.getTextColor(pvi.getColor());
        g2d.setColor(textColor);
        if (pvi.isArray()) {
            g2d.drawRect(2, 2, dim.width - 5, dim.height - 5);
        }
        
        int iconX = dim.width / 2 - 3;
        int iconY = dim.height / 2 - 5;
        
        g2d.drawImage(
                DrawIcon.drawRectangleIcon(USED_ICON_WIDTH, 
                                           USED_ICON_HEIGHT, 
                                           (model.isUsed() ? Color.BLACK : Color.WHITE),
                                           textColor), 
                iconX, iconY, null);
        g2d.setFont(FONT);
        
        int deep = pvi.getDeep();
        if (deep > 0) {
            String deepStr = Integer.toString(deep);
            
            int x;
            int y;
            
            if (pvi.isArray()) {
                x = GAP + 2;
                y = dim.height - GAP - 2;
            } else {
                x = GAP;
                y = dim.height - GAP;
            }
            g2d.drawString(deepStr, x, y);
        }
        
        String specified = pvi.getSpecified();
        if (!specified.equals("")) {
            int specifiedWidth = fm.stringWidth(specified);
            int x;
            int y;
            if (pvi.isArray()) {
                // o dva vetsi mezera diky oramovani a zvetsenemu portu
                x = dim.width - GAP - 2 - specifiedWidth;
                y = dim.height - GAP - 2;
            } else {
                x = dim.width - GAP - specifiedWidth;
                y = dim.height - GAP;
            }
            
            g2d.drawString(specified, x, y);
        }
        
        // nastaveni spravneho tooltipu
        setToolTipText(String.format("<html><b>%s</b>:%s</html>", 
                model.getDisplayName(), model.getDataType().getSimpleName()));
    }
    
    /**
	 * Vypočte velikost portu.
     * @return rozměry portu.
     */
    @Override
    public Dimension getPreferredSize() {
        
        FontMetrics fm = getFontMetrics(FONT);
        int deepWidth = fm.stringWidth(Integer.toString(pvi.getDeep()));
        int specifiedWidth = fm.stringWidth(pvi.getSpecified());
        
        
        // Port je rozdelen symetricky podle stredu, kde se nachazi 
        // ikona upozornujici na to zda je port pouzity, ci nikoliv.
        // Je tedy vybrana vetsi velikost.
        int addWidth = (deepWidth > specifiedWidth) ? deepWidth : specifiedWidth;
        // pricte se velikost mezery, zleva a zprava
        addWidth += 2 * GAP;
        // jsou 2 popisky (hlobka a specifikacni text)
        int width = 2 * addWidth + USED_ICON_WIDTH;
        // pokud je nova sirka vetsi nez minimalni pouzi je se
        width = (width > MIN_WIDTH) ? width : MIN_WIDTH;
        
        if (pvi.isArray()) {
            return new Dimension(width + 4, MIN_HEIGHT + 4);
        } else {
            return new Dimension(width , MIN_HEIGHT);
        }
    }
    
    
    ////////////////////////////////////////////////////////////
    // Registrace posluchacu
    
    /**
     * Přidá posluchače na změnu vlastnosti.
     * @param l posluchač.
     */
    @Override
    public void addPropertyChangeListener(PropertyChangeListener l) {
        pcs.addPropertyChangeListener(l);
    }
    
    /**
     * Smaže posluchače na změnu vlastnosti.
     * @param l posluchač.
     */
    @Override
    public void removePropertyChangeListener(PropertyChangeListener l) {
        pcs.removePropertyChangeListener(l);
    }
    
    /**
     * Přidá posluchače na změnu vizuální reprezentace portu.
     * @param l posluchač.
     */
    public void addPortViewListener(PortViewListener l) {
        pvl.add(l);
    }
    
    /**
	 * Smaže posluchače.
     * @param l posluchač.
     */
    public void removePortViewListener(PortViewListener l) {
        pvl.remove(l);
    }
    
    private void firePortViewListeners() {
        for (PortViewListener l : pvl) {
            l.portChangeView();
        }
    }
    
    ////////////////////////////////////////////////////////////
    
	private class PortMouseClickedListener extends MouseAdapter {

        @Override
		public void mouseClicked(MouseEvent e) {
            if (!model.isUsed()) {
                Container parentPortPanel = getParent();
                if (parentPortPanel instanceof JPanel) {
                    Point panelLoc = parentPortPanel.getLocation();
                    Container parentOp = parentPortPanel.getParent();
                    if (parentOp instanceof Operation) {
                        Point opLoc = parentOp.getLocation();
                        Operation parentOperation = (Operation) parentOp;
                        if (parentOperation != null) {
                            Object parentCanvas = parentOperation.getParent();
                            if (parentCanvas instanceof GraphCanvas) {
                                GraphCanvas gCanvas = (GraphCanvas) parentCanvas;
                                Point portLoc = Port.super.getLocation();
                                Dimension size = getPreferredSize();
                                Point location = new Point(panelLoc.x + opLoc.x + portLoc.x + size.width / 2,
                                                           panelLoc.y + opLoc.y + portLoc.y + size.height / 2);
                                gCanvas.drawEdge(e, location);
                            }
                        }
                    }
                }
            }
		}
	 }
}
