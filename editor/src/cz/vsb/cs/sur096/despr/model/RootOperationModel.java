
package cz.vsb.cs.sur096.despr.model;

import cz.vsb.cs.sur096.despr.Despr;
import cz.vsb.cs.sur096.despr.model.operation.IRootOperation;
import cz.vsb.cs.sur096.despr.utils.ID;

/**
 * Implementace rozhraní {@code IRootOperationModel}. 
 *
 * @author Martin Šurkovský, sur096 
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 * @version 2011/08/10/16:16
 */
public class RootOperationModel extends OperationModel implements IRootOperationModel {

    /**
     * Iniciuje kořenovou operaci.
     * @param rootOp odkaz na uživatelsky definovanou kořenovou operaci.
     */
    public RootOperationModel(IRootOperation rootOp) {
        this(rootOp, ID.getNextID());
    }
    
    /**
     * Iniciuje kořenovou operaci.
     * @param rootOp odkaz na uživatelsky definovanou kořenovou operaci.
     * @param id kladné celé číslo jednoznačně definující ID operace.
	 * @throws IllegalArgumentException pokud je použito nekorektní ID.
	 * Tento konstruktor by měl být využíván je pro načtení již uložených
	 * operací.
     */
    public RootOperationModel(IRootOperation rootOp, int id) 
			throws IllegalArgumentException {
        super(rootOp, id);
        // nactou se lokalizacni zpravy pro rootovskou operaci, plus se pouziji
        // zpravy z obycejne operace
        messages = Despr.loadLocalizeMessages(getClass(), messages, false);
        level = 0;
        
        for (IOutputParameter outputParameter : outputParameters) {
            outputParameter.fireChangeOperationLevel(level);
        }
    }
    
    /**
     * Zjistí počet prvků, které mají být zpracovány.
     * @return počet prvků které mají být zpracovány, nebo -1
	 * pokud by se mělo jednat o nekonečný cyklus.
     * @throws RuntimeException při zjišťování velikost kolekce
	 * může dojít k jakékoliv chybě, takže je ke zprávě připojena
	 * informace o operaci ve které se stala a přeposlána dále ke
	 * zpracování.
     */
    @Override
    public int getCountItems() throws RuntimeException {
        IRootOperation rootOp = (IRootOperation) op;
        if (!rootOp.wasInit()) {
            rootOp.init();
        }
        
        try {
            return rootOp.getCount();
        } catch (RuntimeException ex) {
            String opName = getDisplayName().trim().replaceAll("\\n", " ");
            String message = String.format("%s (%s@%d)", ex.getMessage(),
                    opName, getId());
            throw new RuntimeException(message);
        }
    }
    
    /**
     * Metoda zjistí zda existuje další prvek ke zpracování.
     * @return {@code true} pokud existuje prvek ke zpracování,
	 * jinak {@code false}.
     */
    @Override
    public boolean hasNext() {
        IRootOperation rootOp = (IRootOperation) op;
        if (!rootOp.wasInit()) {
            rootOp.init();
        }
        return rootOp.hasNext();
    }

    /**
     * Metoda resetuje iterátor operace a znovu iniciuje 
	 * počáteční podmínky.
     */
    @Override
    public void resetIterator() {
        IRootOperation rootOp = (IRootOperation) op;
        rootOp.resetIterator();
        rootOp.init();
    }
    
    /**
     * Každá kořenová operace se nachází na úrovni nula.
     */
    @Override
    public void computeLevel() {
        super.computeLevel();
        if (level == -1) {
            level = 0; // pokud jsou pouzity vstupni porty je pouzita
                       // regulerni uroven.
        }
    }
    
    /**
     * Zjistí pokud je existuje prvek ke zpracování, pokud
	 * ano zavolá metodu {@code call()} předka.
     * @return prázdný objekt {@code null}.
     * @throws Exception pokud při zpracování nastane nějaká chyba
	 * nebo nebude další prvek ke zpracování.
     */
    @Override
    public Void call() throws Exception {
        IRootOperation rootOp = (IRootOperation) op;
        if (!rootOp.wasInit()) {
            rootOp.init();
        }
        if (rootOp.hasNext()) {
            super.call();
            rootOp.setNext();
        } else {
            String opName = getDisplayName().trim().replaceAll("\\n", " ");
            throw new RuntimeException(String.format("%s (%s@%d)!",
                    messages.getString("exception.no_more_elements", "No more elements"),
                    opName, getId()));
        }
        return null;
    }
}