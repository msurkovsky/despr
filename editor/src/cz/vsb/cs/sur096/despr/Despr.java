
package cz.vsb.cs.sur096.despr;

import cz.vsb.cs.sur096.despr.structures.LocalizeMessages;
import cz.vsb.cs.sur096.despr.view.GraphCanvas;
import cz.vsb.cs.sur096.despr.window.HeadWindow;
import cz.vsb.cs.sur096.despr.window.actions.CloseAction;
import cz.vsb.cs.sur096.despr.window.actions.SaveGraphAction;
import java.awt.Color;
import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.*;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JOptionPane;

/**
 * Poskytuje základní servis celé aplikaci. Uchovává a zpřístupňuje externí proměnné,
 * zajišťuje výpis a loggování chyb a stará se o načítání lokalizačních souborů ze
 * společného adresáře.
 *
 * @author Martin Surkovsky, sur096, 
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 * @version 2012/01/30/08:17
 */
public final class Despr {
    
    /** Konzolový mód aplikace.*/
    public static final int CONSOLE_MODE = 1;
    /** Grafický mód aplikace.*/
    public static final int GRAPHICS_MODE = 2;
    
    /** Barva která se používá pro označení vybraných komponent.*/
    public static final Color SELECT_OBJECT_COLOR = Color.CYAN;

    /** Jméno proměnné zpřístupňující jméno Look And Feel třídy. */
    public static final String STYLE            = "style.class.name";
    /** Jméno proměnné zpřístupňující nastavenou lokalizaci. */
    public static final String SET_LOCALE       = "set.locale";
    /** Jméno proměnné zpřístupňující seznam podporovaných lokalizací. */
    public static final String SUPPORT_LOCALES  = "supported.localizations";

    /** Jméno souboru kde jsou uloženy proměnné aplikace. */
    private static final String PROPERTIES_FILE = "resources/despr.properties";
    /** Jméno adresáře kde se nachází lokalizační soubory.*/
    private static final String LANG_RESOURCES  = "resources/lang";
    
    private static File lastOpenGraph;
    
    /**
     * Poskytne odkaz na hlavní okno aplikace.
     * @return odkaz na hlavní okno aplikace.
     */
    public static HeadWindow getHeadWindow() {
        return headWindow;
    }
    
    /**
     * Nastaví odkaz na hlavní okno aplikace.
     * @param headWindow odkaz na hlavní okno aplikace.
     */
    public static void setHeadWindow(HeadWindow headWindow) {
        Despr.headWindow = headWindow;
    }
    
    /**
     * Poskytne mód ve kterém je aplikace spuštěna.
     * @return 1 pro konzolový mód, 2 pro grafický.
     */
    public static int getMode() {
        return despr.pGetMode();
    }
    
    /**
     * Nastaví požadovaný mód použití.
     * @param mode mód který má být použit.
     */
    public static void setMode(int mode) {
        despr.pSetMode(mode);
    }
    
    /**
     * Poskytne objekt přináležící k danému klíčí.
     * @param key klíč k objektu.
     * @return objekt přináležící k danému klíču.
     */
    public static Object getProperty(String key) {
        return despr.pGetProperty(key);
    }
    
    /**
     * Nastaví hodnotu ke klíčí.
     * @param key klíč hodnoty.
     * @param value  nová hodnota.
     */
    public static void setProperty(String key, Object value) {
        despr.pSetProperty(key, value);
    }

    /**
     * Metoda načte lokalizační zprávy z adresáře {@code LANG_RESOURCES} podle
     * nastavené lokalizace a třídy.
     * @param cls třída pro kterou má být načten lokalizační soubor.
     * @param lm struktura pro lokalizační zprávy. Pokud je {@code null} pak
     * je vytvořena nová struktura.
     * @param recursive mají být načteny i lokalizační zprávy nadtříd.
     * @return seznam lokalizačních zpráv
     * @throws NullPointerException  pokud je {@code cls == null}.
     */
    public static LocalizeMessages loadLocalizeMessages(
            Class cls, LocalizeMessages lm, boolean recursive) 
            throws NullPointerException {
        
        return despr.pLoadLocalizeMessages(cls, lm, recursive);
    }
    
