
package cz.vsb.cs.sur096.despr.window.actions;

import cz.vsb.cs.sur096.despr.view.portvizualization.TypesTree;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ActionEvent;
import javax.swing.JDialog;
import javax.swing.JScrollPane;

/**
 * Akce sloužící pro vyvolání okna s vizualizací typů použitých pro porty
 * operací a jejich vzájemné vztahy, vč. vizualizačních informací.
 *
 * @author Martin Šurkovský, sur096,
 * <a href="maitlo:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 * @version 2012/03/04/00:16
 */
public class ShowPortTypesStructureAction extends BasicAbstractAction {
    
    private Window own;
    
    /**
     * Iniciace akce.
     * @param own vlastník okna.
     */
    public ShowPortTypesStructureAction(Window own) {
        super();
        this.own = own;
    }

    /**
     * Vyvolá okno se stromem popisující vztahy mezi použitými typy u 
	 * portů operací.
     * @param e 
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        JDialog dialog = new JDialog(own);
        dialog.setTitle(messages.getString("title", "Types structure"));
        dialog.setPreferredSize(new Dimension(250, 400));
        dialog.setModal(true);
        dialog.add(new JScrollPane(new TypesTree()));
        
        Rectangle ownBounds = own.getBounds();
        dialog.setLocation(ownBounds.x + (ownBounds.width - 250) / 2, 
                ownBounds.y + (ownBounds.height - 400) / 2);
        
        dialog.pack();
        dialog.setVisible(true);
    }
}
