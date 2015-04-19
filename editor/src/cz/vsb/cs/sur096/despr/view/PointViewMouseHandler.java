
package cz.vsb.cs.sur096.despr.view;

import cz.vsb.cs.sur096.despr.Despr;
import cz.vsb.cs.sur096.despr.structures.LocalizeMessages;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.AbstractAction;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

/**
 * Obsluhuje reakce myši na vizuální reprezentaci bodu.
 *
 * @author Martin Šurkovský, sur096 
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 * @version 2012/01/16/09:21
 */
public class PointViewMouseHandler implements MouseListener {
    
    /** Lokalizační zprávy. */
    private transient LocalizeMessages messages;
    
    /**
     * Iniciuje posluchače.
     */
    public PointViewMouseHandler() {
        messages = Despr.loadLocalizeMessages(getClass(), null, false);
    }

    /**
     * Stará se o vyvolání pop-up menu.
     * @param evt událost.
     */
    @Override
    public void mousePressed(MouseEvent evt) {
        maybeShowPopup(evt);
    }

    /**
     * Stará se o vyvolání pop-up menu.
     * @param evt událost.
     */
    @Override
    public void mouseReleased(MouseEvent evt) {
        maybeShowPopup(evt);
    }
    
    /**
     * Není implementován (prázdná implementace).
     * @param e událost.
     */
    @Override
    public void mouseClicked(MouseEvent e) {
    }
    
    /**
     * Není implementován (prázdná implementace).
     * @param e událost.
     */
    @Override
    public void mouseEntered(MouseEvent e) {
    }

    /**
     * Není implementován (prázdná implementace).
     * @param e událost.
     */
    @Override
    public void mouseExited(MouseEvent e) {
    }

    /**
     * Zobrazí vyskakovací menu pro umožňující smazání bodu.
     * @param e událost.
     */
    private void maybeShowPopup(MouseEvent e) {
        if (e.isPopupTrigger()) {
            JPopupMenu popup = new JPopupMenu(messages.getString("name.point", "Point"));
            popup.setLocation(e.getLocationOnScreen());
            JMenuItem deletePoint = new JMenuItem(messages.getString("name.point.delete", "Delete"));
            
            Object source = e.getSource();
            if (source instanceof PointView) {
                PointView pw = (PointView) source;
                Container parent = pw.getParent();
                if (parent instanceof GraphCanvas) {
                    deletePoint.addActionListener(
                            new DeleteAction((GraphCanvas) parent, pw));
                    popup.add(deletePoint);
                    popup.show(e.getComponent(), e.getX(), e.getY());   
                }
            }
        }
    }

    
    /**
     * Akce smazání bodu z plátna.
     */
    private class DeleteAction extends AbstractAction {

        private GraphCanvas gCanvas;
        private PointView pw;
        
        public DeleteAction(GraphCanvas gCanvas, PointView pw) {
            this.gCanvas = gCanvas;
            this.pw = pw;
        }
        
        @Override
        public void actionPerformed(ActionEvent e) {
            if (gCanvas != null && pw != null) {
                pw.removePoint();
                gCanvas.remove(pw);
                gCanvas.repaint();
            }
        }
    }
}
