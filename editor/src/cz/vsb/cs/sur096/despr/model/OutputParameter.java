
package cz.vsb.cs.sur096.despr.model;

import cz.vsb.cs.sur096.despr.Despr;
import cz.vsb.cs.sur096.despr.collections.TabuList;
import cz.vsb.cs.sur096.despr.exceptions.IncompatibleDataTypeException;
import cz.vsb.cs.sur096.despr.exceptions.IncorrectEdgeException;
import cz.vsb.cs.sur096.despr.exceptions.IncorrectEdgesException;
import cz.vsb.cs.sur096.despr.model.IOperationModel;
import java.beans.IntrospectionException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 * Implementace výstupního parametru.
 * 
 * @author Martin Šurkovský, sur096
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 */
public class OutputParameter
            extends AbstractParameter
            implements IOutputParameter {

	/** Seznam posluchačů na změnu parametru.*/
    private List<OutputParameterChangeListener> listeners;  
    
	/** Příznak zda má port k dispozici data */
    private boolean hasData;

	/** Počet hran, které vedou z tohoto parametru*/
    private int countOutputs;
    
    /**
     * Iniciuje výstupní parametr.
     * @param propertyName jméno parametru.
     * @param order řazení parametru.
     * @param operationModel odkaz na rodičovskou operaci.
     * @throws IntrospectionException pro načtení přístupových metod
	 * k danému parametru je využívaná využíván objekt {@code PropertyDescriptor} 
	 * ten pokud nenalezne přístupové metody ve formátu JavaBeans pro dané
	 * jméno parametru pak vyvolá tuto výjimku.
     */
    public OutputParameter(String propertyName, int order, IOperationModel operationModel) 
            throws IntrospectionException {
        super(propertyName, order, operationModel);
        
        listeners = new ArrayList<OutputParameterChangeListener>(10);
        countOutputs = 0;
        hasData = false;
    }
    
    /**
     * Zjistí zda jsou k dispozici data na výstupu.
     * @return {@code true} pokud jsou data k dispozici, jinak
	 * {@code false}.
     */
    @Override
    public boolean hasData() {
        return hasData;
    }
    
    /**
     * Zjistí jaký je počet napojených vstupních parametrů.
     * @return počet napojených parametrů.
     */
    @Override
    public int getCountOutputs() {
        return countOutputs;
    }
    
    /**
     * Nastaví počet napojených vstupních parametrů.
     * @param countOutputs počet napojených parametrů.
     */
    @Override
    public void setCountOutputs(int countOutputs) {
        int oldValue = this.countOutputs;
        this.countOutputs = countOutputs;
        pcs.firePropertyChange("countOutputs", oldValue, countOutputs);
    }

    /**
     * Poskytne číslo úrovně rodičovské operace, pokud by náhodou parametr
     * nebyl součástí nějaké operace vrátí -1
     * @return číslo úrovně rodičovské operace, pokud je operace prázdná 
	 * ({@code null}) vrátí -1.
     */
    @Override
    public int getOperationLevel() {
        IOperationModel parent = getParent();
        if (parent != null) {
            return parent.getLevel();
        } else {
            return -1;
        }
    }
    
    /**
     * Informuje napojené parametry o změně úrovně operace.
     * @param level nová úroveň.
     */
    @Override
    public void fireChangeOperationLevel(int level) {
        pcs.firePropertyChange("previousOpLevel", null, level);
    }
    
    /**
     * Informuje o změně hodnoty výstupního parametru.
     */
    @Override
    public void fireOutputParameterValueChanged() {
        hasData = true;
        for (OutputParameterChangeListener l : listeners) {
            l.outputParameterValueChange(getValue(), this);
        }
    }
    
    /**
     * Informuje o změně seznamu zakázaných operací.
     * @param list seznam změn.
     * @param method metoda jakou má být seznam aktualizován.
     */
    @Override
    public void fireTabuListChange(TabuList list, ETabuListUpdateMethod method) {
        for (OutputParameterChangeListener l : listeners) {
            l.outputParameterTabuListChange(list, method);
        }
    }

    /**
     * Přidá posluchače na změnu hodnoty parametru.
     * @param l posluchač.
     */
    @Override
    public void addValueChangeListener(OutputParameterChangeListener l) {
        boolean containL = false;
        for (OutputParameterChangeListener listener : listeners) {
            if (listener == l) {
                containL = true;
                break;
            }
        }
        if (!containL) {
            listeners.add(l);
        }
    }

    /**
     * Smaže posluchače.
     * @param l posluchač.
     */
    @Override
    public void removeValueChangeListener(OutputParameterChangeListener l) {
        
        boolean containL = false;
        for (OutputParameterChangeListener listener : listeners) {
            if (listener == l) {
                containL = true;
                break;
            }
        }
        if (containL) {
            listeners.remove(l);
        }
    }
    
    /**
     * Reaguje na změnu datového typu vázaného vstupního parametru.
     * @param newType nový typ.
     * @throws IncorrectEdgesException pokud změnou typu vzniknout
	 * nekorektní hrany, tj. hrany mezi nekompatibilními typy.
     */
    @Override
    public void inputParameterDataTypeChanged(Class newType) throws IncorrectEdgesException {
        
        try {
            setDataType(newType);
        } catch (IncompatibleDataTypeException ex) {
            Despr.showError(
                    messages.getString("title.incompatible_datat_tipes_excp", 
                    "Incompatible data types"), ex, Level.WARNING, false);
        }
        
        List<IncorrectEdgeException> incorrectEdges = 
                new ArrayList<IncorrectEdgeException>();
        // pokud jsou na port navazane nejake hrany tak jim dam
        // vedet o zmene typu
        for (OutputParameterChangeListener listener : listeners) {
            try {
                listener.outputParameterDataTypeChange(newType, this);
            } catch (IncorrectEdgeException e) {
                incorrectEdges.add(e);
            }
        }
        
        if (!incorrectEdges.isEmpty()) {
            throw new IncorrectEdgesException(incorrectEdges);
        }
    }
}