    /**
     * Zobrazí chybu zachycenou v aplikaci.
     * @param title titulek chyby.
     * @param throwable objekt který nese informace o chybě.
     * @param level úrověň závažnosti jsou akceptovány hodnoty 
	 * {@code Level.WARNING a Level.SEVERE} kdy v prvním případě je podána pouze
	 * informace o chybě v druhem případě spadne aplikace.
     * @param log má být chyba zalogována?
     */
    public static void showError(String title, Throwable throwable, Level level, boolean log) {
        despr.pShowError(title, throwable, level, log);
    }
    
    /**
     * Zobrazí informací v GUI vyskočí okno s informací v konzolovém režimu
	 * je předsazen text {@code [INFO]}.
     * @param title titulek informace.
     * @param msg zpráva.
     */
    public static void showInfo(String title, String msg) {
        despr.pShowInfo(title, msg);
    }
    
    public static void showConsoleInfo(String msg) {
        if (getMode() == Despr.CONSOLE_MODE) {
            System.out.println(msg);
        }
    }
    
    /**
     * Uloží nastavené proměnné do souboru.
     */
    public static void saveDesprProperties() {
        despr.pSaveDesprProperties();
    }
    
    /**
     * Pokud je log prázdný smaže soubor.
     */
    public static void removeEmptyLogFile() {
        despr.pRemoveEmptyLog();
    }
    
    public static File getLastOpenGraph() {
        return lastOpenGraph;
    }
    
    public static void setLastOpenGraph(File lastOpenGraph) {
        Despr.lastOpenGraph = lastOpenGraph;
    }

    ////////////////////////////////////////////////////////////////////////////
    
    private static final Despr despr;
    private static final Logger logger;
    static {
        logger = Logger.getLogger("cz.vsb.cs.sur096.despr");
        despr = new Despr();
    }
    private static HeadWindow headWindow;
    
    private final LocalizeMessages localize;
    private final HashMap<String, Object> desprAttributes;
    private final Properties desprProperties;
    private final String logFileName;
    
    private int mode;
    
    private Despr() {
        DateFormat form = new SimpleDateFormat("yyyyMMddHHmmss");
        logFileName = String.format("%s_despr.log", 
                form.format(new Date(System.currentTimeMillis())));
        localize = pLoadLocalizeMessages(getClass(), null, false);
        desprAttributes = new HashMap<String, Object>();
        desprProperties = new Properties();
        mode = GRAPHICS_MODE;

        pInitLogger(logger);
        pLoadProperties();
    }

    private void pLoadProperties() {
        File f = new File(PROPERTIES_FILE);
        try {
            FileInputStream fis = new FileInputStream(f);
            desprProperties.load(fis);
        } catch (FileNotFoundException ex) {
            String title = localize.getString("title.file_not_found_excp",
                    "File not found");
            pShowError(title, ex, Level.WARNING, true);
        } catch (IOException ex) {
            String title = localize.getString("title.io_excp", "I/O problem");
            String message = String.format("%s '%s'.", ex.getMessage(), f.getAbsolutePath());
            pShowError(title, new IOException(message, ex), Level.WARNING, true);
        }
        
        desprAttributes.put(STYLE, desprProperties.getProperty(STYLE));
        desprAttributes.put(SET_LOCALE, 
                pTextToLocale(desprProperties.getProperty(SET_LOCALE)));
        
        desprAttributes.put(SUPPORT_LOCALES, 
                pTextToLocales(desprProperties.getProperty(SUPPORT_LOCALES)));
    }
    
