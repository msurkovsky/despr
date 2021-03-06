package cz.vsb.cs.sur096.despr.types.copiers;

import cz.vsb.cs.sur096.despr.types.Copier;
import java.io.File;

/**
 * Třída poskytující metodu, která je schopná vytvořit kopii objektu typu:
 * {@code java.lang.File}.
 * 
 * @author Martin Šurkovský, sur096, 
 * <a href='mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 * @version 2012/02/23/17:36
 */
public class FileCopier implements Copier<File> {

    /**
     * Vytvoří nový objekt se stejnou hodnotou jako měl parametr {@code t}.
     * @param t hodnota která má být zkopírována.
     * @return nový objekt.
     */
    @Override
    public File makeCopy(File t) {
        String path = t.getPath();
        return new File(path);
    }
}
