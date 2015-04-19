package cz.vsb.cs.sur096.despr.view;

import cz.vsb.cs.sur096.despr.model.IInputParameter;
import cz.vsb.cs.sur096.despr.model.IOutputParameter;
import cz.vsb.cs.sur096.despr.model.IParameter;
import cz.vsb.cs.sur096.despr.model.operation.parameter.EInputParameterType;
import java.awt.Color;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;

/**
 * Abstraktní implementace panelu uchovávající porty na operaci.
 * Třída poskytuje společnou implementaci pro panely {@code InputPortsPanel &
 * OoutputPortsPanel}. 
 *
 * @author Martin Šurkovský, sur096 
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 * @version 2012/01/17/08:52
 */
public abstract class AbstractPortsPanel 
            extends JPanel
            implements PortViewListener {
    
	/** Seznam portů.*/
    protected List<Port> ports;
    
	/** Iniciuje seznam portů a nastaví průhledné pozadí panelu.*/
    public AbstractPortsPanel() {
        ports = new ArrayList<Port>();
        setLayout(new FlowLayout(FlowLayout.CENTER, 10, 5));
        setBackground(new Color(255,255,255,0));
        setOpaque(false);
    }
    
	/**
	 * Panely se liší v implementaci této metod. Ta zajistí informovaní 
	 * portů o tom že změnily svou pozici v rámci plátna.
	 */
    public abstract void changePosition();

    /**
     * Pokusí se nalézt port v panelu podle modelu portu.
     * @param model model portu.
     * @return pokud je port obsažen pak je vrácen, jinak
	 * vrátí {@code null}.
     */
    protected Port findPort(IParameter model) {
        for (Port p : ports) {
            if (p.getModel().equals(model)) {
                return p;
            }
        }
        return null;
    }

    /**
     * Reaguje na změnu vzhledu portu. Pokud se tak stane
	 * panel přepočte své rozměry překreslí všechny své porty.
	 * Nakonec informuje porty o změně pozice.
     */
    @Override
    public void portChangeView() {
        removeAll();
        for (Port port : ports) {
            IParameter p = port.getModel();
            if (p instanceof IInputParameter) {
                IInputParameter ip = (IInputParameter) p;
                EInputParameterType type = ip.getType();
                if (type == EInputParameterType.OUTER) {
                    add(port);
                }
            } else if (p instanceof IOutputParameter) {
                add(port);
            }
        }
        revalidate();
        
        Object parent = getParent();
        if (parent instanceof Operation) {
            Operation op = (Operation) parent;
            op.repaintNearArea();
        }
        
        // ok nema vliv na vykon, resp. zanedbatelny
        // zajisti pri zmene typu vstupniho parametru i propagaci na
        // vystup, pokud by se zmenila velikost panelu se vstupnimi
        // porty a tim se posunuly i vystupni porty.
        changePosition();
    }
}