    private void pInitLogger(Logger logger) {
        try {
            
            // prvne jsou odstraneni defaultni nastaveni loggeru, 
            // kvluli odstraneni vypisu do konzole
            Handler[] handlers = logger.getHandlers();
            for (Handler handler : handlers) {
                logger.removeHandler(handler);
            }
            logger.setUseParentHandlers(false);
            
            File logDir = new File("log");
            if (!logDir.exists()) {
                logDir.mkdir();
            }
            
            // nastaveni vlastniho loggeru ktery zapisuje poze do souboru
            SimpleFormatter sf = new SimpleFormatter();
            FileHandler fh = new FileHandler(String.format("log/%s", logFileName));
            fh.setFormatter(sf);
            logger.addHandler(fh);
            logger.setLevel(Level.ALL);
        } catch (IOException ex) {
            String title = localize.getString("title.io_excp", "I/O problem");
            pShowError(title, ex, Level.WARNING, true);
        } catch (SecurityException ex) {
            String  title = localize.getString("title.security_excp", "Problem with security");
            pShowError(title, ex, Level.WARNING, true);
        }
    }
    
    private void pSetProperty(String key, Object value) {
        desprAttributes.put(key, value);
    }
    
    private Object pGetProperty(String key) {
        return desprAttributes.get(key);
    }
    
    private void pSaveDesprProperties() {
        File f = new File(PROPERTIES_FILE);
        try {
            FileOutputStream fos = new FileOutputStream(f);
            desprProperties.put(STYLE, desprAttributes.get(STYLE));
            desprProperties.put(SET_LOCALE , 
                    pLocaleToText((Locale) desprAttributes.get(SET_LOCALE)));
            desprProperties.store(fos, "");
        } catch (FileNotFoundException ex) {
            String title = localize.getString("title.file_not_found_excp",
                    "File not found");
            pShowError(title, ex, Level.WARNING, true);
        } catch (IOException ex) {
            String title = localize.getString("title.io_excp", "I/O problem");
            String message = String.format("%s '%s'.", ex.getMessage(), f.getAbsolutePath());
            pShowError(title, new IOException(message, ex), Level.WARNING, true);
        }
    }

    private int pGetMode() {
        return mode;
    }
    
    private void pSetMode(int mode) {
        if (mode == GRAPHICS_MODE || mode == CONSOLE_MODE) {
            this.mode = mode;
        } else {
            throw new IllegalArgumentException(
                    localize.getString("exception.illegal_mode") + " (" + mode + ")");
        }
    }
    
    private Locale pTextToLocale(String code) {
        StringTokenizer st = new StringTokenizer(code, "_");
        String lang = st.nextToken();
        String country = st.nextToken();
        return new Locale(lang, country);
    }
    
    private Locale[] pTextToLocales(String locales) {
        StringTokenizer st = new StringTokenizer(locales, ",");
        List<Locale> supportedLocales = new ArrayList<Locale>();
        while (st.hasMoreTokens()) {
            supportedLocales.add(pTextToLocale(st.nextToken()));
        }
        return supportedLocales.toArray(new Locale[0]);
    }
    
    private String pLocaleToText(Locale locale) {
        return String.format("%s_%s", locale.getLanguage(), locale.getCountry());
    }
    
    private LocalizeMessages pLoadLocalizeMessages(
            Class cls, LocalizeMessages lm, boolean recursive) 
            throws NullPointerException {
        
        if (lm == null) {
            lm = new LocalizeMessages();
        }
        
        if (cls != null) {
            if (Object.class.equals(cls)) {
                return lm;
            } else {
                ResourceBundle rb = null;
                try {
                    rb = pGetBundle(cls, Locale.getDefault());
                } catch (FileNotFoundException ex) {
                    if (!Locale.getDefault().equals(Locale.US)) {
                        try {
                            rb = ResourceBundle.getBundle(cls.getCanonicalName(),
                                    Locale.US);
                        } catch (MissingResourceException excp) {
                            pShowError(localize.getString("title.file_not_found_excp", 
                                                          "File not found"),
                                       ex, Level.WARNING, false);
                        }
                    } else {
                        pShowError(localize.getString("title.file_not_found_excp", 
                                                      "File not found"),
                                   ex, Level.WARNING, false);
                    }
                } catch (IOException ex) {
                    String title = localize.getString("title.io_excp", "I/O problem");
                    pShowError(title, ex, Level.WARNING, false);
                }
                
                if (rb != null) {
                    Set<String> keys = rb.keySet();
                    for (String key : keys) {
                        lm.putMessage(key, rb.getString(key));
                    }
                }
            }
            
            if (recursive) {
                return pLoadLocalizeMessages(cls.getSuperclass(), lm, recursive);
            } else {
                return lm;
            }
            
        } else {
            String message = localize.getString("exception.null_class", "Class is NULL");
            throw new NullPointerException(message);
        }
    }
    
