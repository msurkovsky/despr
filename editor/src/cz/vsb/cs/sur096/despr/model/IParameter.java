package cz.vsb.cs.sur096.despr.model;

import cz.vsb.cs.sur096.despr.exceptions.IncompatibleDataTypeException;
import cz.vsb.cs.sur096.despr.exceptions.IncorrectEdgesException;
import java.beans.PropertyChangeListener;

/**
 * Společné rozhraní pro vstupní a výstupní parametry.
 *
 * @author Martin Šurkovský, sur096 
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 */
public interface IParameter {

    /**
     * Poskytne jméno parametru.
     * @return jméno parametru.
     */
    public String getName();

    /**
     * Poskytne lokalizované jméno parametru, pokud existuje.
     * @return lokalizované jméno parametru, pokud existuje,
	 * jinak jméno z {@code getName()}.
     */
    public String getDisplayName();
    
    /**
     * Metoda vracející odkaz na rodičovské operaci
     * @return odkaz na rodičovskou operaci.
     */
    public IOperationModel getParent();
    
    /**
     * Poskytne hodnotu parametru. Hodnota je uložena v původním modelu
     * tato metoda by měla pouze zprostředkovat komunikaci.
     * @return hodnota parametru.
     */
    public Object getValue();
        
    /**
     * Poskytne datový typ parametru.
     * @return datový typ parametru.
     */
    public Class getDataType();
    
    /**
	 * Vzhledem k tomu, že je možné typy určitým způsobem měnit
	 * (konkretizovat čí zobecňovat zpět k výchozímu typu) 
	 * parametr si vždy pamatuje výchozí datový typ.
     * @return výchozí datový typ.
     */
    public Class getDefaultDataType();

    /**
	 * Nastaví nový datový typ.
     * @param dataType nový datový typ.
     * @throws IncompatibleDataTypeException pokud je typ nekompatibilní
	 * s aktuálním typem. Není možné typy měnit libovolně.
     * @throws IncorrectEdgesException změna typu může vyvolat situaci kdy
     * dva spojené parametry hranou měli před změnou kompatibilní datové typy a
	 * po změně ne.
     */
    public void setDataType(Class dataType) 
            throws IncompatibleDataTypeException, IncorrectEdgesException;
    
    
    /**
     * Zjistí zda je daný parametr použitý. Myšleno tak zda 
	 * je možné z něj číst či do něj zapisovat data.
     * @return {@code true} pokud je možné z parametru číst nebo
	 * do něj zapisovat, jinak {@code false}.
     */
    public boolean isUsed();
    
    /**
     * Nastaví použitelnost parametru.
     * @param used je možné z parametru číst či do něj zapisovat?
     */
    public void setUsed(boolean used);
    
    /**
     * Poskytne pořadové číslo parametru.
     * @return pořadové číslo parametru.
     */
    public int getOrder();
    
    /**
     * Přidá posluchače na změnu hodnoty vlastností parametru
     * @param l posluchač.
     */
    public void addPropertyChangeListener(PropertyChangeListener l);
    
    /**
     * Smaže posluchače.
     * @param l posluchač.
     */
    public void removePropertyChangeListener(PropertyChangeListener l);
}
