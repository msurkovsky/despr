package cz.vsb.cs.sur096.despr.model;

import cz.vsb.cs.sur096.despr.Despr;
import cz.vsb.cs.sur096.despr.collections.TabuList;
import cz.vsb.cs.sur096.despr.exceptions.IncorrectEdgeException;
import cz.vsb.cs.sur096.despr.exceptions.IncorrectEdgesException;
import cz.vsb.cs.sur096.despr.structures.LocalizeMessages;
import cz.vsb.cs.sur096.despr.utils.ID;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * Výchozí implementace modelu hrany.
 *
 * @author Martin Šurkovský, sur096 
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 * @version 2011/08/09/16:38
 */
public class DefaultEdge implements IEdge {

    /** Lokalizační zprávy */
    private transient LocalizeMessages messages;
    
	/** Seznam posluchačů */
    private transient PropertyChangeSupport pcs;
    
	/** Zdrojový parametr hrany*/
    private IOutputParameter source;
    
	/** Cílový parametr hrany*/
    private IInputParameter target;
    
	/** Jednoznačné id hrany */
    private int id;
    
	/** Příznak korektnosti hrany*/
    private boolean incorrect;
    
    /**
     * Vytvoří novou hranu.
     * @param source zdrojový parametr.
     * @param target cílový parametr.
     * @param id požadované id.
	 * @throws IllegalArgumentException pokud zvolené {@code id} nelze použít.
     */
    public DefaultEdge(IOutputParameter source, IInputParameter target, Integer id) 
			throws IllegalArgumentException {
        
        messages = Despr.loadLocalizeMessages(getClass(), null, false);
        pcs = new PropertyChangeSupport(this);
        
        this.source = source;
        this.target = target;
        
        boolean idOk = ID.addId(id);
        if (!idOk) {
            throw new IllegalArgumentException(String.format(
                    "%s '%d'!", 
                    messages.getString("exception.bad_id", "ID could not be use"), 
                    id));
        } else {
            this.id = id;
        }
        incorrect = false;
    }
    
    /**
     * Vytvoří novou hranu.
     * @param source zdrojový parametr.
     * @param target cílový parametr.
     */
    public DefaultEdge(IOutputParameter source, IInputParameter target) {
        this(source, target, ID.getNextID());
    }
    
    /**
     * Poskytne parametr ze kterého hrana vychází.
     * @return zdrojový parametr.
     */
    @Override
    public IOutputParameter getSource() {
        return source;
    }

    /**
     * Poskytne parametr do kterého hrana vstupuje.
     * @return cílový parametr.
     */    
    @Override
    public IInputParameter getTarget() {
        return target;
    }
    
   /**
     * Poskytne jednoznačné id portu. Každou hranu je je možné identifikovat
	 * v rámci aplikace podle jejího id.
     * @return id hrany.
     */
    @Override
    public int getId() {
        return id;
    }
    
    /**
     * Zjistí zda je hrana korektní.
     * @return {@code true} pokud je hrana mezi dvěma nekompatibilními typy,
	 * jinak {@code false}.
     */
    @Override
    public boolean isIncorrect() {
        return incorrect;
    }
    
    /**
     * Nastaví příznak korektnosti hrany.
     * @param incorrect je hrana korektní?
     */
    @Override
    public void setIncorrect(boolean incorrect) {
        boolean oldIncorrect = this.incorrect;
        this.incorrect = incorrect;
        pcs.firePropertyChange("incorrect_changed", oldIncorrect, incorrect);
    }
    
    /**
	 * Překrytá metoda {@code toString} upravující textovou reprezentaci hrany.
	 * @return textovou reprezentaci hrany ve formátu: 
	 * {@code SourceOperation[id](Port) -> TargetOperation[id](Port)}.
	 */
	@Override
	public String toString() {
		return "" + source.getParent().toString() + "(" + source.getName() + ")" + 
				  " -> " +
				  target.getParent().toString() + "(" + target.getName() + ")";
	}

    /**
     * Umožňuje registraci posluchačů na změnu vnitřních vlastností hrany.
     * @param l posluchač.
     */
    @Override
    public void addPropertyChangeListener(PropertyChangeListener l) {
        pcs.addPropertyChangeListener(l);
    }

    /**
     * Smaže posluchače na změnu vlastnosti hrany.
     * @param l posluchače.
     */
    @Override
    public void removePropertyChangeListener(PropertyChangeListener l) {
        pcs.removePropertyChangeListener(l);
    }
    

    /**
     * Změna hodnoty výstupního parametru.
     * @param newValue nová hodnota.
     * @param source zdrojový parametr.
	 * @deprecated hrana na změnu hodnoty nereaguje. Její implementace je prázdna.
     */
    @Override
    @Deprecated
    public void outputParameterValueChange(Object newValue, IOutputParameter source) {
    }

    /**
     * Změna datového typu výstupního parametru.
     * @param dataType nový datový typ.
     * @param source zdrojový parametr.
     * @throws IncorrectEdgeException nikdy. Hrana pouze vyhodnotí zda jsou
	 * typy kompatibilní čí nikoli. A podle toho nastaví svůj vnitřní stav.
     * @throws IncorrectEdgesException nikdy. Hrana pouze vyhodnotí zda jsou
	 * typy kompatibilní čí nikoli. A podle toho nastaví svůj vnitřní stav.
     */
    @Override
    public void outputParameterDataTypeChange(Class dataType, IOutputParameter source) 
            throws IncorrectEdgeException, IncorrectEdgesException {
        
        if (target.getDefaultDataType().isAssignableFrom(dataType)) {
            setIncorrect(false);
        } else {
            setIncorrect(true);
        }
    }
    
    /**
     * Změna seznamu zakázaných operací na výstupním portu.
     * @param list seznam zakázaných operací.
     * @param method 
	 * @deprecated hrana nepotřebuje reagovat na tuhle událost. Má
	 * prázdnou implementaci.
     */
    @Override
	@Deprecated
    public void outputParameterTabuListChange(TabuList list, ETabuListUpdateMethod method) {
    }
}
