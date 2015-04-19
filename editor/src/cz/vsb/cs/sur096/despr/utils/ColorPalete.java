
package cz.vsb.cs.sur096.despr.utils;

import cz.vsb.cs.sur096.despr.Despr;
import cz.vsb.cs.sur096.despr.structures.LocalizeMessages;
import java.awt.Color;
import java.io.*;
import java.util.Date;
import java.util.Properties;
import java.util.Random;
import java.util.Set;
import java.util.logging.Level;

/**
 * Nástroj pro generování barev typům. Má k dispozici cca. paletu 20 barev,
 * které jsou vzájemně relativně kontrastní a ty využívá při počátečním
 * barvení. Pokud je celá paleta použita jsou další barvy generovány 
 * náhodně. Nic méně počáteční paleta je dostatečně velká aby 
 * pokryla značné množství typů.
 *
 * @author Martin Surkovsky, sur096 <martin.surkovsky at gmail.com>
 * @version 2011/11/13/17:19
 */
public class ColorPalete {
    
    /** Seznam lokalizačních zpráv. */
    private transient LocalizeMessages messages;

	/** Zdrojové soubory. */
    private final String COLOR_FILE = "resources/ColorPalete.properties";
    private final String SAVED_COLOR_FILE = "resources/SavedColors.properties";
    
    private static final int USED = 1;
    
    /** Výchozí barva pro {@code java.lang.Object}. */
    private static final String OBJECT_COLOR = "FFFFFF";

    /** Zdroje. */
    private Properties colorPalete, savedColors;
    
    /** Odkaz na inicializovanou paletu.*/
    private static ColorPalete cp = new ColorPalete();
    
    /**
     * Vygeneruje pro daný typ barvu.
     * @param type typ pro který má být barva vygenerována.
     * @return barvu pro daný typ.
     */
    public static Color getColor(Class type) {
		if (type.equals(Object.class)) return Color.WHITE;
        return cp.pGetColor(type);
    }
    
    /**
     * Poskytne barvu pro psaní na zadanou barvu.
     * @param color barva která má tvořit pozadí.
     * @return na základě barevného kontrastu zvolí
	 * bílou nebo černou. Pokud je barevný kontrast
	 * menší než 128 vrátí bílou, pokud je větší pak černou.
     */
    public static Color getTextColor(String color) {
        return getTextColor(Color.decode("#" + color));
    }
    
    /**
     * 
     * Poskytne barvu pro psaní na zadanou barvu.
     * @param color barva která má tvořit pozadí.
     * @return na základě barevného kontrastu zvolí
	 * bílou nebo černou. Pokud je barevný kontrast
	 * menší než 128 vrátí bílou, pokud je větší pak černou.
     */
    public static Color getTextColor(Color color) {
        Color white = Color.WHITE;
        Color black = Color.BLACK;
        
        int cRed   = color.getRed();
        int cGreen = color.getGreen();
        int cBlue  = color.getBlue();
        
        int wRed = 0, wGreen = 0, wBlue = 0;
        wRed = wGreen = wBlue = white.getRed();
        
        int ratioCW = 299 * cRed + 587 * cGreen + 114 * cBlue;
        
        if (ratioCW < 128000) {
            return white;
        } else {
            return black;
        }
    }
    
    /**
     * Nastaví použitelnost typu vůči barvě.
     * @param type typ který má být aktualizován.
     * @param color jeho barva.
     * @param used má být barva použita pro daný typ?
     */
    public static void setUsed(Class type, Color color, boolean used) {
        if (type != null && color != null) {
            String c = Integer.toHexString(color.getRGB()).substring(2).toUpperCase();
            cp.setUsed(type.getCanonicalName(), c, used);
        }
    }
    
    /**
     * Zjistí zda je barva volná k dispozici.
     * @param color barva která má být otestována.
     * @return {@code true} pokud je barva k dispozici, jinak {@code false}.
     */
    public static boolean isColorUsed(Color color) {
        String c = Integer.toHexString(color.getRGB()).substring(2).toUpperCase();
        return cp.isUsed(c);
    }
    
