
package cz.vsb.cs.sur096.despr.model.operation.parameter;

/**
 * Slouží k rozlišení dvou možných typů vstupních parametrů. 
 * <ul>
 *   <li><b>Vnitřní - {@code INNER}</b> reprezentují konstanty 
 * nastavované uživatelem.</li>
 *   <li><b>Vnější - {@code OUTER}</b> reprezentují hodnoty, 
 * které jsou získaný jako výstup z jiné operace. Lze je chápat
 * jako dynamicky zjišťované hodnoty v průběhu výpočtu.</li>
 * </ul>
 * 
 * @author Martin Šurkovský, sur096,
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 * @version 2011/07/03/18:25
 */
public enum EInputParameterType {
    INNER,
    OUTER;
}
