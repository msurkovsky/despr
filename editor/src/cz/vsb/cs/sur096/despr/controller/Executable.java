
package cz.vsb.cs.sur096.despr.controller;

/**
 * Definice rozhraní pro spustitelné částí, resp. ty jejichž průběh
 * by měl být zobrazen v progress baru.
 *
 * @author Martin Šurkovský, sur096,
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 * @version 2012/03/04/19:05
 */
public interface Executable {

    /**
     * Spustí samotné zpracování.
     */
    public void execute() throws Exception;
    
    /**
     * Poskytne celkový počet iterací. Když vrátí -1 pak se použije 
	 * progress bar s nastavením {@code setIndeterminate = true}.
     * @return celkový počet iterací zpracovávaných dat.
     */
    public int getLengthOfExecute();
    
    /**
     * Přidá posluchače na změnu úrovně celkového zpracování grafu.
     * @param l posluchač.
     */
    public void addProgressChangeListener(ProgressChangeListener l);
    
    /**
     * Smaže posluchače změny úrovně celkového zpracování grafu.
     * @param l posluchač.
     */
    public void removeProgressChangeListener(ProgressChangeListener l);
}