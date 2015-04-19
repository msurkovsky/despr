package cz.vsb.cs.sur096.despr.model;

import cz.vsb.cs.sur096.despr.model.operation.parameter.EInputParameterType;
import java.beans.PropertyChangeListener;


/**
 * Definice vstupního parametru.
 * @author Martin Šurkovský, sur096 
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 * * @version 2011/08/08/14:21
 */
public interface IInputParameter
            extends IParameter, PropertyChangeListener, 
                    OutputParameterChangeListener {
    
    /**
     * Metoda nastaví hodnotu parametru. Hodnota je uložena v původním modelu
     * tato metoda pouze zprostředkuje komunikaci.
     * @param o hodnota parametru.
     */
    public void setValue(Object o);
    
    /**
     * Metoda vracejici typ vstupniho parametru:
     * @return 
     * <ul>
     *   <li>{@code EInputParameterType.INNER} - jedna se o vnitrni 
     * parametry funkce (operace), které nastavuje uživatel ručně.</li>
     *   <li>{@code EInputParameterTYpe.OUTER} - jedna se o vnější 
     * parametry operace, které jsou brány z předchozí operace. Výstupní
     * parametry poskytuji vstupy pro další operace. Uživatel explicitně
     * nenastavuje, tyto hodnoty jsou nastaveny na základě propojeni (hran) v
     * definovaném grafu.
     * </ul>
     */
    public EInputParameterType getType();
    
    /**
     * Metoda nastavuje typ vstupního parametru:
     * @param type akceptované hodnoty jsou {@code EInputParameterType.INNER}
     *  nebo {@code EInputParameter.OUTER}
     */
    public void setType(EInputParameterType type);
    
    /**
     * Zjistí zda je možné přepínat mezi typy vstupního parametru.
     * @return {@code true} pokud je možné přepnout výchozí nastavení,
	 * jinka {@code false}.
     */
    public boolean isLockChangeType();
    
    /**
     * Poskytne číslo úrovně předchozí operace.
     * @return číslo úrovně předchozí operace.
     */
    public int getPreviousOpLevel();
    
    /**
     * Nastaví číslo úrovně předchozí operace.
     * @param level číslo úrovně předchozí operace.
     */
    public void setPreviousOpLevel(int level);
    
    /**
     * Nastaví počet hran které vedou ze zdrojového parametru.
     * @param sourceCountEdges počet hran které vedou ze zdrojového parametru.
     */
    public void setSourceCountEdges(int sourceCountEdges);
    
    /**
     * Přidá posluchače na změnu datového typu parametru.
     * @param l posluchač.
     */
    public void addInputParameterTypeChangeListener(InputParameterDataTypeChangeListener l);
    
    /**
     * Smaže posluchače.
     * @param l posluchač.
     */
    public void removeParameterTypeChangeListener(InputParameterDataTypeChangeListener l);
}
