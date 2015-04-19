
package cz.vsb.cs.sur096.despr.view;

import cz.vsb.cs.sur096.despr.events.MessageListener;
import cz.vsb.cs.sur096.despr.events.MessageSupport;
import cz.vsb.cs.sur096.despr.model.IInputParameter;
import cz.vsb.cs.sur096.despr.model.operation.parameter.EInputParameterType;

/**
 * Implementace panelu se vstupními porty.
 *
 * @author Martin Šurkovský, sur096,
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 * @version 2012/02/28/17:59
 */
public class InputPortsPanel extends AbstractPortsPanel {
    
	/** Seznam lokalizačních zpráv.*/
    private MessageSupport messageSupport;
	/** Přiznak toho zda jsou všechny vstupní porty vnitřní.*/
    private boolean areAllInputParametersInner;
    
	/**
	 * Na základě modelu vstupního parametru vytvoří port
	 * a přidá jej do panelu.
	 * @param ip vstupní parametr operace.
	 */
    public void addInputParameter(IInputParameter ip) {
        Port p = new Port(ip);
        ip.addPropertyChangeListener(p);
        ports.add(p);
        p.addPortViewListener(this);
        if (ip.getType() == EInputParameterType.OUTER) {
            add(p);
            areAllInputParametersInner = false;
        }
    }
    
	/**
	 * Informuje všechny své porty o změně pozice. Navíc pokud
	 * se změnila situace kdy alespoň jeden port je viditelný
	 * pak informuje i panel s výstupními porty téže operace, aby
	 * přepočetli svoji pozici. Jelikož změna výšky panelu se vstupními
	 * porty ovlivňuje i změnu pozice výstupních portů.
	 */
    @Override
    public void changePosition() {
        boolean nowAllInputParametersInner = true;
        for (Port p : ports) {
            p.changePosition();
            if (((IInputParameter) p.getModel()).getType() == EInputParameterType.OUTER) {
                nowAllInputParametersInner = false;
            }
        }
        
        if (nowAllInputParametersInner != areAllInputParametersInner) {
            messageSupport.sendMessage("input_ports_panel_changed");
            areAllInputParametersInner = nowAllInputParametersInner;
        }
    }
    
    /**
     * Přidá posluchače zprávy. Panel se vstupními porty posílá
	 * při změně svoji výšky zprávu panelu s výstupními porty.
     * @param listener posluchač, panel s výstupními porty.
     */
    public void addMessageListener(MessageListener listener) {
        if (messageSupport == null) {
            messageSupport = new MessageSupport(this);
        }
        messageSupport.addMessageListener(listener);
    }
    
    /**
     * Smaže posluchače zprávy.
     * @param listener  posluchač.
     */
    public void removeMessageListener(MessageListener listener) {
        messageSupport.removeMessageListener(listener);
    }
}
