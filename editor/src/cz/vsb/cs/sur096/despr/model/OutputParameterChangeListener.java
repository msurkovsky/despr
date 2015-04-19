package cz.vsb.cs.sur096.despr.model;

import cz.vsb.cs.sur096.despr.collections.TabuList;
import cz.vsb.cs.sur096.despr.exceptions.IncorrectEdgeException;
import cz.vsb.cs.sur096.despr.exceptions.IncorrectEdgesException;


/**
 * Posluchač, reagující na změnu hodnoty, datového typu a
 * seznamu zakázaných operací výstupního parametru.
 *
 * @author Martin Šurkovský, sur096 
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 * @version 2011/08/23/08:16
 */
public interface OutputParameterChangeListener {
    
    /**
     * Reaguje na změnu hodnoty výstupního parametru. Pokud předchozí operace
	 * zpracovala data automaticky je přepošle z výstupních parametrů na vstupní.
     * @param newValue nová hodnota.
     * @param source zdrojový parametr ze kterého data přišla.
     */
    public void outputParameterValueChange(Object newValue, IOutputParameter source);
    
    /**
     * Reaguje na změnu datového typu zdrojového parametru.
     * @param dataType nový datový typ.
     * @param source zdrojový port.
     * @throws IncorrectEdgeException pokud se změnou typu stane hrana
	 * nekorektní, tj. datové typy nejsou kompatibilní.
     * @throws IncorrectEdgesException pokud při změně výstupního
	 * parametru vzniklo více nekorektních hran.
     */
    public void outputParameterDataTypeChange(Class dataType, IOutputParameter source) 
            throws IncorrectEdgeException, IncorrectEdgesException;
    
    /**
     * Reaguje na změnu seznamu zakázaných operací předchozí operace.
     * @param list seznam zakázaných operací
     * @param method způsob aktualizace seznamu.
     */
    public void outputParameterTabuListChange(TabuList list, ETabuListUpdateMethod method);
}
