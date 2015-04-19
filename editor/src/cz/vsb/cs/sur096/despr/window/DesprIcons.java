
package cz.vsb.cs.sur096.despr.window;

import cz.vsb.cs.sur096.despr.Despr;
import cz.vsb.cs.sur096.despr.structures.LocalizeMessages;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

/**
 * Stará se o načítání ikon pro aplikaci, menu, atp. Nejedná se o načítaní ikon
 * pro operace, které mohou vracet vizualizace pro prázdné ikony. Nebylo by to
 * ani vhodné proto je načítání ikon pro aplikaci odděleno od {@code DrawIcon}.
 * Navíc jsou obsaženy veřejné proměnné obsahující jména ikon.
 *
 * @author Martin Šurkovský, sur096,
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 */
public final class DesprIcons {
    
    /** Ikona aplikace. */
    public static final String APP_ICON = "despr_icon.png";
    /** Ikona pro vytvoření nového grafu. */
    public static final String NEW_GRAPH_ICON = "new_graph.png";

    /** Ikona pro načtení grafu. */
    public static final String LOAD_GRAPH_ICON = "load_graph.png";

    /** Ikona pro uložení grafu. */
    public static final String SAVE_GRAPH_ICON = "save_graph.png";

    /** Ikona pro ukončení aplikace. */
    public static final String QUIT_ICON = "quit.png";

    /** Ikona pro spuštění operace, grafu, čehokoliv kde je to vhodné. */
    public static final String PLAY_ICON = "play.png";
    
    /** Ikona pro oříznutí plátna. */
    public static final String CUT_ICON = "cut.png";
    
    /** Ikona pro tisk na standardní výstup. */
    public static final String PRINT_ICON = "print.png";

    /** 
	 * Ikona pro spuštění operace. Jedná se o velmi malou ikonku, která
	 * má nastavené sytější barvy aby nesplývala s okolím. Její jediné
	 * použití je na jednom z tlačítek operace.
	 */
    public static final String OPERATION_PLAY_ICON = "operation/play.png";

    /** Ikona pro stopnutí průběhu zpracování. */
    public static final String STOP_ICON = "stop.png";

    /** Ikona pro spuštění náhledu. */
    public static final String THUMBNAIL_ICON = "thumbnail.png";
    
    /**
     * Načte ikonu ze souboru.
     * @param icon jméno ikony.
     * @param small načíst malou verzi?
     * @return načtenou ikonu pokud takový soubor existuje.
     */
    public static ImageIcon getIcon(final String icon, boolean small) {
        return desprIcons.pGetIcon(icon, small);
    }
    
    public static Image getImage(final String icon, boolean small) {
        return desprIcons.pGetImage(icon, small);
    }
    
    ////////////////////////////////////////////////////////////
    // Soukrome parametry a metody
    
    private transient LocalizeMessages messages;
    private String basePath = "resources/icons";
    
    private static DesprIcons desprIcons;
    static {
        desprIcons = new DesprIcons();
    }
    
    private DesprIcons() {
        messages = Despr.loadLocalizeMessages(getClass(), null, false);
    }
    
    private ImageIcon pGetIcon(final String icon, boolean small) {
        
        return new ImageIcon(pGetImage(icon, small));
    }
    
    private Image pGetImage(final String icon, boolean small) {
        
        BufferedImage image;
        File file;
        
        if (small) {
            file = new File(basePath + File.separator + "small" + File.separator + icon);
        } else {
            file = new File(basePath + File.separator + icon);
        }
        
        try {
            image = ImageIO.read(file);
            return image;
        } catch (FileNotFoundException ex) {
            String title = messages.getString("title.file_not_found_excp",
                    "File not found");
            Despr.showError(title, ex, Level.WARNING, true);
        } catch (IOException ex) {
            String title = messages.getString("title.io_excp", "I/O problem");
            String message = String.format("%s '%s'", ex.getMessage(), file.getAbsolutePath());
            Despr.showError(title, new IOException(message, ex), Level.WARNING, true);
        }
        return null;
    }
}
