package cz.vsb.cs.sur096.despr.types.copiers;

import cz.vsb.cs.sur096.despr.types.Copier;

/**
 * Třída poskytující metodu, která je schopná vytvořit kopii objektu typu:
 * {@code java.lang.Boolean}.
 * 
 * @author Martin Šurkovský, sur096, 
 * <a href='mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 * @version 2012/02/23/18:47
 */
public class BooleanCopier implements Copier<Boolean> {

    /**
     * Vytvoří nový objekt se stejnou hodnotou jako měl parametr {@code t}.
     * @param t hodnota která má být zkopírována.
     * @return nový objekt.
     */
    @Override
    public Boolean makeCopy(Boolean t) {
        boolean b = t.booleanValue();
        return b ? Boolean.TRUE : Boolean.FALSE;
    }
}
