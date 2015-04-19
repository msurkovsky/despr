
package cz.vsb.cs.sur096.despr.controller;

/**
 * Definice posluchače reagujícího změnu pokroku zpracování grafu.
 * Na základě toho lze pak spočítat a vizualizovat v jakém stavu zpracování
 * se grafu nachází.
 *
 * @author Martin Šurkovský, sur096, 
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 * @version 2012/01/28/18:19
 */
public interface ProgressChangeListener {
    
    /**
     * Reaguje na změnu pokroku zpracování grafu.
     * @param progress číslo vyjadřujícího jak daleko je zpracování grafu.
     * @param oneCycleTime čas jak dlouho trval jeden měřený úsek.
     */
    public void progressChange(int progress, long oneCycleTime);
}
