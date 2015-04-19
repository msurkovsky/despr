package cz.vsb.cs.sur096.despr.types.copiers;

import cz.vsb.cs.sur096.despr.types.Copier;

/**
 * Třída poskytující metodu, která je schopná vytvořit kopii objektu typu: 
 * {@code java.lang.Long}.
 * 
 * @author Martin Šurkovský, sur096, 
 * <a href='mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 * @version 2012/02/23/17:33
 */
public class LongCopier implements Copier<Long> {

    /**
     * Vytvoří nový objekt se stejnou hodnotou jako má parametr {@code t}.
     * @param t hodnota jejichž kopie se má vytvořit.
     * @return nový objekt.
     */
    @Override
    public Long makeCopy(Long t) {
        long l = t.longValue();
        return new Long(l);
    }
}
