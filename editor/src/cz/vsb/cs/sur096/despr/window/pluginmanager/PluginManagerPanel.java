
package cz.vsb.cs.sur096.despr.window.pluginmanager;

import cz.vsb.cs.sur096.despr.Despr;
import cz.vsb.cs.sur096.despr.DesprClassLoader;
import cz.vsb.cs.sur096.despr.events.MessageEvent;
import cz.vsb.cs.sur096.despr.events.MessageListener;
import cz.vsb.cs.sur096.despr.gui.components.FilesSelector;
import cz.vsb.cs.sur096.despr.structures.LocalizeMessages;
import cz.vsb.cs.sur096.despr.utils.DrawIcon;
import cz.vsb.cs.sur096.despr.utils.ExtensionsOfTypes;
import cz.vsb.cs.sur096.despr.view.operationstree.OperationsTree;
import cz.vsb.cs.sur096.despr.view.operationstree.OperationsTreeRenderer;
import cz.vsb.cs.sur096.despr.view.operationstree.OperationsWriter;
import cz.vsb.cs.sur096.despr.window.MnemonicGenerator;
import cz.vsb.cs.sur096.despr.window.actions.ImportPackageAction;
import cz.vsb.cs.sur096.despr.window.actions.SaveOperationsChangesAction;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;

/**
 * Plug-in manager, který umožňuje přidávat do aplikace nové funkce a typová
 * rozšíření.
 *
 * @author Martin Šurkovský, sur096,
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 * @version 2012/02/12/17:30
 */
public class PluginManagerPanel extends JDialog implements MessageListener {

    /** Jméno složky obsahující balíky s rozšířeními.*/
    public static final String EXTENSIONS_DIR = "extensions";
    
    private transient LocalizeMessages messages;
    
    private ImportPackageAction ipAction;
    private DefaultListModel pluginsListModel;
    private OperationsTree operationsTree;
    private DefaultListModel unusedOperationsListModel;
    private ExtensionsOfTypes typesListModel;
    private AvailableTypesListModel availableTypesListModel;
    
    private boolean pluginsChanged, operationsChanged, typesChanged;
    
    /**
     * Iniciuje plug-in manager.
     * @param owner hlavní okno aplikace.
     * @param operationsTree  odkaz na strom s operacemi.
     */
    public PluginManagerPanel(Window owner, OperationsTree operationsTree) {
        super(owner, ModalityType.MODELESS);
        messages = Despr.loadLocalizeMessages(getClass(), null, false);
        setLayout(new BorderLayout(5, 5));
        setPreferredSize(new Dimension(800,600));
        this.operationsTree = operationsTree;
        init();

        Rectangle bounds = owner.getBounds();
        setLocation(bounds.x + (bounds.width - 800) / 2,
                bounds.y + (bounds.height - 600) / 2);
    }

    /**
     * Reaguje na zasílané zprávy o změnách v rozšíření aplikace.
     * @param event událost.
     */
    @Override
    public void catchMessage(MessageEvent event) {
        String message = event.getMessage();
        if (message.equals("plugins_has_changed")) {
            pluginsChanged = true;
        } else if (message.equals("operations_has_changed")) {
            operationsChanged = true;
        } else if (message.equals("types_has_changed")) {
            typesChanged = true;
        } else if (message.equals("operations_has_saved")) {
            operationsChanged = false;
        } else if (message.equals("types_has_saved")) {
            typesChanged = false;
        }
    }
    
    ////////////////////////////////////////////////////////////
    // Soukrome pomocne metody
    
