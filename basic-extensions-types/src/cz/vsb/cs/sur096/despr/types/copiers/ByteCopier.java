package cz.vsb.cs.sur096.despr.types.copiers;

import cz.vsb.cs.sur096.despr.types.Copier;

/**
 * Třída poskytující metodu, která je schopná vytvořit kopii objektu typu: 
 * {@code java.lang.Byte}.
 * 
 * @author Martin Šurkovský, sur096, 
 * <a href='mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 * @version 2012/02/23/17:26
 */
public class ByteCopier implements Copier<Byte> {

    /**
     * Vytvoří nový objekt se stejnou hodnotou jako měl parametr {@code t}.
     * @param t hodnota která má být zkopírována.
     * @return nový objekt.
     */
    @Override
    public Byte makeCopy(Byte t) {
        byte b = t.byteValue();
        return new Byte(b);
    }
}
