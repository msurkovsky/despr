
package cz.vsb.cs.sur096.despr.window.actions;

import cz.vsb.cs.sur096.despr.Despr;
import java.awt.event.ActionEvent;
import java.util.logging.Level;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

/**
 * Akce slouží pro smazání obsahu standardního výstupu.
 *
 * @author Martin Šurkovský, sur096, 
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 * @version 2012/01/29/16:13
 */
public class OutputClearAction extends BasicAbstractAction {

    private JTextPane output;
    
    /**
     * Iniciuje akci.
     * @param output odkaz na textový panel se standardním výstupem.
     */
    public OutputClearAction(JTextPane output) {
        super();
        this.output = output;
    }
    
    /**
     * Smaže obsah na standardním výstupu.
     * @param e 
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        Document doc = output.getDocument();
        try {
            doc.remove(0, doc.getLength());
        } catch (BadLocationException ex) {
            String title = messages.getString("title.bad_localiton_excp", "Bad location");
            Despr.showError(title, ex, Level.WARNING, true);
        }
    }
}