    private void init() {
        pluginsChanged = false;
        operationsChanged = false;
        typesChanged = false;
        
        addWindowListener(new CloseWindowAction());
        
        unusedOperationsListModel = new DefaultListModel();
        UnusedOperationsReader.readUnusedOperations(unusedOperationsListModel);
        pluginsListModel = new DefaultListModel();
        availableTypesListModel = new AvailableTypesListModel();
        typesListModel = ExtensionsOfTypes.getData();

        // head frame
        JPanel headPanel = new JPanel(new BorderLayout());

        // add plug-in pane
        JPanel pluginPane = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.LINE_START;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.weighty = 0.0;
        gbc.gridx = 0;
        gbc.gridy = 0;
        pluginPane.setBorder(BorderFactory.createTitledBorder(
                messages.getString("title.add_plugin", "Add plug-in")));
        FilesSelector filesSelector = new FilesSelector();
        filesSelector.setSelectionMode(JFileChooser.FILES_ONLY);
        filesSelector.addFileFilter(new JarFileFilter());
        pluginPane.add(filesSelector, gbc);
        
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets.left = 5;
        gbc.weightx = 0.0;
        gbc.gridx = 1;
        gbc.gridy = 0;
        JButton btnImportPackage = new JButton();
        pluginPane.add(btnImportPackage, gbc);
        
        headPanel.add(pluginPane, BorderLayout.NORTH);
        
        // tab panel
        JTabbedPane tabbedPane = new JTabbedPane();
        
        // tab - plugins pagckage
        PluginsList pluginsList = new PluginsList(unusedOperationsListModel, typesListModel,
                availableTypesListModel);
        pluginsList.addMessageListener(this);
        File f = new File(EXTENSIONS_DIR);
        if (f.exists() && f.isDirectory()) {
            File[] jarPackages = f.listFiles(new JarFileFilter());
            for (File jar : jarPackages) {
                pluginsListModel.addElement(jar);
            }
        }
        pluginsList.setModel(pluginsListModel);
        
        tabbedPane.add(
                messages.getString("tab.title.list_of_plugins", "List of plug-ins"), 
                new JScrollPane(pluginsList));
        
        // tab - setting operations
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(250);
        splitPane.setContinuousLayout(true);

        EditableOperationsTree opTree = new EditableOperationsTree(operationsTree.getModel());
        opTree.addMessageListener(this);
        opTree.setCellRenderer((OperationsTreeRenderer) operationsTree.getCellRenderer());
        JPanel treePanel = new JPanel(new GridBagLayout());
        treePanel.setBorder(BorderFactory.createTitledBorder(
                messages.getString("title.tree_editor", "Tree editor")));
        gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.gridx = 0;
        gbc.gridy = 0;
        treePanel.add(new JScrollPane(opTree), gbc);
        
        List<Character> tabuMnemonics = new ArrayList<Character>();
        String btnSaveTitle = messages.getString("button.title.save_changes", "Save changes");
        Character btnSaveMnemonic = MnemonicGenerator.getMnemonicChar(btnSaveTitle, tabuMnemonics);
        JButton btnSave = new JButton(btnSaveTitle);
        SaveOperationsChangesAction saveOperationsAction = new SaveOperationsChangesAction(
                operationsTree.getModel().getRoot(), unusedOperationsListModel);
        saveOperationsAction.addMessageListener(this);
        btnSave.setAction(saveOperationsAction);
        if (btnSaveMnemonic != null) {
            btnSave.setMnemonic(btnSaveMnemonic);
        }
        
        gbc.weighty = 0.0;
        gbc.gridy = 1;
        treePanel.add(btnSave, gbc);
        splitPane.add(treePanel);

        UnusedOperationsList ol = new UnusedOperationsList();
        ol.setModel(unusedOperationsListModel);
        
        JPanel unusedOperationsPanel = new JPanel(new BorderLayout());
        unusedOperationsPanel.setBorder(BorderFactory.createTitledBorder(
                messages.getString("title.unused_operations", "Unused operations")));
        unusedOperationsPanel.add(new JScrollPane(ol));
        splitPane.add(unusedOperationsPanel);
        tabbedPane.add(
                messages.getString("tab.title.operations", "Operations"), splitPane);
        
        opTree.addDeletedItemsListener(ol);
        
        // add tab - setting types
        JPanel pnlTypes = new JPanel(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.PAGE_START;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.weighty = 0.0;
        gbc.gridx = 0;
        gbc.gridy = 0;
        pnlTypes.add(createTypesInfoPanel(), gbc);
        
        // list of assciating types
        JSplitPane settingTypesPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        settingTypesPanel.setDividerLocation(450);
        settingTypesPanel.setContinuousLayout(true);
        
        JPanel pnlTypesList = new JPanel(new GridBagLayout());
        pnlTypesList.setBorder(BorderFactory.createTitledBorder(
                messages.getString("title.connected_extensions", "Connected extensions")));
        GridBagConstraints pnlTypesListGBC = new GridBagConstraints();
        pnlTypesListGBC.anchor = GridBagConstraints.PAGE_START;
        pnlTypesListGBC.fill = GridBagConstraints.BOTH;
        pnlTypesListGBC.weightx = 1.0;
        pnlTypesListGBC.weighty = 1.0;
        pnlTypesListGBC.gridx = 0;
        pnlTypesListGBC.gridy = 0;

        TypesList typesList = new TypesList();
        typesList.addMessageListener(this);
        typesList.setModel(typesListModel);
        pnlTypesList.add(new JScrollPane(typesList), pnlTypesListGBC);

        pnlTypesListGBC.fill = GridBagConstraints.HORIZONTAL;
        pnlTypesListGBC.weighty = 0.0;
        pnlTypesListGBC.gridy = 1;
        pnlTypesList.add(createTypesButtonPanel(), pnlTypesListGBC);

        settingTypesPanel.add(pnlTypesList);
        
        // list of available types
        JPanel pnlAvailableTypes = new JPanel(new BorderLayout());
        pnlAvailableTypes.setBorder(BorderFactory.createTitledBorder(
                messages.getString("title.available_extensions", "Available extensions")));
        AvailableTypesList availableTypesList = new AvailableTypesList();
        availableTypesList.setModel(availableTypesListModel);
        pnlAvailableTypes.add(new JScrollPane(availableTypesList));

        typesList.addDeleteItemListener(availableTypesList);
        settingTypesPanel.add(pnlAvailableTypes);
            
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.gridx = 0;
        gbc.gridy = 1;
        pnlTypes.add(settingTypesPanel, gbc);
        tabbedPane.add(messages.getString("tab.title.types", "Types"), pnlTypes);
        
        headPanel.add(tabbedPane);
        add(headPanel);
        
        ipAction = new ImportPackageAction(this, pluginsList, unusedOperationsListModel, availableTypesListModel);
        ipAction.setEnabled(false);
        btnImportPackage.setAction(ipAction);
        filesSelector.addPropertyChangeListener(ipAction);
    }

    private JPanel createTypesInfoPanel() {
        
        JPanel pnlInfo = new JPanel();
        Font font = pnlInfo.getFont().deriveFont(Font.ITALIC);
        
        JLabel lblWrapper = new JLabel("Wrapper");
        lblWrapper.setFont(font);
        Image wrapperImgIcon = DrawIcon.drawRectangleIcon(
                12, 12, DrawIcon.WRAPPER_COLOR, Color.BLACK);
        lblWrapper.setIcon(new ImageIcon(wrapperImgIcon));
        pnlInfo.add(lblWrapper);
        
        JLabel lblCopier = new JLabel("Copier");
        lblCopier.setFont(font);
        Image copierImgIcon = DrawIcon.drawRectangleIcon(
                12, 12, DrawIcon.COPIER_COLOR, Color.BLACK);
        lblCopier.setIcon(new ImageIcon(copierImgIcon));
        pnlInfo.add(lblCopier);
        
        JLabel lblRenderer = new JLabel("Parameter Cell Renderer");
        lblRenderer.setFont(font);
        Image rendererImgIcon = DrawIcon.drawRectangleIcon(
                12, 12, DrawIcon.RENDERER_COLOR, Color.BLACK);
        lblRenderer.setIcon(new ImageIcon(rendererImgIcon));
        pnlInfo.add(lblRenderer);
        
        JLabel lblEditor = new JLabel("Parameter Cell Editor");
        lblEditor.setFont(font);
        Image editorImgIcon = DrawIcon.drawRectangleIcon(
                12, 12, DrawIcon.EDITOR_COLOR, Color.BLACK);
        lblEditor.setIcon(new ImageIcon(editorImgIcon));
        pnlInfo.add(lblEditor);
        
        return pnlInfo;
    }
    
    private JPanel createTypesButtonPanel() {
        JPanel pnlButtons = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.weighty = 0.0;
        gbc.gridx = 0;
        gbc.gridy = 0;
        
        List<Character> tabuMnemonics = new ArrayList<Character>();
        String btnAddTitle = messages.getString("button.title.add_new", "Add new");
        Character btnAddMnemonic = MnemonicGenerator.getMnemonicChar(btnAddTitle, tabuMnemonics);
        JButton btnAdd = new JButton(btnAddTitle);
        btnAdd.addActionListener(new AddNewTypeAction());
        if (btnAddMnemonic != null) {
            btnAdd.setMnemonic(btnAddMnemonic);
        }
        pnlButtons.add(btnAdd, gbc);
        
        gbc.gridx = 1;
        String btnSaveTitle = messages.getString("button.title.save_changes", "Save changes");
        Character btnSaveMnemonic = MnemonicGenerator.getMnemonicChar(btnSaveTitle, tabuMnemonics);
        JButton btnSave = new JButton(btnSaveTitle);
        btnSave.addActionListener(new SaveTypesAction());
        if (btnSaveMnemonic != null) {
            btnSave.setMnemonic(btnSaveMnemonic);
        }
        pnlButtons.add(btnSave, gbc);
        
        return pnlButtons;
    }

    /**
     * Akce navázána na zavření okna s plug-in managerem. Před
	 * zavřením zkontroluje změny a když tak se zeptá zda mají být
	 * provedené změny uloženy.
     */
    private class CloseWindowAction extends WindowAdapter {
        
        @Override
        public void windowClosing(WindowEvent e) {
            boolean showSaveQuestion = false;

            if (pluginsChanged || operationsChanged || typesChanged) {
                showSaveQuestion = true;
            }

            if (showSaveQuestion) {
                String title = messages.getString("dialog.title.save_changes",
                                                  "Save changes");
                int result;
                if (pluginsChanged) {
                    String message = messages.getString("dialog.message.save_changes", 
                                                        "Changes will be save.");
                    
                    JOptionPane.showMessageDialog(PluginManagerPanel.this,
                        message, title, JOptionPane.INFORMATION_MESSAGE);

                    result = JOptionPane.OK_OPTION;
                } else {
                    String message = messages.getString("dialog.question.save_changes",
                                                        "Save changes?");
                    result = JOptionPane.showConfirmDialog(PluginManagerPanel.this, 
                        message, title, JOptionPane.OK_OPTION, 
                        JOptionPane.QUESTION_MESSAGE);
                }

                if (result == JOptionPane.OK_OPTION) {
                    // save operations changes
                    UnusedOperationsWriter.save(unusedOperationsListModel);
                    OperationsWriter.saveOperationsTree(operationsTree.getModel().getRoot());

                    // save types changed
                    ExtensionsOfTypes.save();
                    availableTypesListModel.save();
                }
            }
        }
    }
    
    /**
     * Akce pro přidání nového typu ke kterému je následně možné
	 * připojit typová rozšíření.
     */
    private class AddNewTypeAction implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            String message = messages.getString("dialog.message.insert_type", 
                    "Insert the canonical name of existing type:");
            String title = messages.getString("title.extension_for_type", 
                    "Extension for type");
            String result = JOptionPane.showInputDialog(PluginManagerPanel.this,
                    message, title, JOptionPane.PLAIN_MESSAGE);
            
            if (result != null) {
                try {
                    Class c = Class.forName(result, true, DesprClassLoader.getClassLoader());
                    typesListModel.addNewType(c);
                } catch (ClassNotFoundException ex) {
                    String excpTitle = messages.getString("title.class_not_found_excp", 
                            "Class not found");
                    Despr.showError(excpTitle, ex, Level.WARNING, false);
                } catch (RuntimeException ex) {
                    String excpTitle = messages.getString("title.type_already_exists",
                            "Type is already exists");
                    Despr.showError(excpTitle, ex, Level.WARNING, false);
                }
            }
        }
        
    }
    
    /**
     * Uloží změny provedené v seznamu napojených typů.
     */
    private class SaveTypesAction implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            ExtensionsOfTypes.save();
            availableTypesListModel.save();
            String message = messages.getString("dialog.message.connected_extensions_saved",
                    "Connected extensions has been saved.");
            String title = messages.getString("dialog.title.extensions_saved", 
                    "Extensions saved");
            JOptionPane.showMessageDialog(PluginManagerPanel.this,
                    message, title, JOptionPane.INFORMATION_MESSAGE);
            typesChanged = false;
        }
        
    }
    
    /**
     * Filtr pro jar balíky.
     */
    private class JarFileFilter extends FileFilter implements java.io.FileFilter {
        @Override
        public boolean accept(File f) {
            if (f.isDirectory()) {
                return true;
            } else {
                return f.getName().toLowerCase().endsWith(".jar");
            }
        }

        @Override
        public String getDescription() {
            return "JAR package";
        }
    }
}
