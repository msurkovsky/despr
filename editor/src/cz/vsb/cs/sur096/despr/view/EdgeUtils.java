package cz.vsb.cs.sur096.despr.view;

import cz.vsb.cs.sur096.despr.structures.IdPoint;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;


/**
 * Utilita pro práci s hranami na plátně. Vzhledem k tomu, že vizuální
 * reprezentace hrany je odvozena od {@code JComponent} a každá komponenta
 * má ohraničení v rámci něhož reaguje na události apod. To však u hrany
 * není žádoucí a jelikož zabírá pouze malou část s celkového prostoru
 * komponenty je třeba specifikovat aktivní oblast na kterou komponenta 
 * hrany reaguje. Tudíž reakce k hranám jsou registrována na plátno
 * a tato utilita pomáhá plátnu vybrat o kterou hranu se jedná.
 *
 * @author Martin Šurkovský, sur096 
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 * @version 2012/01/16/9:10
 */
public final class EdgeUtils {
        
    private EdgeUtils() { }
    
	/**
     * Vybere a označí hranu na základě vybraného bodu na plátně.
	 * Hrana je vybrána tak, že se vypočte nejbližší hrana která
	 * je danému bodu.
     * @param edges seznam všech hran na plátně.
     * @param p bod na plátně na který bylo kliknu to myší.
     * @return Odkaz na hranu která byla vybrána.
     */
    public static Edge setSelected(List<Edge> edges, Point p) {
        List<Edge> tmpEdges = new ArrayList<Edge>(edges);

        // odfiltrovani hran, na kterych 100% nejsem
        int len = tmpEdges.size();
        for (int i = 0; i < len; i++) {
            if (tmpEdges.get(i).isSelected()) {
                tmpEdges.get(i).setSelected(false);
            }
            if (!isInEdgeBound(tmpEdges.get(i), p)) {
                tmpEdges.remove(i);
                len -= 1;
                i--;
            }
        }

        for (Edge edge : tmpEdges) {
            List<IdPoint> arrowPoints = edge.getPoints();
            len = arrowPoints.size() - 1;
            for (int i = 0; i < len; i++) {
                Point tmpP = new Point(p.x -2, p.y -2);
                if (isOnEdge(arrowPoints.get(i), arrowPoints.get(i+1), tmpP)) {
                    edge.setSelected(true);
                    return edge;
                }
            }
        }
        
        return null;
    }

    /**
     * Zjistí zda se daný bod v ohraničení hrany, tj. celá plocha
	 * kterou komponenta hrany zabírá komponenty.
     * @param a komponenta hrany.
     * @param p bod který by se měl nacházet v ohraničení hrany.
     * @return {@code true} pokud se bod v okolí hrany nachází, jinak
	 * {@code false}.
     */
    public static boolean isInEdgeBound(Edge a, Point p) {
        Rectangle rec = a.getBounds();
        int minX = rec.x;
        int maxX = minX + rec.width;
        int x = p.x;
        boolean xIsIn = ((x > minX && x < maxX) ? true : false);
        int minY = rec.y;
        int maxY = minY + rec.height;
        int y = p.y;
        boolean yIsIn = ((y > minY && y < maxY) ? true : false);

        return xIsIn & yIsIn;
    }

    /**
     * Zjistí zda se daný bod nachází na hraně danou body p1 a p2.
     * @param p1 počáteční bod hrany.
     * @param p2 koncový bod hrany.
     * @param a bod který by se měl na hraně nacházet.
     * @return {@code true} pokud se bod na hraně opravdu nachází,
	 * jinak {@code false}.
     */
    public static boolean isOnEdge(Point p1, Point p2, Point a) {

        int u1 = p2.x - p1.x;
        int u2 = p2.y - p1.y;

        int n1 = -u2; // a
        int n2 = u1;  // b
        int c = -1 * (n1 * p1.x + n2 * p1.y);

        int citatel = Math.abs(n1 * a.x + n2 * a.y + c);
        double jmenovatel = Math.sqrt(n1 * n1 + n2 * n2);
        double distance = citatel / jmenovatel;

        if (distance <= 4) {
            return true;
        } else {
            return false;
        }
    }
}
