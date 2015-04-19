
package cz.vsb.cs.sur096.despr.model;

import cz.vsb.cs.sur096.despr.Despr;
import cz.vsb.cs.sur096.despr.collections.TabuList;
import cz.vsb.cs.sur096.despr.exceptions.IncompatibleDataTypeException;
import cz.vsb.cs.sur096.despr.exceptions.IncorrectEdgeException;
import cz.vsb.cs.sur096.despr.exceptions.IncorrectEdgesException;
import cz.vsb.cs.sur096.despr.model.operation.IOperation;
import cz.vsb.cs.sur096.despr.model.operation.parameter.EInputParameterType;
import cz.vsb.cs.sur096.despr.types.Copyable;
import java.beans.IntrospectionException;
import java.beans.PropertyChangeEvent;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 * Implementace vstupního parametru.
 * @author Martin Šurkovský, sur096 
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 */
public class InputParameter 
            extends AbstractParameter
            implements IInputParameter {
    
    /** Seznam posluchačů na změnu datového typu vstupního parametru.*/			
    private transient List<InputParameterDataTypeChangeListener> typeChangeListneres;
    
    /**
     * Hodnota pomáhá s optimalizaci předávání dat.
     * Obrázky nejsou zrovna nejmenší data, takže pokud vede z jednoho
     * zdrojového portu více hran. Pak se jeden obrázek může předat odkazem.
	 * Toto znatelně zvýší rychlost komunikace, jelikož je častější stav
	 * 1 k 1 než 1 k N.
     */
    private int sourceCountEdges;
    
    /**
     * Typ parametru:
     * <ul>
     *   <li>{@code EInputParameterType.INNER} - vnitřní parametr, jedna
     * se o parametr který nastavuje uživatel.</li>
     *   <li>{@code EInputParameterType.OUTER} - vnější parametr, jedna se 
     * o parametr, který se nastavuje pomoci propojeni (hran) v grafu.
     * </ul>
     */
    private EInputParameterType type;
    
	/** Uživatel může uzamknout možnost přepínaní typu vstupního parametru.*/
    private boolean lockChangeType;

	/** Každý vstupní port si pamatuje úroveň předchozí operace*/
    private int previousOpLevel;
    
    /**
     * Iniciuje vstupní parametr.
     * @param propertyName jméno parametru.
     * @param order řazení parametru.
     * @param operationModel model operace.
     * @param type typ vstupního parametru.
     * @param lockChangeType je možné měnit typ vstupního parametru?
     * @throws IntrospectionException pro načtení přístupových metod
	 * k danému parametru je využívaná využíván objekt {@code PropertyDescriptor} 
	 * ten pokud nenalezne přístupové metody ve formátu JavaBeans pro dané
	 * jméno parametru pak vyvolá tuto výjimku.
     */
    public InputParameter(String propertyName, int order, IOperationModel operationModel, 
            EInputParameterType type, boolean lockChangeType) 
            throws IntrospectionException {
        super(propertyName, order, operationModel);

        typeChangeListneres = new ArrayList<InputParameterDataTypeChangeListener>();
        this.type = type;
        sourceCountEdges = 0;
        this.lockChangeType = lockChangeType;
        previousOpLevel = -1;
    }
    
    /**
     * Rozšíření implementace z abstraktní implementace.
	 * Metoda navíc informuje všechny závislé výstupní parametry,
	 *
     * @param dataType nový datový typ
     * @throws IncompatibleDataTypeException pokud není nový datový
	 * typ kompatibilní s výchozím typem.
     * @throws IncorrectEdgesException tuto výjimku vyvolá situace kdy jsou
	 * dva typy již spojeny hranou a po změně typu jednoho z nich by byly
	 * nekompatibilní.
     */
    @Override
    public void setDataType(Class dataType) 
            throws IncompatibleDataTypeException, IncorrectEdgesException {
        
        super.setDataType(dataType);
        // posle zmenu typu vsem zavislym vystupnim parametrum teze rozdicovske
        // operace
        fireInputParameterDataTypeChanged();
    }
    
    /**
     * Nastaví hodnotu danému parametru.
     * @param o hodnota parametru.
     */
    @Override
    public void setValue(Object o) {
        Method setMethod = getWriteMethod();
        if (setMethod != null) {
            try {
                setMethod.invoke(operation, o);
            } catch (IllegalAccessException ex) {
                Despr.showError(messages.getString("title.illegal_acces", "Illegal access"), 
                        ex, Level.WARNING, true);
            } catch (IllegalArgumentException ex) {
                String msg = ex.getMessage();
                IOperationModel opModel = getParent();
                String extendedMsg = String.format("%s (%s@%d)", 
                        msg, getOperationDisplayName(opModel), opModel.getId());
                Despr.showError(
                        messages.getString("title.bad_value", "Bad parameter value"), 
                        new IllegalArgumentException(extendedMsg), 
                        Level.WARNING, false);
            } catch (InvocationTargetException ex) {
                String msg = ex.getTargetException().getMessage();
                IOperationModel opModel = getParent();
                String extendedMsg = String.format("%s (%s@%d)", 
                        msg, getOperationDisplayName(opModel), opModel.getId());
                
                Despr.showError(
                        messages.getString("title.bad_value", "Bad parameter value"),
                        new InvocationTargetException(ex, extendedMsg),
                        Level.WARNING, false);
            } catch (RuntimeException ex) {
                // chyba kterou muze vyvolat
                // metod a operace, napr. pri validaci hodnoty.
                String msg = ex.getMessage();
                IOperationModel opModel = getParent();
                String extendedMsg = String.format("%s (%s@%d)",
                        msg, getOperationDisplayName(opModel), opModel.getId());
                Despr.showError(
                        messages.getString("title.set_value_problem", "Set value problem"),
                        new RuntimeException(extendedMsg), 
                        Level.WARNING, false);
            }
        }
    }
    
    private String getOperationDisplayName(IOperationModel opModel) {
        return opModel.getDisplayName().trim().replaceAll("\\n", " ");
    }
    
    /**
     * Poskytne typ vstupního parametru.
     * @return {@code INNER} nebo {@code OUTER}.
     */
    @Override
    public EInputParameterType getType() {
        return type;
    }

    /**
     * Nastaví typ danému vstupnímu parametru.
     * @param type typ
     * @throws IllegalArgumentException pokud by se jednalo
	 * o jiný typ než je {@code INNER} nebo {@code OUTER}.
     */
    @Override
    public void setType(final EInputParameterType type) 
            throws IllegalArgumentException {
        
        final EInputParameterType old = this.type;
        this.type = type;
        if (type == EInputParameterType.OUTER) {
            setUsed(false);
        } else if (type == EInputParameterType.INNER) {
            setUsed(true);
        } else {
            throw new IllegalArgumentException(String.format("%s '%s'", 
                    messages.getString("exception.unusupported_input_param_type",
                    "Unsupported type of the input parameter!"), type.toString()));
        }
        
        if (old == EInputParameterType.OUTER && type == EInputParameterType.INNER) {
            IOperationModel parentOp = getParent();
            if (parentOp != null) {
                parentOp.computeLevel();
            }
        }
        pcs.firePropertyChange("type", old, type);
    }
    
    /**
     * Zjistí zda je možné přepínat mezi typy vstupního parametru.
     * @return {@code true} pokud je možné přepnout výchozí nastavení,
	 * jinka {@code false}.
     */
    @Override
    public boolean isLockChangeType() {
        return lockChangeType;
    }
    
    /**
     * Poskytne číslo úrovně předchozí operace.
     * @return číslo úrovně předchozí operace.
     */
    @Override
    public int getPreviousOpLevel() {
        return previousOpLevel;
    }
    
    /**
     * Nastaví číslo úrovně předchozí operace.
     * @param level číslo úrovně předchozí operace.
     */
    @Override
    public void setPreviousOpLevel(int level) {
        previousOpLevel = level;
        // rekne rodicovske operaci at zkusi prepocitat svoji uroven
        IOperationModel parent = getParent();
        if (parent != null) {
            parent.computeLevel();
        }
    }
    
    /**
     * Nastaví počet hran které vedou ze zdrojového parametru.
     * @param sourceCountEdges počet hran které vedou ze zdrojového parametru.
     */
    @Override
    public void setSourceCountEdges(int sourceCountEdges) {
        this.sourceCountEdges = sourceCountEdges;
    }
    
    /**
     * Přidá posluchače na změnu datového typu parametru.
     * @param l posluchač.
     */
    @Override
    public void addInputParameterTypeChangeListener(InputParameterDataTypeChangeListener l) {
        typeChangeListneres.add(l);
    }

    /**
     * Smaže posluchače.
     * @param l posluchač.
     */
    @Override
    public void removeParameterTypeChangeListener(InputParameterDataTypeChangeListener l) {
        typeChangeListneres.remove(l);
    }

    /**
     * Reaguje na změnu počtu hran zdrojového parametru a na změnu úrovně
	 * předchozí operace.
     * @param evt událost.
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals("countOutputs")) {
            // pokud byla smazana hrana, snizil se pocet napojenych
            // hran na zdrojovy port. V teto metode si to zjisti vstupni
            // port a pokud je pocet mensi zmensi svoji hodnotu.
            int oldValue = ((Integer) evt.getOldValue()).intValue();
            int newValue = ((Integer) evt.getNewValue()).intValue();
            if (newValue < oldValue) {
                sourceCountEdges--;
            }
        } else if (evt.getPropertyName().equals("previousOpLevel")) {
            if (evt.getNewValue() instanceof Integer) {
                setPreviousOpLevel((Integer) evt.getNewValue());
            }
        }
    }
        
    /**
     * Reaguje na změnu hodnoty výstupního parametru. Pokud předchozí operace
	 * zpracovala data automaticky je přepošle z výstupních parametrů na vstupní.
     * @param newValue nová hodnota.
     * @param source zdrojový parametr ze kterého data přišla.
     */
    @Override
    public void outputParameterValueChange(Object newValue, IOutputParameter source) {
        Object value;

        if (newValue == null) {
            // zkusim nastavit hodnotu null. Nektere parametry mohou tuto
            // hodnotu brat jako regulerni
            value = null;
        } else if (sourceCountEdges == 1) {
            value = newValue;
        } else if (newValue instanceof Copyable) {
            Copyable copyable = (Copyable) newValue;
            value = copyable.copy();
        } else if (CopyableObjects.canMakeCopy(newValue.getClass())) {
            value = CopyableObjects.copy(newValue);
        } else {
            value = newValue;
            source.setUsed(true); 
        }

        setValue(value);
    }

    /**
     * Reaguje na změnu datového typu zdrojového parametru.
     * @param dataType nový datový typ.
     * @param source zdrojový port.
     * @throws IncorrectEdgeException pokud se změnou typu stane hrana
	 * nekorektní, tj. datové typy nejsou kompatibilní.
     * @throws IncorrectEdgesException pokud při změně výstupního
	 * parametru vzniklo více nekorektních hran.
     */
    @Override
    public void outputParameterDataTypeChange(Class dataType, IOutputParameter source) 
            throws IncorrectEdgeException, IncorrectEdgesException {
        
        try {
            setDataType(dataType);
        } catch (IncompatibleDataTypeException e) {
            throw new IncorrectEdgeException(source, this);
        }
    }

    /**
     * Reaguje na změnu seznamu zakázaných operací předchozí operace.
     * @param list seznam zakázaných operací
     * @param method způsob aktualizace seznamu.
     */
    @Override
    public void outputParameterTabuListChange(TabuList list, 
                                              ETabuListUpdateMethod method) {
        
        IOperationModel parentOp = getParent();
        parentOp.updateTabuList(list, method);
    }
    
    private void fireInputParameterDataTypeChanged() throws IncorrectEdgesException {
        for (InputParameterDataTypeChangeListener listener : typeChangeListneres) {
            listener.inputParameterDataTypeChanged(getDataType());
        }
    }
}
