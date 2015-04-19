
package cz.vsb.cs.sur096.despr.view.portvizualization;

/**
 * Definice posluchače zajímajícího se o změnu vizuálních
 * informací o portu ({@code PVI = Port Visual Information})
 *
 * @author Martin Šurkovský, sur096 
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 * @version 2012/01/18/20:11
 */
public interface PVIChangeListener {
    
    /**
     * Informuje o změně vizuálních informací k portu.
     */
    public void pviChanged();
}
