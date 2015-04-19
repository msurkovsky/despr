
package cz.vsb.cs.sur096.despr.view;

import cz.vsb.cs.sur096.despr.events.MessageEvent;
import cz.vsb.cs.sur096.despr.events.MessageListener;
import cz.vsb.cs.sur096.despr.model.IOutputParameter;
import javax.swing.SwingUtilities;

/**
 * Implementace panelu s výstupními porty operace. Tento panel
 * také implementuje rozhraní {@code MessageListener} a naslouchá
 * panelu se vstupními porty jestli nezměnil svojí výšku. Pokud ano
 * musí přepočítat tento panel přepočítat pozici svých portů.
 *
 * @author Martin Šurkovský, sur096, 
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 * @version 2012/02/28/18:07
 */
public class OutputPortsPanel extends AbstractPortsPanel implements MessageListener {

    /**
     * Na základě výstupního parametru operace vytvoří port
	 * a přidá jej do panelu.
     * @param op výstupní parametr operace.
     */
    public void addOutputParameter(IOutputParameter op) {
        Port p = new Port(op);
        op.addPropertyChangeListener(p);
        ports.add(p);
        p.addPortViewListener(this);
        add(p);
    }
    
    /**
     * Informuje porty o změně pozice.
     */
    @Override
    public void changePosition() {
        for (Port p : ports) {
            p.changePosition();
        }
    }
    
    /**
     * Reaguje na zprávu o změně výšky panelu se vstupními porty.
     * @param me událost zprávy
     */
    @Override
    public void catchMessage(MessageEvent me) {
        if (me.getMessage().equals("input_ports_panel_changed")) {
            // prekresleni musi byt zavolano v pravou chbili, 
            // jinak se mine ucinkem
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    changePosition();
                }
            });
        }
    }
}
