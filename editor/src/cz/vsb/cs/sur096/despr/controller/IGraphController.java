
package cz.vsb.cs.sur096.despr.controller;

import cz.vsb.cs.sur096.despr.exceptions.ParameterUsedException;

/**
 * Rozšiřuje rozhraní {@code Executable} a přidává metody o verifikaci grafu
 * a možnost zastavit průběh zpracovaní grafu.
 *
 * @author Martin Šurkovský, sur096,
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 * @version 2012/02/28/08:42
 */
public interface IGraphController extends Executable {
    
    /**
     * Provede kontrolu grafu
     * @throws ParameterUsedException 
     */
    public void verifyGraph() throws Exception;
    
    /**
     * Přeruší zpracovávání grafu.
     */
    public void stopExecuting();
}
