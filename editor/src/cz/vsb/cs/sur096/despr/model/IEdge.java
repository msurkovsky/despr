package cz.vsb.cs.sur096.despr.model;

import java.beans.PropertyChangeListener;

/**
 * Rozhraní modelu hrany.
 * 
 * @author Martin Šurkovský, sur096 
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 * @version 2011/08/09/16:33
 */
public interface IEdge extends OutputParameterChangeListener {
    
	/**
     * Poskytne parametr ze kterého hrana vychází.
     * @return zdrojový parametr.
     */
    public IOutputParameter getSource();
    
    /**
     * Poskytne parametr do kterého hrana vstupuje.
     * @return cílový parametr.
     */
    public IInputParameter getTarget();
    
    /**
     * Poskytne jednoznačné id portu. Každou hranu je je možné identifikovat
	 * v rámci aplikace podle jejího id.
     * @return id hrany.
     */
    public int getId();
    
    /**
     * Zjistí zda je hrana korektní.
     * @return {@code true} pokud je hrana mezi dvěma nekompatibilními typy,
	 * jinak {@code false}.
     */
    public boolean isIncorrect();
    
    /**
     * Nastaví příznak korektnosti hrany.
     * @param incorrect je hrana korektní?
     */
    public void setIncorrect(boolean incorrect);
    
    /**
     * Umožňuje registraci posluchačů na změnu vnitřních vlastností hrany.
     * @param l posluchač.
     */
    public void addPropertyChangeListener(PropertyChangeListener l);
    
    /**
     * Smaže posluchače na změnu vlastnosti hrany.
     * @param l posluchače.
     */
    public void removePropertyChangeListener(PropertyChangeListener l);
}
