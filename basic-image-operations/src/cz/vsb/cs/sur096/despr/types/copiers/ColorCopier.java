
package cz.vsb.cs.sur096.despr.types.copiers;

import cz.vsb.cs.sur096.despr.types.Copier;
import java.awt.Color;

/**
 * Třída je schopná vytvořit kopii objektu {@code java.awt.Color}.
 *
 * @author Martin Šurkovský, sur096, 
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 * @version 2012/02/24/18:59
 */
public class ColorCopier implements Copier<Color> {

    @Override
    public Color makeCopy(Color t) {
        int r = t.getRed();
        int g = t.getGreen();
        int b = t.getBlue();
        int a = t.getAlpha();
        return new Color(r, g, b, a);
    }
}
