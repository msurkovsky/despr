
package cz.vsb.cs.sur096.despr.window.actions;

import cz.vsb.cs.sur096.despr.Despr;
import cz.vsb.cs.sur096.despr.model.IEdge;
import cz.vsb.cs.sur096.despr.model.IGraph;
import cz.vsb.cs.sur096.despr.structures.LocalizeMessages;
import cz.vsb.cs.sur096.despr.view.*;
import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

/**
 * Definuje akci myši pro smazání komponenty z grafu, jak z plátna,
 * tak z modelu. Po kliknutí pravým tlačítkem myši na komponentu vyskočí
 * nabídka s možností smazat komponentu. Metoda mazající komponenty
 * je definována jako statická, takže ji lze využít v aplikaci i bez 
 * inicializace akce.
 *
 * @author Martin Šurkovský, sur096, 
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 * @version  2011/09/02/9:57
 */
public class DeleteComponentFromGraphAction extends MouseAdapter {

    /**
     * Veřejná statická metoda umožnující smazat komponentu z grafu.
     * @param comp komponenta která je součástí grafu, pokud by nebyla 
	 * metoda nereaguje.
     */
    public static void deleteComponent(Component comp) {
        Container container = comp.getParent();
        if (container instanceof GraphCanvas) {
            GraphCanvas gCanvas = (GraphCanvas) container;
            IGraph graphModel = gCanvas.getModel();

            if (comp instanceof Selectable) {
                ((Selectable) comp).setSelected(false);
            }
            
            if (comp instanceof Operation) {
                Operation op = (Operation) comp;
                List<IEdge> removedEdges = graphModel.removeOperation(op.getModel());
                for (IEdge edge : removedEdges) {
                    Edge edgeView = gCanvas.findEdges(edge.getId());
                    if (edgeView != null) {
                        deleteEdge(graphModel, gCanvas, edgeView);
                    }
                }    
                gCanvas.removeComponentFromCanvas(op);
                gCanvas.getSelectedObjects().cancelSelectedObject(op);

            } else if (comp instanceof Edge) {
                Edge edge = (Edge) comp;
                deleteEdge(graphModel, gCanvas, edge);
            }
            gCanvas.revalidate();
        }
    }
    
    /**
     * Smaže hranu z grafu i z modelu, vč záchytných bodů na hraně.
     * @param gModel model grafu.
     * @param gCanvas odkaz na plátno.
     * @param edge hrana, která má být smazána.
     */
    private static void deleteEdge(IGraph gModel, GraphCanvas gCanvas, Edge edge) {
        
        gModel.removeEdge(edge.getModel());
        List<PointView> pointsView = edge.getPointsView();
        for (PointView pv : pointsView) {
            gCanvas.removeComponentFromCanvas(pv);
        }
        gCanvas.removeComponentFromCanvas(edge);
        gCanvas.revalidate();
    }
    
    private LocalizeMessages messages;
    
    public DeleteComponentFromGraphAction() {
        messages = Despr.loadLocalizeMessages(getClass(), null, false);
    }
    
    /**
     * Zobrazí popup menu s možností smazat komponentu.
     * @param e
     */
    public void maybeShowPopup(MouseEvent e) {
        if (e.isPopupTrigger()) {
            JPopupMenu deleteMenu = new JPopupMenu();
            deleteMenu.setLocation(e.getLocationOnScreen());
            
            JMenuItem deleteComponent = new JMenuItem(messages.getString("title.delete", "Delete"));
            final Component comp = e.getSource() instanceof Component ? (Component) e.getSource() : null;
            deleteComponent.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    if (comp != null) {
                        deleteComponent(comp);
                    }
                }
            });
 
            if (comp instanceof Selectable) {
                if (((Selectable) comp).isSelected()) {
                    deleteMenu.add(deleteComponent);
                    deleteMenu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
            
        }
    }
    
    @Override
    public void mousePressed(MouseEvent e) {
        maybeShowPopup(e);
    }
    
    @Override
    public void mouseReleased(MouseEvent e) {
        maybeShowPopup(e);
    }
}