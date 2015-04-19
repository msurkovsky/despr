package cz.vsb.cs.sur096.despr.model;

import cz.vsb.cs.sur096.despr.collections.IParameters;
import cz.vsb.cs.sur096.despr.collections.TabuList;
import cz.vsb.cs.sur096.despr.model.operation.IOperation;
import java.beans.ExceptionListener;
import java.io.Serializable;
import java.util.concurrent.Callable;

/**
 * Rozhraní pro mezivrstvu, která rozšiřuje původní model o společnou 
 * funkcionalitu. Aplikace pak komunikuje s uživatelsky definovanými
 * operacemi přes tuto mezivrstvu.
 *
 * @author Martin Šurkovský, sur096 
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 * @version 2011/08/09/9:16
 */
public interface IOperationModel 
            extends Callable<Void>, Comparable<IOperationModel>, Serializable {
    
    /**
     * Poskytne id operace. 
     * @return unikátní celé kladné číslo podle nějž je možné identifikovat
	 * operaci v rámci aplikace.
     */
    public int getId();
    
    /**
     * Poskytne jméno operace.
     * @return jméno operace.
     */
    public String getName();
    
    /**
     * Poskytne jméno operace, které je použito pro komunikaci
	 * s uživatelem. 
     * @return uživatelsky přívětivé jméno.
     */
    public String getDisplayName();
    
    /**
     * Poskytne krátký popisek operace.
     * @return krátký popisek operace.
     */
    public String getDescription();
    
    /**
     * Vrátí odkaz na uživatelsky definovanou operaci, kterou rozšiřuje.
     * @return odkaz na původní operaci.
     */
    public IOperation getOperation();
    
    /**
     * Poskytne kolekci vstupních parametrů.
     * @return kolekci vstupních parametrů.
     */
    public IParameters<IInputParameter> getInputParameters();
    
    /**
     * Poskytne kolekci výstupních parametrů.
     * @return kolekci výstupních parametrů.
     */
    public IParameters<IOutputParameter> getOutputParameters();
    
    /**
     * Poskytne číslo úrovně, tj. hloubku ve stromě operací.
     * @return celé kladné číslo úrovně na které se operace nachází 
	 * v rámci grafu. Pokud svou úroveň není schopna určit
	 * vrátí -1.
     */
    public int getLevel();
    
    /**
     * Vypočte úroveň, hloubku na které se operace nachází v rámci grafu.
     */
    public void computeLevel();
    
    /**
     * Poskytne seznam zakázaných operací.
     * @return seznam zakázaných operací.
     */
    public TabuList getTabuList();
    
    /**
     * Metoda aktualizuje seznam zakázaných operací a dá o tom vědět 
     * ostatním napojeným operacím.
     * 
     * @param list seznam zakázaných operací který má být přidán/odebrán
     * z aktuálního seznamu.
     * @param method metoda aktualizace buď seznam rozšířen nebo zúžen.
     */
    public void updateTabuList(TabuList list, ETabuListUpdateMethod method);

    /**
     * Přidá posluchače výjimek.
     * @param l posluchač.
     */
    public void addExceptionListener(ExceptionListener l);
    
    /**
     * Smaže posluchače výjimek.
     * @param l posluchač.
     */
    public void removeExceptionListener(ExceptionListener l);
    
    /**
     * Rozešle posluchačům výjimek informaci o vyvolané výjimce.
     * @param e výjimka.
     */
    public void fireException(Exception e);
}