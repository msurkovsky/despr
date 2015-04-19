
package cz.vsb.cs.sur096.despr.gui.components;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.util.*;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;

/**
 * Grafická komponenta sloužící pro výběr souboru ze disku. Obsahuje tlačítko,
 * které vyvolá souborového průzkumníka a pole s adresou načteného souboru.
 * Komponenta poskytuje odkaz na poslední otevřený soubor {@code lastOpenFile}, 
 * přes rozhraní {@code java.beans.PropertyChangeListener}.
 * 
 * @author Martin Šurkovsky, sur096,
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 * @version 2012/02/14/20:50
 */
public class FilesSelector extends JPanel {
    
    private PropertyChangeSupport pcs;
    
    private final JButton btnSearch;
    private final JTextField txtAddress;
    
    private List<FileFilter> filesFilters;
    private File lastOpenFile;
    private int selectionMode;
    
    private transient ResourceBundle messages;
    
    /**
     * Konstruktor inicializuje komponentu.
     */
    public FilesSelector() {
        filesFilters = new ArrayList<FileFilter>();
        
        try {
            messages = ResourceBundle.getBundle(getClass().getName());
        } catch (MissingResourceException ex) {
            // switch to english resources
            messages = ResourceBundle.getBundle(getClass().getName(), Locale.US);
        }
        
        setLayout(new GridBagLayout());
        selectionMode = JFileChooser.FILES_AND_DIRECTORIES;
        String btnSearchName;
        try {
            btnSearchName = messages.getString("button.browse");
        } catch (MissingResourceException ex) { 
            btnSearchName = "Browse ...";
        }
        btnSearch = new JButton(btnSearchName);
        txtAddress = new JTextField();
        init();
    }
    
    private void init() {
        pcs = new PropertyChangeSupport(this);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.LINE_START;
        gbc.weightx = 0.0;
        gbc.weighty = 1.0;
        gbc.gridx = 0;
        gbc.gridy = 0;
        
        btnSearch.addActionListener(new SearchAction());
        add(btnSearch, gbc);
        
        gbc.weightx = 1.0;
        gbc.gridx = 1;
        txtAddress.setEditable(false);
        txtAddress.addMouseListener(null);
        add(txtAddress, gbc);
    }
    
    /**
     * Nastaví filtr pro soubory.
     * @param filter 
     */
    public void addFileFilter(FileFilter filter) {
        filesFilters.add(filter);
    }
    
    /**
     * Poskytne nastavení výběrového módu.
     * @return nastavení výběrového módu.
     */
    public int getSelectionMode() {
        return selectionMode;
    }
    
    /**
     * Nastaví výběrový mód.
     * @param selectionMode  výběrový mód.
     */
    public void setSelectionMode(int selectionMode) {
        this.selectionMode = selectionMode;
    }
    
    /**
     * Nastaví soubor.
     * @param f sobour.
     */
    public void setFile(File f) {
        lastOpenFile = f;
        if (f == null) {
            txtAddress.setText("");    
        } else {
            txtAddress.setText(f.getAbsolutePath());    
        }
    }
    
    /**
     * Poskytne poslední načtený soubor.
     * @return poslední načtený soubor.
     */
    public File getLastOpenFile() {
        return lastOpenFile;
    }
    
    /**
     * Nastaví editovatelnost vybraného souboru.
     * @param editable může být poslední nastavený soubor editován?
     */
    public void setEditable(boolean editable) {
        btnSearch.setEnabled(editable);
    }
    
    /**
     * Povolí, čí zakáže komponentu.
     * @param enabled povolit komponentu.
     */
    @Override
    public void setEnabled(boolean enabled) {
        btnSearch.setEnabled(enabled);
        txtAddress.setEnabled(enabled);
    }
    
    /**
     * Přidá posluchače na změnu načteného souboru.
     * @param l posluchač.
     */
    @Override
    public void addPropertyChangeListener(PropertyChangeListener l) {
        if (pcs == null) {
            pcs = new PropertyChangeSupport(this);
        }
        pcs.addPropertyChangeListener(l);
    }
    
    /**
     * Vymaže posluchače na změnu posledního načteného souboru.
     * @param l 
     */
    @Override
    public void removePropertyChangeListener(PropertyChangeListener l) {
        if (pcs != null) {
            pcs.removePropertyChangeListener(l);
        }
    }
    
    /**
     * Vnitřní třída představující akci tlačítka komponenty.
     */
    protected class SearchAction implements ActionListener {
        File currentDirectory;

        @Override
        public void actionPerformed(ActionEvent e) {
            if (currentDirectory == null) {
                if (lastOpenFile == null) {
                    currentDirectory = new File(System.getProperty("user.home"));
                } else if (lastOpenFile.isDirectory()) {
                    currentDirectory = lastOpenFile;
                } else {
                    currentDirectory = lastOpenFile.getParentFile();
                }
            }
            
            JFileChooser fc = new JFileChooser(currentDirectory);
            fc.setDialogType(JFileChooser.OPEN_DIALOG);
            if (!filesFilters.isEmpty()) {
                for (FileFilter fileFilter : filesFilters) {
                    fc.addChoosableFileFilter(fileFilter);
                }
                fc.setAcceptAllFileFilterUsed(false);
            } 
            fc.setFileSelectionMode(selectionMode);

            if (fc.showOpenDialog(fc) == JFileChooser.APPROVE_OPTION) {
                File oldLastOpenFile = null;
                if (lastOpenFile != null) {
                    oldLastOpenFile = new File(lastOpenFile.getAbsolutePath());
                }
                
                lastOpenFile = fc.getSelectedFile().getAbsoluteFile();
                txtAddress.setText(lastOpenFile.getAbsolutePath());
                currentDirectory = lastOpenFile.isDirectory() ? lastOpenFile : lastOpenFile.getParentFile();
                pcs.firePropertyChange("lastOpenFile", oldLastOpenFile, lastOpenFile);
            }
        }
    }
}