    /** Zabrání vytvoření instance. A načte z externích zdrojů data*/
    private ColorPalete() {
        messages = Despr.loadLocalizeMessages(getClass(), null, false);
        File f1 = new File(COLOR_FILE);
        checkFile(f1);
        File f2 = new File(SAVED_COLOR_FILE);
        checkFile(f2);
        
        colorPalete = new Properties();
        savedColors = new Properties();
        try {
            colorPalete.load(new FileInputStream(f1));
            savedColors.load(new FileInputStream(f2));
        } catch (FileNotFoundException ex) {
            Despr.showError("title.loading_problem", ex, Level.WARNING, false);
        } catch (IOException ex) {
            Despr.showError("title.loading_problem", ex, Level.WARNING, false);
        }
    }
    
    ////////////////////////////////////////////////////////////
    // Soukrome metody
    
    private Color pGetColor(Class type) {
        
        String typeName = type.getCanonicalName();
        String savedColor = savedColors.getProperty(typeName);
        
        if (savedColor != null) {
            return Color.decode("#" + savedColor);
        }
        
        Set colors = colorPalete.keySet();
        for (Object color : colors) {
            String c = (String) color;
            if (!isUsed(c)) {
                setUsed(typeName, c, true);
                return Color.decode("#" + c);
            }
        }
        
        Random rnd = new Random(new Date().getTime());
        int r = Math.abs(rnd.nextInt() % 255);
        int g = Math.abs(rnd.nextInt() % 255);
        int b = Math.abs(rnd.nextInt() % 255);
        Color c = new Color(r, g, b);
        
        String cAsText = Integer.toHexString(c.getRGB()).substring(2).toUpperCase();
//        colorPalete.put(cAsText, Integer.toString(USED));
        setUsed(typeName, cAsText, true);
        return new Color(r, g, b);       
    }
    
    private void setUsed(String type, String color, boolean used) {
        if (color.equals(OBJECT_COLOR)) { // default color is skipped
            return;
        }
        
        int n = used ? 1 : 0;
        colorPalete.setProperty(color, Integer.toString(n));
        
        if (used) {
            savedColors.put(type, color);
        } else {
            savedColors.remove(type);
            // je treba smazat i vsechny ulozene typy se stejnou barvou
            Object[] keys = savedColors.keySet().toArray();
            int size = keys.length;
            for (int i = 0; i < size; i++) {
                String key = keys[i].toString();
                if (savedColors.getProperty(key).equals(color)) {
                    savedColors.remove(key);
                }
            }
        }
        
        // store
        try {
            colorPalete.store(new FileOutputStream(COLOR_FILE), "");
            savedColors.store(new FileOutputStream(SAVED_COLOR_FILE), "");
        } catch (FileNotFoundException ex) {
            Despr.showError("title.saving_problem", ex, Level.WARNING, false);
        } catch (IOException ex) {
            Despr.showError("title.saving_problem", ex, Level.WARNING, false);
        }
    }
    
    private boolean isUsed(String color) {
        String number = colorPalete.getProperty(color);
        if (number == null) return false;
        int n = Integer.parseInt(number);
        if (n == USED) {
            return true;
        } else {
            return false;
        }
    }
    
    private boolean checkFile(File f) {
        if (!f.exists()) {
            try {
                f.createNewFile();
                return true;
            } catch (IOException ex) {
                String message = String.format(
                        messages.getString("exception_cant_create_file", 
                                           "It can't create file '%s'."
                                         + "\nPlease can you create this file?"), 
                        f.getAbsolutePath());
                Despr.showError("title.can_not_create_file", new IOException(message, ex), 
                        Level.SEVERE, false);
                return false;
            }
        } else {
            return true;
        }
    }
}
