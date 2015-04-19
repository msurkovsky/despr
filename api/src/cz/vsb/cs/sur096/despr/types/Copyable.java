
package cz.vsb.cs.sur096.despr.types;

/**
 * Rozhraní pro uživatelem definované nové typy. Pokud je třeba nově definované
 * typy, které jsou použity jako výstupní parametry. Bylo možné použít více
 * než jednou je třeba definovat metodu která je schopná vytvořit kopii daného
 * objektu. Tzn. že vytvoří novou instanci daného objektu.
 * 
 * @author Martin Šurkovský, sur096,
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a> 
 * @version 2011/08/21/16:01
 * @see cz.vsb.cs.sur096.despr.types.images.ColorImage
 */
public interface Copyable<T> {

    /**
     * Vytvoří hlubokou kopii sama sebe.
     * @return novou instanci na daný objekt.
     */
    public T copy();
}
