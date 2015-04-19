
package cz.vsb.cs.sur096.despr.view.operationstree;

import cz.vsb.cs.sur096.despr.Despr;
import cz.vsb.cs.sur096.despr.events.MessageListener;
import cz.vsb.cs.sur096.despr.events.MessageSupport;
import cz.vsb.cs.sur096.despr.model.operation.IOperation;
import cz.vsb.cs.sur096.despr.model.operation.IRootOperation;
import cz.vsb.cs.sur096.despr.structures.LocalizeMessages;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.logging.Level;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;

/**
 * Renderer stromu operací.
 *
 * @author Martin Šurkovský, sur096 
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 * @version 2011/08/21/07:47
 */
public class OperationsTreeRenderer implements TreeCellRenderer {

    /** Podpora zasílání zpráv. */
    private MessageSupport messageSuppor;
    /** Renderer položky stromu. */
    private JPanel renderer;
    /** Label pro jméno položky. */
    private JLabel lblName;
    /** Odkaz na výchozí renderer stromu.*/
    private DefaultTreeCellRenderer defaultRenderer;
    /** Barva pozadí vybrané položky.*/
    private Color backgroundSelectionColor;
    /** Barva pozdaní nevybrané položky.*/
    private Color backgroundNonSelectionColor;
    /** Barva textu vybrané položky.*/
    private Color textSelectionColor;
    /** Barva textu nevybrané položky.*/
    private Color textNonSelectionColor;
    /** Seznam lokalizačních zpráv pro renderer.*/
    private transient LocalizeMessages messages;
    /** Lokalizační zprávy kategorií stromu.*/
    private transient ResourceBundle opTreeMessages;
    /** Cesta k lokalizačnímu zprávám kategorií.*/
    private transient String path;
    
    /**
     * Iniciuje renderer stromu.
     */
    public OperationsTreeRenderer() {
        messages = Despr.loadLocalizeMessages(getClass(), null, false);
        messageSuppor = new MessageSupport(this);
        
        defaultRenderer = new DefaultTreeCellRenderer();
        backgroundSelectionColor = defaultRenderer.getBackgroundSelectionColor();
        backgroundNonSelectionColor = defaultRenderer.getBackgroundNonSelectionColor();
        textSelectionColor = defaultRenderer.getTextSelectionColor();
        textNonSelectionColor = defaultRenderer.getTextNonSelectionColor();
        
        renderer = new JPanel(new FlowLayout(FlowLayout.CENTER, 2, 2));
        lblName = new JLabel();
        renderer.add(lblName);
        
        FileInputStream fis = null;
        try {
            String language = Locale.getDefault().getLanguage();
            String country = Locale.getDefault().getCountry();
            path = String.format("resources/lang/%s_%s/Operations_%s_%s.properties",
                    language, country, language, country);
            fis = new FileInputStream(path);
            opTreeMessages = new PropertyResourceBundle(fis);
        } catch (FileNotFoundException ex) {
            // do nothing, it will be use code name
            opTreeMessages = null;
        } catch (IOException ex) {
            // do nothing, it will be use code name
            opTreeMessages = null;
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
            } catch (IOException ex) {
                String title = messages.getString("title.io_excp", "I/O problem");
                Despr.showError(title, ex, Level.WARNING, true);
            }
        }
    }
    
    /**
     * Poskytne komponentu sloužící jako renderer položky stromu.
     * @param tree strom jímž je vykreslovaná položka členem.
     * @param value hodnota položky.
     * @param selected je vybrána?
     * @param expanded je kategorie rozbalena?
     * @param leaf jedná se o list stromu?
     * @param row index řádku.
     * @param hasFocus má focus?
     * @return panel s popiskem obsahujícím jméno položky (hodnotu).
     */
    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value,
            boolean selected, boolean expanded, boolean leaf, int row, 
            boolean hasFocus) {
        
        if (value instanceof Category) {
            Category cat = (Category) value;
            Object userObject = cat.getUserObject();
            if (userObject instanceof String) {
                String name; 
                if (opTreeMessages == null) {
                    name = (String) userObject;
                } else {
                    try {
                        name = opTreeMessages.getString((String) userObject);
                    } catch (MissingResourceException ex) {
                        name = (String) userObject;
                    }
                }
                return defaultRenderer.getTreeCellRendererComponent(tree, name, leaf, expanded, leaf, row, hasFocus);
            } else if (userObject instanceof IOperation) {
                IOperation op = (IOperation) userObject;
                String opName = op.getLocalizeMessage("name");
                if (opName == null) {
                    opName = op.getClass().getSimpleName();
                } else {
                    opName = opName.replaceAll("\\n", " ");
                }
                lblName.setText(opName);

                Font f = lblName.getFont();
                if (op instanceof IRootOperation) {
                    f = f.deriveFont(Font.BOLD);
                    lblName.setFont(f);
                } else {
                    f = f.deriveFont(Font.PLAIN);
                    lblName.setFont(f);
                }    
            }
        }
        
        if (selected) {
            renderer.setBackground(backgroundSelectionColor);
            lblName.setForeground(textSelectionColor);
        } else {
            renderer.setBackground(backgroundNonSelectionColor);
            lblName.setForeground(textNonSelectionColor);
        }
        
        return renderer;
    }

    /**
     * Umožňuje registrovat posluchače který reaguje na zaslané zprávy.
     * @param listener posluchač.
     */
    public void addMessageListener(MessageListener listener) {
        messageSuppor.addMessageListener(listener);
    }
    
    /**
     * Smaže posluchače reagujícího na zasílané zprávy.
     * @param listener posluchač.
     */
    public void removeMessageListener(MessageListener listener) {
        messageSuppor.removeMessageListener(listener);
    }
    
    /**
     * Znovu načte lokalizační soubor pro jména kategorií a dá o tom
	 * vědět registrovaným posluchačům.
     */
    public void reloadLocalizationMessages() {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(path);
            opTreeMessages = new PropertyResourceBundle(fis);
        } catch (FileNotFoundException ex) {
            opTreeMessages = null;
            String title = messages.getString("title.file_not_found_excp",
                    "File not found");
            Despr.showError(title, ex, Level.WARNING, true);
        } catch (IOException ex) {
            opTreeMessages = null;
            String title = messages.getString("title.io_excp", "I/O problem");
            Despr.showError(title, ex, Level.WARNING, true);
        } finally {
            try {
                fis.close();
            } catch (IOException ex) {
                String title = messages.getString("title.io_excp", "I/O problem");
                Despr.showError(title, ex, Level.WARNING, true);
            }
        }
        messageSuppor.sendMessage("localization_changed");
    }
}
