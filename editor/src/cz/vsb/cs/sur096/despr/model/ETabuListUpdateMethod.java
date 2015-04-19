
package cz.vsb.cs.sur096.despr.model;

/**
 * Definuje dva způsoby jakým lze aktualizovat {@code TabuList}.
 * 
 * @author Martin Šurkovský, sur096,
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 * @version 2012/02/27/16:52
 */
public enum ETabuListUpdateMethod {
    
    /** Seznam zakázaných operací má být přidán. */
    ADD, 
    /** Seznam zakázaných operací má být smazán. */
    REMOVE;
}
