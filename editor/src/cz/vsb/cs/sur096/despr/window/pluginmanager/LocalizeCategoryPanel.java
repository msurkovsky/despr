package cz.vsb.cs.sur096.despr.window.pluginmanager;

import cz.vsb.cs.sur096.despr.Despr;
import cz.vsb.cs.sur096.despr.structures.LocalizeMessages;
import cz.vsb.cs.sur096.despr.view.operationstree.OperationsTreeRenderer;
import cz.vsb.cs.sur096.despr.window.MnemonicGenerator;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.List;
import java.util.*;
import java.util.logging.Level;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/**
 * Panel obsahující tabulku sloužící pro lokalizaci jmen kategorií ve
 * stromu operací.
 *
 * @author Martin Šurkovský, sur096,
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 * @version 2012/02/17/09:06
 */
public class LocalizeCategoryPanel extends JDialog {
    
    private transient LocalizeMessages localizeMessages;
    private Map<Locale, Properties> messages;
    private Map<Locale, Integer> columnIndices;
    private Map<Locale, File> localizationFiles;
    private OperationsTreeRenderer renderer;
    private String key;
    
    /**
     * Iniciuje panel s tabulkou pro lokalizaci.
     * @param key jméno kategorie ve stromu operací pro které má být
	 * definována lokalizace.
     * @param renderer renderer stromu operací.
     */
    public LocalizeCategoryPanel(String key, OperationsTreeRenderer renderer) {
        localizeMessages = Despr.loadLocalizeMessages(getClass(), null, false);
        setLayout(new GridBagLayout());
        this.key = key;
        this.renderer = renderer;
        setTitle(localizeMessages.getString("title", "Set localization message"));
        setModal(true);
        init();
    }
    
    /**
     * Inicializuje panel.
     */
    private void init() {
        
        GridBagConstraints  gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.FIRST_LINE_START;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.weightx = 0.0;
        gbc.weighty = 0.0;
        
        JLabel lblTitle = new JLabel(localizeMessages.getString(
                "title.messages_for","Messages for key:"));
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(lblTitle, gbc);
        
        JLabel lblKey = new JLabel(key);
        Font f = lblKey.getFont().deriveFont(Font.BOLD);
        lblKey.setFont(f);
        gbc.gridx = 1;
        gbc.gridy = 0;
        add(lblKey, gbc);
        
        
        DefaultTableModel model = createTable(
                (Locale[]) Despr.getProperty(Despr.SUPPORT_LOCALES));
        JTable table = new JTable(model);
        table.setPreferredScrollableViewportSize(new Dimension(500, 50));
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.gridwidth = 4;
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(new JScrollPane(table), gbc);
        
        List<Character> tabuMnemonics = new ArrayList<Character>();
        
        JPanel btnPanel = new JPanel();
        String btnCancelTitle = localizeMessages.getString("button.title.cancel", "Cancel");
        Character btnCancelMnemonic = MnemonicGenerator.getMnemonicChar(btnCancelTitle, tabuMnemonics);
        JButton btnCancel = new JButton(btnCancelTitle);
        btnCancel.addActionListener(new CancelAction());
        if (btnCancelMnemonic != null) {
            btnCancel.setMnemonic(btnCancelMnemonic);
        }
        btnPanel.add(btnCancel);
        
        String btnSaveTitle = localizeMessages.getString("button.title.save", "Save");
        Character btnSaveMnemonic = MnemonicGenerator.getMnemonicChar(btnSaveTitle, tabuMnemonics);
        JButton btnSave = new JButton(btnSaveTitle);
        btnSave.addActionListener(new SaveAction(model));
        if (btnSaveMnemonic != null) {
            btnSave.setMnemonic(btnSaveMnemonic);
        }
        btnPanel.add(btnSave);
        
        gbc.anchor = GridBagConstraints.LINE_END;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0.0;
        gbc.insets.top = 0;
        gbc.gridx = 0;
        gbc.gridy = 2;
        add(btnPanel, gbc);
    }
    
