package cz.vsb.cs.sur096.despr.window;

import cz.vsb.cs.sur096.despr.Despr;
import cz.vsb.cs.sur096.despr.structures.LocalizeMessages;
import java.awt.Color;
import java.awt.Font;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.logging.Level;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;


/**
 * Poskytne komponentu {@code JTextPane} do kterého je přesměrován jak standardní
 * tak chybový výstup. Chybový výstup je vypsán tučně červeným písmem. Dále 
 * poskytuje metody umožňující i přímí zápis přes které lze ovlivnit vzhled
 * vypisovaného textu.
 *
 * @author Martin Šurkovský, sur096,
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 * @version  2012/01/26/10:10
 */
public final class DesprOut {
    
    /**
     * Poskytne textovou komponentu, do které je přesměrován 
	 * jak standardní tak chybový výstup.
     * @return {@code JTextPane} do kterého je přesměrovány standardní a
	 * chybový výstup.
     */
    public static JTextPane getStdoutView() {
        return desprOutput.pGetStdout();
    }
    
    /**
     * Vepíše do komponenty text u kterého je možné ovlivnit font
	 * tloušťku a barvy popředí a pozadí.
     * @param text text který má být vepsán.
     * @param fontSize font jaký má být použit.
     * @param bold jedná se o tučný text?
     * @param fg barva písma.
     * @param bg barva pozadí textu.
     */
    public static void print(final String text, final int fontSize, 
            final boolean bold, final Color fg, final Color bg) {
        
        desprOutput.pPrint(text, fontSize, bold, fg, bg);
    }
    
    /**
     * Standardní tisk, je použita velikost písma 11, 
	 * standardní tloušťka písma s černým textem a bílím pozadí.
     * @param text text který má být vepsán.
     */
    public static void print(String text) {
        print(text, 11, false, Color.BLACK, Color.WHITE);
    }
    
    /**
     * Výpis chyby, je použita velikost písma 11, 
	 * text je tučný, barva písma červená s bílím pozadí.
     * @param text text který má být vepsán.
     */
    public static void printErr(String text) {
        print(text, 11, true, Color.RED, Color.WHITE);
    }
    
    /**
     * Výpis zvýrazněné informace. Velikost písma 14, 
	 * text je tučný, barva tmavší zelená, bílé pozadí.
     * @param text text který má být vepsán.
     */
    public static void printInfo(String text) {
        print(text, 14, true, new Color(0, 160, 0), Color.WHITE);
    }
    
    /**
     * Poskytne {@code JTextPane} do kterého je přesměrován standardní 
	 * výstup a chybový výstup
     * @return {@code JTextPane} do kterého je přesměrován standardní
	 * výstup a chybový výstup. do kterého je přesměrován standardní
	 * výstup a chybový výstup.
     */
    private JTextPane pGetStdout() {
        return stdout;
    }
    
    ////////////////////////////////////////////////////////////
    // Soukrome promene a metody
    
    private transient LocalizeMessages messages;
    private JTextPane stdout;
    
    private static DesprOut desprOutput;
    static {
        desprOutput = new DesprOut();
    }
    
    private DesprOut() {
        messages = Despr.loadLocalizeMessages(getClass(), null, false);
        stdout = new JTextPane();
        redirectSystemStreams();
    }
    
    private void pPrint(final String text, final int fontSize,
            final boolean bold, final Color fg, final Color bg) {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                StyledDocument doc = stdout.getStyledDocument();
                try {
                    Style style = doc.addStyle("StyleName", null);
                    StyleConstants.setFontFamily(style, Font.MONOSPACED);
                    StyleConstants.setFontSize(style, fontSize);
                    StyleConstants.setBold(style, bold);
                    StyleConstants.setForeground(style, fg);
                    StyleConstants.setBackground(style, bg);
                    doc.insertString(doc.getLength(), text, style);
                } catch (BadLocationException ex) {
                    String title = messages.getString("title.bad_location_excp",
                            "Bad location");
                    Despr.showError(title, ex, Level.WARNING, true);
                }
            }
        });
    }
    
    ////////////////////////////////////////////////////////////////////////////
    // redirect stdout

    private void redirectSystemStreams() {
        OutputStream out = new OutputStream() {
            @Override
            public void write(int b) throws IOException {
                print(String.valueOf((char) b));
            }

            @Override
            public void write(byte[] b, int off, int len) throws IOException {
                print(new String(b, off, len));
            }

            @Override
            public void write(byte[] b) throws IOException {
                write(b, 0, b.length);
            }
        };

        OutputStream err = new OutputStream() {
           
            @Override
            public void write(int b) throws IOException {
                printErr(String.valueOf( (char) b));
            }

            @Override
            public void write(byte[] b, int off, int len) throws IOException {
                printErr(new String( b, off, len));
            }

            @Override
            public void write(byte[] b) throws IOException {
                write(b, 0, b.length);
            }
        };

        System.setOut(new PrintStream(out, true));
        System.setErr(new PrintStream(err, true));
    }
}
