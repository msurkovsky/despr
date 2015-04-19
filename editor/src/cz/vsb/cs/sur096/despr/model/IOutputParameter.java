
package cz.vsb.cs.sur096.despr.model;

import cz.vsb.cs.sur096.despr.collections.TabuList;


/**
 * Definice výstupního parametru.
 *  
 * @author Martin Šurkovský, sur096
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 * @version 2011/08/08/14:24
 */
public interface IOutputParameter 
            extends IParameter, InputParameterDataTypeChangeListener {
    
    /**
     * Zjistí zda jsou k dispozici data na výstupu.
     * @return {@code true} pokud jsou data k dispozici, jinak
	 * {@code false}.
     */
    public boolean hasData();
    
    /**
     * Zjistí jaký je počet napojených vstupních parametrů.
     * @return počet napojených parametrů.
     */
    public int getCountOutputs();
    
    /**
     * Nastaví počet napojených vstupních parametrů.
     * @param countOutputs počet napojených parametrů.
     */
    public void setCountOutputs(int countOutputs);
    
    /**
     * Poskytne číslo úrovně rodičovské operace, pokud by náhodou parametr
     * nebyl součástí nějaké operace vrátí -1
     * @return číslo úrovně rodičovské operace, pokud je operace prázdná 
	 * ({@code null}) vrátí -1.
     */
    public int getOperationLevel();
    
    /**
     * Informuje napojene hrany ze operace zmenila uroven
     * @param level nova uroven operace
     */
    public void fireChangeOperationLevel(int level);
    
    /**
     * Informuje o změně hodnoty výstupního parametru.
     */
    public void fireOutputParameterValueChanged();
    
    /**
     * Informuje o změně seznamu zakázaných operací.
     * @param list seznam změn.
     * @param method metoda jakou má být seznam aktualizován.
     */
    public void fireTabuListChange(TabuList list, ETabuListUpdateMethod method);
    
    /**
     * Přidá posluchače na změnu hodnoty parametru.
     * @param l posluchač.
     */
    public void addValueChangeListener(OutputParameterChangeListener l);
    
    /**
     * Smaže posluchače.
     * @param l posluchač.
     */
    public void removeValueChangeListener(OutputParameterChangeListener l);
}