    private ResourceBundle pGetBundle(Class cls, Locale loc) 
            throws FileNotFoundException, IOException {
        
        String langCode = String.format("%s_%s", loc.getLanguage(), loc.getCountry());
        
        File localizeFile = new File(
                String.format("%s%c%s%c%s_%s.properties",
                              LANG_RESOURCES,
                              File.separatorChar,
                              langCode,
                              File.separatorChar,
                              cls.getSimpleName(),
                              langCode));
        
        if (!localizeFile.exists()) {
            throw new FileNotFoundException(localizeFile.getAbsolutePath());
        }
        
        FileInputStream fis = new FileInputStream(localizeFile);
        return new PropertyResourceBundle(fis);
    }
    
    private void pShowInfo(String title, String msg) {
        if (mode == CONSOLE_MODE) {
            String info = localize.getString("mgs.info", "Info");
            System.out.println(info + ": " + msg);
        } else if (mode == GRAPHICS_MODE) {
            JOptionPane.showMessageDialog(headWindow, msg, title, JOptionPane.INFORMATION_MESSAGE);
        } else {
            throw new IllegalArgumentException(
                localize.getString("exception.illegal_mode") + " (" + mode + ")");
        }
    }
    
    private void pShowError(String title, Throwable throwable, Level level, boolean log) {
        
        if (mode == CONSOLE_MODE) {
            String info = localize.getString("msg.err");
            System.out.println(info + ": " + throwable.getMessage());
            System.exit(1);
        } else if (mode == GRAPHICS_MODE) {
            if (level.equals(Level.WARNING)) {
                JOptionPane.showMessageDialog(headWindow, throwable.getMessage(), title, JOptionPane.WARNING_MESSAGE);
            } else if (level.equals(Level.SEVERE)) {
                StringBuilder sb = new StringBuilder();
                sb.append(throwable.getMessage());
                sb.append("\n");
                sb.append(localize.getString("msg.kill_app"));
                GraphCanvas gCanvas = headWindow.getGraphCanvas();
                if (gCanvas != null) {
                    SaveGraphAction saveAction = new SaveGraphAction(headWindow, gCanvas);
                    saveAction.putValue(AbstractAction.DISPLAYED_MNEMONIC_INDEX_KEY, 0);
                    saveAction.putValue(AbstractAction.LARGE_ICON_KEY, null);
                    saveAction.putValue(AbstractAction.SMALL_ICON, null);
                    JButton save = new JButton(saveAction);
                    
                    CloseAction closeAction = new CloseAction(headWindow);
                    closeAction.putValue(AbstractAction.DISPLAYED_MNEMONIC_INDEX_KEY, 0);
                    JButton close = new JButton(closeAction);
                    
                    Object[] options = {save, close};
                    JOptionPane.showOptionDialog(headWindow, sb.toString(), title, 
                            JOptionPane.YES_OPTION, JOptionPane.ERROR_MESSAGE, null, 
                            options, save);

                    System.exit(1);
                }
            }
        } else {
            throw new IllegalArgumentException(
                    localize.getString("exception.illegal_mode") + " (" + mode + ")");
        }
        if (log) {
            pLogException(Level.WARNING, throwable.getMessage(), throwable);
        }
    }
    
    private void pLogException(Level level, String msg, Throwable throwable) {
        logger.fine("Start record *********************************************************");
        logger.log(level, msg, throwable);
        logger.fine("End record ***********************************************************\n");
    }
    
    private void pRemoveEmptyLog() {
        File f = new File("log" + File.separator + logFileName);
        long size = f.length();
        if (size == 0) {
            f.delete();
        }
    }
}
