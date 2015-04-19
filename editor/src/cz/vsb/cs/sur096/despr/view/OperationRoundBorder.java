package cz.vsb.cs.sur096.despr.view;
import java.awt.*;
import javax.swing.border.AbstractBorder;


/**
 * Stará se o vykreslení orámování operace.
 * @author Martin Šurkovský,
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky@gmail.com</a>
 * @version 2010/11/07/20:46
 */
public class OperationRoundBorder
        extends AbstractBorder{

    private Color color;
    private int thickness;
    private int arcSize;
    
    /**
     * Iniciuje rámeček.
     * @param color barva rámečku.
     * @param thickness tloušťka rámečku.
     */
    public OperationRoundBorder(Color color, int thickness) {
        this.color = color;
        this.thickness = thickness;
    }

	
    /**
     * Vykreslí orámování.
     * @param c komponenta které má být orámována.
     * @param g grafický kontext.
     * @param x <i>x</i> souřadnice.
     * @param y <i>y</i> souřadnice.
     * @param width šířka rámečku.
     * @param height výška rámečku.
     */
    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(color);
        g2.setStroke(new BasicStroke(thickness));
        int halfThickness = thickness / 2;
        
        // zuzeni oramovani tak at prochazi v polovine vstupich/vystupnich protu
        if (c instanceof Operation) {
            Operation op = (Operation) c;
            int ipHeight = op.getInputPortsPnlHeight();
            int opHeight = op.getOutputPortsPnlHeight();
            
            int halfIpHeight = ipHeight / 2;
            int halfOpHeight = opHeight / 2;
            
            height = height - halfIpHeight - halfOpHeight;
            
            arcSize = Math.min(width, height);
            arcSize /= 4;
            
            g2.drawRoundRect(x + halfThickness, halfIpHeight + y + halfThickness,
                    width - 2 * halfThickness - 1, height - 2 * halfThickness - 1, arcSize, arcSize);
            g2.setStroke(new BasicStroke()); // kvuli podkomponent je nutne nastavit puvodni sirku stetce
            
        } else {
        
            arcSize = Math.min(width, height);
            arcSize /= 4;
            g2.drawRoundRect(x + halfThickness, y + halfThickness,
                    width - 2 * halfThickness - 1, height - 2 * halfThickness - 1, arcSize, arcSize);
            g2.setStroke(new BasicStroke()); // kvuli podkomponent je nutne nastavit puvodni sirku stetce
        }
    }
}