    /**
     * Vytvoří model tabulky pro definované lokalizace. 
	 * Tabulka obsahuje jeden řádek a N sloupců podle toho
	 * kolik je definováno lokalizací, pro každou jeden.
     * @param loc seznam definovaných lokalizací.
     * @return model tabulky.
     */
    private DefaultTableModel createTable(Locale... loc) {
        int locLength = loc.length;
        File file;
        messages          = new HashMap<Locale, Properties>(locLength);
        columnIndices     = new HashMap<Locale, Integer>(locLength);
        localizationFiles = new HashMap<Locale, File>(locLength);
        String[] row = new String[locLength];
        
        String[] languages = new String[locLength];
        for (int i = 0; i < locLength; i++) {
            languages[i] = loc[i].getDisplayLanguage();
            file = new File(
                    String.format("resources/lang/%s_%s/Operations_%s_%s.properties", 
                    loc[i].getLanguage(), loc[i].getCountry(), 
                    loc[i].getLanguage(), loc[i].getCountry()));
            
            if (!file.exists()) {
                if (!file.getParentFile().exists()) {
                    file.getParentFile().mkdir();
                }
                try {
                    file.createNewFile();
                } catch (IOException ex) {
                    String title = localizeMessages.getString("title.io_excp", "I/O problem");
                    Despr.showError(title, ex, Level.WARNING, true);
                }
            }
            localizationFiles.put(loc[i], file);
            Properties prop = new Properties();

            try {
                FileInputStream fis = new FileInputStream(file);
                prop.load(fis);
            } catch (FileNotFoundException ex) {
                String title = localizeMessages.getString("title.file_not_found",
                        "File not found");
                Despr.showError(title, ex, Level.WARNING, true);
            } catch (IOException ex) {
                String title = localizeMessages.getString("title.io_excp", "I/O problem");
                String message = String.format("%s '%s'", ex.getMessage(), 
                        file.getAbsolutePath());
                Despr.showError(title, new IOException(message, ex), Level.WARNING, true);
            }
            
            messages.put(loc[i], prop);
            columnIndices.put(loc[i], i);

            String msg = prop.getProperty(key);
            if (msg == null) {
                prop.put(key, key);
                FileOutputStream fos;
                try {
                    fos = new FileOutputStream(file);
                    prop.store(fos, "");
                } catch (FileNotFoundException ex) {
                    String title = localizeMessages.getString("title.file_not_found",
                            "File not found");
                    Despr.showError(title, ex, Level.WARNING, true);
                } catch (IOException ex) {
                    String title = localizeMessages.getString("title.io_excp", "I/O problem");
                    String message = String.format("%s '%s'", ex.getMessage(), 
                            file.getAbsolutePath());
                    Despr.showError(title, new IOException(message, ex), Level.WARNING, true);
                }
                msg = key;
            }
            row[i] = msg;
        }

        DefaultTableModel model = new DefaultTableModel(languages, 0);
        model.addRow(row);
        return model;
    }

    /**
     * Definice akce pro uložení změn v lokalizačním souboru.
     */
    private class SaveAction implements ActionListener {

        private DefaultTableModel model;
        
        public SaveAction(DefaultTableModel model) {
            this.model = model;
        }
        
        @Override
        public void actionPerformed(ActionEvent e) {
            Set<Locale> keys = messages.keySet();
            for (Locale loc : keys) {
                String value = (String) model.getValueAt(0, columnIndices.get(loc));
                if (value == null) {
                    value = key;
                }
                Properties prop = messages.get(loc);
                prop.put(key, value);
                try {
                    FileOutputStream fos = new FileOutputStream(localizationFiles.get(loc));
                    prop.store(fos, "");
                } catch (FileNotFoundException ex) {
                    String title = localizeMessages.getString("title.file_not_found",
                            "File not found");
                    Despr.showError(title, ex, Level.WARNING, true);
                } catch (IOException ex) {
                    String title = localizeMessages.getString("title.io_excp", "I/O problem");
                    String message = String.format("%s '%s'", ex.getMessage(), 
                            localizationFiles.get(loc).getAbsolutePath());
                    Despr.showError(title, new IOException(message, ex), Level.WARNING, true);
                }
            }
            renderer.reloadLocalizationMessages();
            dispose();
        }
    }
    
    /**
     * Definice akce pro zavření panelu bez uložení.
     */
    private class CancelAction implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            dispose();
        }
    }
}
