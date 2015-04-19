
package cz.vsb.cs.sur096.despr.view.operation;

import java.awt.Image;


/**
 * Slouží pro operace, které jako výstup poskytují nebo mohou poskytnout
 * grafickou reprezentaci toho co zpracovali. Typicky obrázky. 
 * U takto definovaných operací je pak možné v grafickém nadstavbě 
 * zobrazit náhled na výstup.
 * 
 * @author Martin Šurkovský, sur096,
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 * @version 2012/02/04/17:09
 */
public interface Displayable {
   
    /**
     * Poskytuje grafickou reprezentaci výstupu operace.
     * @return obrázek reprezentující výstupní parametr(y).
     */
    public Image getThumbnail();
}
