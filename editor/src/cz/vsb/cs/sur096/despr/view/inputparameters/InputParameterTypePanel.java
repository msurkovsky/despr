
package cz.vsb.cs.sur096.despr.view.inputparameters;

import cz.vsb.cs.sur096.despr.model.operation.parameter.EInputParameterType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

/**
 * Komponenta pro sloužící pro vizualizaci a přepínaní typu vstupních parametrů.
 * Používá se ve druhém sloupci tabulky pro editaci parametrů.
 *
 * @author Martin Šurkovský, sur096 
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 * @version 2011/08/19/17:47
 */
class InputParameterTypePanel extends JPanel {
    
    /** Seznam posluchačů reagujících na změnu typu vstupního parametru.*/
    private PropertyChangeSupport pcs;
    
    /** Typ vstupního parametru. */
    private EInputParameterType type;
    
    /** 
     * Iniciuje komponentu podle daného typu.
     * @param type typ vstupního parametru.
     */
    public InputParameterTypePanel(EInputParameterType type) {
        this.type = type;
        
        JRadioButton type1 = new JRadioButton();
        type1.setToolTipText("INNER");
        type1.addActionListener(new TypeAction());
        type1.setActionCommand("inner");
        add(type1);
        
        JRadioButton type2 = new JRadioButton();
        type2.addActionListener(new TypeAction());
        type2.setActionCommand("outer");
        type2.setToolTipText("OUTER");
        add(type2);
        
        ButtonGroup group = new ButtonGroup();
        group.add(type1);
        group.add(type2);
        
        switch (type) {
            case INNER:
                type1.setSelected(true);
                break;
            case OUTER:
                type2.setSelected(true);
                break;
        }
    }
    
    /**
     * Poskytne aktuální nastavený typ parametru.
     * @return aktuální nastavený typ parametru.
     */
    public EInputParameterType getInputParametr() {
        return type;
    }
        
    /**
     * Přidá posluchače reagujícího na změnu typu vstupního parametru.
     * @param l posluchač.
     */
    @Override
    public void addPropertyChangeListener(PropertyChangeListener l) {
        if (pcs == null) {
            pcs = new PropertyChangeSupport(this);
        }
        pcs.addPropertyChangeListener(l);
    }
    
    /**
     * Smaže posluchače reagujícího na změnu typu vstupního parametru.
     * @param l posluchač.
     */
    @Override
    public void removePropertyChangeListener(PropertyChangeListener l) {
        if (pcs != null) {
            pcs.removePropertyChangeListener(l);
        }
    }
    
    private class TypeAction implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {

            if (e.getActionCommand().equals("inner")) {
                type = EInputParameterType.INNER;
            } else if (e.getActionCommand().equals("outer")) {
                type = EInputParameterType.OUTER;
            }
            if (pcs != null) {
                pcs.firePropertyChange("edit_done", false, true);
            }
        }
    }
}
