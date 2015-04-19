
package cz.vsb.cs.sur096.despr.window.actions;

import cz.vsb.cs.sur096.despr.view.GraphCanvas;
import cz.vsb.cs.sur096.despr.window.DesprIcons;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;

/**
 * Akce ořízne velikost plátna buď na minimální rozměry a nebo na to kolik
 * místa zabírá graf na plátně.
 *
 * @author Martin Šurkovský, sur096, 
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 * @version 2012/02/07/10:21
 */
public class CutGraphAction extends BasicAbstractAction {

	/** odkaz na plátno grafu. */
    private GraphCanvas gCanvas;
    
	/**
	 * Iniciuje akci s plátnem grafu.
	 * @param gCanvas plátno grafu.
	 */
    public CutGraphAction(GraphCanvas gCanvas) {
        super();
        
        this.gCanvas = gCanvas;
        putValue(SMALL_ICON, DesprIcons.getIcon(DesprIcons.CUT_ICON, true));
        putValue(LARGE_ICON_KEY, DesprIcons.getIcon(DesprIcons.CUT_ICON, false));
    }
    
	/**
	 * Ořízne plátno buď na minimální rozměry plátna nebo
	 * na minimální velikost kterou vyžadují komponenty na plátně.
	 * @param e
	 */
    @Override
    public void actionPerformed(ActionEvent e) {
        
        Component[] comps = gCanvas.getComponents();
        if (comps.length > 0) {
            int minX, minY;
            minX = minY = Integer.MAX_VALUE;
            int maxX, maxY;
            maxX = maxY = 0;
            
            for (Component comp : comps) {
                Rectangle bounds = comp.getBounds();
                if (bounds.x < minX) {
                    minX = bounds.x;
                }
                
                if (bounds.y < minY) {
                    minY = bounds.y;
                }
                
                int compMaxPosX = bounds.x + bounds.width;
                if (compMaxPosX > maxX) {
                    maxX = compMaxPosX;
                }
                
                int compMaxPosY = bounds.y + bounds.height;
                if (compMaxPosY > maxY) {
                    maxY = compMaxPosY;
                }
            }
            
            Dimension minimumCanvasSize = gCanvas.getMinimumSize();
            int minWidth = minimumCanvasSize.width;
            int minHeight = minimumCanvasSize.height;
            gCanvas.setPreferredSize(new Dimension(maxX < minWidth ? minWidth : maxX, 
                    maxY < minHeight ? minHeight : maxY));
            gCanvas.scrollRectToVisible(new Rectangle(minX, minY, 1,1));
        }
    }
}
