
package cz.vsb.cs.sur096.despr.window.actions;

import cz.vsb.cs.sur096.despr.Despr;
import cz.vsb.cs.sur096.despr.model.IOperationModel;
import cz.vsb.cs.sur096.despr.window.ThumbnailPanel;
import java.awt.event.ActionEvent;
import java.util.logging.Level;
import javax.swing.JDialog;

/**
 * Akce sloužící pro zobrazení okna s náhledem na náhled operace pokud jej 
 * poskytuje.
 *
 * @author Martin Šurkovský, sur096, 
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 */
public class ShowThumbnailAction extends BasicAbstractAction {

    private IOperationModel op;
    
    /**
     * Iniciace akce.
     * @param op odkaz na model operace.
     */
    public ShowThumbnailAction(IOperationModel op) {
        super();
        this.op = op;
    } 
    
    /**
     * Zobrazí okno s náhledem a možnostmi nastavení.
     * @param e 
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        JDialog dialog = new JDialog(Despr.getHeadWindow());
        dialog.setModal(false);
        String opName = op.getDisplayName().trim().replaceAll("\\n", " ");
        String title = String.format("%s (%s)",
                messages.getString("panel.title", "Thumbnail"),
                opName);
        dialog.setTitle(title);
        ThumbnailPanel tp;
        try {
            tp = new ThumbnailPanel(op);
        } catch (NullPointerException ex) {
            String titleExcp = messages.getString("title.null_pointer_excp", 
                    "Null input image");
            Despr.showError(titleExcp, ex, Level.WARNING, false);
            return;
        }
        
        dialog.add(tp);
        dialog.pack();
        dialog.setVisible(true);
    }
}