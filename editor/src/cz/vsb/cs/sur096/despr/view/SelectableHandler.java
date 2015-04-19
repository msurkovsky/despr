
package cz.vsb.cs.sur096.despr.view;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Implementace reakce kliknutí myši na vybratelný objekt.
 * @author Martin Šurkovský, sur096 
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 * @version 2011/08/29/14:37
 */
public class SelectableHandler extends MouseAdapter {

    /**
     * Reakce na kliknutí myši.
     * @param e událost.
     */
    @Override
    public void mouseClicked(MouseEvent e) {
        Object o = e.getSource();
        if (e.getModifiers() == MouseEvent.BUTTON1_MASK) {
            if (o instanceof Selectable) {
                Selectable selectable = (Selectable) o;
                selectable.setSelected(true);
            }
        }
    }
}
