
package cz.vsb.cs.sur096.despr.model;

/**
 * Jedna se o typ rozšířující původní rozhraní {@code IOperationModel}.
 * Jsou definovány metody, které umožní komunikaci s objekty typu:
 * {@code IRootOperation}.
 *
 * @author Martin Šurkovský, sur096 
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 * @version 2011/08/10/16:04
 */
public interface IRootOperationModel extends IOperationModel {
    
	/**
	 * Zjistí počet položek které mají být zpracovány. 
	 * @return počet položek, které mají být zpracovány,
	 * nebo -1 pokud by jedná o nekonečný cyklus.
	 */
    public int getCountItems();
    
    /**
     * Metoda zjistí zda existuje další prvek ke zpracování.
     * @return <code>true</code> pokud existuje další prvek ke zpracování,
     * jinak <code>false</code>.
     */
    public boolean hasNext();
    
	/**
	 * Resetuje iterátor.
	 */
    public void resetIterator();
}
